package com.Finzo.user_service.service.impl;

import com.Finzo.user_service.dto.CreateRequest;
import com.Finzo.user_service.model.User;
import com.Finzo.user_service.repository.UserRepository;
import com.Finzo.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


   private final UserRepository userRepository;

    @Override
    public User createUser(CreateRequest createRequest) {
        Optional<User> userOptional = userRepository.findByEmail(createRequest.getEmail());
        if(userOptional.isPresent()){
            throw new RuntimeException("User Already Exists With This Email id");
        }
        return userRepository.save(createRequest);

    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
