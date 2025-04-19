package net.ictcampus.campustype.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final String SECRET_KEY = "f01e4659731c918ab3cd7f5f193079a9b64717896bc722f44754ab91e38f8885595998ad88b001a2553ebed2fadd4438be54bf01a6f6cf95afe646b53c49774f8e8662bde513c9b7153f8b76b3c42197b419f6945ec74a8512be53df327199891169d4ab4cd091725eed9812df6f58f5254b3ce1e262972d3b5ec5202be64720";
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours

    public String generateToken(UserDetails userDetails) {
        String email = userDetails.getUsername();
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getAuthorities().toString().contains("USER") ? email : "unknown");
        logger.info("Generating JWT token for email: {}", email);
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
        logger.info("JWT token generated: {}", token);
        return token;
    }

    public String generateToken(UserDetails userDetails, String username, Long userId) {
        String email = userDetails.getUsername();
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("userId", userId);
        logger.info("Generating JWT token for email: {}, username: {}, and userId: {}", email, username, userId);
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
        logger.info("JWT token generated: {}", token);
        return token;
    }

    public String generateTestToken(String sentence, Long startTime, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sentence", sentence);
        claims.put("startTime", startTime);
        claims.put("userId", userId);
        logger.info("Generating test token with sentence: {}, startTime: {}, userId: {}", sentence, startTime, userId);
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // 10 minutes
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
        logger.info("Test token generated: {}", token);
        return token;
    }

    public Claims getClaims(String token) {
        logger.debug("Extracting claims from token: {}", token);
        try {
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            logger.error("Failed to extract claims from token: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String extractUsername(String token) {
        logger.debug("Extracting username from token: {}", token);
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
            String username = (String) claims.get("username");
            logger.debug("Username extracted: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("Failed to extract username from token: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Long extractUserId(String token) {
        logger.debug("Extracting userId from token: {}", token);
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
            Long userId = claims.get("userId", Long.class);
            logger.debug("UserId extracted: {}", userId);
            return userId;
        } catch (Exception e) {
            logger.error("Failed to extract userId from token: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String extractEmail(String token) {
        logger.debug("Extracting email from token: {}", token);
        try {
            String email = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
            logger.debug("Email extracted: {}", email);
            return email;
        } catch (Exception e) {
            logger.error("Failed to extract email from token: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Long extractStartTime(String token) {
        logger.debug("Extracting startTime from token: {}", token);
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
            Long startTime = claims.get("startTime", Long.class);
            logger.debug("StartTime extracted: {}", startTime);
            return startTime;
        } catch (Exception e) {
            logger.error("Failed to extract startTime from token: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String extractSentence(String token) {
        logger.debug("Extracting sentence from token: {}", token);
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
            String sentence = (String) claims.get("sentence");
            logger.debug("Sentence extracted: {}", sentence);
            return sentence;
        } catch (Exception e) {
            logger.error("Failed to extract sentence from token: {}", e.getMessage(), e);
            throw e;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        logger.debug("Validating token for email: {}", userDetails.getUsername());
        try {
            final String email = extractEmail(token);
            boolean isValid = (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
            logger.debug("Token validation result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("Failed to validate token: {}", e.getMessage(), e);
            throw e;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getExpiration();
        boolean isExpired = expiration.before(new Date());
        logger.debug("Token expiration check - expired: {}", isExpired);
        return isExpired;
    }
}