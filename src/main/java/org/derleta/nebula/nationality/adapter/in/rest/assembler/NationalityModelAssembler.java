package org.derleta.nebula.nationality.adapter.in.rest.assembler;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import org.derleta.nebula.nationality.adapter.in.rest.NationalityController;
import org.derleta.nebula.nationality.adapter.in.rest.dto.NationalityResponse;
import org.derleta.nebula.nationality.adapter.in.rest.mapper.NationalityRestMapper;
import org.derleta.nebula.nationality.domain.model.Nationality;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/** HATEOAS assembler — converts Nationality domain model to NationalityResponse with self link. */
@Component
public class NationalityModelAssembler
        extends RepresentationModelAssemblerSupport<Nationality, NationalityResponse> {

    private final String resourcesBaseUrl;

    public NationalityModelAssembler(@Value("${app.resources.base-url}") String resourcesBaseUrl) {
        super(NationalityController.class, NationalityResponse.class);
        this.resourcesBaseUrl = resourcesBaseUrl;
    }

    @Override
    @Nonnull
    public NationalityResponse toModel(@Nonnull Nationality entity) {
        NationalityResponse response = NationalityRestMapper.toResponse(entity, resourcesBaseUrl);
        Link selfLink = linkTo(NationalityController.class)
                .slash(NationalityController.DEFAULT_PATH)
                .slash(response.getId())
                .withSelfRel();
        response.add(selfLink);
        return response;
    }
}

