package com.israr.jwtpractice.service;

import com.israr.jwtpractice.dto.UserDto;
import com.israr.jwtpractice.models.Role;
import com.israr.jwtpractice.models.User;
import com.israr.jwtpractice.repository.RoleRepository;
import com.israr.jwtpractice.repository.UserRepository;
import com.israr.jwtpractice.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final JwtTokenProvider jwtTokenProvider;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    private AuthenticationManager authenticationManager;

    public String authenticate(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        System.out.println(token);
        return token;
    }

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    public void saveUser(UserDto userDto) {
        User user = new User();

        user.setUsername(userDto.getUsername());
       user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        String roleName;
        if (userDto.isAdminRegistration()) {
            roleName = "ROLE_ADMIN";
        } else {
            roleName = "ROLE_USER";
        }

        //Check if role already exists in database, otherwise create it
        Role role = roleRepository.findByName(roleName);
        if(role == null){
            role = new Role();
            role.setName((roleName));
            roleRepository.save(role);
        }

        //Assign the role to the user
        user.setRoles(( Collections.singletonList(role)));
        userRepository.save(user);
    }


    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users;
    }

    private UserDto convertEntityToDto(List<User> users) {
        UserDto userDto = new UserDto();
        userDto.setUsername(users.get(0).getUsername());
        userDto.setEmail(users.get(0).getEmail());
        userDto.setPassword(passwordEncoder.encode(users.get(0).getPassword()));
        return userDto;
    }
}
