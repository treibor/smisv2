package com.smis.security;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smis.entity.UsersRoles;
import com.smis.entity.Users;
import com.smis.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private final UserRepository userRepository;
	
	public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        
    }

    @Override
    @Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	//stem.out.println(loginform.code.getValue());
		//Users user = userRepository.findByUserName(username);
    	Users user = userRepository.findByUserNameAndEnabled(username, true);
		if (user == null) {
			//audit.saveAudit("Login Failure", username);
			throw new UsernameNotFoundException("No user present with username: " + username);
		} else {
			//audit.saveAudit("Login Success", username);
			return new User(user.getUserName(), user.getPassword(), getAuthorities(user));
			
		}

	}
	/*
	 * private static List<GrantedAuthority> getAuthorities(Users user) { List<Role>
	 * role = user.getRoles(); // Create a SimpleGrantedAuthority with the role
	 * string SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"
	 * + role); // Return a singleton list containing the authority return
	 * Collections.singletonList(authority); }
	 */
    
	private static List<GrantedAuthority> getAuthorities(Users user) {
		return user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))

				.collect(Collectors.toList());

	}

   

}
