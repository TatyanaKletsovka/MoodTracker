package com.syberry.mood.emotion.record.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.syberry.mood.emotion.record.dto.Emotion;
import com.syberry.mood.emotion.record.dto.EmotionRecordByPatientDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordCreationDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordUpdatingDto;
import com.syberry.mood.emotion.record.dto.Period;
import com.syberry.mood.emotion.record.entity.EmotionRecord;
import com.syberry.mood.user.entity.Role;
import com.syberry.mood.user.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EmotionRecordConverterUnitTest {

  @InjectMocks
  private EmotionRecordConverter emotionRecordConverter;
  @Mock
  private PeriodConverter periodConverter;
  @Mock
  private EmotionConverter emotionConverter;
  private final User patient = new User(1L, "Super Man", "cat_dog_frog", new Role(),
      LocalDateTime.now(), null, false);
  private final Long id = 1L;
  private final Emotion emotion = Emotion.HAPPY;
  private final String emotionStr = "happy";
  private final int intensity = 5;
  private final String note = "Note";
  private final LocalDateTime createdAt = LocalDateTime.now();
  private final LocalDateTime updatedAt = LocalDateTime.now();
  private final LocalDate date = LocalDate.now();
  private final Period period = Period.MORNING;
  private final String periodStr = "morning";
  private EmotionRecord emotionRecord = EmotionRecord.builder()
      .id(id)
      .emotion(emotion)
      .intensity(intensity)
      .period(period)
      .build();
  private EmotionRecord emotionRecordDb = EmotionRecord.builder()
      .id(id)
      .emotion(emotion)
      .intensity(intensity)
      .note(note)
      .createdAt(createdAt)
      .updatedAt(updatedAt)
      .patient(patient)
      .period(period)
      .build();
  private EmotionRecordDto emotionRecordDto = EmotionRecordDto.builder()
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
  private EmotionRecordCreationDto creationDto = EmotionRecordCreationDto.builder()
      .emotion(emotionStr)
      .intensity(intensity)
      .patientId(patient.getId())
      .date(date)
      .period(periodStr)
      .build();
  private EmotionRecordUpdatingDto updatingDto = EmotionRecordUpdatingDto.builder()
      .id(id)
      .emotion(emotionStr)
      .intensity(intensity)
      .build();
  private EmotionRecordByPatientDto byPatientDto = EmotionRecordByPatientDto.builder()
      .emotion(emotionStr)
      .intensity(intensity)
      .build();


  @BeforeEach
  public void mock() {
    when(periodConverter.convertToEnum(any())).thenReturn(period);
    when(emotionConverter.convertToEnum(any())).thenReturn(emotion);
  }


  @Test
  public void should_SuccessfullyConvertEmotionRecordCreationDtoToEntity() {
    assertThat(emotionRecordConverter.convertToEntity(creationDto))
        .usingRecursiveComparison()
        .ignoringFields("id", "createdAt")
        .isEqualTo(emotionRecord);
  }

  @Test
  public void should_SuccessfullyConvertEmotionRecordUpdatingDtoToEntity() {
    assertThat(emotionRecordConverter.convertToEntity(updatingDto, emotionRecord))
        .isEqualTo(emotionRecord);
  }

  @Test
  public void should_SuccessfullyConvertEmotionRecordByPatientDtoToEntity() {
    assertThat(emotionRecordConverter.convertToEntity(byPatientDto, period))
        .usingRecursiveComparison()
        .ignoringFields("id", "createdAt")
        .isEqualTo(emotionRecord);
  }

  @Test
  public void should_SuccessfullyConvertEmotionRecordByPatientDtoAndEntityToEntity() {
    assertThat(emotionRecordConverter.convertToEntity(byPatientDto, emotionRecord))
        .isEqualTo(emotionRecord);
  }

  @Test
  public void should_SuccessfullyConvertToDto() {
    assertEquals(emotionRecordConverter.convertToDto(emotionRecordDb), emotionRecordDto);
  }
}
