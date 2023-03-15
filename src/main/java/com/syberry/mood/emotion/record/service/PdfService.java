package com.syberry.mood.emotion.record.service;

import com.syberry.mood.emotion.record.dto.EmotionRecordDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordFilter;
import com.syberry.mood.emotion.record.dto.EmotionsStatisticDto;
import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 * Service interface for managing pdf file generating.
 */
public interface PdfService {

  /**
   * Generates pdf file with emotion records.
   *
   * @param filter  filter with startDate and endDate parameters
   * @param records records for creating file
   * @return ByteArrayInputStream with a created file
   */
  ByteArrayInputStream createPdfWithEmotionRecords(
      EmotionRecordFilter filter, Map<String, Map<String, Map<String, EmotionRecordDto>>> records);

  /**
   * Generates pdf file with patient's emotion records.
   *
   * @param filter       filter with startDate and endDate parameters
   * @param records      records for creating file
   * @param statisticDto statistics for creating statistics page
   * @return ByteArrayInputStream with a created file
   */
  ByteArrayInputStream createPdfWithPatientEmotionRecords(
      EmotionRecordFilter filter, Map<String, Map<String, Map<String, EmotionRecordDto>>> records,
      EmotionsStatisticDto statisticDto);
}
