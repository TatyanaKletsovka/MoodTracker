package com.syberry.mood.emotion.record.service;

import com.syberry.mood.emotion.record.dto.EmotionRecordByPatientDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordCreationDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordFilter;
import com.syberry.mood.emotion.record.dto.EmotionRecordUpdatingDto;
import com.syberry.mood.emotion.record.dto.EmotionsStatisticDto;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Service interface for managing emotion records.
 */
public interface EmotionRecordService {

  /**
   * Finds all emotion records filtered by the given filter.
   *
   * @param filter the filter to apply to the search
   * @return a map of emotion records grouped by date, patient, period
   */
  Map<String, Map<String, Map<String, EmotionRecordDto>>> findAllEmotionRecordsGroupByDate(
      EmotionRecordFilter filter);

  /**
   * Finds emotion records for a specified patient, filtered by the given filter.
   *
   * @param id the ID of the patient to search for
   * @param filter the filter to apply to the search
   * @return a map of emotion records for the specified patient grouped by date, patient, period
   */
  Map<String, Map<String, Map<String, EmotionRecordDto>>> findEmotionRecordsByPatient(
      Long id, EmotionRecordFilter filter);

  /**
   * Retrieves emotion statistics for a specified patient based on the given filter.
   *
   * @param id the ID of the patient to retrieve statistics for
   * @param filter the filter to apply to the statistics search
   * @return an EmotionsStatisticDto object containing the retrieved statistics
   */
  EmotionsStatisticDto getStatistic(Long id, EmotionRecordFilter filter);

  /**
   * Finds an Emotion Record with the given ID.
   *
   * @param id the ID of the Emotion Record
   * @return the EmotionRecordDto object
   */
  EmotionRecordDto findEmotionRecordById(Long id);

  /**
   * Creates a new Emotion Record based on the given EmotionRecordCreationDto.
   *
   * @param dto the EmotionRecordCreationDto object to create the Emotion Record
   * @return the EmotionRecordDto object of the created Emotion Record
   */
  EmotionRecordDto createEmotionRecord(EmotionRecordCreationDto dto);

  /**
   * Finds today's emotion records for the current patient.
   *
   * @return a Map object containing today's emotion records for the current patient
   */
  Map<String, EmotionRecordDto> findTodayEmotionRecordsForCurrentPatient();

  /**
   * Creates a new Emotion Record based on the given EmotionRecordByPatientDto.
   *
   * @param dto the EmotionRecordByPatientDto object to create the Emotion Record
   * @return the EmotionRecordDto object of the created Emotion Record
   */
  EmotionRecordDto createEmotionRecordByPatient(EmotionRecordByPatientDto dto);

  /**
   * Updates the Emotion Record with the given ID based on the given EmotionRecordUpdatingDto.
   *
   * @param dto the EmotionRecordUpdatingDto object to update the Emotion Record
   * @return the EmotionRecordDto object of the updated Emotion Record
   */
  EmotionRecordDto updateEmotionRecordById(EmotionRecordUpdatingDto dto);

  /**
   * Updates the Emotion Record for the current patient
   * based on the given EmotionRecordByPatientDto.
   *
   * @param dto the EmotionRecordByPatientDto object to update the Emotion Record
   * @return the EmotionRecordDto object of the updated Emotion Record
   */
  EmotionRecordDto updateEmotionRecordByPatient(EmotionRecordByPatientDto dto);

  /**
   * Deletes an emotion record by its ID.
   *
   * @param id the ID of the emotion record to be deleted
   */
  void deleteEmotionRecordById(Long id);

  /**
   * Generates csv file with patient's emotion records.
   *
   * @param patientId the ID of patient
   * @param filter filter with startDate and endDate parameters
   * @return byteArrayOutputStream with created csv file
   */
  ByteArrayOutputStream getCsvFile(Long patientId, EmotionRecordFilter filter);

  /**
   * Generates pdf file with emotion records.
   *
   * @param filter filter with startDate and endDate parameters
   * @return ByteArrayInputStream with a created file
   */
  ByteArrayInputStream getEmotionRecordsDataInPdf(EmotionRecordFilter filter);

  /**
   * Generates pdf file with patient emotion records.
   *
   * @param filter filter with startDate and endDate parameters
   * @param patientId the ID of patient
   * @return ByteArrayInputStream with a created file
   */
  ByteArrayInputStream getPatientEmotionRecordsDataInPdf(
      EmotionRecordFilter filter, Long patientId);
}
