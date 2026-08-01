package com.API.BlogV2.Service;

import com.API.BlogV2.DTO.LoginDTO;
import com.API.BlogV2.DTO.UserDTO;
import com.API.BlogV2.DTO.UserSignupDTO;
import com.API.BlogV2.Entity.User;

import java.util.List;


public interface UserService {
    void registerUser(UserSignupDTO dto);
    UserDTO getUserDetails(Long id);
    List<UserDTO> getAllUser();
    String verifyUser(LoginDTO u);
    void deleteUser(Long id);
    void updateUser(Long id, UserDTO userDTO);
    UserDTO updateProfilePic(Long userId, String imageUrl);
    String getResizedProfilePic(Long userId, int width, int height);
    void updateUserRole(Long userId, com.API.BlogV2.Entity.Role role);
}
