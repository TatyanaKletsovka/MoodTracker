package com.syberry.mood.user.service;

import com.syberry.mood.user.dto.PatientCreationDto;
import com.syberry.mood.user.dto.PatientDto;
import java.util.List;

/**
 * Service interface for managing patients.
 */
public interface PatientService {


  /**
   * Finds all patients and returns them as a list of DTOs.
   *
   * @return a list of all patients, represented as DTOs
   */
  List<PatientDto> findAllPatients();

  /**
   * Finds a patient by their ID and returns them as a DTO.
   *
   * @param id the ID of the patient to find
   * @return the patient with the given ID, represented as a DTO
   */
  PatientDto findPatientById(Long id);

  /**
   * Finds the currently authenticated patient's profile and returns it as a DTO.
   *
   * @return the currently authenticated patient's profile, represented as a DTO
   */
  PatientDto findPatientProfile();

  /**
   * Creates a new patient from the given DTO and returns it as a DTO.
   *
   * @param dto the DTO representing the patient to create
   * @return the created patient, represented as a DTO
   */
  PatientDto createPatient(PatientCreationDto dto);

  /**
   * Updates a patient's superhero name by their ID and returns it as a DTO.
   *
   * @param id            the ID of the patient to update
   * @param superheroName the new superhero name to set for the patient
   * @return the updated patient, represented as a DTO
   */
  PatientDto updatePatientHeroNameById(Long id, String superheroName);

  /**
   * Disables or enables a patient by their ID and returns it as a DTO.
   *
   * @param id the ID of the patient to disable or enable
   * @return the updated patient, represented as a DTO
   */
  PatientDto disablePatientById(Long id);

  /**
   * Updates a patient's password by their ID.
   *
   * @param id       the ID of the patient to update
   * @param password the new password to set for the patient
   */
  void updatePasswordByPatientId(Long id, String password);
}
