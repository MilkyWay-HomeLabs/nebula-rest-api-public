package org.derleta.nebula.nationality.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.derleta.nebula.nationality.application.port.in.GetAllNationalitiesUseCase;
import org.derleta.nebula.nationality.application.port.in.GetNationalityUseCase;
import org.derleta.nebula.nationality.application.port.out.NationalityRepositoryPort;
import org.derleta.nebula.nationality.domain.exception.NationalityNotFoundException;
import org.derleta.nebula.nationality.domain.model.Nationality;

import java.util.List;

/** Application service implementing nationality use cases. */
@Service
@RequiredArgsConstructor
public class NationalityService implements GetNationalityUseCase, GetAllNationalitiesUseCase {

    private final NationalityRepositoryPort nationalityRepository;

    @Override
    public Nationality getNationality(int id) {
        return nationalityRepository.findById(id)
                .orElseThrow(() -> new NationalityNotFoundException("Nationality with id: " + id + " not found"));
    }

    @Override
    public List<Nationality> getAllNationalities() {
        return nationalityRepository.findAll();
    }
}

