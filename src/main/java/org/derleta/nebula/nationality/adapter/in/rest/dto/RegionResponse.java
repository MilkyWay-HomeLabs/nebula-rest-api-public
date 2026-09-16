package org.derleta.nebula.nationality.adapter.in.rest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class RegionResponse {
    private Integer id;
    private String name;
}

