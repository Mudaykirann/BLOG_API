package com.API.BlogV2.Service;

import com.API.BlogV2.DTO.LoginDTO;
import com.API.BlogV2.DTO.UserDTO;
import com.API.BlogV2.DTO.UserMapper;
import com.API.BlogV2.DTO.UserSignupDTO;
import com.API.BlogV2.Entity.Role;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
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

    public void registerUser(UserSignupDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setBio(dto.getBio());
        user.setOccupation(dto.getOccupation());
        user.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);
        user.setDisplayName(dto.getDisplayName());
        userRepository.save(user);
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

    public String verifyUser(LoginDTO u) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(u.getName(),u.getPassword()));

        User user = userRepository.findByEmail(u.getEmail())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if(auth.isAuthenticated()){
            return jwtService.generateToken(u.getName(),user.getId());
        }

        return "Fail";
    }

    public void deleteUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        userRepository.deleteById(id);
    }

    public  void updateUser(Long id,UserDTO userDTO){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setOccupation(userDTO.getOccupation());
        user.setBio(userDTO.getBio());
        userRepository.save(user);
    }
}
