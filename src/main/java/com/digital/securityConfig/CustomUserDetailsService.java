package com.digital.securityConfig;

import com.digital.entity.Admin;
import com.digital.entity.User;
import com.digital.enums.Status;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.AdminRepository;
import com.digital.repository.UserRepository;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final AdminRepository adminRepository;

    public CustomUserDetailsService(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    /* Here we have already hard coded the admin credentials so that's why we have applied if condition
       to reduce response time.
       getAuthorities() method is used to extract user role from user record.
    */

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if(username.equals("admin@school.com")) {
            Admin admin = adminRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Admin record with given username not found in database"));

              return new org.springframework.security.core.userdetails.User(
                    admin.getUsername(),
                    admin.getPassword(),
                    getAuthorities(admin)
             );
        }

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User record with given username not found in database"));

        if(user.getStatus().equals(Status.INACTIVE))
            throw new AuthorizationDeniedException("Inactive users cannot login");

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthorities(user));
    }

//    public Collection<SimpleGrantedAuthority> getAuthorities(Admin admin){
//        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + admin.getRole()));
//    }
//
//    public Collection<SimpleGrantedAuthority> getAuthorities(User user){
//        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
//    }


    public Collection<SimpleGrantedAuthority> getAuthorities(User user){
        String role = String.valueOf(user.getRole());
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    public Collection<SimpleGrantedAuthority> getAuthorities(Admin admin){
        String role = String.valueOf(admin.getRole());
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }


//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User u = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//        // Map your Role to a GrantedAuthority (prefix ROLE_)
//        String authority = "ROLE_" + u.getRole().name();
//        return org.springframework.security.core.userdetails.User
//                .withUsername(u.getEmail())
//                .password(u.getPassword())
//                .authorities(authority)
//                .build();


//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority("ROLE_" + this.userRepository));
//    }


}
