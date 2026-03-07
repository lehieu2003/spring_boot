package com.example.demo.service.imp;

import org.springframework.stereotype.Service;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import java.util.List;

@Service
public class UserServiceImp implements UserService{
  private final UserRepository userRepository;

  public UserServiceImp(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User create(User user) {
    return userRepository.save(user);
  }

  @Override
  public User getById(Long id) {
    return userRepository.findById(id).orElseThrow(null);
  }

  @Override
  public List<User> getAll() {
    return userRepository.findAll();
  }

  @Override
  public void delete(Long id) {
    userRepository.deleteById(id);
  }

  @Override
  public User update(Long id, User user) {
    User existingUser = userRepository.findById(id).orElseThrow(null);
    existingUser.setName(user.getName());
    existingUser.setEmail(user.getEmail());
    return userRepository.save(existingUser);
  }
}
