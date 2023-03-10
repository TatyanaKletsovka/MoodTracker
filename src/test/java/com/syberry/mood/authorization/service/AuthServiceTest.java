package com.syberry.mood.authorization.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.cache.LoadingCache;
import com.syberry.mood.authorization.dto.LoginDto;
import com.syberry.mood.authorization.dto.LoginRequestDto;
import com.syberry.mood.authorization.dto.RestorePasswordDto;
import com.syberry.mood.authorization.entity.RefreshToken;
import com.syberry.mood.authorization.security.UserDetailsImpl;
import com.syberry.mood.authorization.service.impl.AuthServiceImpl;
import com.syberry.mood.authorization.util.SecurityUtils;
import com.syberry.mood.exception.EmailException;
import com.syberry.mood.exception.EntityNotFoundException;
import com.syberry.mood.exception.TokenRefreshException;
import com.syberry.mood.exception.ValidationException;
import com.syberry.mood.user.converter.UserConverter;
import com.syberry.mood.user.dto.RoleName;
import com.syberry.mood.user.dto.UserDto;
import com.syberry.mood.user.entity.Role;
import com.syberry.mood.user.entity.User;
import com.syberry.mood.user.repository.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthServiceTest {

  @InjectMocks
  private AuthServiceImpl authService;
  @Mock
  private AuthenticationManager authenticationManager;
  @Mock
  private UserRepository userRepository;
  @Mock
  private RefreshTokenService refreshTokenService;
  @Mock
  private EmailService emailService;
  @Mock
  private LoadingCache<String, String> passwordResetTokenCache;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private SecurityUtils securityUtils;
  @Mock
  private Authentication authentication;
  @Mock
  private UserConverter userConverter;
  @Mock
  private SecurityContext securityContext;
  private RestorePasswordDto restorePasswordDto = new RestorePasswordDto(
      "doc@gmail.com", "9b61291e-975e-41dd-992d-3dc0ab3a41b8", "Pass1234");

  @Test
  public void should_SuccessfullyLogin() {
    Role role = new Role(1L, RoleName.ADMIN);
    User user = User.builder()
        .id(1L)
        .username("doc@gmail.com")
        .password("AbSdA_21sSA")
        .role(role)
        .build();
    UserDto userDto = UserDto.builder()
        .id(1L)
        .username("doc@gmail.com")
        .build();
    UserDetailsImpl userDetails = UserDetailsImpl.create(user);
    RefreshToken refreshToken = RefreshToken.builder()
        .id(1L)
        .user(user)
        .token("refresh-token")
        .expiryDate(Instant.now().plus(1, ChronoUnit.DAYS))
        .build();
    ResponseCookie jwtCookie = ResponseCookie.from(
        "access-token", "access-token").build();
    ResponseCookie refreshJwtCookie = ResponseCookie.from(
        "refresh-token", "refresh-token").build();
    ResponseCookie accessCookie = ResponseCookie.from(
        "access-token", "access-token").build();
    LoginRequestDto loginRequestDto =
        new LoginRequestDto("doc@gmail.com", "AbSdA_21sSA");
    LoginDto loginDto = LoginDto.builder()
        .cookie(jwtCookie.toString())
        .refreshCookie(refreshJwtCookie.toString())
        .userDto(userDto)
        .build();

    when(authenticationManager.authenticate(any())).thenReturn(authentication);
    when(userRepository.findUserByUsernameAndDisabledFalseIfExists(any())).thenReturn(user);
    when(refreshTokenService.createRefreshToken(1L)).thenReturn(refreshToken);
    when(securityUtils.generateJwtCookie(userDetails)).thenReturn(accessCookie);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userConverter.convertToUserDto(any(User.class))).thenReturn(userDto);
    when(securityUtils.generateRefreshJwtCookie(any())).thenReturn(refreshJwtCookie);
    when(userRepository.findUserByIdIfExists(any())).thenReturn(user);
    assertThat(authService.login(loginRequestDto)).isEqualTo(loginDto);
  }

  @Test
  public void should_ThrowError_WhenLoginWithNoneExistingUsername() {
    when(authenticationManager.authenticate(any())).thenReturn(null);
    when(userRepository.findUserByUsernameAndDisabledFalseIfExists(any()))
        .thenThrow(EntityNotFoundException.class);
    assertThrows(EntityNotFoundException.class, () -> authService.login(new LoginRequestDto()));
  }

  @Test
  public void should_SuccessfullyRefreshToken() {
    String token = "refreshToken";
    when(securityUtils.getJwtRefreshFromCookies(any())).thenReturn(token);
    ResponseCookie jwtCookie = ResponseCookie.from("cookie", token).build();
    LoginDto loginDto = new LoginDto(jwtCookie.toString(), "refresh", null);
    when(refreshTokenService.refreshAccessToken(any())).thenReturn(loginDto);
    assertThat(authService.refreshToken(null)).isEqualTo(loginDto);
  }

  @Test
  public void should_ThrowError_When_RefreshingWithEmptyToken() {
    when(securityUtils.getJwtRefreshFromCookies(any())).thenReturn("");
    assertThrows(TokenRefreshException.class, () -> authService.refreshToken(null));
  }

  @Test
  public void should_ThrowError_When_RefreshingWithExpiredToken() {
    String token = "token";
    when(securityUtils.getJwtRefreshFromCookies(any())).thenReturn(token);
    when(refreshTokenService.refreshAccessToken(any()))
        .thenThrow(new TokenRefreshException("Session time was expired. Please log in again"));
    assertThrows(TokenRefreshException.class, () -> authService.refreshToken(null));
  }

  @Test
  public void should_SuccessfullyLogout() {
    SecurityContextHolder.setContext(securityContext);
    UserDetailsImpl userDetails =
        new UserDetailsImpl(
            1L, "doc@gmail.com", "AbSdA_21sSA",
            new SimpleGrantedAuthority(RoleName.ADMIN.name()));
    ResponseCookie refreshCookie = ResponseCookie.from("refresh-token", null).build();
    ResponseCookie accessCookie = ResponseCookie.from("access-token", null).build();
    LoginDto loginDto
        = new LoginDto("access-token=", "refresh-token=", null);

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(securityUtils.getCleanJwtCookie()).thenReturn(accessCookie);
    when(securityUtils.getCleanJwtRefreshCookie()).thenReturn(refreshCookie);
    assertThat(authService.logout()).isEqualTo(loginDto);
  }

  @Test
  void resetPasswordWhenUserExistsThenSendsEmail() {
    String email = "doc@gmail.com";
    User user = new User();
    user.setUsername(email);
    when(userRepository.findUserByUsernameAndDisabledFalseIfExists(email)).thenReturn(user);
    doNothing().when(emailService).constructResetTokenEmail(any(), any());
    doNothing().when(passwordResetTokenCache).put(eq(email), any());

    authService.resetPassword(email);

    verify(emailService).constructResetTokenEmail(any(), eq(email));
  }

  @Test
  void resetPasswordWhenUserIsNotFoundThenThrowsException() {
    when(userRepository.findUserByUsernameAndDisabledFalseIfExists(any()))
        .thenThrow(EntityNotFoundException.class);

    assertThrows(EntityNotFoundException.class, () -> authService.resetPassword(any()));
  }

  @Test
  void restorePasswordWhenTokenForEmailIsNotFoundInCacheThenThrowsException()
      throws ExecutionException {
    when(passwordResetTokenCache.get(any())).thenThrow(ExecutionException.class);

    assertThrows(EmailException.class, () -> authService.restorePassword(restorePasswordDto));
  }

  @Test
  void restorePasswordWhenTokenIsInvalidThenThrowsException() throws ExecutionException {
    when(passwordResetTokenCache.get(any())).thenReturn("");

    assertThrows(ValidationException.class, () -> authService.restorePassword(restorePasswordDto));
  }

  @Test
  void restorePasswordWhenTokenIsCorrectThenUpdatesPassword() throws ExecutionException {
    String email = restorePasswordDto.getEmail();
    String newPassword = restorePasswordDto.getNewPassword();
    User user = new User();
    user.setUsername(email);
    user.setPassword(newPassword + "old");
    when(passwordResetTokenCache.get(any())).thenReturn(restorePasswordDto.getToken());
    when(userRepository.findUserByUsernameAndDisabledFalseIfExists(email)).thenReturn(user);
    when(passwordEncoder.encode(any())).thenReturn(newPassword);

    authService.restorePassword(restorePasswordDto);

    assertThat(user.getPassword()).isEqualTo(newPassword);
  }
}
