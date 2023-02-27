package com.syberry.mood.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.syberry.mood.MoodApplication;
import com.syberry.mood.user.dto.RoleName;
import com.syberry.mood.user.entity.Role;
import com.syberry.mood.user.repository.RoleRepository;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MoodApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@WithMockUser(username = "Doc", roles = "ADMIN")
public class AuthIntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private RoleRepository roleRepository;
  private String loginRequestDto;
  private Cookie[] cookies;

  @BeforeEach
  public void setUp() throws Exception {
    roleRepository.save(new Role(1L, RoleName.ADMIN));
    roleRepository.save(new Role(2L, RoleName.MODERATOR));
    roleRepository.save(new Role(3L, RoleName.USER));

    final File createPatientFile =
        new ClassPathResource("json/create-patient.json").getFile();
    String patientToCreate = Files.readString(createPatientFile.toPath());
    mockMvc.perform(post("/patients")
        .contentType(MediaType.APPLICATION_JSON)
        .content(patientToCreate));

    final File loginRequestDtoFile =
        new ClassPathResource("json/login-request-dto.json").getFile();
    loginRequestDto = Files.readString(loginRequestDtoFile.toPath());

    MvcResult result = mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(loginRequestDto)).andReturn();
    cookies = result.getResponse().getCookies();
  }

  @Test
  public void should_Login() throws Exception {
    mockMvc.perform(post("/auth/logout")
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(cookies));
    mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(loginRequestDto))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(cookie().value("accessToken", Matchers.not("")))
        .andExpect(cookie().value("refreshToken", Matchers.not("")));
  }

  @Test
  public void should_Logout() throws Exception {
    mockMvc.perform(post("/auth/logout")
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(cookies))
        .andDo(print())
        .andExpect(cookie().value("accessToken", ""))
        .andExpect(cookie().value("refreshToken", ""));
  }

  @Test
  public void should_RefreshToken() throws Exception {
    Map<String, String> cookieMap = Arrays.stream(cookies)
        .collect(Collectors.toMap(Cookie::getName, Cookie::getValue));

    mockMvc.perform(post("/auth/refresh")
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(cookies))
        .andDo(print())
        .andExpect(cookie().value("accessToken",
            Matchers.containsString(cookieMap.get("accessToken"))))
        .andExpect(cookie().value("refreshToken",
            Matchers.not(Matchers.containsString(cookieMap.get("refreshToken")))));
  }
}
