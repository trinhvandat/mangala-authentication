package org.mangala.authentication.policy.adapter.web;

import lombok.RequiredArgsConstructor;
import org.mangala.authentication.policy.usecase.PolicyQueryService;
import org.mangala.security.model.ApiPermissionDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/v1/internal", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class InternalPolicyController {

    private final PolicyQueryService policyQueryService;

    @GetMapping("/policies")
    public List<ApiPermissionDTO> getPolicies() {
        return policyQueryService.getActivePolicies();
    }

    @GetMapping("/policies/version")
    public long getPolicyVersion() {
        return policyQueryService.getPolicyVersion();
    }
}
