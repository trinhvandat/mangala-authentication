package org.mangala.authentication.auth.adapter.web.mapper;

import org.mangala.authentication.auth.adapter.web.dto.CompletePasskeyAuthenticationResponseDTO;
import org.mangala.authentication.auth.adapter.web.dto.PasskeyAuthenticationOptionsDTO;
import org.mangala.authentication.auth.usecase.model.CompleteAuthenticationResponse;
import org.mangala.authentication.auth.usecase.model.StartAuthenticationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AuthenticationResponseMapper {

    @Mapping(target = "allowCredentials", expression = "java(mapAllowCredentials(response.allowCredentials()))")
    PasskeyAuthenticationOptionsDTO toPasskeyAuthenticationOptionsDTO(StartAuthenticationResponse response);

    CompletePasskeyAuthenticationResponseDTO toCompletePasskeyAuthenticationResponseDTO(CompleteAuthenticationResponse response);

    default List<PasskeyAuthenticationOptionsDTO.AllowedCredentialDTO> mapAllowCredentials(
            List<StartAuthenticationResponse.AllowedCredential> allowCredentials) {
        return allowCredentials.stream()
                .map(credential -> new PasskeyAuthenticationOptionsDTO.AllowedCredentialDTO(
                        credential.id(),
                        credential.type()
                ))
                .collect(Collectors.toList());
    }
}
