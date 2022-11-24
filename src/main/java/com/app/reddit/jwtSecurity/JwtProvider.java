package com.app.reddit.jwtSecurity;

import com.app.reddit.exception.SpringRedditException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;


@Service
public class JwtProvider {
    private KeyStore keyStore;

    @PostConstruct
    public void init(){
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream inputStream = getClass().getResourceAsStream("/springredit.jks");
            keyStore.load(inputStream, "vinkotino".toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SpringRedditException("Exception occurred when loading keystore");
        }
    }

    public String generateToken(Authentication authentication){
        User principal = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                .compact();
    }

    private Key getPrivateKey() {
        try {
            return keyStore.getKey("springredit", "vinkotino".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SpringRedditException("Exception occurred while retrieving private key from keystore");
        }
    }

    public boolean validateToken(String jwt) {
        Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);
        return true;
    }

    private PublicKey getPublicKey() {
        try{
            return keyStore.getCertificate("springredit").getPublicKey();
        } catch (KeyStoreException e) {
            throw new SpringRedditException("Exception occurred while trying to retrive a public key");
        }
    }

    public String getUsernameFromToken(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
