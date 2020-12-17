package br.com.musicrecords.security;

import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import br.com.musicrecords.model.User;
import br.com.musicrecords.repository.UserRepository;

@Service
public class UserDetailService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    Optional<User> maybeUser = this.userRepository.findByEmail(email);

    if (maybeUser.isPresent()) {
      User user = maybeUser.get();
      return new org.springframework.security.core.userdetails.User(user.getEmail(),
          user.getPassword(), new ArrayList<>());
    } else {
      throw new UsernameNotFoundException("User not found with email: " + email);
    }
  }

}
