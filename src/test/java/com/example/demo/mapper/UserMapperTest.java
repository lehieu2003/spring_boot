package com.example.demo.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.Role;

class UserMapperTest {

  private final UserMapper mapper = new UserMapper();

  @Test
  void toEntityShouldSetDefaultsWhenRoleAndEnabledAreNull() {
    UserCreateRequest request = new UserCreateRequest("Alice", "alice@example.com", "secret123", null, null);

    User user = mapper.toEntity(request);

    assertEquals("Alice", user.getName());
    assertEquals("alice@example.com", user.getEmail());
    assertEquals(Role.USER, user.getRole());
    assertTrue(user.isEnabled());
  }

  @Test
  void updateEntityShouldOnlyApplyNonNullFields() {
    User user = User.builder()
        .name("Old")
        .email("old@example.com")
        .role(Role.USER)
        .enabled(true)
        .build();

    UserUpdateRequest request = new UserUpdateRequest(null, "new@example.com", null, Role.ADMIN, null);

    mapper.updateEntityFromRequest(request, user);

    assertEquals("Old", user.getName());
    assertEquals("new@example.com", user.getEmail());
    assertEquals(Role.ADMIN, user.getRole());
    assertTrue(user.isEnabled());
  }
}
