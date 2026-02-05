package org.mangala.authentication.passkey.usecase.mapper;

import org.hibernate.validator.constraints.UUID;
import org.mangala.authentication.passkey.domain.PasskeyChallengeEntity;
import org.mangala.authentication.passkey.usecase.command.CreatePasskeyChallengeCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public abstract class PasskeyChallengeCommandMapper {

    @Mapping(target = "isUsed", constant = "false")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    public abstract PasskeyChallengeEntity toEntity(CreatePasskeyChallengeCommand command);
}
