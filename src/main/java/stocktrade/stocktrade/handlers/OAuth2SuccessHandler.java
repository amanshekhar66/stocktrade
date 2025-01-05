package stocktrade.stocktrade.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import stocktrade.stocktrade.entities.UserDetailsEntity;
import stocktrade.stocktrade.enums.Permissions;
import stocktrade.stocktrade.enums.Roles;
import stocktrade.stocktrade.services.JwtService;
import stocktrade.stocktrade.services.SignUpService;

import java.io.IOException;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final SignUpService signUpService;
    private final JwtService jwtService;
    @Value("${app_env}")
    private String appEnv;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authenticationToken.getPrincipal();
        String userEmail = oAuth2User.getAttribute("email");
        UserDetailsEntity userDetails = UserDetailsEntity.builder()
                .userEmail(userEmail)
                .firstName(oAuth2User.getAttribute("name"))
                .roles(Set.of(Roles.CUSTOMER))
               // .permissions(Permissions.VIEW_PLATFORM)
                .isVerified(true)
                .build();
        UserDetailsEntity savedUser = signUpService.createNewUserThroughOAuth2(userDetails);

        log.info("A user has been registered using OAuth2Login with email id - {}", userEmail);
        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);
        Cookie accessCookie = new Cookie("accessToken",accessToken);
        Cookie refreshCookie = new Cookie("refreshToken",refreshToken);
        if(appEnv.equals("production")){
            accessCookie.setSecure(true);
            accessCookie.setHttpOnly(true);
            accessCookie.setDomain("stocksense.com");
            accessCookie.setPath("/");

            refreshCookie.setSecure(true);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setDomain("stocksense.com");
            refreshCookie.setPath("/");
        }
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
        response.sendRedirect("http://localhost:8080/swagger-ui/index.html");
    }
}
