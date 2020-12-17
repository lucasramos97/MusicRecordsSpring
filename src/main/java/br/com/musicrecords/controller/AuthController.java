package br.com.musicrecords.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.musicrecords.model.MessageResponse;
import br.com.musicrecords.model.User;
import br.com.musicrecords.repository.UserRepository;
import br.com.musicrecords.security.AuthenticationService;
import br.com.musicrecords.security.JwtTokenService;
import br.com.musicrecords.security.UserDetailService;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserDetailService userDetailService;

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private JwtTokenService jwtTokenUtil;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @PostMapping("/login")
  public ResponseEntity<MessageResponse> login(@RequestBody User user) {
    try {
      this.authenticationService.authenticate(user);
      UserDetails userDetails = this.userDetailService.loadUserByUsername(user.getEmail());
      String token = jwtTokenUtil.generateToken(userDetails);
      return new ResponseEntity<>(new MessageResponse(token), HttpStatus.OK);
    } catch (DisabledException e) {
      return new ResponseEntity<>(new MessageResponse("Disabled user!"), HttpStatus.UNAUTHORIZED);
    } catch (BadCredentialsException e) {
      return new ResponseEntity<>(new MessageResponse("Invalid E-Mail or Password!"),
          HttpStatus.UNAUTHORIZED);
    }
  }

  @PostMapping("/create")
  public ResponseEntity<MessageResponse> create(@Valid @RequestBody User user) {
    try {
      user.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));
      this.userRepository.save(user);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (DataIntegrityViolationException e) {
      String message = String.format("%s has already been registered!", user.getEmail());
      return new ResponseEntity<>(new MessageResponse(message), HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/test")
  public ResponseEntity<MessageResponse> test() {
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
