package org.derleta.nebula.nationality.adapter.in.rest.dto;

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
public class NationalityResponse extends RepresentationModel<NationalityResponse> {
    private Integer id;
    private String name;
    private String code;
    private RegionResponse region;
    private String imgURL;
}

