package com.syberry.mood.emotion.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A data transfer object that represents an emotion record entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionRecordDto {

  private Long id;
  private Emotion emotion;
  private int intensity;
  private Period period;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime createdAt;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime updatedAt;
  private String note;
  private Long patientId;
  private String superheroName;
}
