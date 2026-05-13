package org.derleta.nebula.theme.adapter.in.rest.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

/** REST response DTO for a single theme. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ThemeResponse extends RepresentationModel<ThemeResponse> {
    private Integer id;
    private String name;
    private String imgURL;
}

