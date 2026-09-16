package org.derleta.nebula.account.adapter.in.rest.dto.request;

/** REST request DTO carrying the confirmation token id and token value. */
public record UserConfirmationRequest(Long tokenId, String token) {}
