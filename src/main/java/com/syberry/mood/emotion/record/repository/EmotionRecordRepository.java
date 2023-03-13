package com.syberry.mood.emotion.record.repository;

import com.syberry.mood.emotion.record.dto.Period;
import com.syberry.mood.emotion.record.entity.EmotionRecord;
import com.syberry.mood.exception.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository interface for managing emotion record entities.
 */
public interface EmotionRecordRepository extends JpaRepository<EmotionRecord, Long>,
    JpaSpecificationExecutor<EmotionRecord> {

  /**
   * Finds an EmotionRecord entity with the specified patient id,
   * created at timestamp between start and end, and period.
   *
   * @param id             The id of the patient
   * @param createdAtStart The start timestamp of the created at time
   * @param createdAtEnd   The end timestamp of the created at time
   * @param period         The period of EmotionRecord
   * @return The EmotionRecord entity wrapped in an Optional if found,
   *     or an empty Optional if not found
   */
  Optional<EmotionRecord> findByPatientIdAndCreatedAtBetweenAndPeriod(
      Long id, LocalDateTime createdAtStart, LocalDateTime createdAtEnd, Period period);

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
        id, Period.findOutCurrentPeriod(), LocalDate.now()).orElseThrow(
            () -> new EntityNotFoundException(String.format(
        "EmotionRecord for patient with id: %s and current date is not found", id)));
  }
}
