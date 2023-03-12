package com.app.reddit.jwtSecurity;

import com.app.reddit.exception.SpringRedditException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;


@Service
public class JwtProvider {
    private KeyStore keyStore;

    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;

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
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    public String generateTokenWithUsername(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
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

    public Long getJwtExpirationInMillis() {
        return jwtExpirationInMillis;
    }
}
