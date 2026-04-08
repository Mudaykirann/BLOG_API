package com.API.BlogV2.Controller;

import com.API.BlogV2.DTO.UserDTO;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "api/v1/users")
public class UserController {

    private  final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public List<UserDTO> getAllUsers() throws IllegalAccessException{
        return userService.getAllUser();
    }

    @GetMapping(path = "/{id}")
    public UserDTO getUserDetails(@PathVariable("id") Long id) { // Change void to UserDTO
        return userService.getUserDetails(id); // Added 'return'
    }


    @PostMapping
    public void addNewUser(@Valid @RequestBody UserDTO u) {
        userService.addNewUser(u);
    }

}
