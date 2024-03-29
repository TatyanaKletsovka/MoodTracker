package com.syberry.mood.emotion.record.validation;

import com.syberry.mood.emotion.record.dto.Period;
import com.syberry.mood.emotion.record.entity.EmotionRecord;
import com.syberry.mood.emotion.record.repository.EmotionRecordRepository;
import com.syberry.mood.exception.ValidationException;
import com.syberry.mood.user.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * A class that provides validation methods for the emotion record service.
 */
@Service
@RequiredArgsConstructor
public class EmotionRecordValidator {

  private final EmotionRecordRepository recordRepository;

  /**
   * Validates that there is no other emotion record with the same patient ID, period, and date.
   *
   * @param patientId the ID of the patient to check for an existing record
   * @param period    the period to check for an existing record
   * @param date      the date to check for an existing record
   * @throws ValidationException if there is already an emotion record
   *                             for the specified patient, period, and date
   */
  public void validateIsNoOtherRecordSameTime(Long patientId, Period period, LocalDate date) {
    Optional<EmotionRecord> optional = recordRepository.findByPatientIdAndPeriodAndDate(
        patientId, period, date);
    if (optional.isPresent()) {
      throw new ValidationException(
          String.format("There is already an emotion record for: %s %s",
              date, period.toString().toLowerCase()));
    }
  }

  /**
   * Validates that the given emotion record has not already been updated.
   *
   * @param emotionRecord the emotion record to check if it has been updated
   * @throws ValidationException if the emotion record has already been updated
   */
  public void validateIsNotUpdated(EmotionRecord emotionRecord) {
    if (emotionRecord.getUpdatedAt() != null) {
      throw new ValidationException("Current emotion record is already updated. "
          + "It can't be updated more than one time");
    }
  }

  /**
   * Validates that the given date and period does not fall
   * after the disabled date of the patient.
   *
   * @param patient The user for which the date and period are being validated
   * @param date The date for which the validation is being done
   * @param period The period for which the validation is being done
   * @throws ValidationException if the patient is disabled
   *     and the given date and period fall after the patient's disabled date.
   */
  public void validateDateNotAfterDisable(User patient, LocalDate date, Period period) {
    LocalDateTime dateTime = date.atTime(period.getPeriodStartTime());
    if (patient.isDisabled() && dateTime.isAfter(patient.getUpdatedAt())) {
      throw new ValidationException(
          "Emotion record can't be created after patient's disable date");
    }
  }
}
