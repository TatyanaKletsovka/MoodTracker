package com.syberry.mood.emotion.record.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.syberry.mood.emotion.record.dto.Emotion;
import com.syberry.mood.emotion.record.dto.EmotionRecordDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordFilter;
import com.syberry.mood.emotion.record.dto.EmotionsStatisticDto;
import com.syberry.mood.emotion.record.dto.Period;
import com.syberry.mood.emotion.record.service.impl.PdfServiceImpl;
import com.syberry.mood.user.dto.RoleName;
import com.syberry.mood.user.entity.Role;
import com.syberry.mood.user.entity.User;
import com.syberry.mood.user.repository.UserRepository;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
class PdfServiceTest {

  public static final String CRAZY_FROG = "Crazy Frog";
  public static final long PATIENT_ID = 3L;
  @InjectMocks
  PdfServiceImpl pdfService;

  @Mock
  UserRepository userRepository;
  Map<String, Map<String, Map<String, EmotionRecordDto>>> records = new HashMap<>();
  EmotionsStatisticDto statisticDto;
  EmotionRecordFilter filter;
  User user;

  @BeforeEach
  void init() {
    user = new User(PATIENT_ID, CRAZY_FROG,
        "$2a$10$E6qtGfGqO0G.GPacxsNuQ.yh5ct.rvzVTCj0H0xqwTmFS9YGEeRtO",
        new Role(PATIENT_ID, RoleName.USER), LocalDateTime.now(), null, false);
    filter = new EmotionRecordFilter(LocalDate.now(), LocalDate.now().plusDays(1));
    EmotionRecordDto dto = new EmotionRecordDto(1L, Emotion.HAPPY, 3, Period.MORNING,
        LocalDateTime.now(), null, null, PATIENT_ID, CRAZY_FROG);
    Map<String, EmotionRecordDto> dayEmotionRecords = new HashMap<>();
    dayEmotionRecords.put("MORNING", dto);
    dayEmotionRecords.put("AFTERNOON", null);
    dayEmotionRecords.put("EVENING", null);
    Map<String, Map<String, EmotionRecordDto>> patientEmotionRecords = new HashMap<>();
    patientEmotionRecords.put(CRAZY_FROG, dayEmotionRecords);
    records.put(LocalDate.now().toString(), patientEmotionRecords);

    Map<Emotion, Long> frequencyOfEmotions = new HashMap<>();
    for (Emotion emotion : Emotion.values()) {
      frequencyOfEmotions.put(emotion, 10L);
    }
    statisticDto = new EmotionsStatisticDto(PATIENT_ID, Emotion.HAPPY,
        List.of(Emotion.HAPPY, Emotion.SAD), 70, 15, frequencyOfEmotions);
  }

  @Test
  void should_SuccessfullyGetEmotionRecordsDataInPdf() {
    ByteArrayInputStream inputStream = pdfService.createPdfWithEmotionRecords(filter, records);

    assertNotNull(inputStream);
  }

  @Test
  void should_SuccessfullyGetPatientEmotionRecordsDataInPdf() {
    when(userRepository.findPatientByIdIfExists(any())).thenReturn(user);

    ByteArrayInputStream inputStream =
        pdfService.createPdfWithPatientEmotionRecords(filter, records, statisticDto);

    assertNotNull(inputStream);
  }
}
