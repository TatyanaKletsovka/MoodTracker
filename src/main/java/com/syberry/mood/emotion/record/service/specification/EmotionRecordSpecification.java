package com.syberry.mood.emotion.record.service.specification;

import com.syberry.mood.emotion.record.dto.EmotionRecordFilter;
import com.syberry.mood.emotion.record.entity.EmotionRecord;
import com.syberry.mood.emotion.record.entity.EmotionRecord_;
import com.syberry.mood.user.entity.User_;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Specification for creating request to EmotionRecordRepository.
 */
@Component
public class EmotionRecordSpecification {

  private static final long DAY_AMOUNT = 1;
  private static final long NANOS_AMOUNT = 1L;

  /**
   * Creates specification with filter parameters.
   *
   * @param filter includes startDate and endDate parameters
   * @return created specification for EmotionRecordRepository
   */
  public Specification<EmotionRecord> buildGetAllByDatesSpecification(EmotionRecordFilter filter) {
    return buildCreatedAtBetweenSpecification(filter.getStartDate().atStartOfDay(),
        filter.getEndDate().atStartOfDay().plusDays(DAY_AMOUNT).minusNanos(NANOS_AMOUNT));
  }

  /**
   * Creates specification with patient ID and filter parameters.
   *
   * @param patientId the ID of patient entity
   * @param filter includes startDate and endDate parameters
   * @return created specification for EmotionRecordRepository
   */
  public Specification<EmotionRecord> buildGetAllByPatientIdSpecification(
      Long patientId, EmotionRecordFilter filter) {
    return buildGetAllByDatesSpecification(filter)
        .and(buildWherePatientIdIsSpecification(patientId));
  }

  private Specification<EmotionRecord> buildCreatedAtBetweenSpecification(LocalDateTime startDate,
      LocalDateTime endDate) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.between(root.get(EmotionRecord_.CREATED_AT), startDate, endDate);
  }

  private Specification<EmotionRecord> buildWherePatientIdIsSpecification(Long patientId) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(EmotionRecord_.PATIENT).get(User_.ID), patientId);
  }
}
