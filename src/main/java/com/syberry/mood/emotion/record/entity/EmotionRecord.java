package com.syberry.mood.emotion.record.entity;

import com.syberry.mood.emotion.record.dto.Emotion;
import com.syberry.mood.emotion.record.dto.Period;
import com.syberry.mood.user.entity.User;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The entity representing an emotion record in the application.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @NotNull
  @Enumerated(EnumType.STRING)
  private Emotion emotion;
  @NotNull
  @Min(value = 1)
  @Max(value = 5)
  private int intensity;
  private String note;
  @NotNull
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;
  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User patient;
  @NotNull
  @Enumerated(EnumType.STRING)
  private Period period;
}
