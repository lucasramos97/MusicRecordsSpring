package br.com.musicrecords.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import br.com.musicrecords.model.User;

@Service
public class AuthService {

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private UserDetailService userDetailService;

  @Autowired
  private JwtTokenService jwtTokenService;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private UserService userService;

  public String login(User user) {
    try {
      this.authenticationService.authenticate(user);
      UserDetails userDetails = this.userDetailService.loadUserByUsername(user.getEmail());
      return jwtTokenService.generateToken(userDetails);
    } catch (DisabledException e) {
      throw new DisabledException("Disabled user!", e);
    } catch (BadCredentialsException e) {
      throw new BadCredentialsException("Invalid E-Mail or Password!", e);
    }
  }

  public void create(User user) {
    try {
      user.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));
      this.userService.save(user);
    } catch (DataIntegrityViolationException e) {
      String message = String.format("%s has already been registered!", user.getEmail());
      throw new DataIntegrityViolationException(message);
    }
  }

}
