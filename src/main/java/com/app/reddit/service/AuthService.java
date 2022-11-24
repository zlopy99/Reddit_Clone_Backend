package com.app.reddit.service;

import com.app.reddit.dto.AuthenticationResponse;
import com.app.reddit.dto.LoginRequest;
import com.app.reddit.dto.RegisterRequest;
import com.app.reddit.exception.SpringRedditException;
import com.app.reddit.jwtSecurity.JwtProvider;
import com.app.reddit.model.NotificationEmail;
import com.app.reddit.model.User;
import com.app.reddit.model.VerificationToken;
import com.app.reddit.repo.UserRepo;
import com.app.reddit.repo.VerificationTokenRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepo verificationTokenRepo;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Transactional
    public void signup(RegisterRequest registerRequest){
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        //  Save user to DB
        userRepo.save(user);

        //  Generate random verification token for the user
        //  He needs to confirm it by email
        String token = generateVerificationToken(user);

        //  After generating token we send email to the user to confirm his credentials
        mailService.sendEmail(new NotificationEmail("Please activate your account",
                user.getEmail(),
                "Thank you for signing up to Spring Reddit, please click on the below url to activate your account : " +
                        "http://localhost:8080/api/auth/accountVerification/" +
                        token));
    }

    //  It's not enough to just send token by email, we need to save it in our DB
    //  Because user can confirm it in about 3 days...and if we leave token just in memory it will be deleted
    //  because it is not enough time to hold the token
    private String generateVerificationToken(User user){
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepo.save(verificationToken);
        return token;
    }

    //  When user verifies his account by email
    //  We need to check if that token exists in our DB
    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepo.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new SpringRedditException("Invalid Token")));
    }

    //  Then if it exists we need to find user with that token and enable his account
    @Transactional
    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepo.findByUsername(username).orElseThrow(() -> new SpringRedditException("User not found with name " + username));
        user.setEnabled(true);
        userRepo.save(user);
    }

    //  Login logic with Jwt token
    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return new AuthenticationResponse(token, loginRequest.getUsername());
    }

    @Transactional
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepo.findByUsername(principal.getUsername()).
                orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }
}
