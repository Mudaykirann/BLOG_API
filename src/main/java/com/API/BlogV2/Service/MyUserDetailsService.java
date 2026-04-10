package com.API.BlogV2.Service;

import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Entity.UserPrincple;
import com.API.BlogV2.Repository.UserRepo;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {


    @Autowired
    private UserRepo userRepo;

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByName(username);

        if(user == null){
            throw  new UsernameNotFoundException("User Not Found Exception");
        }

        return new UserPrincple(user);
    }
}
