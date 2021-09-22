package br.com.musicrecordsspring.controllers;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import br.com.musicrecordsspring.models.Authenticable;
import br.com.musicrecordsspring.models.Login;
import br.com.musicrecordsspring.models.User;
import br.com.musicrecordsspring.services.UserService;

@CrossOrigin(origins = "http://localhost:3000/")
@RestController
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/users")
  public ResponseEntity<User> create(@Valid @RequestBody User user) {
    return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public Authenticable login(@Valid @RequestBody Login login) {
    return userService.login(login);
  }

}
