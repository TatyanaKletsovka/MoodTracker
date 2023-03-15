package com.syberry.mood.emotion.record.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.syberry.mood.emotion.record.dto.Emotion;
import com.syberry.mood.emotion.record.dto.EmotionRecordDto;
import com.syberry.mood.emotion.record.dto.EmotionRecordFilter;
import com.syberry.mood.emotion.record.dto.EmotionsStatisticDto;
import com.syberry.mood.emotion.record.dto.Period;
import com.syberry.mood.emotion.record.service.PdfService;
import com.syberry.mood.user.repository.UserRepository;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;

/**
 * Implementation of service interface for managing pdf file generating.
 */
@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

  private static final Font FONT_TITLE = new Font(Font.TIMES_ROMAN, 24, Font.BOLD, Color.BLACK);
  private static final Font FONT_PERIOD = new Font(Font.TIMES_ROMAN, 16, Font.NORMAL, Color.BLACK);
  private static final Font FONT_HEADERS = new Font(Font.TIMES_ROMAN, 14, Font.BOLD, Color.BLACK);
  private static final Font FONT_CELLS = new Font(Font.TIMES_ROMAN, 14, Font.NORMAL, Color.BLACK);
  private static final DateTimeFormatter PARSER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMMM, yyyy");
  private static final int SPACING_AFTER = 8;
  private static final int SPACING_BEFORE = 5;
  private static final int CELL_PADDING = 5;
  private static final int WIGHT = 500;
  private static final int HEIGHT = 400;
  private static final int NUM_COLUMNS = 4;
  private static final int COORDINATE_X = 50;
  private static final int COORDINATE_Y = 180;
  public static final String EMOTION_RECORDS = "Emotion records";
  public static final String SECRET_NAME = "Secret Name";
  public static final String DASH = "-";
  public static final String PATIENT_EMOTION_RECORDS = "Patient emotion records";
  public static final String EMOTION_INTENSITY_FORMAT = "%s (%s)";
  public static final String PATIENT = "Patient: ";
  public static final String DATE = "DATE";
  public static final String EMOTION_RECORDS_STATISTICS = "Emotion records (statistics)";
  public static final String LAST_EMOTION = "Last emotion: ";
  public static final String MOST_OFTEN_EMOTIONS = "Most often emotions: ";
  public static final String TOTAL_EMOTION_RECORDS = "Total emotion records: ";
  public static final String MISSED_EMOTION_RECORDS = "Missed emotion records: ";
  public static final String FROM = "from ";
  public static final String TO = " to ";
  public static final String FREQUENCY_OF_EMOTIONS = "Frequency of emotions";
  public static final String LABEL_FORMAT = "{0}: {1} ({2})";
  public static final String NUMBER_FORMAT = "0";
  public static final String PERCENT_FORMAT = "0%";

  private final UserRepository userRepository;

  @Override
  public ByteArrayInputStream createPdfWithEmotionRecords(
      EmotionRecordFilter filter, Map<String, Map<String, Map<String, EmotionRecordDto>>> records) {
    Document document = new Document(PageSize.A4);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PdfWriter.getInstance(document, out);
    addNameAndPeriod(filter, document, EMOTION_RECORDS);

    List<String> dates = getListOfSortedDateReverse(records.keySet());
    for (String date : dates) {
      Paragraph paragraph = new Paragraph(
          LocalDate.parse(date, PARSER).format(FORMATTER), FONT_HEADERS);
      paragraph.setAlignment(Element.ALIGN_LEFT);
      document.add(paragraph);

      PdfPTable table = new PdfPTable(NUM_COLUMNS);
      table.setWidthPercentage(100f);
      table.setWidths(new int[]{2, 1, 1, 1});
      table.setSpacingBefore(SPACING_BEFORE);
      table.setSpacingAfter(SPACING_AFTER);

      addTableHeader(table, Element.ALIGN_LEFT, SECRET_NAME);
      addTableBodyToEmotionRecordsPdf(records, date, table);
      document.add(table);
    }
    document.close();
    return new ByteArrayInputStream(out.toByteArray());
  }

  @Override
  public ByteArrayInputStream createPdfWithPatientEmotionRecords(
      EmotionRecordFilter filter,
      Map<String, Map<String, Map<String, EmotionRecordDto>>> records,
      EmotionsStatisticDto statisticDto) {
    Document document = new Document(PageSize.A4);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PdfWriter writer = PdfWriter.getInstance(document, out);
    String patient = userRepository.findPatientByIdIfExists(
        statisticDto.getPatientId()).getUsername();
    addPatientEmotionRecordsToPdf(filter, records, patient, document);
    addStatisticsPage(statisticDto, document, writer, patient);
    document.close();
    return new ByteArrayInputStream(out.toByteArray());
  }

  private void addPatientEmotionRecordsToPdf(
      EmotionRecordFilter dto, Map<String, Map<String, Map<String, EmotionRecordDto>>> records,
      String patient, Document document) {
    addNameAndPeriod(dto, document, PATIENT_EMOTION_RECORDS);

    Paragraph paragraph = new Paragraph(PATIENT + patient, FONT_PERIOD);
    paragraph.setAlignment(Element.ALIGN_LEFT);
    paragraph.setSpacingAfter(SPACING_AFTER);
    document.add(paragraph);

    PdfPTable table = new PdfPTable(NUM_COLUMNS);
    table.setWidthPercentage(100f);
    table.setSpacingBefore(SPACING_BEFORE);

    addTableHeader(table, Element.ALIGN_CENTER, DATE);
    addTableBodyToPatientEmotionRecordsPdf(records, patient, table);
    document.add(table);
  }

  private void addStatisticsPage(EmotionsStatisticDto statistic,
                                 Document document, PdfWriter writer, String patient) {
    document.newPage();
    Paragraph paragraph = new Paragraph(EMOTION_RECORDS_STATISTICS, FONT_TITLE);
    paragraph.setAlignment(Element.ALIGN_CENTER);
    paragraph.setSpacingAfter(SPACING_AFTER);
    document.add(paragraph);

    paragraph = new Paragraph(PATIENT + patient, FONT_PERIOD);
    paragraph.setAlignment(Element.ALIGN_LEFT);
    paragraph.setSpacingAfter(SPACING_AFTER);
    document.add(paragraph);

    paragraph = new Paragraph(LAST_EMOTION + statistic.getLastEmotion(), FONT_PERIOD);
    document.add(paragraph);

    paragraph = new Paragraph(MOST_OFTEN_EMOTIONS
        + statistic.getMostOftenEmotions().stream().map(Enum::name)
        .collect(Collectors.joining(", ")), FONT_PERIOD);
    document.add(paragraph);

    paragraph = new Paragraph(TOTAL_EMOTION_RECORDS
        + statistic.getTotalEmotionRecords(), FONT_PERIOD);
    document.add(paragraph);

    paragraph = new Paragraph(MISSED_EMOTION_RECORDS
        + statistic.getMissedRecords(), FONT_PERIOD);
    document.add(paragraph);

    PdfContentByte contentByte = writer.getDirectContent();

    PdfTemplate template = contentByte.createTemplate(WIGHT, HEIGHT);
    Graphics2D graphics2d = template.createGraphics(WIGHT, HEIGHT,
        new DefaultFontMapper());
    java.awt.geom.Rectangle2D rectangle2d = new java.awt.geom.Rectangle2D.Double(0, 0, WIGHT,
        HEIGHT);

    generatePieChart(statistic.getFrequencyOfEmotions()).draw(graphics2d, rectangle2d);
    graphics2d.dispose();
    contentByte.addTemplate(template, COORDINATE_X, COORDINATE_Y);
  }

  private void addNameAndPeriod(EmotionRecordFilter dto, Document document, String name) {
    document.open();

    Paragraph paragraph = new Paragraph(name, FONT_TITLE);
    paragraph.setAlignment(Element.ALIGN_CENTER);
    document.add(paragraph);

    paragraph = new Paragraph(FROM + dto.getStartDate() + TO + dto.getEndDate(), FONT_PERIOD);
    paragraph.setAlignment(Element.ALIGN_CENTER);
    paragraph.setSpacingAfter(SPACING_AFTER);
    document.add(paragraph);
  }

  private static JFreeChart generatePieChart(Map<Emotion, Long> frequencyOfEmotions) {
    DefaultPieDataset dataSet = new DefaultPieDataset();
    for (Map.Entry<Emotion, Long> item : frequencyOfEmotions.entrySet()) {
      dataSet.setValue(item.getKey().name(), item.getValue());
    }
    JFreeChart chart = ChartFactory.createPieChart(
        FREQUENCY_OF_EMOTIONS, dataSet, true, true, false);
    PiePlot plot = (PiePlot) chart.getPlot();
    PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
        LABEL_FORMAT, new DecimalFormat(NUMBER_FORMAT), new DecimalFormat(PERCENT_FORMAT));
    plot.setLabelGenerator(gen);
    plot.setBackgroundPaint(Color.WHITE);
    return chart;
  }

  private List<String> getListOfSortedDateReverse(Set<String> keys) {
    List<String> dates = new ArrayList<>(keys);
    dates.sort(Comparator.reverseOrder());
    return dates;
  }

  private void addTableHeader(PdfPTable table, int firstColumnAlignment, String firstColumnName) {
    PdfPCell cell = new PdfPCell();
    cell.setPadding(CELL_PADDING);
    cell.setBackgroundColor(Color.LIGHT_GRAY);
    cell.setHorizontalAlignment(firstColumnAlignment);
    cell.setVerticalAlignment(Element.ALIGN_CENTER);
    cell.setPhrase(new Phrase(firstColumnName, FONT_HEADERS));
    table.addCell(cell);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    for (Period period : Period.values()) {
      cell.setPhrase(new Phrase(period.toString(), FONT_HEADERS));
      table.addCell(cell);
    }
  }

  private void addTableBodyToEmotionRecordsPdf(
      Map<String, Map<String, Map<String, EmotionRecordDto>>> records,
      String date, PdfPTable table) {
    PdfPCell cell = new PdfPCell();
    for (Map.Entry<String, Map<String, EmotionRecordDto>> userRecords :
        records.get(date).entrySet()) {
      cell.setHorizontalAlignment(Element.ALIGN_LEFT);
      cell.setPhrase(new Phrase(userRecords.getKey(), FONT_CELLS));
      table.addCell(cell);

      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      for (Period period : Period.values()) {
        EmotionRecordDto emotionRecordDto = userRecords.getValue().get(period.toString());
        cell.setPhrase(new Phrase(
            emotionRecordDto != null
                ? String.format(EMOTION_INTENSITY_FORMAT, emotionRecordDto.getEmotion(),
                emotionRecordDto.getIntensity())
                : DASH,
            FONT_CELLS));
        table.addCell(cell);
      }
    }
  }

  private void addTableBodyToPatientEmotionRecordsPdf(
      Map<String, Map<String, Map<String, EmotionRecordDto>>> records,
      String patient, PdfPTable table) {
    PdfPCell cell = new PdfPCell();
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    List<String> dates = getListOfSortedDateReverse(records.keySet());
    for (String date : dates) {
      cell.setPhrase(new Phrase(date, FONT_CELLS));
      table.addCell(cell);
      for (Period period : Period.values()) {
        EmotionRecordDto emotionRecordDto = records.get(date).get(patient).get(period.toString());
        cell.setPhrase(new Phrase(
            emotionRecordDto != null
                ? String.format(EMOTION_INTENSITY_FORMAT, emotionRecordDto.getEmotion(),
                emotionRecordDto.getIntensity())
                : DASH,
            FONT_CELLS));
        table.addCell(cell);
      }
    }
  }
}
