package unq.dapp.grupoj.soccergenius.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import unq.dapp.grupoj.soccergenius.exceptions.TokenVerificationException;

import java.util.Date;

public class JwtTokenProvider {

    private static final String JWT_SECRET_KEY_SOCCERGENIUS = System.getenv("JWT_SECRET_KEY_SOCCERGENIUS");
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hora
    private static final Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET_KEY_SOCCERGENIUS);

    private JwtTokenProvider() {
        throw new IllegalStateException("Utility class");
    }

    public static String generateToken(Long id) {
        return JWT.create()
                .withSubject(id.toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(algorithm);
    }

    public static String validateTokenAndGetUsername(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getSubject(); // username
        } catch (JWTVerificationException e) {
            throw new TokenVerificationException("Token inválido o expirado");
        }
    }
}