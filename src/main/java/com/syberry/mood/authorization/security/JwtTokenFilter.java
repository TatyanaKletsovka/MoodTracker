package com.syberry.mood.authorization.security;

import com.syberry.mood.authorization.util.SecurityUtils;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Component for filtering users requests.
 */
@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

  private UserDetailsService userDetailsService;
  private SecurityUtils securityUtils;

  /**
   * Injects UserDetailsService bean.
   *
   * @param userDetailsService service for managing userDetails bean
   */
  @Autowired
  public void setUserDetailsService(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  /**
   * Injects SecurityUtils bean.
   *
   * @param securityUtils security utils bean.
   */
  @Autowired
  public void setSecurityUtils(SecurityUtils securityUtils) {
    this.securityUtils = securityUtils;
  }

  /**
   * Analyzes users requests, puts users details into Security Context.
   *
   * @param request http request
   * @param response http response
   * @param filterChain chain of filter
   * @throws ServletException servlet exception
   * @throws IOException input and output exception
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String jwtFromCookies = securityUtils.getJwtFromCookies(request);
    if (jwtFromCookies != null && securityUtils.validateJwtToken(jwtFromCookies)) {
      String username = securityUtils.getUsernameFromJwtToken(jwtFromCookies);
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userDetails, null,
              userDetails.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
  }
}
