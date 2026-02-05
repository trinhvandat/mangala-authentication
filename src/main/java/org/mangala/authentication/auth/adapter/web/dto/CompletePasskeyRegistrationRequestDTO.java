package org.mangala.authentication.auth.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CompletePasskeyRegistrationRequestDTO {

    @NotBlank(message = "Session ID is required")
    private String sessionId;

    @Valid
    @NotNull(message = "Credential is required")
    private CredentialDTO credential;

    private String authenticatorAttachment; // "platform", "cross-platform"

    private Map<String, Object> clientExtensionResults;

    @Data
    public static class CredentialDTO {
        @NotBlank(message = "Credential ID is required")
        private String id;  // Base64URL

        @NotBlank(message = "Raw ID is required")
        private String rawId;  // Base64URL

        @NotBlank(message = "Type is required")
        private String type;  // "public-key"

        @Valid
        @NotNull(message = "Response is required")
        private AuthenticatorAttestationResponseDTO response;
    }

    @Data
    public static class AuthenticatorAttestationResponseDTO {
        @NotBlank(message = "Client data JSON is required")
        private String clientDataJSON;  // Base64URL

        @NotBlank(message = "Attestation object is required")
        private String attestationObject;  // Base64URL

        private List<String> transports;  // ["internal", "hybrid", "usb", "nfc", "ble"]
    }
}
