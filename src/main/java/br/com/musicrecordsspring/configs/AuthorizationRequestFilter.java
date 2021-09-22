package br.com.musicrecordsspring.configs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.musicrecordsspring.models.User;
import br.com.musicrecordsspring.repositories.UserRepository;
import br.com.musicrecordsspring.services.JwtService;

@Component
public class AuthorizationRequestFilter extends OncePerRequestFilter {

  @Autowired
  private JwtService jwtService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {

    if (isNotNeedAuthorization(request)) {
      chain.doFilter(request, response);
      return;
    }

    String authorizationHeader = request.getHeader("Authorization");

    if (authorizationHeader == null) {
      sendUnauthorizedResponse(response, "Header Authorization not present!");
      return;
    }

    String[] schemeAndToken = authorizationHeader.split(" ");
    String scheme = schemeAndToken[0];

    if (!"Bearer".equals(scheme)) {
      sendUnauthorizedResponse(response, "No Bearer HTTP authentication scheme!");
      return;
    }

    if (schemeAndToken.length == 1) {
      sendUnauthorizedResponse(response, "No token provided!");
      return;
    }

    String token = schemeAndToken[1];

    String payload = jwtService.decode(token);

    Long userId = Long.valueOf(payload);

    User user = userRepository.findById(userId).get();

    Authentication authentication =
        new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());

    SecurityContextHolder.getContext().setAuthentication(authentication);

    chain.doFilter(request, response);
  }

  private boolean isNotNeedAuthorization(HttpServletRequest request) {
    return "POST".equals(request.getMethod())
        && ("/login".equals(request.getPathInfo()) || "/users".equals(request.getPathInfo()));
  }

  private void sendUnauthorizedResponse(HttpServletResponse response, String message)
      throws IOException {

    Map<String, String> messageContent = new HashMap<>();
    messageContent.put("message", "Header Authorization not present!");

    PrintWriter out = response.getWriter();
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json");
    out.print(objectMapper.writeValueAsString(messageContent));
    out.flush();
  }

}