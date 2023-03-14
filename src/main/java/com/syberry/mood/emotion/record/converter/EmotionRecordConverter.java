package com.syberry.mood.emotion.record.converter;

import com.syberry.mood.emotion.record.dto.EmotionRecordByPatientDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordCreationDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordFilter;
import com.syberry.mood.emotion.record.dto.EmotionRecordUpdatingDto;
import com.syberry.mood.emotion.record.dto.Period;
import com.syberry.mood.emotion.record.entity.EmotionRecord;
import com.syberry.mood.emotion.record.util.DateUtil;
import com.syberry.mood.user.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  /**
   * Convert a list of EmotionRecordDto into a map structure.
   *
   * @param records the list of EmotionRecordDto to be converted
   * @param filter the EmotionRecordFilter to be used for filtering the data
   * @param patients the list of User objects to be used
   *     for generating the required structure of the map
   * @return a map structure which contains data grouped by date, superhero name and period
   */
  public Map<String, Map<String, Map<String, EmotionRecordDto>>> convertToMap(
      List<EmotionRecordDto> records, EmotionRecordFilter filter, List<User> patients) {
    Map<String, Map<String, Map<String, EmotionRecordDto>>> data
        = generateMap(filter, patients);
    for (EmotionRecordDto record : records) {
      data.computeIfAbsent(record.getCreatedAt().toLocalDate().toString(), k1 -> new HashMap<>())
          .computeIfAbsent(record.getSuperheroName(), k2 -> new HashMap<>())
          .put(record.getPeriod().toString(), record);
    }
    return data;
  }

  /**
   * Generate a required structure of the map using EmotionRecordFilter and list of User objects.
   *
   * @param filter the EmotionRecordFilter to be used for filtering the data
   * @param patients the list of User objects to be used
   *     for generating the required structure of the map
   * @return a map structure which contains required data structure
   *     grouped by date, superhero name and period
   */
  private Map<String, Map<String, Map<String, EmotionRecordDto>>> generateMap(
      EmotionRecordFilter filter, List<User> patients) {
    Map<String, Map<String, Map<String, EmotionRecordDto>>> data = new HashMap<>();
    for (LocalDate date : getDatesDesc(filter)) {
      Map<String, Map<String, EmotionRecordDto>> dateMap = new HashMap<>();
      for (User patient : patients) {
        if (mayBeRecordOnDate(patient, date)) {
          Map<String, EmotionRecordDto> emotionMap = new HashMap<>();
          emotionMap.put("MORNING", null);
          emotionMap.put("AFTERNOON", null);
          emotionMap.put("EVENING", null);
          dateMap.put(patient.getUsername(), emotionMap);
        }
      }
      if (!dateMap.isEmpty()) {
        data.put(date.toString(), dateMap);
      }
    }
    return data;
  }

  /**
   * Get a list of LocalDate objects based on dates from EmotionRecordFilter.
   *
   * @param filter the EmotionRecordFilter to be used for generating the list of dates
   * @return a list of LocalDate objects
   */
  private List<LocalDate> getDatesDesc(EmotionRecordFilter filter) {
    List<LocalDate> dates = new ArrayList<>();
    LocalDate date = filter.getEndDate().isBefore(LocalDate.now())
        ? filter.getEndDate() : LocalDate.now();
    while (!date.isBefore(filter.getStartDate())) {
      dates.add(date);
      date = date.minusDays(1);
    }
    return dates;
  }

  /**
   * Checks whether a user may have a record on a specific date.
   *
   * @param patient the user to check for
   * @param date the date to check
   * @return true if the user may have a record on the given date, false otherwise
   */
  private boolean mayBeRecordOnDate(User patient, LocalDate date) {
    return !patient.isDisabled()
        || patient.getUpdatedAt().isAfter(DateUtil.convertToDateTimeEndDay(date));
  }
}
