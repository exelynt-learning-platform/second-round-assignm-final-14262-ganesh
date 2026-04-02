package com.optshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.optshop.entity.User;
import com.optshop.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock private UserRepository repo;
    @InjectMocks private UserController controller;

    @Test
    void testGetAllUsers() {
        when(repo.findAll()).thenReturn(List.of(new User()));
        assertEquals(HttpStatus.OK, controller.getAllUsers().getStatusCode());
    }

    @Test
    void testGetUserById() {
        when(repo.findById(1L)).thenReturn(Optional.of(new User()));
        assertEquals(HttpStatus.OK, controller.getUserById(1L).getStatusCode());
    }

    @Test
    void testDeleteUser() {
        assertEquals(HttpStatus.NO_CONTENT, controller.deleteUser(1L).getStatusCode());
    }
}
