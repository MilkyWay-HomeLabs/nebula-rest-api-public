package org.derleta.nebula.nationality.adapter.in.rest.assembler;

import org.derleta.nebula.nationality.adapter.in.rest.dto.NationalityResponse;
import org.derleta.nebula.nationality.domain.model.Nationality;
import org.derleta.nebula.nationality.domain.model.Region;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NationalityModelAssemblerExtendedTest {

    private static final String RESOURCES_BASE_URL = "https://milkyway.test/resources/nebula/";

    private final NationalityModelAssembler assembler = new NationalityModelAssembler(RESOURCES_BASE_URL);

    private Nationality nationality(int id) {
        return new Nationality(id, "Polish", "POL", new Region(1, "Europe"));
    }

    @Test
    void toModel_addsFieldsAndSelfLink() {
        NationalityResponse response = assembler.toModel(nationality(1));

        assertEquals(1, response.getId());
        assertTrue(response.hasLink("self"));
    }

    @Test
    void toModel_selfLinkContainsId() {
        NationalityResponse response = assembler.toModel(nationality(5));
        Link selfLink = response.getLink("self").orElseThrow();
        assertTrue(selfLink.getHref().contains("5"));
    }

    @Test
    void toCollectionModel_mapsAll() {
        var models = assembler.toCollectionModel(List.of(nationality(1), nationality(2)));
        assertEquals(2, models.getContent().size());
    }
}

