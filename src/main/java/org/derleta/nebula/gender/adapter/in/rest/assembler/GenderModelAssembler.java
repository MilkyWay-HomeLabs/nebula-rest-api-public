package org.derleta.nebula.gender.adapter.in.rest.assembler;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import org.derleta.nebula.gender.adapter.in.rest.GenderController;
import org.derleta.nebula.gender.adapter.in.rest.dto.GenderResponse;
import org.derleta.nebula.gender.adapter.in.rest.mapper.GenderRestMapper;
import org.derleta.nebula.gender.domain.model.Gender;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/** HATEOAS assembler — converts Gender domain model to GenderResponse with self link. */
@Component
public class GenderModelAssembler extends RepresentationModelAssemblerSupport<Gender, GenderResponse> {

    private final String resourcesBaseUrl;

    public GenderModelAssembler(@Value("${app.resources.base-url}") String resourcesBaseUrl) {
        super(GenderController.class, GenderResponse.class);
        this.resourcesBaseUrl = resourcesBaseUrl;
    }

    @Override
    @Nonnull
    public GenderResponse toModel(@Nonnull Gender entity) {
        GenderResponse response = GenderRestMapper.toResponse(entity, resourcesBaseUrl);
        Link selfLink = linkTo(GenderController.class)
                .slash(GenderController.DEFAULT_PATH)
                .slash(response.getId())
                .withSelfRel();
        response.add(selfLink);
        return response;
    }
}

