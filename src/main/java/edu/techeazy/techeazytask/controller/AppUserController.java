package edu.techeazy.techeazytask.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.techeazy.techeazytask.entity.AppUser;
import edu.techeazy.techeazytask.repository.AppUserRepository;
import edu.techeazy.techeazytask.request.AppUserRequest;
import edu.techeazy.techeazytask.request.AuthRequest;
import edu.techeazy.techeazytask.response.AppUserResponse;
import edu.techeazy.techeazytask.response.AuthResponse;
import edu.techeazy.techeazytask.security.JwtService;

@RestController
public class AppUserController {

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private JwtService jwtService;

	@PostMapping(path = "/user")
	public ResponseEntity<AppUserResponse> addAppUser(@RequestBody AppUserRequest appUserRequest) {
		AppUser appUser = new AppUser();
		appUser.setName(appUserRequest.getName());
		appUser.setPassword(appUserRequest.getPassword());
		appUser.setRole(appUserRequest.getRole());
		AppUser addedAppUser = appUserRepository.save(appUser);
		AppUserResponse appUserResponse = new AppUserResponse();
		appUserResponse.setName(addedAppUser.getName());
		appUserResponse.setPassword(addedAppUser.getPassword());
		appUserResponse.setRole(addedAppUser.getRole());
		return new ResponseEntity<AppUserResponse>(appUserResponse, HttpStatus.CREATED);
	}

	@PostMapping(path = "/auth")
	public ResponseEntity<Object> findAppUserByNameAndPassword(@RequestBody AuthRequest authRequest) {
		Optional<AppUser> optional = appUserRepository.findAppUserByNameAndPassword(authRequest.getName(),
				authRequest.getPassword());
		if (optional.isPresent()) {
			AppUser appUser = optional.get();
			String token = jwtService.getToken(appUser.getName());
			AuthResponse authResponse = new AuthResponse();
			authResponse.setName(appUser.getName());
			authResponse.setPassword(appUser.getPassword());
			authResponse.setRole(appUser.getRole());
			authResponse.setToken(token);
			return new ResponseEntity<Object>(authResponse, HttpStatus.FOUND);
		} else {
			return new ResponseEntity<Object>("Invalid username or password", HttpStatus.NOT_FOUND);
		}
	}

}
