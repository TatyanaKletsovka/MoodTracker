package com.syberry.mood.emotion.record.service.impl;

import static com.syberry.mood.authorization.util.SecurityUtils.getUserDetails;

import com.syberry.mood.emotion.record.converter.EmotionRecordConverter;
import com.syberry.mood.emotion.record.converter.PeriodConverter;
import com.syberry.mood.emotion.record.dto.EmotionRecordByPatientDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordCreationDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordFilter;
import com.syberry.mood.emotion.record.dto.EmotionRecordUpdatingDto;
import com.syberry.mood.emotion.record.dto.Period;
import com.syberry.mood.emotion.record.entity.EmotionRecord;
import com.syberry.mood.emotion.record.repository.EmotionRecordRepository;
import com.syberry.mood.emotion.record.service.CsvService;
import com.syberry.mood.emotion.record.service.EmotionRecordService;
import com.syberry.mood.emotion.record.service.specification.EmotionRecordSpecification;
import com.syberry.mood.emotion.record.validation.EmotionRecordValidator;
import com.syberry.mood.user.entity.User;
import com.syberry.mood.user.repository.UserRepository;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing emotion records.
 */
@Service
@RequiredArgsConstructor
public class EmotionRecordServiceImpl implements EmotionRecordService {

  private final CsvService csvService;
  private final EmotionRecordConverter recordConverter;
  private final EmotionRecordRepository recordRepository;
  private final UserRepository userRepository;
  private final EmotionRecordValidator validator;
  private final PeriodConverter periodConverter;
  private final EmotionRecordSpecification emotionRecordSpecification;

  /**
   * Finds an Emotion Record with the given ID.
   *
   * @param id the ID of the Emotion Record
   * @return the EmotionRecordDto object
   */
  @Override
  public EmotionRecordDto findEmotionRecordById(Long id) {
    return recordConverter.convertToDto(recordRepository.findByIdIfExists(id));
  }

  /**
   * Finds today's emotion records for the current patient.
   *
   * @return a Map object containing today's emotion records for the current patient
   */
  @Override
  public Map<String, EmotionRecordDto> findTodayEmotionRecordsForCurrentPatient() {
    Map<String, EmotionRecordDto> todayEmotions = new HashMap<>();
    for (Period period : Period.values()) {
      Optional<EmotionRecord> optional = recordRepository.findByPatientIdAndPeriodAndDate(
          getUserDetails().getId(), period, LocalDate.now());
      EmotionRecord emotionRecord = optional.orElse(null);
      EmotionRecordDto dto = emotionRecord != null
          ? recordConverter.convertToDto(emotionRecord)
          : null;
      todayEmotions.put(period.toString(), dto);
    }
    return todayEmotions;
  }

  /**
   * Creates a new Emotion Record based on the given EmotionRecordCreationDto.
   *
   * @param dto the EmotionRecordCreationDto object to create the Emotion Record
   * @return the EmotionRecordDto object of the created Emotion Record
   */
  @Override
  public EmotionRecordDto createEmotionRecord(EmotionRecordCreationDto dto) {
    validator.validateIsNoOtherRecordSameTime(dto.getPatientId(),
        periodConverter.convertToEnum(dto.getPeriod()), dto.getDate());
    User patient = userRepository.findPatientByIdIfExists(dto.getPatientId());
    EmotionRecord emotionRecord = recordConverter.convertToEntity(dto);
    emotionRecord.setPatient(patient);
    return recordConverter.convertToDto(recordRepository.save(emotionRecord));
  }

  /**
   * Creates a new Emotion Record based on the given EmotionRecordByPatientDto.
   *
   * @param dto the EmotionRecordByPatientDto object to create the Emotion Record
   * @return the EmotionRecordDto object of the created Emotion Record
   */
  @Override
  public EmotionRecordDto createEmotionRecordByPatient(EmotionRecordByPatientDto dto) {
    Long patientId = getUserDetails().getId();
    User patient = userRepository.findPatientByIdIfExists(patientId);
    Period period = Period.findOutCurrentPeriod();
    validator.validateIsNoOtherRecordSameTime(patientId, period, LocalDate.now());
    EmotionRecord emotionRecord = recordConverter.convertToEntity(dto, period);
    emotionRecord.setPatient(patient);
    return recordConverter.convertToDto(recordRepository.save(emotionRecord));
  }

  /**
   * Updates the Emotion Record with the given ID based on the given EmotionRecordUpdatingDto.
   *
   * @param dto the EmotionRecordUpdatingDto object to update the Emotion Record
   * @return the EmotionRecordDto object of the updated Emotion Record
   */
  @Override
  @Transactional
  public EmotionRecordDto updateEmotionRecordById(EmotionRecordUpdatingDto dto) {
    EmotionRecord emotionRecord = recordRepository.findByIdIfExists(dto.getId());
    emotionRecord = recordConverter.convertToEntity(dto, emotionRecord);
    return recordConverter.convertToDto(emotionRecord);
  }

  /**
   * Updates the Emotion Record for the current patient
   * based on the given EmotionRecordByPatientDto.
   *
   * @param dto the EmotionRecordByPatientDto object to update the Emotion Record
   * @return the EmotionRecordDto object of the updated Emotion Record
   */
  @Override
  @Transactional
  public EmotionRecordDto updateEmotionRecordByPatient(EmotionRecordByPatientDto dto) {
    EmotionRecord emotionRecord = recordRepository.findByPatientIdAndCurrentDate(
        getUserDetails().getId());
    validator.validateIsNotUpdated(emotionRecord);
    emotionRecord = recordConverter.convertToEntity(dto, emotionRecord);
    return recordConverter.convertToDto(emotionRecord);
  }

  /**
   * Deletes an emotion record by its ID.
   *
   * @param id the ID of the emotion record to be deleted
   */
  @Override
  public void deleteEmotionRecordById(Long id) {
    recordRepository.findByIdIfExists(id);
    recordRepository.deleteById(id);
  }

  /**
   * Generates csv file with patient's emotion records.
   *
   * @param patientId the ID of patient
   * @param filter filter with startDate and endDate parameters
   * @return byteArrayOutputStream with created csv file
   */
  @Override
  public ByteArrayOutputStream getCsvFile(Long patientId, EmotionRecordFilter filter) {
    Specification<EmotionRecord> specification = patientId != null
        ? emotionRecordSpecification.buildGetAllByPatientIdSpecification(patientId, filter)
        : emotionRecordSpecification.buildGetAllByDatesSpecification(filter);

    List<EmotionRecord> emotionRecords = recordRepository.findAll(specification);
    List<EmotionRecordDto> emotionRecordsDto = emotionRecords.stream()
        .map(recordConverter::convertToDto).toList();
    return csvService.createCsv(emotionRecordsDto, EmotionRecordDto.class);
  }
}
