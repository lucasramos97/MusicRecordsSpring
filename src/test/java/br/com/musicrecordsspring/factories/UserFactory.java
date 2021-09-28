package br.com.musicrecordsspring.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import br.com.musicrecordsspring.models.User;
import br.com.musicrecordsspring.repositories.UserRepository;

@Component
public class UserFactory {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserRepository userRepository;

  public User create(String complement) {

    User user = new User();
    user.setUsername(String.format("test%s", complement));
    user.setEmail(String.format("test%s@email.com", complement));
    user.setPassword(passwordEncoder.encode("123"));

    return userRepository.save(user);
  }
}
