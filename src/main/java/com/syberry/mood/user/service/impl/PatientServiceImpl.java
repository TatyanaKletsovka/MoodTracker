package com.syberry.mood.user.service.impl;

import com.syberry.mood.user.converter.UserConverter;
import com.syberry.mood.user.dto.PatientCreationDto;
import com.syberry.mood.user.dto.PatientDto;
import com.syberry.mood.user.entity.User;
import com.syberry.mood.user.repository.UserRepository;
import com.syberry.mood.user.service.PatientService;
import com.syberry.mood.user.validation.PatientValidator;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing patients.
 */
@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

  private final UserConverter userConverter;
  private final UserRepository userRepository;
  private final PatientValidator patientValidator;

  /**
   * Finds all patients and returns them as a list of DTOs.
   *
   * @return a list of all patients, represented as DTOs
   */
  @Override
  public List<PatientDto> findAllPatients() {
    return userRepository.findAllPatientsSortIdDesc()
        .stream().map(userConverter::convertToPatientDto).toList();
  }

  /**
   * Finds a patient by their ID and returns them as a DTO.
   *
   * @param id the ID of the patient to find
   * @return the patient with the given ID, represented as a DTO
   */
  @Override
  public PatientDto findPatientById(Long id) {
    return userConverter.convertToPatientDto(userRepository.findPatientByIdIfExists(id));
  }

  /**
   * Finds the currently authenticated patient's profile and returns it as a DTO.
   *
   * @return the currently authenticated patient's profile, represented as a DTO
   */
  @Override
  public PatientDto findPatientProfile() {
    // TODO: add realization after authorization
    return null;
  }

  /**
   * Creates a new patient from the given DTO and returns it as a DTO.
   *
   * @param dto the DTO representing the patient to create
   * @return the created patient, represented as a DTO
   */
  @Override
  public PatientDto createPatient(PatientCreationDto dto) {
    patientValidator.validateSuperheroName(dto.getSuperheroName(), null);
    // TODO: add password encoder after authorization
    User user = userConverter.convertToEntity(dto);
    return userConverter.convertToPatientDto(userRepository.save(user));
  }

  /**
   * Updates a patient's superhero name by their ID and returns it as a DTO.
   *
   * @param id            the ID of the patient to update
   * @param superheroName the new superhero name to set for the patient
   * @return the updated patient, represented as a DTO
   */
  @Override
  @Transactional
  public PatientDto updatePatientHeroNameById(Long id, String superheroName) {
    patientValidator.validateSuperheroName(superheroName, id);
    User user = userRepository.findPatientByIdIfExists(id);
    patientValidator.validateUpdating(user);
    user.setUsername(superheroName);
    return userConverter.convertToPatientDto(user);
  }

  /**
   * Disables or enables a patient by their ID and returns it as a DTO.
   *
   * @param id the ID of the patient to disable or enable
   * @return the updated patient, represented as a DTO
   */
  @Override
  @Transactional
  public PatientDto disablePatientById(Long id) {
    User user = userRepository.findPatientByIdIfExists(id);
    user.setDisabled(!user.isDisabled());
    return userConverter.convertToPatientDto(user);
  }

  /**
   * Updates a patient's password by their ID.
   *
   * @param id       the ID of the patient to update
   * @param password the new password to set for the patient
   */
  @Override
  @Transactional
  public void updatePasswordByPatientId(Long id, String password) {
    User user = userRepository.findPatientByIdIfExists(id);
    patientValidator.validateUpdating(user);
    // TODO: add password encoder after authorization
    user.setPassword(password);
  }
}
