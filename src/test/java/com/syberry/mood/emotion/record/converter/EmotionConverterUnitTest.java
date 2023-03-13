package com.syberry.mood.emotion.record.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.syberry.mood.emotion.record.dto.Period;
import com.syberry.mood.exception.InvalidArgumentTypeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EmotionConverterUnitTest {

  @InjectMocks
  private PeriodConverter periodConverter;

  @Test
  public void should_SuccessfullyConvertToEnum() {
    assertEquals(periodConverter.convertToEnum("morning"), Period.MORNING);
  }

  @Test
  public void should_ThrowError_When_ConvertingInvalidString() {
    assertThrows(InvalidArgumentTypeException.class, () -> periodConverter.convertToEnum(""));
  }
}
