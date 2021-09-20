package br.com.musicrecordsspring.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import br.com.musicrecordsspring.models.User;
import br.com.musicrecordsspring.repositories.UserRepository;

@Component
public class UserFactory {

  @Autowired
  private UserRepository userRepository;

  public User create() {

    User user = new User();
    user.setUsername("test");
    user.setEmail("test@email.com");
    user.setPassword("123");

    return userRepository.save(user);
  }

}
