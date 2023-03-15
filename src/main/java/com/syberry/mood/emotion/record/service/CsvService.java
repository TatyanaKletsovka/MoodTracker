package com.syberry.mood.emotion.record.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Service interface for managing csv file generating.
 */
public interface CsvService {

  /**
   * Generates csv file with emotion records.
   *
   * @param items list of emotion records
   * @param classType class type of dto for creating csv file
   * @param <I> dto for creating csv file
   * @return byteArrayOutput stream with created csv file.
   */
  <I> ByteArrayOutputStream createCsv(List<I> items, Class<?> classType);
}
