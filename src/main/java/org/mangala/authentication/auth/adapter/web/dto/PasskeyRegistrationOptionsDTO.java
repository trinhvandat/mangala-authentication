package org.mangala.authentication.auth.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PasskeyRegistrationOptionsDTO {

    private String sessionId;

    private RelyingPartyDTO rp;
    private UserDTO user;

    @JsonProperty("challenge")
    private String challenge;  // ✅ Base64URL encoded string

    @JsonProperty("pubKeyCredParams")
    private List<PublicKeyCredParamDTO> pubKeyCredParams;

    private Long timeout;

    private List<CredentialDescriptorDTO> excludeCredentials;

    private AuthenticatorSelectionDTO authenticatorSelection;

    private String attestation;  // ✅ "none", "direct", "indirect"

    // Nested DTOs

    @Data
    @Builder
    public static class RelyingPartyDTO {
        private String id;
        private String name;
    }

    @Data
    @Builder
    public static class UserDTO {
        @JsonProperty("id")
        private String id;  // ✅ Base64URL encoded

        @JsonProperty("name")
        private String name;

        @JsonProperty("displayName")
        private String displayName;
    }

    @Data
    @Builder
    public static class PublicKeyCredParamDTO {
        private String type;  // "public-key"
        private Long alg;   // -7, -257, etc.
    }

    @Data
    @Builder
    public static class CredentialDescriptorDTO {
        private String type;  // "public-key"
        private String id;    // ✅ Base64URL encoded
        private List<String> transports;  // ["internal", "hybrid"]
    }

    @Data
    @Builder
    public static class AuthenticatorSelectionDTO {
        private String authenticatorAttachment;  // "platform", "cross-platform"
        private String residentKey;  // "required", "preferred", "discouraged"
        private String userVerification;  // "required", "preferred", "discouraged"
    }
}
