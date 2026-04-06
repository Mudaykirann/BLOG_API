package com.API.BlogV2.Service;

import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addNewUser(User u){
        userRepository.save(u);
    }

    public List<User> getAllUser(){
        return userRepository.findAll();
    }


}
