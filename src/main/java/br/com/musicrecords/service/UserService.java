package br.com.musicrecords.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import br.com.musicrecords.model.User;
import br.com.musicrecords.repository.UserRepository;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AuthenticationService authenticationService;

  public User save(User user) {
    return this.userRepository.save(user);
  }

  public User getUserIfExistsByAuthenticationUserName() {
    String authenticationUserName = this.authenticationService.getAuthenticationUserName();
    Optional<User> maybeUser = this.userRepository.findByEmail(authenticationUserName);
    if (!maybeUser.isPresent()) {
      String message = String.format("User not found by 'E-Mail' $s!", authenticationUserName);
      throw new UsernameNotFoundException(message);
    }
    return maybeUser.get();
  }

  public User getUserIfExistsByEmail(String email) {
    Optional<User> maybeUser = this.userRepository.findByEmail(email);
    if (!maybeUser.isPresent()) {
      String message = String.format("User not found by 'E-Mail' $s!", email);
      throw new UsernameNotFoundException(message);
    }
    return maybeUser.get();
  }

}
