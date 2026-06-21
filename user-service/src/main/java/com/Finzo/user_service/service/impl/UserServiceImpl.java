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
        User user = new User();
        user.setEmail(createRequest.getEmail());
        user.setPassword(createRequest.getPassword());
        user.setRole(createRequest.getRole());
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public void deleteUserById(Long id) {
        if(!userRepository.existsById(id)){
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    @Override
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }
    
    @Override
    public User updateUser(Long id, CreateRequest updateRequest) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        if(updateRequest.getEmail() != null){
            existingUser.setEmail(updateRequest.getEmail());
        }
        if(updateRequest.getPassword() != null){
            existingUser.setPassword(updateRequest.getPassword());
        }
        if(updateRequest.getRole() != null){
            existingUser.setRole(updateRequest.getRole());
        }
        
        return userRepository.save(existingUser);
    }
}
