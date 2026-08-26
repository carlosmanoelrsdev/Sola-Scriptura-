package br.com.carlos.solascriptura.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.carlos.solascriptura.repository.UserRepository;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public ApplicationUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		return userRepository.findByEmailIgnoreCase(username)
				.map(user -> org.springframework.security.core.userdetails.User
						.withUsername(user.getEmail())
						.password(user.getPassword())
						.roles(user.getRole().name())
						.build())
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}
}
