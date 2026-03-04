package org.mangala.authentication.auth.adapter.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class CompletePasskeyAuthenticationRequestDTO {

    @Valid
    @NotNull(message = "Credential is required")
    private CredentialDTO credential;

    private String email;

    @Data
    public static class CredentialDTO {
        @NotBlank(message = "Credential ID is required")
        private String id;  // Base64URL

        @NotBlank(message = "Raw ID is required")
        private String rawId;  // Base64URL

        @NotBlank(message = "Type is required")
        private String type;  // "public-key"

        private String authenticatorAttachment; // "platform", "cross-platform"

        private Map<String, Object> clientExtensionResults;

        @Valid
        @NotNull(message = "Response is required")
        private AuthenticatorAssertionResponseDTO response;
    }

    @Data
    public static class AuthenticatorAssertionResponseDTO {
        @NotBlank(message = "Authenticator data is required")
        private String authenticatorData;  // Base64URL

        @NotBlank(message = "Client data JSON is required")
        private String clientDataJSON;  // Base64URL

        @NotBlank(message = "Signature is required")
        private String signature;  // Base64URL

        private String userHandle;  // Base64URL (optional)
    }
}
