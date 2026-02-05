package org.mangala.authentication.auth.adapter.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.mangala.authentication.auth.adapter.web.dto.PasskeyRegistrationOptionsDTO;
import org.mangala.authentication.auth.adapter.web.mapper.PasskeyResponseMapper;
import org.mangala.authentication.auth.usecase.StartRegistrationUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final StartRegistrationUseCase startRegistrationUseCase;

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
}
