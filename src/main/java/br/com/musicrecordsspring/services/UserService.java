package br.com.musicrecordsspring.services;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import br.com.musicrecordsspring.exceptions.InvalidCredentialsException;
import br.com.musicrecordsspring.models.Authenticable;
import br.com.musicrecordsspring.models.Login;
import br.com.musicrecordsspring.models.User;
import br.com.musicrecordsspring.repositories.UserRepository;
import br.com.musicrecordsspring.utils.Messages;

@Service
public class UserService {

  @Autowired
  private JwtService jwtService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserRepository userRepository;

  public User createUser(User user) {

    user.setPassword(passwordEncoder.encode(user.getPassword()));

    try {

      return userRepository.save(user);
    } catch (DataIntegrityViolationException e) {

      throw new DataIntegrityViolationException(
          Messages.getEmailAlreadyRegistered(user.getEmail()));
    }
  }

  public Authenticable login(Login login) {

    Optional<User> optionalUser = userRepository.findUserByEmail(login.getEmail());

    if (optionalUser.isEmpty()) {
      throw new InvalidCredentialsException(Messages.getUserNotFoundByEmail(login.getEmail()));
    }

    User user = optionalUser.get();

    if (!passwordEncoder.matches(login.getPassword(), user.getPassword())) {
      throw new InvalidCredentialsException(
          Messages.getPasswordDoesNotMatchWithEmail(login.getEmail()));
    }

    String token = jwtService.encode(user.getId().toString());

    return new Authenticable(token, user.getUsername(), user.getEmail());
  }
}
