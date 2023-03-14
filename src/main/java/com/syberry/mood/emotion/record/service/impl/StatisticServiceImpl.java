package com.syberry.mood.emotion.record.service.impl;

import com.syberry.mood.emotion.record.dto.Emotion;
import com.syberry.mood.emotion.record.dto.Period;
import com.syberry.mood.emotion.record.entity.EmotionRecord;
import com.syberry.mood.emotion.record.repository.EmotionRecordRepository;
import com.syberry.mood.emotion.record.service.StatisticService;
import com.syberry.mood.user.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * A utility class for statistical analysis of emotion records.
 */
@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

  private final EmotionRecordRepository recordRepository;

  /**
   * Finds the last recorded emotion for the specified patient within the specified time period.
   *
   * @param patientId the ID of the patient whose records to search.
   * @param startDateTime the start of the time period.
   * @param endDateTime the end of the time period.
   * @return the last recorded Emotion for the patient, or null if no such record exists.
   */
  public Emotion findLastEmotion(Long patientId, LocalDateTime startDateTime,
                                 LocalDateTime endDateTime) {
    Optional<EmotionRecord> lastEmotionOptional =
        recordRepository.findFirstByPatientIdAndCreatedAtBetweenOrderByCreatedAtDesc(patientId,
            startDateTime, endDateTime);
    return lastEmotionOptional.map(EmotionRecord::getEmotion).orElse(null);
  }

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
  public List<Emotion> findMostFrequentEmotions(Long patientId, LocalDateTime startDateTime,
                                                 LocalDateTime endDateTime) {
    Map<Emotion, Long> frequency = findFrequencyOfEmotions(patientId, startDateTime, endDateTime);
    if (frequency == null || frequency.isEmpty()) {
      return Collections.emptyList();
    }
    Long maxFrequency = Collections.max(frequency.values());
    return frequency.entrySet().stream()
        .filter(entry -> Objects.equals(entry.getValue(), maxFrequency))
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  /**
   * Counts the total number of records for a given patient within a specified time range.
   *
   * @param patientId the ID of the patient to count records for
   * @param startDateTime the start date and time of the time range
   * @param endDateTime the end date and time of the time range
   * @return the total number of records for the given patient within the specified time range
   */
  public int countTotalRecords(Long patientId, LocalDateTime startDateTime,
                                                LocalDateTime endDateTime) {
    return recordRepository.countByPatientIdAndCreatedAtBetween(patientId,
        startDateTime, endDateTime);
  }

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
  public Map<Emotion, Long> getFrequencyOfEmotions(Long patientId, LocalDateTime startDateTime,
                                                     LocalDateTime endDateTime) {
    Map<Emotion, Long> frequencyOfEmotions = findFrequencyOfEmotions(patientId,
        startDateTime, endDateTime);
    for (Emotion emotion : Emotion.values()) {
      frequencyOfEmotions.putIfAbsent(emotion, 0L);
    }
    return frequencyOfEmotions;
  }

  /**
   * Counts the number of missed records for a given patient within a specified time range.
   *
   * @param patient the user representing the patient to count missed records for
   * @param startDateTime the start date and time of the time range
   * @param endDateTime the end date and time of the time range
   * @return the number of missed records for the given patient within the specified time range
   */
  public int countMissedRecords(User patient, LocalDateTime startDateTime,
                               LocalDateTime endDateTime) {
    LocalDateTime createdAt = patient.getCreatedAt();
    LocalDateTime updatedAt = patient.getUpdatedAt();
    LocalDateTime start = startDateTime.isAfter(createdAt) ? startDateTime : createdAt;
    LocalDateTime end = !patient.isDisabled() || endDateTime.isBefore(updatedAt)
        ? endDateTime : updatedAt;
    end = end.isAfter(LocalDateTime.now()) ? LocalDateTime.now() : end;
    int totalRecords = countTotalRecords(patient.getId(), start, end);
    int totalRecordsRequired = countPeriodsBetween(start, end);
    return totalRecordsRequired - totalRecords;
  }

  /**
   * Counts the number of periods between two LocalDateTime objects.
   *
   * @param startDateTime the start LocalDateTime object
   * @param endDateTime the end LocalDateTime object
   * @return the number of periods between the start and end LocalDateTime objects
   */
  private int countPeriodsBetween(LocalDateTime startDateTime,
                                LocalDateTime endDateTime) {
    LocalDate startDate = startDateTime.toLocalDate();
    LocalDate endDate = endDateTime.toLocalDate();
    int periodCount = 0;
    Period startPeriod = Period.findOutPeriodByTime(startDateTime.toLocalTime());
    Period endPeriod = Period.findOutPeriodByTime(endDateTime.toLocalTime());

    if (!startDate.isEqual(endDate)) {
      long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) - 1;
      periodCount += daysBetween * 3;
      periodCount += startPeriod.getPeriodsAfter();
      periodCount += endPeriod.countPeriodsBefore();
    } else {
      periodCount += startPeriod.countDayPeriodsBetween(endPeriod);
    }
    return periodCount;
  }

  /**
   * Finds the frequency of each emotion recorded by a patient
   * between a given start date and end date.
   *
   * @param patientId the ID of the patient to search for
   * @param startDateTime the start date and time of the search range
   * @param endDateTime the end date and time of the search range
   * @return a Map of each emotion and its frequency
   */
  private Map<Emotion, Long> findFrequencyOfEmotions(Long patientId, LocalDateTime startDateTime,
                                                     LocalDateTime endDateTime) {
    List<Object[]> results = recordRepository.countRecordsByEmotion(patientId,
        startDateTime, endDateTime);
    return results.stream()
        .collect(Collectors.toMap(
            obj -> (Emotion) obj[0],
            obj -> (Long) obj[1]
        ));
  }
}
