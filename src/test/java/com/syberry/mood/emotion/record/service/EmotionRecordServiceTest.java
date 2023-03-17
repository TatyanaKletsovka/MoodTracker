package com.syberry.mood.emotion.record.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.syberry.mood.authorization.security.UserDetailsImpl;
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
import com.syberry.mood.emotion.record.service.impl.EmotionRecordServiceImpl;
import com.syberry.mood.emotion.record.service.impl.PdfServiceImpl;
import com.syberry.mood.emotion.record.specification.EmotionRecordSpecification;
import com.syberry.mood.emotion.record.validation.EmotionRecordValidator;
import com.syberry.mood.exception.CsvFileException;
import com.syberry.mood.exception.EntityNotFoundException;
import com.syberry.mood.exception.ValidationException;
import com.syberry.mood.user.dto.RoleName;
import com.syberry.mood.user.entity.Role;
import com.syberry.mood.user.entity.User;
import com.syberry.mood.user.repository.UserRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EmotionRecordServiceTest {

  @InjectMocks
  private EmotionRecordServiceImpl recordService;
  @Mock
  private EmotionRecordConverter recordConverter;
  @Mock
  private EmotionRecordRepository recordRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private PeriodConverter periodConverter;
  @Mock
  private EmotionRecordValidator recordValidator;
  @Mock
  private SecurityContext securityContext;
  @Mock
  private Authentication authentication;
  @Mock
  private EmotionRecordSpecification specification;
  @Mock
  private StatisticService statisticService;
  @Mock
  private PdfServiceImpl pdfService;
  @Mock
  private CsvService csvService;

  private final Long id = 1L;
  private final String username = "Super Man";
  private final String password = "cat_dog_frog";
  private final User patient = new User(1L, username, password, new Role(),
      LocalDateTime.now(), null, false);
  private final Emotion emotion = Emotion.HAPPY;
  private final String emotionStr = "happy";
  private final int intensity = 5;
  private final String note = "Note";
  private final LocalDateTime createdAt = LocalDateTime.now();
  private final LocalDateTime updatedAt = LocalDateTime.now();
  private final LocalDate date = LocalDate.now();
  private final Period period = Period.MORNING;
  private final String periodStr = "morning";
  private final EmotionRecord emotionRecordDb = EmotionRecord.builder()
      .id(id)
      .emotion(emotion)
      .intensity(intensity)
      .note(note)
      .createdAt(createdAt)
      .updatedAt(updatedAt)
      .patient(patient)
      .period(period)
      .build();
  private final EmotionRecord emotionRecordDb2 = EmotionRecord.builder()
      .id(id)
      .emotion(emotion)
      .intensity(intensity)
      .note(note)
      .createdAt(createdAt)
      .patient(patient)
      .period(period)
      .build();
  private final EmotionRecordDto emotionRecordDto = EmotionRecordDto.builder()
      .id(id)
      .emotion(emotion)
      .intensity(intensity)
      .period(period)
      .note(note)
      .createdAt(createdAt)
      .updatedAt(updatedAt)
      .patientId(patient.getId())
      .superheroName(patient.getUsername())
      .build();
  private final EmotionRecordCreationDto creationDto = EmotionRecordCreationDto.builder()
      .emotion(emotionStr)
      .intensity(intensity)
      .patientId(patient.getId())
      .date(date)
      .period(periodStr)
      .build();
  private final EmotionRecordByPatientDto byPatientDto = EmotionRecordByPatientDto.builder()
      .emotion(emotionStr)
      .intensity(intensity)
      .build();
  private final EmotionRecordUpdatingDto updatingDto = EmotionRecordUpdatingDto.builder()
      .id(id)
      .emotion(emotionStr)
      .intensity(intensity)
      .build();
  private final EmotionRecordDto dto = EmotionRecordDto.builder()
      .id(id)
      .emotion(emotion)
      .intensity(intensity)
      .period(Period.findOutPeriodByTime(LocalTime.now()))
      .createdAt(createdAt)
      .patientId(patient.getId())
      .superheroName(patient.getUsername())
      .build();
  private final EmotionsStatisticDto statistic = EmotionsStatisticDto.builder()
      .patientId(id)
        .lastEmotion(emotion)
        .mostOftenEmotions(new ArrayList<>(Collections.singletonList(emotion)))
        .totalEmotionRecords(3)
        .missedRecords(0)
        .frequencyOfEmotions(new HashMap<>())
        .build();
  private final EmotionRecordFilter filter = new EmotionRecordFilter();
  private Map<String, Map<String, Map<String, EmotionRecordDto>>> map = new HashMap<>();

  private static final String CSV_CONTENT = "id,emotion,intensity,note,createdAt,"
      + "updatedAt,patient,period\n\"1\",\"SAD\",\"5\",\"Note\",\"2023-01-01T17:00\",\"-\","
      + "\"Magical Fairy\",\"EVENING\"\n";
  private static final String CSV_HEADER = "\"id\",\"emotion\",\"intensity\",\"period\","
      + "\"createdAt\",\"updatedAt\",\"note\",\"patientId\",\"superheroName\"\n";

  @BeforeEach
  public void mock() {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(authentication.getPrincipal())
        .thenReturn(new UserDetailsImpl(1L, username, password,
            new SimpleGrantedAuthority(RoleName.USER.name())));
  }

  @Test
  public void should_SuccessfullyReturnAllEmotionRecords() {
    when(userRepository.findAllPatientsSortIdDesc())
        .thenReturn(new ArrayList<>(Collections.singletonList(patient)));
    when(recordRepository.findAll(
        specification.buildGetAllByDatesSpecification(any(EmotionRecordFilter.class))))
        .thenReturn(new ArrayList<>());
    when(recordConverter.convertToMap(anyList(), any(EmotionRecordFilter.class), anyList()))
        .thenReturn(map);
    assertEquals(recordService.findAllEmotionRecordsGroupByDate(filter), map);
  }

  @Test
  public void should_SuccessfullyReturnAllEmotionRecordsByPatientId() {
    when(userRepository.findPatientByIdIfExists(anyLong()))
        .thenReturn(patient);
    when(recordRepository.findAll(
        specification.buildGetAllByPatientIdSpecification(anyLong(), any(EmotionRecordFilter.class))))
        .thenReturn(new ArrayList<>());
    when(recordConverter.convertToMap(anyList(), any(EmotionRecordFilter.class), anyList())).thenReturn(map);
    assertEquals(recordService.findEmotionRecordsByPatient(id, filter), map);
  }

  @Test
  public void should_SuccessfullyGetStatisticByPatientId() {
    when(userRepository.findPatientByIdIfExists(anyLong()))
        .thenReturn(patient);
    when(statisticService.findLastEmotion(anyLong(),
        any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(emotion);
    when(statisticService.findMostFrequentEmotions(anyLong(),
        any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(new ArrayList<>(Collections.singletonList(emotion)));
    when(statisticService.countTotalRecords(anyLong(),
        any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(3);
    when(statisticService.countMissedRecords(any(User.class),
        any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(0);
    when(statisticService.getFrequencyOfEmotions(anyLong(),
        any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(new HashMap<>());
    assertEquals(recordService.getStatistic(id, filter), statistic);
  }

  @Test
  public void should_SuccessfullyFindEmotionRecordById() {
    when(recordRepository.findByIdIfExists(anyLong())).thenReturn(emotionRecordDb);
    when(recordConverter.convertToDto(any(EmotionRecord.class))).thenReturn(emotionRecordDto);
    assertEquals(recordService.findEmotionRecordById(anyLong()), emotionRecordDto);
  }

  @Test
  public void should_ThrowError_WhenFindingByIdNoneExistingEmotionRecord() {
    when(recordRepository.findByIdIfExists(-1L)).thenThrow(EntityNotFoundException.class);
    assertThrows(EntityNotFoundException.class,
        () -> recordService.findEmotionRecordById(-1L));
  }

  @Test
  public void should_SuccessfullyFindTodayEmotionRecordForCurrentPatient() {
    when(recordRepository.findByPatientIdAndPeriodAndDate(anyLong(), any(Period.class),
        any(LocalDate.class)))
        .thenReturn(Optional.empty());
    when(recordConverter.convertToDto(any(EmotionRecord.class))).thenReturn(emotionRecordDto);
    Map<String, EmotionRecordDto> todayEmotions = new HashMap<>();
    for (Period period : Period.values()) {
      todayEmotions.put(period.toString(), null);
    }
    assertEquals(recordService.findTodayEmotionRecordsForCurrentPatient(), todayEmotions);
  }

  @Test
  public void should_SuccessfullyCreateEmotionRecord() {
    when(userRepository.findPatientByIdIfExists(anyLong())).thenReturn(patient);
    when(recordConverter.convertToEntity(any(EmotionRecordCreationDto.class)))
        .thenReturn(emotionRecordDb);
    when(recordRepository.save(any(EmotionRecord.class))).thenReturn(emotionRecordDb);
    when(recordConverter.convertToDto(any(EmotionRecord.class))).thenReturn(emotionRecordDto);
    assertEquals(recordService.createEmotionRecord(creationDto), emotionRecordDto);
  }

  @Test
  public void should_ThrowError_When_CreatingEmotionRecordWhenAlreadyCreated() {
    when(periodConverter.convertToEnum(anyString())).thenReturn(period);
    doThrow(ValidationException.class).when(recordValidator)
        .validateIsNoOtherRecordSameTime(anyLong(), any(Period.class), any(LocalDate.class));
    assertThrows(ValidationException.class, () -> recordService.createEmotionRecord(creationDto));
  }

  @Test
  public void should_SuccessfullyCreateEmotionRecordByPatient() {
    when(userRepository.findPatientByIdIfExists(anyLong())).thenReturn(patient);
    when(recordConverter.convertToEntity(any(EmotionRecordByPatientDto.class), any(Period.class)))
        .thenReturn(emotionRecordDb);
    when(recordRepository.save(any(EmotionRecord.class))).thenReturn(emotionRecordDb);
    when(recordConverter.convertToDto(any(EmotionRecord.class))).thenReturn(dto);
    assertEquals(recordService.createEmotionRecordByPatient(byPatientDto), dto);
  }

  @Test
  public void should_ThrowError_When_CreatingEmotionRecordByPatientWhenAlreadyCreated() {
    doThrow(ValidationException.class).when(recordValidator)
        .validateIsNoOtherRecordSameTime(anyLong(), any(Period.class), any(LocalDate.class));
    assertThrows(ValidationException.class,
        () -> recordService.createEmotionRecordByPatient(byPatientDto));
  }

  @Test
  public void should_SuccessfullyUpdateEmotionRecord() {
    when(recordRepository.findByIdIfExists(anyLong())).thenReturn(emotionRecordDb);
    when(recordConverter.convertToEntity(
        any(EmotionRecordUpdatingDto.class), any(EmotionRecord.class)))
        .thenReturn(emotionRecordDb);
    when(recordConverter.convertToDto(any(EmotionRecord.class))).thenReturn(emotionRecordDto);
    assertEquals(recordService.updateEmotionRecordById(updatingDto), emotionRecordDto);
  }

  @Test
  public void should_ThrowError_When_UpdatingNotExistedEmotionRecord() {
    when(recordRepository.findByIdIfExists(anyLong())).thenThrow(EntityNotFoundException.class);
    assertThrows(EntityNotFoundException.class, ()
        -> recordService.updateEmotionRecordById(updatingDto));
  }

  @Test
  public void should_SuccessfullyUpdateEmotionRecordByPatient() {
    when(recordRepository.findByPatientIdAndCurrentDate(anyLong())).thenReturn(emotionRecordDb2);
    doNothing().when(recordValidator).validateIsNotUpdated(any(EmotionRecord.class));
    when(recordConverter.convertToEntity(any(EmotionRecordByPatientDto.class), any(EmotionRecord.class)))
        .thenReturn(emotionRecordDb);
    when(recordConverter.convertToDto(any(EmotionRecord.class))).thenReturn(dto);
    assertEquals(recordService.updateEmotionRecordByPatient(byPatientDto), dto);
  }

  @Test
  public void should_ThrowError_When_UpdatingByPatientNotExistedEmotionRecord() {
    when(recordRepository.findByPatientIdAndCurrentDate(anyLong()))
        .thenThrow(EntityNotFoundException.class);
    assertThrows(EntityNotFoundException.class,
        () -> recordService.updateEmotionRecordByPatient(byPatientDto));
  }

  @Test
  public void should_SuccessfullyDeleteEmotionRecord() {
    when(recordRepository.findByIdIfExists(anyLong())).thenReturn(emotionRecordDb);
    recordService.deleteEmotionRecordById(anyLong());
  }

  @Test
  public void should_ThrowError_When_DeletingNotExistedEmotionRecord() {
    when(recordRepository.findByIdIfExists(anyLong())).thenThrow(EntityNotFoundException.class);
    assertThrows(EntityNotFoundException.class,
        () -> recordService.deleteEmotionRecordById(anyLong()));
  }

  @Test
  public void should_SuccessfullyCreateCsvFile() {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
      byteArrayOutputStream.write(CSV_CONTENT.getBytes());
      createCsv(null, null, null, byteArrayOutputStream);
    } catch (IOException e) {
      throw new CsvFileException("Can't generate CSV file.", e);
    }
  }

  @Test
  public void should_ReturnCsvFileWithEmptyBodyWhenFilterByNonExistingPatient() {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
      byteArrayOutputStream.write(CSV_HEADER.getBytes());
      createCsv(99L, null, null, byteArrayOutputStream);
    } catch (IOException e) {
      throw new CsvFileException("Can't generate CSV file.", e);
    }
  }

  @Test
  public void should_ReturnCsvFileByDate() {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
      byteArrayOutputStream.write(CSV_CONTENT.getBytes());
      createCsv(null, LocalDate.now(), LocalDate.now().plusDays(1), byteArrayOutputStream);
    } catch (IOException e) {
      throw new CsvFileException("Can't generate CSV file.", e);
    }
  }

  private void createCsv(Long patientId, LocalDate startDate, LocalDate endDate,
      ByteArrayOutputStream byteArrayOutputStream) {
    EmotionRecordFilter emotionRecordFilterDto = new EmotionRecordFilter(startDate, endDate);
    when(csvService.createCsv(new ArrayList<>(), EmotionRecordDto.class))
        .thenReturn(byteArrayOutputStream);
    assertEquals(recordService.getCsvFile(patientId, emotionRecordFilterDto),
        byteArrayOutputStream);
  }

  @Test
  void should_SuccessfullyGetEmotionRecordsDataInPdf() {
    when(userRepository.findAllPatientsSortIdDesc())
        .thenReturn(new ArrayList<>(Collections.singletonList(patient)));
    when(recordRepository.findAll(
        specification.buildGetAllByDatesSpecification(any(EmotionRecordFilter.class))))
        .thenReturn(new ArrayList<>());
    when(recordConverter.convertToMap(anyList(), any(EmotionRecordFilter.class), anyList()))
        .thenReturn(map);

    recordService.getEmotionRecordsDataInPdf(filter);

    verify(pdfService, times(1)).createPdfWithEmotionRecords(filter, map);
  }

  @Test
  void should_SuccessfullyGetPatientEmotionRecordsDataInPdf() {
    when(userRepository.findPatientByIdIfExists(anyLong()))
        .thenReturn(patient);
    when(recordRepository.findAll(
        specification.buildGetAllByPatientIdSpecification(anyLong(),
            any(EmotionRecordFilter.class)))).thenReturn(new ArrayList<>());
    when(recordConverter.convertToMap(anyList(), any(EmotionRecordFilter.class), anyList()))
        .thenReturn(map);
    when(statisticService.findLastEmotion(anyLong(),
        any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(emotion);
    when(statisticService.findMostFrequentEmotions(anyLong(),
        any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(new ArrayList<>(Collections.singletonList(emotion)));
    when(statisticService.countTotalRecords(anyLong(),
        any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(3);
    when(statisticService.countMissedRecords(any(User.class),
        any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(0);
    when(statisticService.getFrequencyOfEmotions(anyLong(),
        any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(new HashMap<>());

    recordService.getPatientEmotionRecordsDataInPdf(filter, id);

    verify(pdfService, times(1)).createPdfWithPatientEmotionRecords(filter, map, statistic);
  }
}
