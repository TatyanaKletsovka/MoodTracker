package com.syberry.mood.emotion.record.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.syberry.mood.emotion.record.dto.Emotion;
import com.syberry.mood.exception.InvalidArgumentTypeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PeriodConverterUnitTest {

  @InjectMocks
  private EmotionConverter emotionConverter;

  @Test
  public void should_SuccessfullyConvertToEnum() {
    assertEquals(emotionConverter.convertToEnum("happy"), Emotion.HAPPY);
  }

  @Test
  public void should_ThrowError_When_ConvertingInvalidString() {
    assertThrows(InvalidArgumentTypeException.class,
        () -> emotionConverter.convertToEnum(""));
  }
}
