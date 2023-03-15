package com.syberry.mood.emotion.record.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.syberry.mood.emotion.record.dto.Emotion;
import com.syberry.mood.emotion.record.dto.EmotionRecordDto;
import com.syberry.mood.emotion.record.dto.Period;
import com.syberry.mood.emotion.record.service.impl.CsvServiceImpl;
import com.syberry.mood.exception.CsvFileException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CsvServiceTest {

  @InjectMocks
  private CsvServiceImpl csvService;
  private List<EmotionRecordDto> emotionRecordDtoList = new ArrayList<>();

  private static final String CSV_CONTENT = "\"id\",\"emotion\",\"intensity\",\"period\","
      + "\"createdAt\",\"updatedAt\",\"note\",\"patientId\",\"superheroName\""
      + "\n\"1\",\"HAPPY\",\"5\",\"MORNING\","
      + "\"2023-03-08T21:01:52.069182637\",\"2023-03-08T21:01:52.069233675\","
      + "\"Note\",\"1\",\"-\"\n";

  @BeforeEach
  public void mock() {
    Long id = 1L;
    Emotion emotion = Emotion.HAPPY;
    int intensity = 5;
    String note = "Note";
    LocalDateTime createdAt = LocalDateTime.parse("2023-03-08T21:01:52.069182637");
    LocalDateTime updatedAt = LocalDateTime.parse("2023-03-08T21:01:52.069233675");
    Period period = Period.MORNING;
    EmotionRecordDto emotionRecordDb = EmotionRecordDto.builder()
        .id(id)
        .emotion(emotion)
        .intensity(intensity)
        .note(note)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .patientId(1L)
        .period(period)
        .build();
    emotionRecordDtoList.add(emotionRecordDb);
  }

  @Test
  public void should_SuccessfullyCreateCsv() {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
      byteArrayOutputStream.write(CSV_CONTENT.getBytes());
      assertEquals(csvService.createCsv(emotionRecordDtoList, EmotionRecordDto.class).toString(),
          byteArrayOutputStream.toString());
    } catch (IOException e) {
      throw new CsvFileException("Can't generate CSV file.", e);
    }
  }
}
