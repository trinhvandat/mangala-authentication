package org.mangala.authentication.auth.adapter.web;

import com.webauthn4j.util.Base64UrlUtil;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mangala.authentication.auth.adapter.web.dto.*;
import org.mangala.authentication.auth.adapter.web.mapper.AuthenticationResponseMapper;
import org.mangala.authentication.auth.adapter.web.mapper.PasskeyResponseMapper;
import org.mangala.authentication.auth.usecase.*;
import org.mangala.authentication.auth.usecase.command.CompleteAuthenticationCommand;
import org.mangala.authentication.auth.usecase.command.CompletePasskeyRegistrationCommand;
import org.mangala.authentication.auth.usecase.command.RefreshTokenCommand;
import org.mangala.authentication.auth.usecase.command.StartAuthenticationCommand;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final StartRegistrationUseCase startRegistrationUseCase;
    private final CompleteRegistrationUseCase completeRegistrationUseCase;
    private final StartAuthenticationUseCase startAuthenticationUseCase;
    private final CompleteAuthenticationUseCase completeAuthenticationUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final AuthenticationResponseMapper authenticationResponseMapper;
    private final MeterRegistry meterRegistry;

    @Timed("auth.registration.start")
    @PostMapping("/v1/register/passkeys:start")
    @ResponseStatus(HttpStatus.OK)
    public PasskeyRegistrationOptionsDTO startRegistration(
            @Valid @RequestBody StartPasskeyRegistrationRequestDTO requestDTO,
            @RequestHeader("User-Agent") String userAgent,
            HttpServletRequest request
    ) {
        String ipAddress = request.getRemoteAddr();
        var response = startRegistrationUseCase.execute(requestDTO.email(), requestDTO.displayName(), ipAddress, userAgent);
        return PasskeyResponseMapper.toRegistrationOptionsDTO(response);
    }

    @Timed("auth.registration.complete")
    @PostMapping("/v1/register/passkeys:complete")
    @ResponseStatus(HttpStatus.CREATED)
    public CompletePasskeyRegistrationResponseDTO completeRegistration(
            @Valid @RequestBody CompletePasskeyRegistrationRequestDTO requestDTO,
            @RequestHeader("User-Agent") String userAgent,
            HttpServletRequest request
    ) {
        String ipAddress = request.getRemoteAddr();

        // Decode Base64URL data from client
        byte[] credentialId = Base64UrlUtil.decode(requestDTO.getCredential().getId());
        byte[] clientDataJSON = Base64UrlUtil.decode(requestDTO.getCredential().getResponse().getClientDataJSON());
        byte[] attestationObject = Base64UrlUtil.decode(requestDTO.getCredential().getResponse().getAttestationObject());

        // Build command
        var command = CompletePasskeyRegistrationCommand.builder()
            .sessionId(requestDTO.getSessionId())
            .credentialId(credentialId)
            .credentialIdBase64Url(requestDTO.getCredential().getId())
            .clientDataJSON(clientDataJSON)
            .attestationObject(attestationObject)
            .transports(requestDTO.getCredential().getResponse().getTransports())
            .authenticatorAttachment(requestDTO.getAuthenticatorAttachment())
            .ipAddress(ipAddress)
            .userAgent(userAgent)
            .build();

        // Execute use case
        var response = completeRegistrationUseCase.execute(command);

        // Map to DTO
        return CompletePasskeyRegistrationResponseDTO.builder()
            .userId(response.getUserId())
            .credentialId(response.getCredentialId())
            .userVerified(response.getUserVerified())
            .backupEligible(response.getBackupEligible())
            .backupState(response.getBackupState())
            .deviceName(response.getDeviceName())
            .createdAt(response.getCreatedAt())
            .build();
    }

    @Timed("auth.authentication.start")
    @PostMapping("/v1/authenticate/passkeys:start")
    @ResponseStatus(HttpStatus.OK)
    public PasskeyAuthenticationOptionsDTO startAuthentication(
            @Valid @RequestBody StartPasskeyAuthenticationRequestDTO requestDTO,
            @RequestHeader("User-Agent") String userAgent,
            HttpServletRequest request
    ) {
        String ipAddress = request.getRemoteAddr();

        var command = new StartAuthenticationCommand(
                requestDTO.email(),
                userAgent,
                ipAddress
        );

        var response = startAuthenticationUseCase.execute(command);
        return authenticationResponseMapper.toPasskeyAuthenticationOptionsDTO(response);
    }

    @Timed("auth.authentication.complete")
    @Counted("auth.login.attempts")
    @PostMapping("/v1/authenticate/passkeys:complete")
    @ResponseStatus(HttpStatus.OK)
    public CompletePasskeyAuthenticationResponseDTO completeAuthentication(
            @Valid @RequestBody CompletePasskeyAuthenticationRequestDTO requestDTO,
            @RequestHeader("User-Agent") String userAgent,
            HttpServletRequest request
    ) {
        String ipAddress = request.getRemoteAddr();

        var credential = requestDTO.getCredential();
        var response = credential.getResponse();

        var command = new CompleteAuthenticationCommand(
                credential.getId(),
                response.getAuthenticatorData(),
                response.getClientDataJSON(),
                response.getSignature(),
                response.getUserHandle(),
                userAgent,
                ipAddress
        );

        try {
            var result = completeAuthenticationUseCase.execute(command);
            return authenticationResponseMapper.toCompletePasskeyAuthenticationResponseDTO(result);
        } catch (Exception e) {
            Counter.builder("auth.login.failures")
                    .description("Number of failed authentication attempts")
                    .register(meterRegistry)
                    .increment();
            throw e;
        }
    }

    @Timed("auth.token.refresh")
    @PostMapping("/v1/auth/refresh")
    @ResponseStatus(HttpStatus.OK)
    public CompletePasskeyAuthenticationResponseDTO refreshToken(
            @Valid @RequestBody RefreshTokenRequestDTO requestDTO
    ) {
        var response = refreshTokenUseCase.execute(new RefreshTokenCommand(requestDTO.refreshToken()));
        return authenticationResponseMapper.toCompletePasskeyAuthenticationResponseDTO(response);
    }
}
