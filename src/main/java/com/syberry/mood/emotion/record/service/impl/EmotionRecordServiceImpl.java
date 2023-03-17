package com.syberry.mood.emotion.record.service.impl;

import static com.syberry.mood.authorization.util.SecurityUtils.getUserDetails;

import com.syberry.mood.emotion.record.converter.EmotionRecordConverter;
import com.syberry.mood.emotion.record.converter.PeriodConverter;
import com.syberry.mood.emotion.record.dto.Emotion;
import com.syberry.mood.emotion.record.dto.EmotionRecordByPatientDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordCreationDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordFilter;
import com.syberry.mood.emotion.record.dto.EmotionRecordUpdatingDto;
import com.syberry.mood.emotion.record.dto.EmotionsStatisticDto;
import com.syberry.mood.emotion.record.dto.Period;
import com.syberry.mood.emotion.record.entity.EmotionRecord;
import com.syberry.mood.emotion.record.repository.EmotionRecordRepository;
import com.syberry.mood.emotion.record.service.CsvService;
import com.syberry.mood.emotion.record.service.EmotionRecordService;
import com.syberry.mood.emotion.record.service.PdfService;
import com.syberry.mood.emotion.record.service.StatisticService;
import com.syberry.mood.emotion.record.specification.EmotionRecordSpecification;
import com.syberry.mood.emotion.record.util.DateUtil;
import com.syberry.mood.emotion.record.validation.EmotionRecordValidator;
import com.syberry.mood.user.entity.User;
import com.syberry.mood.user.repository.UserRepository;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
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
  private final PdfService pdfService;
  private final EmotionRecordSpecification specification;
  private final StatisticService statisticService;

  /**
   * Finds all emotion records filtered by the given filter.
   *
   * @param filter the filter to apply to the search
   * @return a map of emotion records grouped by date, patient, period
   */
  public Map<String, Map<String, Map<String, EmotionRecordDto>>> findAllEmotionRecordsGroupByDate(
      EmotionRecordFilter filter) {
    List<User> patients = userRepository.findAllPatientsSortIdDesc();
    List<EmotionRecord> emotionRecords = recordRepository
        .findAll(specification.buildGetAllByDatesSpecification(filter));
    List<EmotionRecordDto> recordDtos = emotionRecords.stream()
        .map(recordConverter::convertToDto).toList();
    return recordConverter.convertToMap(recordDtos, filter, patients);
  }

  /**
   * Finds emotion records for a specified patient, filtered by the given filter.
   *
   * @param id the ID of the patient to search for
   * @param filter the filter to apply to the search
   * @return a map of emotion records for the specified patient grouped by date, patient, period
   */
  public Map<String, Map<String, Map<String, EmotionRecordDto>>> findEmotionRecordsByPatient(
      Long id, EmotionRecordFilter filter) {
    User patient = userRepository.findPatientByIdIfExists(id);
    List<EmotionRecord> emotionRecords = recordRepository
        .findAll(specification.buildGetAllByPatientIdSpecification(id, filter));
    List<EmotionRecordDto> recordDtos = emotionRecords.stream()
        .map(recordConverter::convertToDto).toList();
    return recordConverter.convertToMap(
        recordDtos, filter, new ArrayList<>(Collections.singletonList(patient)));
  }

  /**
   * Retrieves emotion statistics for a specified patient based on the given filter.
   *
   * @param id the ID of the patient to retrieve statistics for
   * @param filter the filter to apply to the statistics search
   * @return an EmotionsStatisticDto object containing the retrieved statistics
   */
  public EmotionsStatisticDto getStatistic(Long id, EmotionRecordFilter filter) {
    User patient = userRepository.findPatientByIdIfExists(id);
    LocalDateTime startDateTime = filter.getStartDate().atStartOfDay();
    LocalDateTime endDateTime = DateUtil.convertToDateTimeEndDay(filter.getEndDate());
    Emotion lastEmotion = statisticService.findLastEmotion(id, startDateTime, endDateTime);
    List<Emotion> mostFrequentEmotions = statisticService.findMostFrequentEmotions(patient.getId(),
        startDateTime, endDateTime);
    int totalRecords = statisticService.countTotalRecords(id, startDateTime, endDateTime);
    int missedRecords = statisticService.countMissedRecords(patient, startDateTime, endDateTime);
    Map<Emotion, Long> frequencyOfEmotions = statisticService.getFrequencyOfEmotions(
        patient.getId(), startDateTime, endDateTime);
    return EmotionsStatisticDto.builder()
        .patientId(id)
        .lastEmotion(lastEmotion)
        .mostOftenEmotions(mostFrequentEmotions)
        .totalEmotionRecords(totalRecords)
        .missedRecords(missedRecords)
        .frequencyOfEmotions(frequencyOfEmotions)
        .build();
  }

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
    Period period = periodConverter.convertToEnum(dto.getPeriod());
    validator.validateIsNoOtherRecordSameTime(dto.getPatientId(), period, dto.getDate());
    User patient = userRepository.findPatientByIdIfExists(dto.getPatientId());
    validator.validateDateNotAfterDisable(patient, dto.getDate(), period);
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
    Period period = Period.findOutPeriodByTime(LocalTime.now());
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
    Specification<EmotionRecord> recordSpecification = patientId != null
        ? specification.buildGetAllByPatientIdSpecification(patientId, filter)
        : specification.buildGetAllByDatesSpecification(filter);

    List<EmotionRecord> emotionRecords = recordRepository.findAll(recordSpecification);
    List<EmotionRecordDto> emotionRecordsDto = emotionRecords.stream()
        .map(recordConverter::convertToDto).toList();
    return csvService.createCsv(emotionRecordsDto, EmotionRecordDto.class);
  }

  @Override
  public ByteArrayInputStream getEmotionRecordsDataInPdf(EmotionRecordFilter filter) {
    Map<String, Map<String, Map<String, EmotionRecordDto>>> records =
        findAllEmotionRecordsGroupByDate(filter);
    return pdfService.createPdfWithEmotionRecords(filter, records);
  }

  @Override
  public ByteArrayInputStream getPatientEmotionRecordsDataInPdf(
      EmotionRecordFilter filter, Long patientId) {
    Map<String, Map<String, Map<String, EmotionRecordDto>>> records =
        findEmotionRecordsByPatient(patientId, filter);
    EmotionsStatisticDto statisticsDto = getStatistic(patientId, filter);
    return pdfService.createPdfWithPatientEmotionRecords(filter, records, statisticsDto);
  }
}
