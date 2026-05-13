package org.derleta.nebula.theme.adapter.in.rest.assembler;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import org.derleta.nebula.theme.domain.model.Theme;
import org.derleta.nebula.theme.adapter.in.rest.ThemeController;
import org.derleta.nebula.theme.adapter.in.rest.dto.ThemeResponse;
import org.derleta.nebula.theme.adapter.in.rest.mapper.ThemeRestMapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/** HATEOAS assembler — converts Theme domain model to ThemeResponse with a self-link. */
@Component
public class ThemeModelAssembler extends RepresentationModelAssemblerSupport<Theme, ThemeResponse> {

    private final String resourcesBaseUrl;

    public ThemeModelAssembler(@Value("${app.resources.base-url}") String resourcesBaseUrl) {
        super(ThemeController.class, ThemeResponse.class);
        this.resourcesBaseUrl = resourcesBaseUrl;
    }

    @Override
    @Nonnull
    public ThemeResponse toModel(@Nonnull Theme entity) {
        ThemeResponse model = ThemeRestMapper.toResponse(entity, resourcesBaseUrl);
        Link selfLink = linkTo(ThemeController.class)
                .slash(ThemeController.DEFAULT_PATH)
                .slash(model.getId())
                .withSelfRel();
        model.add(selfLink);
        return model;
    }
}

