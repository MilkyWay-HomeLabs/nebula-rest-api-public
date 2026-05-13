package org.derleta.nebula.theme.adapter.in.rest.assembler;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.hateoas.Link;
import org.derleta.nebula.theme.domain.model.Theme;
import org.derleta.nebula.theme.adapter.in.rest.ThemeController;
import org.derleta.nebula.theme.adapter.in.rest.dto.ThemeResponse;
import org.derleta.nebula.theme.adapter.in.rest.mapper.ThemeRestMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

class ThemeModelAssemblerTest {

    private static final String RESOURCES_BASE_URL = "https://milkyway.test/resources/nebula/";

    @Test
    void toModel_validTheme_addsSelfLinkToResponse() {
        ThemeModelAssembler assembler = new ThemeModelAssembler(RESOURCES_BASE_URL);
        Theme entity = new Theme(1, "Dark");
        ThemeResponse mappedResponse = ThemeResponse.builder()
                .id(entity.id())
                .name(entity.name())
                .build();

        try (MockedStatic<ThemeRestMapper> mockedStatic = Mockito.mockStatic(ThemeRestMapper.class)) {
            mockedStatic.when(() -> ThemeRestMapper.toResponse(entity, RESOURCES_BASE_URL)).thenReturn(mappedResponse);

            Link expectedLink = linkTo(ThemeController.class)
                    .slash(ThemeController.DEFAULT_PATH)
                    .slash(mappedResponse.getId())
                    .withSelfRel();

            ThemeResponse result = assembler.toModel(entity);

            assertThat(result.getId()).isEqualTo(entity.id());
            assertThat(result.getName()).isEqualTo(entity.name());
            assertThat(result.getLinks()).containsExactly(expectedLink);
            mockedStatic.verify(() -> ThemeRestMapper.toResponse(entity, RESOURCES_BASE_URL), times(1));
        }
    }

    @Test
    void toModel_validTheme_mapsFieldsCorrectly() {
        ThemeModelAssembler assembler = new ThemeModelAssembler(RESOURCES_BASE_URL);
        Theme entity = new Theme(2, "Light");
        ThemeResponse mappedResponse = ThemeResponse.builder()
                .id(entity.id())
                .name(entity.name())
                .build();

        try (MockedStatic<ThemeRestMapper> mockedStatic = Mockito.mockStatic(ThemeRestMapper.class)) {
            mockedStatic.when(() -> ThemeRestMapper.toResponse(entity, RESOURCES_BASE_URL)).thenReturn(mappedResponse);

            ThemeResponse result = assembler.toModel(entity);

            assertThat(result.getId()).isEqualTo(entity.id());
            assertThat(result.getName()).isEqualTo(entity.name());
        }
    }
}

