package org.mangala.authentication.auth.adapter.web;

import com.webauthn4j.util.Base64UrlUtil;
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

    @PostMapping("/v1/register/passkeys:start")
    @ResponseStatus(HttpStatus.OK)
    public PasskeyRegistrationOptionsDTO startRegistration(
            @RequestParam(required = false) String email,
            @RequestHeader("User-Agent") String userAgent,
            HttpServletRequest request
    ) {
        String ipAddress = request.getRemoteAddr();
        var response = startRegistrationUseCase.execute(email, ipAddress, userAgent);
        return PasskeyResponseMapper.toRegistrationOptionsDTO(response);
    }

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

    @PostMapping("/v1/authenticate/passkeys:complete")
    @ResponseStatus(HttpStatus.OK)
    public CompletePasskeyAuthenticationResponseDTO completeAuthentication(
            @Valid @RequestBody CompletePasskeyAuthenticationRequestDTO requestDTO,
            @RequestHeader("User-Agent") String userAgent,
            HttpServletRequest request
    ) {
        String ipAddress = request.getRemoteAddr();

        var command = new CompleteAuthenticationCommand(
                requestDTO.credentialId(),
                requestDTO.authenticatorData(),
                requestDTO.clientDataJSON(),
                requestDTO.signature(),
                requestDTO.userHandle(),
                userAgent,
                ipAddress
        );

        var response = completeAuthenticationUseCase.execute(command);
        return authenticationResponseMapper.toCompletePasskeyAuthenticationResponseDTO(response);
    }

    @PostMapping("/v1/auth/refresh")
    @ResponseStatus(HttpStatus.OK)
    public CompletePasskeyAuthenticationResponseDTO refreshToken(
            @Valid @RequestBody RefreshTokenRequestDTO requestDTO
    ) {
        var response = refreshTokenUseCase.execute(new RefreshTokenCommand(requestDTO.refreshToken()));
        return authenticationResponseMapper.toCompletePasskeyAuthenticationResponseDTO(response);
    }
}
