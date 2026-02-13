package org.mangala.authentication.policy.usecase;

import org.mangala.security.model.ApiPermissionDTO;

import java.util.List;

public interface PolicyQueryService {

    List<ApiPermissionDTO> getActivePolicies();

    long getPolicyVersion();
}
