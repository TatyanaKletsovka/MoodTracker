package com.syberry.mood.emotion.record.service;

import com.syberry.mood.emotion.record.dto.Emotion;
import com.syberry.mood.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * A service class for statistical analysis of emotion records.
 */
public interface StatisticService {
  /**
   * Finds the last recorded emotion for the specified patient within the specified time period.
   *
   * @param patientId the ID of the patient whose records to search.
   * @param startDateTime the start of the time period.
   * @param endDateTime the end of the time period.
   * @return the last recorded Emotion for the patient, or null if no such record exists.
   */
  Emotion findLastEmotion(Long patientId, LocalDateTime startDateTime, LocalDateTime endDateTime);

  /**
   * Returns a list of the most frequent emotions
   * recorded by the patient within the specified time range.
   *
   * @param patientId the ID of the patient whose emotions to search for
   * @param startDateTime the start of the time range to search for emotions
   * @param endDateTime the end of the time range to search for emotions
   * @return a list of the most frequent emotions
   *     recorded by the patient within the specified time range
   */
  List<Emotion> findMostFrequentEmotions(Long patientId, LocalDateTime startDateTime,
                                                LocalDateTime endDateTime);

  /**
   * Counts the total number of records for a given patient within a specified time range.
   *
   * @param patientId the ID of the patient to count records for
   * @param startDateTime the start date and time of the time range
   * @param endDateTime the end date and time of the time range
   * @return the total number of records for the given patient within the specified time range
   */
  int countTotalRecords(Long patientId, LocalDateTime startDateTime, LocalDateTime endDateTime);

  /**
   * Retrieves a map of the frequency of emotions
   * for a given patient within a specified time range.
   *
   * @param patientId the ID of the patient to retrieve emotion frequency for
   * @param startDateTime the start date and time of the time range
   * @param endDateTime the end date and time of the time range
   * @return a map of the frequency of emotions for the given patient
   *     within the specified time range
   */
  Map<Emotion, Long> getFrequencyOfEmotions(Long patientId, LocalDateTime startDateTime,
                                                   LocalDateTime endDateTime);

  /**
   * Counts the number of missed records for a given patient within a specified time range.
   *
   * @param patient the user representing the patient to count missed records for
   * @param startDateTime the start date and time of the time range
   * @param endDateTime the end date and time of the time range
   * @return the number of missed records for the given patient within the specified time range
   */
  int countMissedRecords(User patient, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
