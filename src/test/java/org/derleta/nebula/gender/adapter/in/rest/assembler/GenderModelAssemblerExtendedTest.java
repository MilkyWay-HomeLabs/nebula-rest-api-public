package org.derleta.nebula.gender.adapter.in.rest.assembler;

import org.derleta.nebula.gender.adapter.in.rest.dto.GenderResponse;
import org.derleta.nebula.gender.domain.model.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;

import static org.junit.jupiter.api.Assertions.*;

class GenderModelAssemblerExtendedTest {

    private static final String RESOURCES_BASE_URL = "https://milkyway.test/resources/nebula/";

    private final GenderModelAssembler assembler = new GenderModelAssembler(RESOURCES_BASE_URL);

    @Test
    void toModel_addsIdAndNameAndSelfLink() {
        Gender gender = new Gender(1, "Male");
        GenderResponse response = assembler.toModel(gender);

        assertEquals(1, response.getId());
        assertEquals("Male", response.getName());
        assertTrue(response.hasLink("self"));
    }

    @Test
    void toModel_selfLinkContainsId() {
        Gender gender = new Gender(2, "Female");
        GenderResponse response = assembler.toModel(gender);

        Link selfLink = response.getLink("self").orElseThrow();
        assertTrue(selfLink.getHref().contains("2"));
    }

    @Test
    void toCollectionModel_returnsAllMapped() {
        var models = assembler.toCollectionModel(java.util.List.of(
                new Gender(1, "Male"), new Gender(2, "Female")));
        assertEquals(2, models.getContent().size());
    }
}

