package org.derleta.nebula.theme.adapter.in.rest.mapper;

import org.junit.jupiter.api.Test;
import org.derleta.nebula.theme.domain.model.Theme;
import org.derleta.nebula.theme.adapter.in.rest.dto.ThemeResponse;

import static org.junit.jupiter.api.Assertions.*;

class ThemeRestMapperTest {

    private static final String RESOURCES_BASE_URL = "https://milkyway.test/resources/nebula/";

    @Test
    void toResponse_validTheme_returnsThemeResponse() {
        Theme theme = new Theme(1, "Dark");

        ThemeResponse result = ThemeRestMapper.toResponse(theme, RESOURCES_BASE_URL);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Dark", result.getName());
        assertEquals(RESOURCES_BASE_URL + "icon/theme/1.png", result.getImgURL());
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void toResponse_nullTheme_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ThemeRestMapper.toResponse(null, RESOURCES_BASE_URL));
    }
}

