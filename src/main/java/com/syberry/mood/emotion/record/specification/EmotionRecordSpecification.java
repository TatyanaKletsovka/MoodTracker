package com.syberry.mood.emotion.record.specification;

import com.syberry.mood.emotion.record.dto.EmotionRecordFilter;
import com.syberry.mood.emotion.record.entity.EmotionRecord;
import com.syberry.mood.emotion.record.entity.EmotionRecord_;
import com.syberry.mood.emotion.record.util.DateUtil;
import com.syberry.mood.user.entity.User_;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * A class for building JPA Specification objects for querying EmotionRecords.
 */
@Component
public class EmotionRecordSpecification {

  /**
   * Builds a Specification object for retrieving all EmotionRecords between the start
   * and end dates specified in the given EmotionRecordFilter.
   *
   * @param filter the filter to apply to the search
   * @return a Specification object for the specified date range
   */
  public Specification<EmotionRecord> buildGetAllByDatesSpecification(EmotionRecordFilter filter) {
    return buildCreatedAtBetweenSpecification(filter.getStartDate().atStartOfDay(),
        DateUtil.convertToDateTimeEndDay(filter.getEndDate()));
  }

  /**
   * Builds a Specification object for retrieving all EmotionRecords for the specified
   * patient ID and within the date range specified in the given EmotionRecordFilter.
   *
   * @param patientId the ID of the patient to retrieve records for
   * @param filter    the filter to apply to the search
   * @return a Specification object for the specified patient and date range
   */
  public Specification<EmotionRecord> buildGetAllByPatientIdSpecification(
      Long patientId, EmotionRecordFilter filter) {
    return buildGetAllByDatesSpecification(filter)
        .and(buildWherePatientIdIsSpecification(patientId));
  }

  /**
   * Builds a JPA Specification object to query EmotionRecord entities
   * with CREATED_AT property between the given start and end dates (inclusive).
   *
   * @param startDate the start date of the query range
   * @param endDate   the end date of the query range
   * @return a JPA Specification object to be used in the repository's query methods
   */
  private Specification<EmotionRecord> buildCreatedAtBetweenSpecification(LocalDateTime startDate,
                                                                          LocalDateTime endDate) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.between(root.get(EmotionRecord_.CREATED_AT), startDate, endDate);
  }

  /**
   * Builds a JPA Specification object to query EmotionRecord entities with the given patient ID.
   *
   * @param patientId the ID of the patient to query EmotionRecord entities for
   * @return a JPA Specification object to be used in the repository's query methods
   */
  private Specification<EmotionRecord> buildWherePatientIdIsSpecification(Long patientId) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(EmotionRecord_.PATIENT).get(User_.ID), patientId);
  }
}
