package org.derleta.nebula.theme.adapter.in.rest.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.derleta.nebula.theme.domain.model.Theme;
import org.derleta.nebula.theme.adapter.in.rest.dto.ThemeResponse;

/** Maps Theme domain model to REST response DTO. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThemeRestMapper {

    public static ThemeResponse toResponse(Theme theme, String resourcesBaseUrl) {
        return ThemeResponse.builder()
                .id(theme.id())
                .name(theme.name())
                .imgURL(buildImageBaseUrl(resourcesBaseUrl) + theme.id() + ".png")
                .build();
    }

    private static String buildImageBaseUrl(String resourcesBaseUrl) {
        return normalizeBaseUrl(resourcesBaseUrl) + "icon/theme/";
    }

    private static String normalizeBaseUrl(String resourcesBaseUrl) {
        return resourcesBaseUrl.endsWith("/") ? resourcesBaseUrl : resourcesBaseUrl + "/";
    }
}

