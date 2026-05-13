package org.derleta.nebula.shared.adapter.in.rest.dto;

import java.time.LocalDateTime;

public record ErrorResponse(String message,
                            String error,
                            LocalDateTime timestamp
) { }

