package com.syberry.mood.integration;

import static org.hamcrest.CoreMatchers.is;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/**
 * This class is responsible for integration testing of the employee controller.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MoodApplication.class})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
public class EmployeeIntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private RoleRepository roleRepository;
  private String employeeToCreate;

  @BeforeEach
  void setUp() throws Exception {
    roleRepository.save(new Role(1L, RoleName.ADMIN));
    roleRepository.save(new Role(2L, RoleName.MODERATOR));
    roleRepository.save(new Role(3L, RoleName.USER));

    final File jsonFile = new ClassPathResource("json/create-employee.json").getFile();
    employeeToCreate = Files.readString(jsonFile.toPath());
    mockMvc.perform(post("/employees")
        .contentType(MediaType.APPLICATION_JSON)
        .content(employeeToCreate));
  }

  @Test
  void findAllEmployeesWhenCalledThenReturnJsonArray() throws Exception {
    mockMvc.perform(get("/employees"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].email", is("test1@gmail.com")))
        .andExpect(jsonPath("$[0].firstName", is("Forrest")))
        .andExpect(jsonPath("$[0].lastName", is("Gump")))
        .andExpect(jsonPath("$[0].roleName", is("ADMIN")))
        .andExpect(jsonPath("$[0].disabled", is(false)));
  }

  @Test
  void findEmployeeByIdWhenCalledThenReturnJsonWithEmployeeDto() throws Exception {
    mockMvc.perform(get("/employees/1").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.email", is("test1@gmail.com")))
        .andExpect(jsonPath("$.firstName", is("Forrest")))
        .andExpect(jsonPath("$.lastName", is("Gump")))
        .andExpect(jsonPath("$.roleName", is("ADMIN")))
        .andExpect(jsonPath("$.disabled", is(false)));
  }

  @Test
  void createEmployeeWhenCalledThenReturnJsonWithEmployeeDto() throws Exception {
    final File jsonFile = new ClassPathResource("json/create-employee2.json").getFile();
    String employeeToCreate = Files.readString(jsonFile.toPath());

    mockMvc.perform(post("/employees").contentType(MediaType.APPLICATION_JSON)
            .content(employeeToCreate))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(2)))
        .andExpect(jsonPath("$.email", is("test2@gmail.com")))
        .andExpect(jsonPath("$.firstName", is("John")))
        .andExpect(jsonPath("$.lastName", is("Doe")))
        .andExpect(jsonPath("$.roleName", is("MODERATOR")))
        .andExpect(jsonPath("$.disabled", is(false)));
  }

  @Test
  void updateEmployeeWhenCalledThenReturnJsonWithEmployeeDto() throws Exception {
    final File jsonFile = new ClassPathResource("json/update-employee.json").getFile();
    String employeeToUpdate = Files.readString(jsonFile.toPath());

    mockMvc.perform(put("/employees/1").contentType(MediaType.APPLICATION_JSON)
            .content(employeeToUpdate))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.email", is("test3@gmail.com")))
        .andExpect(jsonPath("$.firstName", is("Tom")))
        .andExpect(jsonPath("$.lastName", is("Gump")))
        .andExpect(jsonPath("$.roleName", is("ADMIN")))
        .andExpect(jsonPath("$.disabled", is(false)));
  }

  @Test
  void disableEmployeeWhenCalledThenReturnJsonWithEmployeeDto() throws Exception {
    File jsonFile = new ClassPathResource("json/create-employee3.json").getFile();
    employeeToCreate = Files.readString(jsonFile.toPath());
    mockMvc.perform(post("/employees")
        .contentType(MediaType.APPLICATION_JSON)
        .content(employeeToCreate));

    mockMvc.perform(put("/employees/2/disabled").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.disabled").value(true));
  }
}
