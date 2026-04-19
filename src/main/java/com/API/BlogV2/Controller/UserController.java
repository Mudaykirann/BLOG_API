package com.API.BlogV2.Controller;

import com.API.BlogV2.DTO.UserDTO;
import com.API.BlogV2.Entity.Role;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Exception.ApiResponse;
import com.API.BlogV2.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "api/")
public class UserController {

    private  final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping(path = "/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserDTO>> getAllUsers() throws IllegalAccessException{
        List<UserDTO> users =  userService.getAllUser();
        return new ApiResponse<>("success","Users Fetched",users);
    }

    @GetMapping(path = "/users/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<UserDTO> getUserDetails(@PathVariable("id") Long id) { // Change void to UserDTO
        UserDTO user =  userService.getUserDetails(id); // Added 'return'
        return new ApiResponse<>(
                "success",
                "User fetched successfully",
                user
        );
    }

    @PostMapping(path="auth/login")
    public String login(@RequestBody User u){
        return userService.verifyUser(u);
    }


    @PostMapping(path = "auth/register")
    public ApiResponse<Void> registerUser(@Valid @RequestBody User u) {
        // default role is USER if none is provided:
        if (u.getRole() == null) {
            u.setRole(Role.USER);
        }
        userService.registerUser(u);
        return new ApiResponse<>("success", "User Registered successfully", null);
    }

    @DeleteMapping(path = "/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.principal.id)")
    public ApiResponse<Void> deleteUser(@PathVariable("userId") Long id){
        userService.deleteUser(id);
        return new ApiResponse<>("success", "User Deleted successfully", null);
    }

    @PutMapping(path = "/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.principal.id)")
    public ApiResponse<Void> updateUser(@PathVariable("userId") Long id , @RequestBody UserDTO userDTO) throws AccessDeniedException {
        userService.updateUser(id,userDTO);
        return new ApiResponse<>("Success","User Updated Successfully",null);
    }
}
