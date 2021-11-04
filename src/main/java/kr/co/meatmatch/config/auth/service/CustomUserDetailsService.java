package kr.co.meatmatch.config.auth.service;

import kr.co.meatmatch.config.auth.dto.CustomUserDetails;
import kr.co.meatmatch.mapper.meatmatch.AuthMapper;
import kr.co.meatmatch.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AuthMapper authMapper;

    @Override
    public UserDetails loadUserByUsername(String authId) throws UsernameNotFoundException {
        HashMap<String, Object> user = authMapper.findUserByAuthId(authId);

        if(user == null) {
            throw new UsernameNotFoundException(authId + "is not found");
        }

        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setUsername(user.get("auth_id").toString());
        customUserDetails.setPassword(user.get("password").toString());
        customUserDetails.setAuthorities(getAuthorities(user.get("type").toString()));
        customUserDetails.setEnabled(true);
        customUserDetails.setAccountNonExpired(true);
        customUserDetails.setAccountNonLocked(true);
        customUserDetails.setCredentialsNonExpired(true);

        return customUserDetails;
    }

    public Collection<GrantedAuthority> getAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }
}
