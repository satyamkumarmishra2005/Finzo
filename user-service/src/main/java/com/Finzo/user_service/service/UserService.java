package com.Finzo.user_service.service;

import com.Finzo.user_service.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService{

    User createUser(User user);

    User getUserById(Long id);

    List<User> getAllUsers();

}
