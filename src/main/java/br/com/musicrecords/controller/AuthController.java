package br.com.musicrecords.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.musicrecords.model.MessageResponse;
import br.com.musicrecords.model.User;
import br.com.musicrecords.service.AuthService;
import io.swagger.annotations.Api;

@Api(tags = "Auth")
@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<MessageResponse> login(@RequestBody User user) {
    try {
      MessageResponse messageResponse = this.authService.login(user);
      return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }
  }

  @PostMapping("/create")
  public ResponseEntity<MessageResponse> create(@Valid @RequestBody User user) {
    try {
      this.authService.create(user);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/test")
  public ResponseEntity<MessageResponse> test() {
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
