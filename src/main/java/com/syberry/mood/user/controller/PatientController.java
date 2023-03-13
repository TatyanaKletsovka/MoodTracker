package com.syberry.mood.user.controller;

import com.syberry.mood.user.dto.PatientCreationDto;
import com.syberry.mood.user.dto.PatientDto;
import com.syberry.mood.user.service.PatientService;
import com.syberry.mood.user.util.Constants;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller for handling patient-related HTTP requests.
 */
@RestController
@CrossOrigin
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/patients")
public class PatientController {

  private final PatientService patientService;

  /**
   * Returns a list of all patients ordered by id DESC.
   *
   * @return a list of all patients
   */
  @GetMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
  public List<PatientDto> findAllPatients() {
    log.info("GET-request: getting all patients");
    return patientService.findAllPatients();
  }

  /**
   * Returns the patient with the specified ID.
   *
   * @param id the ID of the patient to return
   * @return the patient with the specified ID
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
  public PatientDto findPatientById(@PathVariable("id") Long id) {
    log.info("GET-request: getting patient with id: {}", id);
    return patientService.findPatientById(id);
  }

  /**
   * Returns the profile of the current patient.
   *
   * @return the profile of the current patient
   */
  @GetMapping("/profile")
  @PreAuthorize("hasAnyRole('USER')")
  public PatientDto findPatientProfile() {
    log.info("GET-request: getting current patient profile");
    return patientService.findPatientProfile();
  }

  /**
   * Creates a new patient with the specified data.
   *
   * @param dto the data of the patient to create
   * @return the created patient
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
  public PatientDto createPatient(@Valid @RequestBody PatientCreationDto dto) {
    log.info("POST-request: creating new patient");
    return patientService.createPatient(dto);
  }

  /**
   * Updates the superhero name of the patient with the specified ID.
   *
   * @param id            the ID of the patient to update
   * @param superheroName the new superhero name of the patient
   * @return the updated patient
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
  public PatientDto updatePatientHeroNameById(@PathVariable("id") Long id,
                                       @Pattern(regexp = Constants.SUPERHERO_NAME_REGEX,
                                           message = Constants.SUPERHERO_NAME_MESSAGE)
                                       @RequestBody String superheroName) {
    log.info("PUT-request: updating patient with id: {}", id);
    return patientService.updatePatientHeroNameById(id, superheroName);
  }

  /**
   * Reverses the disabled status of the patient with the specified ID.
   *
   * @param id the ID of the patient to update
   * @return the updated patient
   */
  @PutMapping("/{id}/disabled")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
  public PatientDto disablePatientById(@PathVariable("id") Long id) {
    log.info("PUT-request: reverse is patient disabled for patient with id: {}", id);
    return patientService.disablePatientById(id);
  }

  /**
   * Updates the password of the patient with the specified ID.
   *
   * @param id       the ID of the patient to update
   * @param password the new password of the patient
   */
  @PutMapping("/{id}/new-password")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
  public void updatePasswordByPatientId(@PathVariable("id") Long id,
                                 @Pattern(regexp = Constants.PATIENT_PASSWORD_REGEX,
                                     message = Constants.PATIENT_PASSWORD_MESSAGE)
                                 @RequestBody String password) {
    log.info("PUT-request: updating password for patient with id: {}", id);
    patientService.updatePasswordByPatientId(id, password);
  }
}
