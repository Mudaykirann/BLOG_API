package com.API.BlogV2.Controller;

import com.API.BlogV2.DTO.UserDTO;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Service.JWTService;
import com.API.BlogV2.Service.UserService;
import com.API.BlogV2.Utils.JwtFilter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
// USE THESE INSTEAD
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(UserController.class) //Loads only controller layer
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private JwtFilter jwtFilter;


    @MockitoBean
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser(roles="ADMIN")
    void testGetAllUsers() throws Exception{

        List<UserDTO> users = List.of(new UserDTO());

        when(userService.getAllUser()).thenReturn(users);

        mockMvc.perform((RequestBuilder) get("/api/users"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetUserById() throws Exception {

        UserDTO user = new UserDTO();
        when(userService.getUserDetails(1L)).thenReturn(user);

        mockMvc.perform(get("/api/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User fetched successfully"));
    }


//    @Test
//    void testLogin() throws Exception {
//
//        when(userService.verifyUser(any(User.class)))
//                .thenReturn("token123");
//
//        mockMvc.perform(post("/api/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//            {
//                "name": "mohan",
//                "email": "mohan@gmail.com",
//                "occupation": "barber",
//                "password": "m@123",
//                "role":"ADMIN"
//            }
//        """))
//                .andExpect(status().isOk())
//                .andExpect(content().string("token123"));
//    }
    //@Test
//    void testRegister() throws Exception {
//
//        //because it doesn't return any value
//        doNothing().when(userService).registerUser(User.class);
//
//        mockMvc.perform(post("/api/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//            {
//                "name": "mohan",
//                "email": "mohan@gmail.com",
//                "occupation": "barber",
//                "password": "m@123",
//                "role":"ADMIN"
//            }
//        """))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message")
//                        .value("User Registered successfully"))
//                .andExpect(jsonPath("$.status")
//                        .value("success"));
//    }

}
