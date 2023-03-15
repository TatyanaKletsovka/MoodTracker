package com.syberry.mood.emotion.record.service.impl;

import com.syberry.mood.emotion.record.service.CsvService;
import com.syberry.mood.exception.CsvFileException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of service interface for managing csv file generating.
 */
@Service
@RequiredArgsConstructor
public class CsvServiceImpl implements CsvService {

  private static final String SEPARATOR = ",";
  private static final String QUOTE = "\"";
  private static final String NEW_LINE = "\n";
  private static final String NULL_ROW = "-";
  private static final String CSV_ERROR_MESSAGE = "An error occurred while creating the csv file.";

  @Override
  public <I> ByteArrayOutputStream createCsv(List<I> items, Class<?> classType) {
    StringBuilder stringBuilder = new StringBuilder();
    List<String> header = createHeader(classType, stringBuilder);
    createBody(items, getClassGetMethods(classType, header), stringBuilder);
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
      byteArrayOutputStream.write(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
      return byteArrayOutputStream;
    } catch (IOException e) {
      throw new CsvFileException(CSV_ERROR_MESSAGE, e);
    }
  }

  private List<String> createHeader(Class<?> classType, StringBuilder stringBuilder) {
    List<String> fieldsName = Arrays.stream(classType.getDeclaredFields())
        .map(Field::getName)
        .toList();
    writeLine(fieldsName, stringBuilder);
    return fieldsName;
  }

  private <I> void createBody(List<I> items, List<Method> classGetMethods,
      StringBuilder stringBuilder) {
    for (I item : items) {
      List<String> getMethodsValues = classGetMethods.stream().map(x -> {
            try {
              return isNullCheck(x.invoke(item));
            } catch (IllegalAccessException | InvocationTargetException e) {
              throw new CsvFileException(CSV_ERROR_MESSAGE, e);
            }
          }
      ).toList();
      writeLine(getMethodsValues, stringBuilder);
    }
  }

  private void writeLine(List<String> cells, StringBuilder stringBuilder) {
    for (String cell : cells) {
      writeAndSeparate(cell, stringBuilder);
    }
    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    stringBuilder.append(NEW_LINE);
  }

  private void writeAndSeparate(String cell, StringBuilder stringBuilder) {
    stringBuilder.append(String.format("%s%s%s%s", QUOTE, isNullCheck(cell), QUOTE, SEPARATOR));
  }

  private List<Method> getClassGetMethods(Class<?> classType, List<String> fieldsName) {
    List<Method> getMethods = new ArrayList<>();
    for (String fieldName : fieldsName) {
      String getMethodName = createGetMethodNameFromFieldName(fieldName);
      try {
        Method getMethod = classType.getMethod(getMethodName);
        getMethods.add(getMethod);
      } catch (NoSuchMethodException e) {
        throw new CsvFileException(CSV_ERROR_MESSAGE, e);
      }
    }
    return getMethods;
  }

  private String createGetMethodNameFromFieldName(String fieldName) {
    fieldName = fieldName.replaceFirst(String.valueOf(fieldName.charAt(0)),
        String.valueOf(fieldName.charAt(0)).toUpperCase());
    return String.format("get%s", fieldName);
  }

  private <I> String isNullCheck(I item) {
    return item != null ? String.valueOf(item) : NULL_ROW;
  }
}
