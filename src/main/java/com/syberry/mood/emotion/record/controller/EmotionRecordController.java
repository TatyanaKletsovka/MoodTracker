package com.syberry.mood.emotion.record.controller;

import com.syberry.mood.emotion.record.dto.EmotionRecordByPatientDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordCreationDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordUpdatingDto;
import com.syberry.mood.emotion.record.service.EmotionRecordService;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller for handling emotion records related HTTP requests.
 */
@RestController
@Validated
@Slf4j
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/emotion-records")
public class EmotionRecordController {

  private final EmotionRecordService emotionRecordService;

  /**
   * Returns the emotion-record with the specified ID.
   *
   * @param id the ID of the emotion record to return
   * @return the emotion record with the specified ID
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
  public EmotionRecordDto findEmotionRecordById(@PathVariable("id") Long id) {
    log.info("GET-request: getting emotion record with id: {}", id);
    return emotionRecordService.findEmotionRecordById(id);
  }

  /**
   * Retrieves the emotion records for the current patient for today's date.
   *
   * @return a map where the keys are periods
   *     and the values are DTOs representing the emotion records
   */
  @GetMapping("/today")
  @PreAuthorize("hasAnyRole('USER')")
  public Map<String, EmotionRecordDto> findTodayEmotionRecordsForCurrentPatient() {
    log.info("GET-request: getting today emotion records for current patient");
    return emotionRecordService.findTodayEmotionRecordsForCurrentPatient();
  }

  /**
   * Creates a new emotion record for a patient with the specified ID.
   *
   * @param id  the ID of the patient for whom to create an emotion record
   * @param dto the data of the emotion record to create
   * @return the created emotion record
   */
  @PostMapping("/patients/{id}")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
  public EmotionRecordDto createEmotionRecord(@PathVariable("id") Long id,
                                              @Valid @RequestBody EmotionRecordCreationDto dto) {
    log.info("POST-request: creating new emotion record by admin");
    dto.setPatientId(id);
    return emotionRecordService.createEmotionRecord(dto);
  }

  /**
   * Creates a new emotion record for the current patient.
   *
   * @param dto the DTO containing the emotion record creation data
   * @return the created emotion record
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole('USER')")
  public EmotionRecordDto createEmotionRecordByPatient(
      @Valid @RequestBody EmotionRecordByPatientDto dto) {
    log.info("POST-request: creating new emotion record for current patient");
    return emotionRecordService.createEmotionRecordByPatient(dto);
  }

  /**
   * Updates the emotion record with the specified ID.
   *
   * @param id  the ID of the emotion record to update
   * @param dto the DTO containing the emotion record updating data
   * @return the updated emotion record
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
  public EmotionRecordDto updateEmotionRecordById(
      @PathVariable("id") Long id, @Valid @RequestBody EmotionRecordUpdatingDto dto) {
    log.info("PUT-request: updating emotion record with id: {}", id);
    dto.setId(id);
    return emotionRecordService.updateEmotionRecordById(dto);
  }

  /**
   * Updates the current patient's emotion record.
   *
   * @param dto the DTO containing the emotion record updating data
   * @return the updated emotion record
   */
  @PutMapping
  @PreAuthorize("hasAnyRole('USER')")
  public EmotionRecordDto updateEmotionRecordByPatient(
      @Valid @RequestBody EmotionRecordByPatientDto dto) {
    log.info("PUT-request: updating emotion record of this period for current patient");
    return emotionRecordService.updateEmotionRecordByPatient(dto);
  }

  /**
   * Deletes the emotion record with the specified ID.
   *
   * @param id the ID of the emotion record to delete
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
  public void deleteEmotionRecordById(@PathVariable("id") Long id) {
    emotionRecordService.deleteEmotionRecordById(id);
  }
}
