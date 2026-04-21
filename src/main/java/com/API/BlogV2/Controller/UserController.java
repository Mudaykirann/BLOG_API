package com.API.BlogV2.Controller;

import com.API.BlogV2.DTO.UserDTO;
import com.API.BlogV2.Entity.Role;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Exception.UnifiedResponse;
import com.API.BlogV2.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UnifiedResponse<List<UserDTO>>> getAllUsers() throws IllegalAccessException {
        List<UserDTO> users = userService.getAllUser();
        return ResponseEntity.ok(UnifiedResponse.ok( "Users Fetched", users));
    }

    @GetMapping(path = "/users/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UnifiedResponse<UserDTO>> getUserDetails(@PathVariable("id") Long id) {
        UserDTO user = userService.getUserDetails(id);
        return ResponseEntity.ok(UnifiedResponse.ok( "User fetched successfully", user));
    }

    @PostMapping(path = "auth/login")
    public ResponseEntity<String> login(@RequestBody User u) {
        String token = userService.verifyUser(u);
        return ResponseEntity.ok(token);
    }

    @PostMapping(path = "auth/register")
    public ResponseEntity<UnifiedResponse<Void>> registerUser(@Valid @RequestBody User u) {
        if (u.getRole() == null) {
            u.setRole(Role.USER);
        }
        userService.registerUser(u);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UnifiedResponse.ok( "User Registered successfully", null));
    }

    @DeleteMapping(path = "/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == authentication.principal.id)")
    public ResponseEntity<UnifiedResponse<Void>> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(UnifiedResponse.ok( "User Deleted successfully", null));
    }

    @PutMapping(path = "/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == authentication.principal.id)")
    public ResponseEntity<UnifiedResponse<Void>> updateUser(@PathVariable("id") Long id, @RequestBody UserDTO userDTO) throws AccessDeniedException {
        userService.updateUser(id, userDTO);
        return ResponseEntity.ok(UnifiedResponse.ok( "User Updated Successfully", null));
    }
}
