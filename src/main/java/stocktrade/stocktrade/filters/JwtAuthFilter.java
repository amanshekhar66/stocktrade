package stocktrade.stocktrade.filters;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import stocktrade.stocktrade.entities.OtpEntity;
import stocktrade.stocktrade.entities.UserDetailsEntity;
import stocktrade.stocktrade.services.JwtService;
import stocktrade.stocktrade.services.OtpService;
import stocktrade.stocktrade.services.SignUpService;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final SignUpService signUpService;
    private final OtpService otpService;
    @Value("${app_env}")
    private String appEnv;
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String requestTokenHeader = request.getHeader("Authorization");
            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = requestTokenHeader.split("Bearer ")[1];
            Long userId = jwtService.getUserIdFromToken(token);
            if (userId.equals(0L)){
                userId = generateAccessTokenFromRefreshToken(request,response);
            }
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetailsEntity user = signUpService.getUserById(userId);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        }
        catch (Exception e){
            handlerExceptionResolver.resolveException(request,response,null,e);
        }


    }

    private Long generateAccessTokenFromRefreshToken(HttpServletRequest request,
                                                     HttpServletResponse response) {
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue).orElseThrow(
                        ()-> new JwtException("Refresh token has been expired, direct user to the login page")
                );
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        if(userId.equals(0L)){
            throw new JwtException("Refresh token has been expired, direct user to the login page");
        }
        String accessToken = jwtService.GenerateAccessTokenFromRefreshToken(userId);
        Cookie cookie = new Cookie("accessToken",accessToken);
        if(appEnv.equals("production")){
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setDomain("stocksense.com");
            cookie.setPath("/");
        }
        response.addCookie(cookie);
        return userId;
    }
}
