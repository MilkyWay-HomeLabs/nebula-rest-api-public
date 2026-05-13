package org.derleta.nebula.nationality.adapter.in.rest.mapper;

import org.junit.jupiter.api.Test;
import org.derleta.nebula.nationality.adapter.in.rest.dto.NationalityResponse;
import org.derleta.nebula.nationality.domain.model.Nationality;
import org.derleta.nebula.nationality.domain.model.Region;

import static org.junit.jupiter.api.Assertions.*;

class NationalityRestMapperTest {

    private static final String RESOURCES_BASE_URL = "https://milkyway.test/resources/nebula/";

    @Test
    void toResponse_validNationality_returnsNationalityResponse() {
        Region region = new Region(1, "Europe");
        Nationality nationality = new Nationality(1, "Polish", "PL", region);

        NationalityResponse result = NationalityRestMapper.toResponse(nationality, RESOURCES_BASE_URL);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Polish", result.getName());
        assertEquals("PL", result.getCode());
        assertNotNull(result.getRegion());
        assertEquals(1, result.getRegion().getId());
        assertEquals("Europe", result.getRegion().getName());
        assertEquals(RESOURCES_BASE_URL + "icon/nationality/1.png", result.getImgURL());
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void toResponse_nullNationality_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> NationalityRestMapper.toResponse(null, RESOURCES_BASE_URL));
    }

    @Test
    void toResponse_nationalityWithNullRegion_throwsNullPointerException() {
        Nationality nationality = new Nationality(1, "Polish", "PL", null);
        assertThrows(NullPointerException.class, () -> NationalityRestMapper.toResponse(nationality, RESOURCES_BASE_URL));
    }
}

