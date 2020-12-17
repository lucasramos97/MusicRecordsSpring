package br.com.musicrecords.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import br.com.musicrecords.security.AuthenticationService;
import br.com.musicrecords.security.JwtTokenService;
import br.com.musicrecords.security.UserDetailService;

@Component
public class AuthenticationRequestFilter extends OncePerRequestFilter {

  @Autowired
  private UserDetailService userDetailService;

  @Autowired
  private JwtTokenService jwtTokenUtil;

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  @Qualifier("handlerExceptionResolver")
  private HandlerExceptionResolver handlerExceptionResolver;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String requestHeader = request.getHeader("Authorization");

    boolean isNotError = true;
    String email = null;
    String token = null;

    if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
      token = requestHeader.substring(7);
      try {
        email = jwtTokenUtil.getUsernameFromToken(token);
      } catch (RuntimeException e) {
        isNotError = false;
        this.handlerExceptionResolver.resolveException(request, response, null, e);
      }
    }

    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

      UserDetails userDetails = this.userDetailService.loadUserByUsername(email);

      if (jwtTokenUtil.validateToken(token, userDetails)) {

        Authentication authentication =
            this.authenticationService.getAuthentication(userDetails, request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    if (isNotError) {
      filterChain.doFilter(request, response);
    }

  }

}
