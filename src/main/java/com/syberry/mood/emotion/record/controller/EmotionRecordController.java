package com.syberry.mood.emotion.record.controller;

import com.syberry.mood.emotion.record.dto.EmotionRecordByPatientDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordCreationDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordFilter;
import com.syberry.mood.emotion.record.dto.EmotionRecordUpdatingDto;
import com.syberry.mood.emotion.record.dto.EmotionsStatisticDto;
import com.syberry.mood.emotion.record.service.EmotionRecordService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

  private static final String ATTACHMENT = "attachment;filename=emotion-records.csv";

  private final EmotionRecordService emotionRecordService;

  /**
   * Retrieves all emotion records grouped by date, filtered by the given dates from filter.
   *
   * @param filter the filter to use for retrieving the emotion records
   * @return a map containing the emotion records grouped by date
   */
  @GetMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
  public Map<String, Map<String, Map<String, EmotionRecordDto>>> findAllEmotionRecords(
      EmotionRecordFilter filter) {
    log.info("GET-request: getting all emotion records with id");
    return emotionRecordService.findAllEmotionRecordsGroupByDate(filter);
  }

  /**
   * Retrieves all emotion records for a specific patient, filtered by the given dates from filter.
   *
   * @param id the ID of the patient for whom to retrieve emotion records
   * @param filter the filter to use for retrieving the emotion records
   * @return a map containing the emotion records grouped by date
   */
  @GetMapping("/patients/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
  public Map<String, Map<String, Map<String, EmotionRecordDto>>> findEmotionRecordsByPatient(
      @PathVariable("id") Long id, EmotionRecordFilter filter) {
    log.info("GET-request: getting all emotion records for patient with id: {}", id);
    return emotionRecordService.findEmotionRecordsByPatient(id, filter);
  }

  /**
   * Retrieves emotion statistics for a specific patient, filtered by the given dates from filter.
   *
   * @param id the ID of the patient for whom to retrieve emotion statistics
   * @param filter the filter to use for retrieving the emotion statistics
   * @return a DTO containing various statistics about the patient's emotions
   */
  @GetMapping("/patients/{id}/statistic")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
  public EmotionsStatisticDto getStatistic(
      @PathVariable("id") Long id, EmotionRecordFilter filter) {
    log.info("GET-request: getting statistic for patient with id: {}", id);
    return emotionRecordService.getStatistic(id, filter);
  }

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

  /**
   * Generates csv file with emotion records.
   *
   * @param filter filter with startDate and endDate parameters
   * @param patientId the ID of the patient
   * @return response entity with byte array
   */
  @GetMapping(value = {"/csv-file", "/csv-file/patients/{id}", "/csv-file/patients"})
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
  public ResponseEntity<?> getCsvFile(EmotionRecordFilter filter,
      @PathVariable(value = "id", required = false) Long patientId) {
    log.info("GET-request: creating csv file");
    MediaType mediaType = MediaType.parseMediaType("text/csv");
    ByteArrayOutputStream csvFile = emotionRecordService.getCsvFile(patientId, filter);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT)
        .contentType(mediaType)
        .body(csvFile.toByteArray());
  }

  /**
   * Generates pdf file with emotion records.
   *
   * @param filter filter with startDate and endDate parameters
   * @return response entity with attachment
   */
  @GetMapping(value = "/pdf-file")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
  public ResponseEntity<InputStreamResource> getEmotionRecordsDataInPdf(
      EmotionRecordFilter filter) {
    log.info("GET-request: creating pdf file with emotion records");
    String contentDispositionValue = "attachment; filename=emotion_records_"
        + LocalDate.now() + ".pdf";
    ByteArrayInputStream bis = emotionRecordService.getEmotionRecordsDataInPdf(filter);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionValue)
        .contentType(MediaType.APPLICATION_PDF)
        .body(new InputStreamResource(bis));
  }

  /**
   * Generates pdf file with patient emotion records.
   *
   * @param filter    filter with startDate and endDate parameters
   * @param patientId the ID of the patient
   * @return response entity with attachment
   */
  @GetMapping(value = "/pdf-file/patients/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
  public ResponseEntity<InputStreamResource> getPatientEmotionRecordsDataInPdf(
      EmotionRecordFilter filter, @PathVariable(value = "id") Long patientId) {
    log.info("GET-request: creating pdf file with patient emotion records");
    String contentDispositionValue = "attachment; filename=patient_emotion_records_"
        + LocalDate.now() + ".pdf";
    ByteArrayInputStream bis = emotionRecordService
        .getPatientEmotionRecordsDataInPdf(filter, patientId);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionValue)
        .contentType(MediaType.APPLICATION_PDF)
        .body(new InputStreamResource(bis));
  }
}
