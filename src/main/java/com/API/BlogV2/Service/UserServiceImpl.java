package com.API.BlogV2.Service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import com.API.BlogV2.Exception.ResourceNotFoundException;
import com.API.BlogV2.Exception.BlogAPIException;
import com.API.BlogV2.DTO.LoginDTO;
import com.API.BlogV2.DTO.UserDTO;
import com.API.BlogV2.DTO.UserMapper;
import com.API.BlogV2.DTO.UserSignupDTO;
import com.API.BlogV2.Entity.Role;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final ImageKitService imageKitService; // inject this


    private final AuthenticationManager authenticationManager;

    private final JWTService jwtService;



    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    public void registerUser(UserSignupDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BlogAPIException(org.springframework.http.HttpStatus.BAD_REQUEST, "Email already exists");
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

    @Transactional()
    public UserDTO getUserDetails(Long id) {
        // Use .orElseThrow for cleaner code
        return userRepository.findById(id)
                .map(userMapper::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }


    // In the getUserDetails method, the findById returns an Optional<User>.
    // The .map() function on an Optional is very clever: if the user exists, it runs the mapper;
    // if the user is missing, it does nothing and allows the .orElseThrow() to trigger.
    @Transactional()
    public List<UserDTO> getAllUser() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::mapToDTO)
                .toList();
    }

    public String verifyUser(LoginDTO u) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(u.getName(),u.getPassword()));

        User user = userRepository.findByEmail(u.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", u.getEmail()));

        if(auth.isAuthenticated()){
            return jwtService.generateToken(u.getName(),user.getId());
        }

        return "Fail";
    }


    @Transactional
    public void deleteUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.deleteById(id);
    }


    @Transactional
    public  void updateUser(Long id,UserDTO userDTO){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setName(userDTO.getName());
        user.setOccupation(userDTO.getOccupation());
        user.setBio(userDTO.getBio());
        userRepository.save(user);
    }


    // In your existing UserService.java — ADD this method



    /**
     * Called after frontend uploads the image and gets back a URL from ImageKit.
     * Frontend sends that URL here — we just save it.
     */
    public UserDTO updateProfilePic(Long userId, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setProfilePicUrl(imageUrl);
        return userMapper.mapToDTO(userRepository.save(user));
    }

    // Optional: get a resized version of the profile pic
    @Transactional()
    public String getResizedProfilePic(Long userId, int width, int height) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return imageKitService.getTransformedUrl(user.getProfilePicUrl(), width, height);
    }
}
