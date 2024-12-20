package com.example.mobipay.domain.invitation.controller;

import com.example.mobipay.domain.invitation.dto.InvitationDecisionRequest;
import com.example.mobipay.domain.invitation.dto.InvitationDecisionResponse;
import com.example.mobipay.domain.invitation.dto.InvitationRequest;
import com.example.mobipay.domain.invitation.dto.InvitationResponse;
import com.example.mobipay.domain.invitation.service.InvitationService;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/invitations")
public class InvitationController {

    private final InvitationService invitationService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<InvitationResponse> invite(@AuthenticationPrincipal CustomOAuth2User oauth2User,
                                                     @RequestBody @Valid InvitationRequest request) {
        InvitationResponse response = invitationService.invite(oauth2User, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{invitationId}/response")
    public ResponseEntity<InvitationDecisionResponse> decide(
            @AuthenticationPrincipal CustomOAuth2User oauth2User,
            @PathVariable("invitationId") Long invitationId,
            @RequestBody @Valid InvitationDecisionRequest request) {
        InvitationDecisionResponse reponse = invitationService.decide(oauth2User, invitationId, request.getApproved());
        return ResponseEntity.ok(reponse);
    }

}
