package com.Finzo.user_service.service;

import com.Finzo.user_service.dto.CreateRequest;
import com.Finzo.user_service.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService{

    User createUser(CreateRequest createRequest);

    User getUserById(Long id);

    List<User> getAllUsers();
    
    void deleteUserById(Long id);
    
    void deleteAllUsers();
    
    User updateUser(Long id, CreateRequest updateRequest);

}
