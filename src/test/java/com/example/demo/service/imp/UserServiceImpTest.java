package com.example.demo.service.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.Role;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserServiceImp userService;

  @Test
  void createShouldHashPasswordAndReturnResponse() {
    UserCreateRequest request = new UserCreateRequest("John", "john@example.com", "plainPass", Role.USER, true);
    User entity = new User();
    entity.setEmail("john@example.com");
    User saved = new User();
    saved.setId(1L);
    saved.setEmail("john@example.com");
    UserResponse response = UserResponse.builder().id(1L).email("john@example.com").build();

    when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
    when(userMapper.toEntity(request)).thenReturn(entity);
    when(passwordEncoder.encode("plainPass")).thenReturn("hashed");
    when(userRepository.save(entity)).thenReturn(saved);
    when(userMapper.toResponse(saved)).thenReturn(response);

    UserResponse result = userService.create(request);

    assertEquals(1L, result.getId());
    verify(passwordEncoder).encode("plainPass");
    assertEquals("hashed", entity.getPassword());
  }

  @Test
  void createShouldThrowConflictWhenEmailAlreadyExists() {
    UserCreateRequest request = new UserCreateRequest("John", "john@example.com", "plainPass", Role.USER, true);
    when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> userService.create(request));
  }

  @Test
  void createShouldTranslateDataIntegrityViolationToConflict() {
    UserCreateRequest request = new UserCreateRequest("John", "john@example.com", "plainPass", Role.USER, true);
    User entity = new User();

    when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
    when(userMapper.toEntity(request)).thenReturn(entity);
    when(passwordEncoder.encode("plainPass")).thenReturn("hashed");
    when(userRepository.save(entity)).thenThrow(new DataIntegrityViolationException("unique violation"));

    assertThrows(DuplicateResourceException.class, () -> userService.create(request));
  }

  @Test
  void updateShouldThrowConflictWhenEmailBelongsToAnotherUser() {
    User existing = User.builder().id(10L).email("old@example.com").build();
    UserUpdateRequest request = new UserUpdateRequest(null, "new@example.com", null, null, null);

    when(userRepository.findById(10L)).thenReturn(java.util.Optional.of(existing));
    when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> userService.update(10L, request));
  }

  @Test
  void updateShouldThrowNotFoundWhenMissingUser() {
    when(userRepository.findById(100L)).thenReturn(java.util.Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> userService.update(100L, new UserUpdateRequest()));
  }

  @Test
  void updateShouldEncodePasswordWhenProvided() {
    User existing = User.builder().id(10L).email("old@example.com").password("old").role(Role.USER).enabled(true).build();
    UserUpdateRequest request = new UserUpdateRequest(null, null, "newPassword", null, null);
    UserResponse response = UserResponse.builder().id(10L).email("old@example.com").build();

    when(userRepository.findById(10L)).thenReturn(java.util.Optional.of(existing));
    when(passwordEncoder.encode("newPassword")).thenReturn("hashedNew");
    when(userRepository.save(any(User.class))).thenReturn(existing);
    when(userMapper.toResponse(existing)).thenReturn(response);

    userService.update(10L, request);

    assertEquals("hashedNew", existing.getPassword());
  }
}
