package com.tutorial.service;

import com.tutorial.Enum.AuthorityEnum;
import com.tutorial.model.dto.RegistrationRequest;
import com.tutorial.model.entity.Authority;
import com.tutorial.model.entity.User;
import com.tutorial.repository.AuthorityRepository;
import com.tutorial.repository.UserRepository;

import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

  private final UserRepository userRepository;
  private final AuthorityRepository authorityRepository;
  private final PasswordEncoder passwordEncoder;

  public RegistrationService(UserRepository userRepository, AuthorityRepository authorityRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.authorityRepository = authorityRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public RegistrationResult register(RegistrationRequest req) {
    if (req.getUsername() == null || req.getPassword() == null) {
      return RegistrationResult.failure("username and password are required");
    }
    if (userRepository.findByUsername(req.getUsername()).isPresent()) {
      return RegistrationResult.failure("username already exists");
    }

    User u = new User();
    u.setUsername(req.getUsername());
    u.setPassword(passwordEncoder.encode(req.getPassword()));

    User saved = userRepository.save(u);

    Authority auth = new Authority();
    auth.setUsername(saved.getUsername());
    auth.setAuthority(AuthorityEnum.USER);
    authorityRepository.save(auth);

    return RegistrationResult.success(new Payload(saved.getUsername(), saved.getApiKey()));
  }

  @Data
  public static class Payload {
    private final String username;
    private final String apiKey;
  }

  @Data
  public static class RegistrationResult {
    private final boolean success;
    private final String message;
    private final Payload payload;

    public static RegistrationResult success(Payload p) {
      return new RegistrationResult(true, null, p);
    }

    public static RegistrationResult failure(String msg) {
      return new RegistrationResult(false, msg, null);
    }
  }
}
