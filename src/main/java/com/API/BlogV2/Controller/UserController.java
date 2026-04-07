package com.API.BlogV2.Controller;

import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "api/v1/users")
public class UserController {

    private  final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public void getAllUsers(){
        userService.getAllUser();
    }




    @PostMapping
    public void addNewUser(@RequestBody User u) throws IllegalAccessException {
        userService.addNewUser(u);
    }

}
