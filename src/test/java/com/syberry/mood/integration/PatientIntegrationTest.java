package com.syberry.mood.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.syberry.mood.MoodApplication;
import com.syberry.mood.user.dto.RoleName;
import com.syberry.mood.user.entity.Role;
import com.syberry.mood.user.repository.RoleRepository;
import java.io.File;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MoodApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@WithMockUser(username = "Super Man", roles = "ADMIN")
public class PatientIntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  private String patientToCreate;

  @BeforeEach
  public void setUp() throws Exception {
    roleRepository.save(new Role(1L, RoleName.ADMIN));
    roleRepository.save(new Role(2L, RoleName.MODERATOR));
    roleRepository.save(new Role(3L, RoleName.USER));

    final File jsonFile = new ClassPathResource("json/create-patient.json").getFile();
    patientToCreate = Files.readString(jsonFile.toPath());
    mockMvc.perform(post("/patients")
        .contentType(MediaType.APPLICATION_JSON)
        .content(patientToCreate));
  }

  @Test
  public void should_GetAllPatients() throws Exception {
    mockMvc.perform(post("/patients")
        .contentType(MediaType.APPLICATION_JSON)
        .content(patientToCreate));
    mockMvc.perform(get("/patients"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].superheroName").value("Super Man"))
        .andExpect(jsonPath("$[0].disabled").value(false));
  }

  @Test
  public void should_GetPatientById() throws Exception {
    mockMvc.perform(get("/patients/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.superheroName").value("Super Man"))
        .andExpect(jsonPath("$.disabled").value(false));
  }

  @Test
  public void should_CreatePatient() throws Exception {
    final File jsonFile = new ClassPathResource("json/create-patient2.json").getFile();
    String patientToCreate2 = Files.readString(jsonFile.toPath());
    mockMvc.perform(post("/patients")
        .contentType(MediaType.APPLICATION_JSON)
        .content(patientToCreate2)).andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(2L))
        .andExpect(jsonPath("$.superheroName").value("Magical Fairy"))
        .andExpect(jsonPath("$.disabled").value(false));
  }

  @Test
  public void should_UpdatePatient() throws Exception {
    String name = "Updated Name";
    mockMvc.perform(put("/patients/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(name))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.superheroName").value("Updated Name"))
        .andExpect(jsonPath("$.disabled").value(false));
  }

  @Test
  public void should_DisablePatient() throws Exception {
    mockMvc.perform(put("/patients/1/disabled")
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.superheroName").value("Super Man"))
        .andExpect(jsonPath("$.disabled").value(true));
  }

  @Test
  public void should_UpdatePatientPassword() throws Exception {
    String password = "cat_cat_cat";
    mockMvc.perform(put("/patients/1/new-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(password))
        .andDo(print())
        .andExpect(status().isNoContent());
  }
}
