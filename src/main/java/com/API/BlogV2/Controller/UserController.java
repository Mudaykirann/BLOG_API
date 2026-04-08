package com.API.BlogV2.Controller;

import com.API.BlogV2.DTO.UserDTO;
import com.API.BlogV2.Exception.ApiResponse;
import com.API.BlogV2.Service.UserService;
import jakarta.validation.Valid;
import org.apache.catalina.User;
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
    public ApiResponse<List<UserDTO>> getAllUsers() throws IllegalAccessException{
        List<UserDTO> users =  userService.getAllUser();
        return new ApiResponse<>("success","Users Fetched",users);
    }

    @GetMapping(path = "/{id}")
    public ApiResponse<UserDTO> getUserDetails(@PathVariable("id") Long id) { // Change void to UserDTO
        UserDTO user =  userService.getUserDetails(id); // Added 'return'
        return new ApiResponse<>(
                "success",
                "User fetched successfully",
                user
        );
    }


    @PostMapping
    public ApiResponse<Void> addNewUser(@Valid @RequestBody UserDTO u) {
        userService.addNewUser(u);
        return new ApiResponse<>("success", "User created successfully", null);
    }

}
