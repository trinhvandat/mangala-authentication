package org.mangala.authentication.auth.usecase.model;

import com.webauthn4j.data.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class StartRegisterPasskeyResponse {
    private PublicKeyCredentialRpEntity rp;
    private PublicKeyCredentialUserEntity user;
    private byte[] challenge;
    private List<PublicKeyCredentialParameters> publicKeyCredentialParameters;
    private Long timeout;
    private List<PublicKeyCredentialDescriptor> excludeCredentials;
    private AttestationConveyancePreference attestation;
    private AuthenticatorSelectionCriteria authenticatorSelectionCriteria;
}
