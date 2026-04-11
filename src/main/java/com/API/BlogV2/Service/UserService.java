package com.API.BlogV2.Service;

import com.API.BlogV2.DTO.UserDTO;
import com.API.BlogV2.DTO.UserMapper;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Autowired
    AuthenticationManager authenticationManager;


    @Autowired
    private  JWTService jwtService;



    // TO encrypt the password given by the users
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    @Autowired
    public UserService(UserRepository userRepository,UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public void registerUser(User u) {
        u.setPassword(bCryptPasswordEncoder.encode(u.getPassword()));
        userRepository.save(u);
    }

    public UserDTO getUserDetails(Long id) {
        // Use .orElseThrow for cleaner code
        return userRepository.findById(id)
                .map(userMapper::mapToDTO)
                .orElseThrow(() -> new RuntimeException("User ID " + id + " does not exist"));
    }


    // In the getUserDetails method, the findById returns an Optional<User>.
    // The .map() function on an Optional is very clever: if the user exists, it runs the mapper;
    // if the user is missing, it does nothing and allows the .orElseThrow() to trigger.

    public List<UserDTO> getAllUser() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::mapToDTO)
                .toList();
    }

    public String verifyUser(User u) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(u.getName(),u.getPassword()));

        if(auth.isAuthenticated()){
            return jwtService.generateToken(u.getName());
        }

        return "Fail";

    }
}
