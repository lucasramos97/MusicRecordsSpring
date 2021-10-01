package br.com.musicrecordsspring.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import br.com.musicrecordsspring.models.User;
import br.com.musicrecordsspring.services.UserService;

@Component
public class UserFactory {

  @Autowired
  private UserService userService;

  public User create(String complement) {

    User user = new User();
    user.setUsername(String.format("user%s", complement));
    user.setEmail(String.format("user%s@email.com", complement));
    user.setPassword("123");

    return userService.createUser(user);
  }
}
