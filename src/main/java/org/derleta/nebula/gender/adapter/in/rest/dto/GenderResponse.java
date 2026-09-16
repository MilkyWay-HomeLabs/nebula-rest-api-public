package org.derleta.nebula.gender.adapter.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GenderResponse extends RepresentationModel<GenderResponse> {
    private Integer id;
    private String name;
    private String imgURL;
}

