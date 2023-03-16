package com.syberry.mood.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.syberry.mood.MoodApplication;
import com.syberry.mood.emotion.record.dto.Period;
import com.syberry.mood.user.dto.RoleName;
import com.syberry.mood.user.entity.Role;
import com.syberry.mood.user.entity.User;
import com.syberry.mood.user.repository.RoleRepository;
import com.syberry.mood.user.repository.UserRepository;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MoodApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
public class EmotionRecordIntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private UserRepository userRepository;

  private static final String PARAM_DATE = "2023-01-01";
  private static final String PARAM_START_DATE = "startDate";
  private static final String PARAM_END_DATE = "endDate";
  private static final String CSV_CONTENT = "\"id\",\"emotion\",\"intensity\",\"period\","
      + "\"createdAt\",\"updatedAt\",\"note\",\"patientId\",\"superheroName\""
      + "\n\"1\",\"SAD\",\"5\",\"EVENING\",\"2023-01-01T17:00\","
      + "\"-\",\"Note\",\"2\",\"Magical Fairy\"\n";

  @BeforeEach
  public void setUp() {
    Role adminRole = new Role(1L, RoleName.SUPER_ADMIN);
    Role userRole = new Role(4L, RoleName.USER);
    roleRepository.save(adminRole);
    roleRepository.save(new Role(2L, RoleName.ADMIN));
    roleRepository.save(new Role(3L, RoleName.MODERATOR));
    roleRepository.save(userRole);
    userRepository.save(new User(1L, "doc@gmail.com", "#tv!ghw36", adminRole,
        LocalDateTime.now(), null, false));
    userRepository.save(new User(2L, "Magical Fairy",
        "$2a$10$06JtH78fSVtkurq0agdiO.R.H5MnpZkoxks.tIlvxmTwYjZHIjYv6", userRole,
        LocalDateTime.now(), null, false));
    userRepository.save(new User(3L, "Magical Frog",
        "$2a$10$06JtH78fSVtkurq0agdiO.R.H5MnpZkoxks.tIlvxmTwYjZHIjYv6", userRole,
        LocalDateTime.now(), null, false));
  }

  @Test
  @WithMockUser(username = "doc@gmail.com", roles = "SUPER_ADMIN")
  public void should_GetAllEmotionRecords() throws Exception {
    final File jsonFile = new ClassPathResource("json/expected-get-all.json").getFile();
    String expected = Files.readString(jsonFile.toPath());
    MvcResult response = mockMvc.perform(
        get("/emotion-records")
        .param("startDate", "2023-03-14")
        .param("endDate", "2023-03-14")
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();
    String responseJson = response.getResponse().getContentAsString();
    JSONAssert.assertEquals(expected, responseJson, false);
  }

  @Test
  @WithMockUser(username = "doc@gmail.com", roles = "SUPER_ADMIN")
  public void should_GetAllEmotionRecordsByPatientId() throws Exception {
    final File jsonFile = new ClassPathResource("json/expected-get-all-by-patient.json").getFile();
    String expected = Files.readString(jsonFile.toPath());
    MvcResult response = mockMvc.perform(
        get("/emotion-records/patients/2")
        .param("startDate", "2023-03-14")
        .param("endDate", "2023-03-14")
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();
    String responseJson = response.getResponse().getContentAsString();
    JSONAssert.assertEquals(expected, responseJson, false);
  }

  @Test
  @WithMockUser(username = "doc@gmail.com", roles = "SUPER_ADMIN")
  public void should_GetAllStatisticByPatientId() throws Exception {
    final File jsonFile = new ClassPathResource("json/expected-get-statistic.json").getFile();
    String expected = Files.readString(jsonFile.toPath());
    MvcResult response = mockMvc.perform(
        get("/emotion-records/patients/2/statistic")
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();
    String responseJson = response.getResponse().getContentAsString();
    JSONAssert.assertEquals(expected, responseJson, false);
  }

  @Test
  @WithMockUser(username = "doc@gmail.com", roles = "SUPER_ADMIN")
  public void should_GetEmotionRecordById() throws Exception {
    final File jsonFile = new ClassPathResource("json/create-emotion-record.json").getFile();
    String emotionRecord = Files.readString(jsonFile.toPath());
    mockMvc.perform(post("/emotion-records/patients/2")
        .contentType(MediaType.APPLICATION_JSON)
        .content(emotionRecord));
    mockMvc.perform(get("/emotion-records/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.emotion").value("SAD"))
        .andExpect(jsonPath("$.intensity").value(5))
        .andExpect(jsonPath("$.period").value("EVENING"))
        .andExpect(jsonPath("$.note").value("Note"))
        .andExpect(jsonPath("$.patientId").value(2L))
        .andExpect(jsonPath("$.superheroName").value("Magical Fairy"));
  }

  @Test
  @WithMockUser(username = "Magical Fairy", roles = "USER")
  public void should_FindTodayEmotionRecordForCurrentPatient() throws Exception {
    final File jsonFile = new ClassPathResource("json/create-emotion-record.json").getFile();
    String emotionRecord = Files.readString(jsonFile.toPath());
    mockMvc.perform(post("/emotion-records/patients/2")
        .contentType(MediaType.APPLICATION_JSON)
        .content(emotionRecord));
    login();
    mockMvc.perform(get("/emotion-records/today")
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.MORNING").doesNotExist())
        .andExpect(jsonPath("$.AFTERNOON").doesNotExist())
        .andExpect(jsonPath("$.EVENING").doesNotExist());
  }

  @Test
  @WithMockUser(username = "doc@gmail.com", roles = "SUPER_ADMIN")
  public void should_CreateEmotionRecord() throws Exception {
    final File jsonFile = new ClassPathResource("json/create-emotion-record.json").getFile();
    String emotionRecord = Files.readString(jsonFile.toPath());
    mockMvc.perform(post("/emotion-records/patients/2")
        .contentType(MediaType.APPLICATION_JSON)
        .content(emotionRecord))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.emotion").value("SAD"))
        .andExpect(jsonPath("$.intensity").value(5))
        .andExpect(jsonPath("$.period").value("EVENING"))
        .andExpect(jsonPath("$.note").value("Note"))
        .andExpect(jsonPath("$.patientId").value(2L))
        .andExpect(jsonPath("$.superheroName").value("Magical Fairy"));
  }

  @Test
  @WithMockUser(username = "Magical Fairy", roles = "USER")
  public void should_CreateEmotionRecordByPatient() throws Exception {
    login();
    final File jsonFile = new ClassPathResource("json/create-emotion-record-patient.json")
        .getFile();
    String emotionRecord = Files.readString(jsonFile.toPath());
    mockMvc.perform(post("/emotion-records")
        .contentType(MediaType.APPLICATION_JSON)
        .content(emotionRecord))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.emotion").value("SAD"))
        .andExpect(jsonPath("$.intensity").value(5))
        .andExpect(jsonPath("$.period")
            .value(Period.findOutPeriodByTime(LocalTime.now()).toString()))
        .andExpect(jsonPath("$.note").doesNotExist())
        .andExpect(jsonPath("$.patientId").value(2L))
        .andExpect(jsonPath("$.superheroName").value("Magical Fairy"));
  }

  @Test
  @WithMockUser(username = "doc@gmail.com", roles = "SUPER_ADMIN")
  public void should_UpdateEmotionRecord() throws Exception {
    final File jsonFile = new ClassPathResource("json/create-emotion-record.json").getFile();
    String emotionRecord = Files.readString(jsonFile.toPath());
    mockMvc.perform(post("/emotion-records/patients/2")
        .contentType(MediaType.APPLICATION_JSON)
        .content(emotionRecord));

    final File jsonFileUpdate = new ClassPathResource("json/update-emotion-record.json")
        .getFile();
    String emotionRecordToUpdate = Files.readString(jsonFileUpdate.toPath());
    mockMvc.perform(put("/emotion-records/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(emotionRecordToUpdate))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.emotion").value("HAPPY"))
        .andExpect(jsonPath("$.intensity").value(3))
        .andExpect(jsonPath("$.period").value("EVENING"))
        .andExpect(jsonPath("$.note").value("New Note"))
        .andExpect(jsonPath("$.patientId").value(2L))
        .andExpect(jsonPath("$.superheroName").value("Magical Fairy"));
  }

  @Test
  @WithMockUser(username = "Magical Fairy", roles = "USER")
  public void should_UpdateEmotionRecordByPatient() throws Exception {
    login();

    final File jsonFile = new ClassPathResource("json/create-emotion-record-patient.json")
        .getFile();
    String emotionRecord = Files.readString(jsonFile.toPath());
    mockMvc.perform(post("/emotion-records")
        .contentType(MediaType.APPLICATION_JSON)
        .content(emotionRecord))
        .andDo(print());

    final File jsonFileUpdate = new ClassPathResource("json/update-emotion-record-patient.json")
        .getFile();
    String emotionRecordToUpdate = Files.readString(jsonFileUpdate.toPath());
    mockMvc.perform(put("/emotion-records")
        .contentType(MediaType.APPLICATION_JSON)
        .content(emotionRecordToUpdate))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.emotion").value("HAPPY"))
        .andExpect(jsonPath("$.intensity").value(3))
        .andExpect(jsonPath("$.period")
            .value(Period.findOutPeriodByTime(LocalTime.now()).toString()))
        .andExpect(jsonPath("$.note").doesNotExist())
        .andExpect(jsonPath("$.patientId").value(2L))
        .andExpect(jsonPath("$.superheroName").value("Magical Fairy"));
  }

  @Test
  @WithMockUser(username = "doc@gmail.com", roles = "SUPER_ADMIN")
  public void should_DeleteEmotionRecord() throws Exception {
    final File jsonFile = new ClassPathResource("json/create-emotion-record.json").getFile();
    String emotionRecord = Files.readString(jsonFile.toPath());
    mockMvc.perform(post("/emotion-records/patients/2")
        .contentType(MediaType.APPLICATION_JSON)
        .content(emotionRecord));

    mockMvc.perform(delete("/emotion-records/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNoContent());
    mockMvc.perform(get("/emotion-records/1"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "doc@gmail.com", roles = "ADMIN")
  public void should_CreateCsvFileByPatientId() throws Exception {
    createEmotionRecord();
    perform("/emotion-records/csv-file/patients/2")
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.parseMediaType("text/csv")))
        .andExpect(content().bytes(CSV_CONTENT.getBytes()));
  }

  @Test
  @WithMockUser(username = "doc@gmail.com", roles = "ADMIN")
  public void should_CreateCsvFileByDate() throws Exception {
    createEmotionRecord();
    perform("/emotion-records/csv-file")
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.parseMediaType("text/csv")))
        .andExpect(content().bytes(CSV_CONTENT.getBytes()));
  }

  private void login() throws Exception {
    final File loginRequestDtoFile =
        new ClassPathResource("json/login-request-dto2.json").getFile();
    String loginRequestDto = Files.readString(loginRequestDtoFile.toPath());
    mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(loginRequestDto))
        .andDo(print());
  }

  private void createEmotionRecord() throws Exception {
    final File jsonFile = new ClassPathResource("json/create-emotion-record.json").getFile();
    String emotionRecord = Files.readString(jsonFile.toPath());
    mockMvc.perform(post("/emotion-records/patients/2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(emotionRecord))
        .andDo(print());
  }

  private ResultActions perform(String url) throws Exception {
    return mockMvc.perform(get(url)
        .param(PARAM_START_DATE, PARAM_DATE)
        .param(PARAM_END_DATE, PARAM_DATE));
  }
}
