package com.syberry.mood.emotion.record.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A data transfer object for representing emotion statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionsStatisticDto {

  private Long patientId;
  private Emotion lastEmotion;
  private List<Emotion> mostOftenEmotions;
  private int totalEmotionRecords;
  private int missedRecords;
  private Map<Emotion, Long> frequencyOfEmotions;
}
