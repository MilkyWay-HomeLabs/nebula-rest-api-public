package org.derleta.nebula.gender.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.derleta.nebula.gender.application.port.in.GetAllGendersUseCase;
import org.derleta.nebula.gender.application.port.in.GetGenderUseCase;
import org.derleta.nebula.gender.application.port.out.GenderRepositoryPort;
import org.derleta.nebula.gender.domain.exception.GenderNotFoundException;
import org.derleta.nebula.gender.domain.model.Gender;

import java.util.List;

/** Application service implementing gender use cases. */
@Service
@RequiredArgsConstructor
public class GenderService implements GetGenderUseCase, GetAllGendersUseCase {

    private final GenderRepositoryPort genderRepository;

    @Override
    public Gender getGender(int id) {
        return genderRepository.findById(id)
                .orElseThrow(() -> new GenderNotFoundException("Gender with id: " + id + " not found"));
    }

    @Override
    public List<Gender> getAllGenders() {
        return genderRepository.findAll();
    }
}

