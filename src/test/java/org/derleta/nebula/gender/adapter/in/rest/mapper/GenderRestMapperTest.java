package org.derleta.nebula.gender.adapter.in.rest.mapper;

import org.junit.jupiter.api.Test;
import org.derleta.nebula.gender.adapter.in.rest.dto.GenderResponse;
import org.derleta.nebula.gender.domain.model.Gender;

import static org.junit.jupiter.api.Assertions.*;

class GenderRestMapperTest {

    private static final String RESOURCES_BASE_URL = "https://milkyway.test/resources/nebula/";

    @Test
    void toResponse_validGender_returnsGenderResponse() {
        Gender gender = new Gender(1, "Male");

        GenderResponse result = GenderRestMapper.toResponse(gender, RESOURCES_BASE_URL);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Male", result.getName());
        assertEquals(RESOURCES_BASE_URL + "icon/gender/1.png", result.getImgURL());
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void toResponse_nullGender_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> GenderRestMapper.toResponse(null, RESOURCES_BASE_URL));
    }
}

