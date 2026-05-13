package org.derleta.nebula.nationality.adapter.in.rest.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.derleta.nebula.nationality.adapter.in.rest.dto.NationalityResponse;
import org.derleta.nebula.nationality.adapter.in.rest.dto.RegionResponse;
import org.derleta.nebula.nationality.domain.model.Nationality;

/** Maps Nationality domain model to REST response DTO. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NationalityRestMapper {

    public static NationalityResponse toResponse(Nationality nationality, String resourcesBaseUrl) {
        RegionResponse regionResponse = RegionResponse.builder()
                .id(nationality.region().id())
                .name(nationality.region().name())
                .build();
        return NationalityResponse.builder()
                .id(nationality.id())
                .name(nationality.name())
                .code(nationality.code())
                .region(regionResponse)
                .imgURL(buildImageBaseUrl(resourcesBaseUrl) + nationality.id() + ".png")
                .build();
    }

    private static String buildImageBaseUrl(String resourcesBaseUrl) {
        return normalizeBaseUrl(resourcesBaseUrl) + "icon/nationality/";
    }

    private static String normalizeBaseUrl(String resourcesBaseUrl) {
        return resourcesBaseUrl.endsWith("/") ? resourcesBaseUrl : resourcesBaseUrl + "/";
    }
}

