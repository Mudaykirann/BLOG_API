package com.API.BlogV2.DTO;

import com.API.BlogV2.Entity.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service // This allows Spring to manage this class
public class UserMapper {

    public  UserDTO mapToDTO(User u) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(u.getId()); // Don't forget the ID!
        userDTO.setName(u.getName());
        userDTO.setEmail(u.getEmail());
        userDTO.setOccupation(u.getOccupation());
        userDTO.setBio(u.getBio());
        userDTO.setProfilePicUrl(u.getProfilePicUrl());
        return userDTO;
    }

    // Inside UserMapper
    public User mapToEntity(UserDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setOccupation(dto.getOccupation());
        return user;
    }
}