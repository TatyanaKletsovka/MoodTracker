package com.syberry.mood.emotion.record.repository;

import com.syberry.mood.emotion.record.dto.Period;
import com.syberry.mood.emotion.record.entity.EmotionRecord;
import com.syberry.mood.exception.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository interface for managing emotion record entities.
 */
public interface EmotionRecordRepository extends JpaRepository<EmotionRecord, Long>,
    JpaSpecificationExecutor<EmotionRecord> {

  /**
   * Finds an EmotionRecord entity with the specified patient id,
   * created at timestamp between start and end, and period.
   *
   * @param id The id of the patient
   * @param createdAtStart The start timestamp of the created at time
   * @param createdAtEnd The end timestamp of the created at time
   * @param period The period of EmotionRecord
   * @return The EmotionRecord entity wrapped in an Optional if found,
   *     or an empty Optional if not found
   */
  Optional<EmotionRecord> findByPatientIdAndCreatedAtBetweenAndPeriod(
      Long id, LocalDateTime createdAtStart, LocalDateTime createdAtEnd, Period period);

  /**
   * Finds the most recent emotion record for a given patient within a specified time range.
   *
   * @param id the ID of the patient to search for
   * @param createdAtStart the start of the time range to search for emotion records
   * @param createdAtEnd the end of the time range to search for emotion records
   * @return an Optional containing the most recent emotion record for the given patient
   *     within the specified time range, or an empty Optional if no such record is found
   */
  Optional<EmotionRecord> findFirstByPatientIdAndCreatedAtBetweenOrderByCreatedAtDesc(
      Long id, LocalDateTime createdAtStart, LocalDateTime createdAtEnd);

  /**
   * Counts the number of emotion records for a given user
   * within a specified time range, grouped by emotion type.
   *
   * @param userId the ID of the user to search for
   * @param startDate the start of the time range to search for emotion records
   * @param endDate the end of the time range to search for emotion records
   * @return a list of Object arrays, where each array contains an emotion type
   *     and the count of records for that type
   */
  @Query("SELECT er.emotion, COUNT(er) FROM EmotionRecord er "
      + "WHERE er.patient.id = :userId "
      + "AND er.createdAt >= :startDate "
      + "AND er.createdAt <= :endDate "
      + "GROUP BY er.emotion")
  List<Object[]> countRecordsByEmotion(Long userId, LocalDateTime startDate, LocalDateTime endDate);


  /**
   * Counts the number of emotion records for a given patient within a specified time range.
   *
   * @param id the ID of the patient to search for
   * @param startDate the start of the time range to search for emotion records
   * @param endDate the end of the time range to search for emotion records
   * @return the number of emotion records for the given patient within the specified time range
   */
  int countByPatientIdAndCreatedAtBetween(Long id, LocalDateTime startDate, LocalDateTime endDate);

  /**
   * Finds an EmotionRecord entity with the specified id,
   * or throws an EntityNotFoundException if not found.
   *
   * @param id The id of the EmotionRecord entity to retrieve
   * @return The EmotionRecord entity if found
   * @throws EntityNotFoundException If the EmotionRecord entity is not found
   */
  default EmotionRecord findByIdIfExists(Long id) {
    return findById(id).orElseThrow(
        () -> new EntityNotFoundException(
            String.format("EmotionRecord with id: %s is not found", id)));
  }

  /**
   * Finds an EmotionRecord entity with the specified patient id, period, and date.
   *
   * @param id     The id of the patient
   * @param period The period of EmotionRecord
   * @param date   The date of EmotionRecord
   * @return The EmotionRecord entity wrapped in an Optional if found,
   *     or an empty Optional if not found
   */
  default Optional<EmotionRecord> findByPatientIdAndPeriodAndDate(
      Long id, Period period, LocalDate date) {
    LocalDateTime start = date.atTime(period.getPeriodStartTime());
    LocalDateTime end = date.atTime(period.getPeriodEndTime().minusNanos(1));
    return findByPatientIdAndCreatedAtBetweenAndPeriod(id, start, end, period);
  }

  /**
   * Finds an EmotionRecord for a patient with the specified ID and the current date.
   *
   * @param id the ID of the patient to find the EmotionRecord for
   * @return the EmotionRecord for the specified patient and the current date, if it exists
   * @throws EntityNotFoundException if no EmotionRecord is found
   *     for the specified patient and current date
   */
  default EmotionRecord findByPatientIdAndCurrentDate(Long id) {
    return findByPatientIdAndPeriodAndDate(
        id, Period.findOutPeriodByTime(LocalTime.now()), LocalDate.now()).orElseThrow(
            () -> new EntityNotFoundException(String.format(
        "EmotionRecord for patient with id: %s and current date is not found", id)));
  }
}
