package org.derleta.nebula.game.adapter.in.rest.dto;

/** Inbound DTO for creating or updating a game. */
public record GameNewRequest(String name, boolean enable, String iconUrl, String pageUrl) {}

