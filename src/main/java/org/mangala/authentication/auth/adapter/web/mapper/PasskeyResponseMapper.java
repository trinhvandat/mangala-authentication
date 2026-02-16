package org.mangala.authentication.auth.adapter.web.mapper;

import com.webauthn4j.data.*;
import com.webauthn4j.data.client.challenge.Challenge;
import org.mangala.authentication.auth.adapter.web.dto.PasskeyRegistrationOptionsDTO;
import org.mangala.authentication.auth.usecase.model.StartRegisterPasskeyResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mapper để convert từ WebAuthn4J domain objects sang API DTOs
 * Sử dụng static methods vì không có dependencies
 */
public final class PasskeyResponseMapper {

    // Private constructor để prevent instantiation
    private PasskeyResponseMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Convert StartRegisterPasskeyResponse sang PasskeyRegistrationOptionsDTO
     */
    public static PasskeyRegistrationOptionsDTO toRegistrationOptionsDTO(
            StartRegisterPasskeyResponse response) {

        if (response == null) {
            return null;
        }

        return PasskeyRegistrationOptionsDTO.builder()
                .sessionId(response.getSessionId())
                .rp(toRelyingPartyDTO(response.getRp()))
                .user(toUserDTO(response.getUser()))
                .challenge(encodeBase64Url(response.getChallenge()))
                .pubKeyCredParams(toPubKeyCredParamsDTO(response.getPublicKeyCredentialParameters()))
                .timeout(response.getTimeout())
                .excludeCredentials(toExcludeCredentialsDTO(response.getExcludeCredentials()))
                .authenticatorSelection(toAuthenticatorSelectionDTO(response.getAuthenticatorSelectionCriteria()))
                .attestation(toAttestationString(response.getAttestation()))
                .build();
    }

    /**
     * Convert PublicKeyCredentialRpEntity sang RelyingPartyDTO
     */
    private static PasskeyRegistrationOptionsDTO.RelyingPartyDTO toRelyingPartyDTO(
            PublicKeyCredentialRpEntity rp) {

        if (rp == null) {
            return null;
        }

        return PasskeyRegistrationOptionsDTO.RelyingPartyDTO.builder()
                .id(rp.getId())
                .name(rp.getName())
                .build();
    }

    /**
     * Convert PublicKeyCredentialUserEntity sang UserDTO
     */
    private static PasskeyRegistrationOptionsDTO.UserDTO toUserDTO(
            PublicKeyCredentialUserEntity user) {

        if (user == null) {
            return null;
        }

        return PasskeyRegistrationOptionsDTO.UserDTO.builder()
                .id(encodeBase64Url(user.getId()))
                .name(user.getName())
                .displayName(user.getDisplayName())
                .build();
    }

    /**
     * Convert List<PublicKeyCredentialParameters> sang List<PublicKeyCredParamDTO>
     */
    private static List<PasskeyRegistrationOptionsDTO.PublicKeyCredParamDTO> toPubKeyCredParamsDTO(
            List<PublicKeyCredentialParameters> params) {

        if (params == null || params.isEmpty()) {
            return Collections.emptyList();
        }

        return params.stream()
                .filter(Objects::nonNull)
                .map(PasskeyResponseMapper::toPubKeyCredParamDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert PublicKeyCredentialParameters sang PublicKeyCredParamDTO
     */
    private static PasskeyRegistrationOptionsDTO.PublicKeyCredParamDTO toPubKeyCredParamDTO(
            PublicKeyCredentialParameters param) {

        if (param == null) {
            return null;
        }

        return PasskeyRegistrationOptionsDTO.PublicKeyCredParamDTO.builder()
                .type(param.getType().getValue())
                .alg(param.getAlg().getValue())
                .build();
    }

    /**
     * Convert List<PublicKeyCredentialDescriptor> sang List<CredentialDescriptorDTO>
     */
    private static List<PasskeyRegistrationOptionsDTO.CredentialDescriptorDTO> toExcludeCredentialsDTO(
            List<PublicKeyCredentialDescriptor> excludeCredentials) {

        if (excludeCredentials == null || excludeCredentials.isEmpty()) {
            return Collections.emptyList();
        }

        return excludeCredentials.stream()
                .filter(Objects::nonNull)
                .map(PasskeyResponseMapper::toCredentialDescriptorDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert PublicKeyCredentialDescriptor sang CredentialDescriptorDTO
     */
    private static PasskeyRegistrationOptionsDTO.CredentialDescriptorDTO toCredentialDescriptorDTO(
            PublicKeyCredentialDescriptor descriptor) {

        if (descriptor == null) {
            return null;
        }

        return PasskeyRegistrationOptionsDTO.CredentialDescriptorDTO.builder()
                .type(descriptor.getType() != null ? descriptor.getType().getValue() : "public-key")
                .id(encodeBase64Url(descriptor.getId()))
                .transports(toTransportsDTO(descriptor.getTransports()))
                .build();
    }

    /**
     * Convert Set<AuthenticatorTransport> sang List<String>
     */
    private static List<String> toTransportsDTO(java.util.Set<AuthenticatorTransport> transports) {
        if (transports == null || transports.isEmpty()) {
            return Collections.emptyList();
        }

        return transports.stream()
                .filter(Objects::nonNull)
                .map(AuthenticatorTransport::getValue)
                .collect(Collectors.toList());
    }

    /**
     * Convert AuthenticatorSelectionCriteria sang AuthenticatorSelectionDTO
     */
    private static PasskeyRegistrationOptionsDTO.AuthenticatorSelectionDTO toAuthenticatorSelectionDTO(
            AuthenticatorSelectionCriteria criteria) {

        if (criteria == null) {
            return null;
        }

        return PasskeyRegistrationOptionsDTO.AuthenticatorSelectionDTO.builder()
                .authenticatorAttachment(toAuthenticatorAttachmentString(criteria.getAuthenticatorAttachment()))
                .residentKey(toResidentKeyString(criteria.isRequireResidentKey()))
                .userVerification(toUserVerificationString(criteria.getUserVerification()))
                .build();
    }

    /**
     * Convert AuthenticatorAttachment sang String
     */
    private static String toAuthenticatorAttachmentString(AuthenticatorAttachment attachment) {
        if (attachment == null) {
            return null;
        }
        return attachment.getValue();
    }

    /**
     * Convert boolean requireResidentKey sang String
     */
    private static String toResidentKeyString(boolean requireResidentKey) {
        return requireResidentKey ? "required" : "preferred";
    }

    /**
     * Convert UserVerificationRequirement sang String
     */
    private static String toUserVerificationString(UserVerificationRequirement requirement) {
        if (requirement == null) {
            return "preferred"; // default value
        }
        return requirement.getValue();
    }

    /**
     * Convert AttestationConveyancePreference sang String
     */
    private static String toAttestationString(AttestationConveyancePreference attestation) {
        if (attestation == null) {
            return "none"; // default value
        }
        return attestation.getValue();
    }

    /**
     * Encode byte array sang Base64URL string (without padding)
     */
    private static String encodeBase64Url(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(data);
    }

    /**
     * Decode Base64URL string sang byte array
     */
    public static byte[] decodeBase64Url(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return null;
        }

        try {
            return Base64.getUrlDecoder().decode(encoded);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Base64URL string: " + encoded, e);
        }
    }

//    // ========== Additional Mapping Methods cho Authentication Flow ==========
//
//    /**
//     * Convert StartAuthenticationPasskeyResponse sang PasskeyAuthenticationOptionsDTO
//     * (Cho authentication flow)
//     */
//    public static PasskeyAuthenticationOptionsDTO toAuthenticationOptionsDTO(
//            StartAuthenticationPasskeyResponse response) {
//
//        if (response == null) {
//            return null;
//        }
//
//        return PasskeyAuthenticationOptionsDTO.builder()
//                .challenge(encodeBase64Url(response.getChallenge()))
//                .timeout(response.getTimeout())
//                .rpId(response.getRpId())
//                .allowCredentials(toAllowCredentialsDTO(response.getAllowCredentials()))
//                .userVerification(toUserVerificationString(response.getUserVerification()))
//                .build();
//    }
//
//    /**
//     * Convert List<PublicKeyCredentialDescriptor> sang List<AllowCredentialDTO>
//     */
//    private static List<PasskeyAuthenticationOptionsDTO.AllowCredentialDTO> toAllowCredentialsDTO(
//            List<PublicKeyCredentialDescriptor> allowCredentials) {
//
//        if (allowCredentials == null || allowCredentials.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        return allowCredentials.stream()
//                .filter(Objects::nonNull)
//                .map(PasskeyResponseMapper::toAllowCredentialDTO)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Convert PublicKeyCredentialDescriptor sang AllowCredentialDTO
//     */
//    private static PasskeyAuthenticationOptionsDTO.AllowCredentialDTO toAllowCredentialDTO(
//            PublicKeyCredentialDescriptor descriptor) {
//
//        if (descriptor == null) {
//            return null;
//        }
//
//        return PasskeyAuthenticationOptionsDTO.AllowCredentialDTO.builder()
//                .type(descriptor.getType() != null ? descriptor.getType().getValue() : "public-key")
//                .id(encodeBase64Url(descriptor.getId()))
//                .transports(toTransportsDTO(descriptor.getTransports()))
//                .build();
//    }
//
//    // ========== Mapping cho Credential Management ==========
//
//    /**
//     * Convert PasskeyCredential entity sang CredentialInfoDTO
//     */
//    public static CredentialInfoDTO toCredentialInfoDTO(PasskeyCredential credential) {
//        if (credential == null) {
//            return null;
//        }
//
//        return CredentialInfoDTO.builder()
//                .id(credential.getId())
//                .credentialId(credential.getCredentialId())
//                .label(credential.getLabel())
//                .type(credential.getType() != null ? credential.getType().name().toLowerCase() : null)
//                .signatureCount(credential.getSignatureCount())
//                .createdAt(credential.getCreatedAt())
//                .lastUsedAt(credential.getLastUsedAt())
//                .build();
//    }
//
//    /**
//     * Convert List<PasskeyCredential> sang List<CredentialInfoDTO>
//     */
//    public static List<CredentialInfoDTO> toCredentialInfoDTOList(List<PasskeyCredential> credentials) {
//        if (credentials == null || credentials.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        return credentials.stream()
//                .filter(Objects::nonNull)
//                .map(PasskeyResponseMapper::toCredentialInfoDTO)
//                .collect(Collectors.toList());
//    }

    // ========== Validation Helpers ==========

    /**
     * Validate Base64URL string format
     */
    public static boolean isValidBase64Url(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return false;
        }

        try {
            Base64.getUrlDecoder().decode(encoded);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Safely encode nullable byte array
     */
    public static String safeEncodeBase64Url(byte[] data, String defaultValue) {
        if (data == null || data.length == 0) {
            return defaultValue;
        }
        return encodeBase64Url(data);
    }

    /**
     * Safely decode nullable Base64URL string
     */
    public static byte[] safeDecodeBase64Url(String encoded, byte[] defaultValue) {
        if (encoded == null || encoded.isEmpty()) {
            return defaultValue;
        }
        try {
            return decodeBase64Url(encoded);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
}
