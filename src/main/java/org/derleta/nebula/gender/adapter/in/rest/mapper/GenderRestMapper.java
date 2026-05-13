package org.derleta.nebula.gender.adapter.in.rest.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.derleta.nebula.gender.adapter.in.rest.dto.GenderResponse;
import org.derleta.nebula.gender.domain.model.Gender;

/** Maps Gender domain model to REST response DTO. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenderRestMapper {

    public static GenderResponse toResponse(Gender gender, String resourcesBaseUrl) {
        return GenderResponse.builder()
                .id(gender.id())
                .name(gender.name())
                .imgURL(buildImageBaseUrl(resourcesBaseUrl) + gender.id() + ".png")
                .build();
    }

    private static String buildImageBaseUrl(String resourcesBaseUrl) {
        return normalizeBaseUrl(resourcesBaseUrl) + "icon/gender/";
    }

    private static String normalizeBaseUrl(String resourcesBaseUrl) {
        return resourcesBaseUrl.endsWith("/") ? resourcesBaseUrl : resourcesBaseUrl + "/";
    }
}

