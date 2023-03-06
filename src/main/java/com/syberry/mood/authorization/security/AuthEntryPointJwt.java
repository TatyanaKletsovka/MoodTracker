package com.syberry.mood.authorization.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Entry point for unauthorized users.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  /**
   * Generates message in case when user doesn't have permission to URL.
   *
   * @param request http request
   * @param response http response
   * @param authException authentication exception
   * @throws IOException input or output exception
   */
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    log.error("Unauthorized error: {} ", authException.getMessage());

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    Map<String, String> responseBodyMap = new HashMap<>();
    responseBodyMap.put("status", String.valueOf(HttpServletResponse.SC_UNAUTHORIZED));
    responseBodyMap.put("error", "Unauthorized");
    responseBodyMap.put("message", authException.getMessage());
    responseBodyMap.put("path", request.getServletPath());

    objectMapper.writeValue(response.getOutputStream(), responseBodyMap);
  }
}
