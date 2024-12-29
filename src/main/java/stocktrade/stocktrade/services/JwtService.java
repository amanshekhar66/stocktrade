package stocktrade.stocktrade.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import stocktrade.stocktrade.entities.OtpEntity;
import stocktrade.stocktrade.entities.UserDetailsEntity;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${token_key}")
    private String tokenKey;
    private final SignUpService signUpService;
    private SecretKey generateKey(){
        return Keys.hmacShaKeyFor(tokenKey.getBytes(StandardCharsets.UTF_8));
    }
    public String generateAccessToken(UserDetailsEntity user){
        return Jwts.builder()
                .subject(user.getUserId().toString())
                .claim("roles",user.getAuthorities())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000*60*3))
                .signWith(generateKey())
                .compact();
    }
    public String generateRefreshToken(UserDetailsEntity userEntity){
        return Jwts.builder()
                .subject(userEntity.getUserId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L*60*60*24*365))
                .signWith(generateKey())
                .compact();
    }

    public Long getUserIdFromToken(String token){
        String errMsg  = "";
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(generateKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.valueOf(claims.getSubject());
        }
        catch (JwtException jwtException){
            errMsg = jwtException.getLocalizedMessage();
            if(jwtException instanceof ExpiredJwtException){
                return 0L;
            }
        }
        throw new JwtException(errMsg);
    }

    public String GenerateAccessTokenFromRefreshToken(Long userId){
        UserDetailsEntity user = signUpService.getUserById(userId);
        if(user!=null) {
            return Jwts.builder()
                    .subject(user.getUserId().toString())
                    .claim("role",user.getAuthorities())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 1000 * 600))
                    .signWith(generateKey())
                    .compact();
        }
        return null;
    }
}

