package br.com.musicrecords.service;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import br.com.musicrecords.model.User;

@Service
public class UserDetailService implements UserDetailsService {

  @Autowired
  private UserService userService;

  @Override
  public UserDetails loadUserByUsername(String email) {
    User user = this.userService.getUserIfExistsByEmail(email);
    return new org.springframework.security.core.userdetails.User(user.getEmail(),
        user.getPassword(), new ArrayList<>());
  }

}
