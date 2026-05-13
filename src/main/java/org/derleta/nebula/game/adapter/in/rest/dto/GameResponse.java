package org.derleta.nebula.game.adapter.in.rest.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

/** REST response DTO for a single game. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GameResponse extends RepresentationModel<GameResponse> {
    private Integer id;
    private String name;
    private Boolean enable;
    private String iconUrl;
    private String pageUrl;
}

