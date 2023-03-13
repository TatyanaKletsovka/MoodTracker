package com.syberry.mood.emotion.record.converter;

import com.syberry.mood.emotion.record.dto.EmotionRecordByPatientDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordCreationDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordUpdatingDto;
import com.syberry.mood.emotion.record.dto.Period;
import com.syberry.mood.emotion.record.entity.EmotionRecord;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * A component that provides converting methods for the EmotionRecord entity.
 */
@Component
@RequiredArgsConstructor
public class EmotionRecordConverter {

  private final EmotionConverter emotionConverter;
  private final PeriodConverter periodConverter;

  /**
   * Converts an EmotionRecordCreationDto to an EmotionRecord entity.
   *
   * @param dto the EmotionRecordCreationDto to convert
   * @return the converted EmotionRecord entity
   */
  public EmotionRecord convertToEntity(EmotionRecordCreationDto dto) {
    Period period = periodConverter.convertToEnum(dto.getPeriod());
    return EmotionRecord.builder()
        .emotion(emotionConverter.convertToEnum(dto.getEmotion()))
        .intensity(dto.getIntensity())
        .note(dto.getNote())
        .createdAt(dto.getDate().atTime(period.getPeriodStartTime()))
        .period(period)
        .build();
  }

  /**
   * Converts an EmotionRecordUpdatingDto to an EmotionRecord entity.
   *
   * @param dto           the EmotionRecordUpdatingDto to convert
   * @param emotionRecord the EmotionRecord to update
   * @return the updated EmotionRecord entity
   */
  public EmotionRecord convertToEntity(EmotionRecordUpdatingDto dto, EmotionRecord emotionRecord) {
    emotionRecord.setEmotion(emotionConverter.convertToEnum(dto.getEmotion()));
    emotionRecord.setIntensity(dto.getIntensity());
    emotionRecord.setNote(dto.getNote());
    emotionRecord.setUpdatedAt(LocalDateTime.now());
    return emotionRecord;
  }

  /**
   * Converts an EmotionRecordByPatientDto to an EmotionRecord entity.
   *
   * @param dto    the EmotionRecordByPatientDto to convert
   * @param period the Period to set in the entity
   * @return the converted EmotionRecord entity
   */
  public EmotionRecord convertToEntity(EmotionRecordByPatientDto dto, Period period) {
    return EmotionRecord.builder()
        .emotion(emotionConverter.convertToEnum(dto.getEmotion()))
        .intensity(dto.getIntensity())
        .createdAt(LocalDateTime.now())
        .period(period)
        .build();
  }

  /**
   * Converts an EmotionRecordByPatientDto to an EmotionRecord entity.
   *
   * @param dto           the EmotionRecordByPatientDto to convert
   * @param emotionRecord the EmotionRecord to update
   * @return the updated EmotionRecord entity
   */
  public EmotionRecord convertToEntity(EmotionRecordByPatientDto dto,
                                       EmotionRecord emotionRecord) {
    emotionRecord.setEmotion(emotionConverter.convertToEnum(dto.getEmotion()));
    emotionRecord.setIntensity(dto.getIntensity());
    emotionRecord.setUpdatedAt(LocalDateTime.now());
    return emotionRecord;
  }

  /**
   * Converts an EmotionRecord entity to an EmotionRecordDto data transfer object.
   *
   * @param emotionRecord The EmotionRecord entity to convert
   * @return The converted EmotionRecordDto object
   */
  public EmotionRecordDto convertToDto(EmotionRecord emotionRecord) {
    return EmotionRecordDto.builder()
        .id(emotionRecord.getId())
        .emotion(emotionRecord.getEmotion())
        .intensity(emotionRecord.getIntensity())
        .period(emotionRecord.getPeriod())
        .createdAt(emotionRecord.getCreatedAt())
        .updatedAt(emotionRecord.getUpdatedAt())
        .note(emotionRecord.getNote())
        .patientId(emotionRecord.getPatient().getId())
        .superheroName(emotionRecord.getPatient().getUsername())
        .build();
  }
}
