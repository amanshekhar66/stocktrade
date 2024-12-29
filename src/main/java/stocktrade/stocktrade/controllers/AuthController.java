package stocktrade.stocktrade.controllers;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import stocktrade.stocktrade.dto.OtpDTO;
import stocktrade.stocktrade.dto.TokenDTO;
import stocktrade.stocktrade.dto.UserDetailsDTO;
import stocktrade.stocktrade.exceptions.InvalidOtp;
import stocktrade.stocktrade.exceptions.InvalidRefreshToken;
import stocktrade.stocktrade.exceptions.UserAlreadyRegistered;
import stocktrade.stocktrade.services.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class AuthController {

    private final SendEmailService sendEmailService;
    private final OtpService otpService;
    private final SignUpService signUpService;
    private final LoginService loginService;
    private final JwtService jwtService;
    private final SessionService sessionService;
    @Value("${app_env}")
    private String appEnv;

    @PostMapping({"/signup/sendOtp","/signup/resendOtp","/login/sendOtp"})
    public ResponseEntity<String> sendOtpEmail(@RequestBody @Valid OtpDTO otpDTO) throws MessagingException {
        if(signUpService.ifVerifiedUserAlreadyRegistered(otpDTO.getUserEmail())){
            throw new UserAlreadyRegistered("User has already been registered and verified");
        }
        int verificationCode = otpService.createOtp();
        OtpDTO otpDTO1 = OtpDTO.builder()
                .userEmail(otpDTO.getUserEmail())
                .verificationCode(verificationCode)
                .createdAt(LocalDateTime.now())
                .otpVerified(false)
                .build();
        otpService.saveOtpDetails(otpDTO1);
        String emailBody = "Dear User,\n\nYour OTP for verification is: " + verificationCode +
                "\n\nPlease use this OTP to complete your registration. The OTP is valid for 6 minutes.\n\nThank you,\nStockSense team";
        sendEmailService.sendOtpEmail(otpDTO.getUserEmail(),
                "Account Verification code for StockSense", emailBody);
        return new ResponseEntity<>("Email sent successfully", HttpStatus.OK);
    }

    @GetMapping({"/signup/verifyOtp"})
    public ResponseEntity<Boolean> verifyOtp(@RequestBody OtpDTO otpDTO){
        OtpDTO otpServiceVerification = otpService.getVerificationCode(otpDTO.getUserEmail());
        if(Objects.equals(otpServiceVerification.getVerificationCode(), otpDTO.getVerificationCode())){
            otpService.updateVerificationOfOtp(otpServiceVerification);
            return new ResponseEntity<>(true,HttpStatus.ACCEPTED);
        }
        else{
            return new ResponseEntity<>(false,HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<TokenDTO> createNewUser(@RequestBody UserDetailsDTO userDetailsDTO,
                                                   HttpServletResponse response){
        userDetailsDTO.setIsVerified(true);
        UserDetailsDTO savedUser = signUpService.createNewUser(userDetailsDTO);
        TokenDTO tokenDTO = loginService.generateTokensAfterRegisteration(userDetailsDTO);
        if(tokenDTO!=null) {
            Cookie refreshCookie = new Cookie("refreshToken", tokenDTO.getRefreshToken());
            Cookie accessCookie = new Cookie("accessToken",tokenDTO.getAccessToken());
            if (appEnv.equals("production")) {
                refreshCookie.setHttpOnly(true);
                refreshCookie.setSecure(true);
                refreshCookie.setDomain("stocksense.com");
                refreshCookie.setPath("/");
                accessCookie.setHttpOnly(true);
                accessCookie.setSecure(true);
                accessCookie.setDomain("stocksense.com");
                accessCookie.setPath("/");
            }
            response.addCookie(refreshCookie);
            response.addCookie(accessCookie);
            tokenDTO.setRefreshToken("");
            tokenDTO.setAccessToken("");
            return new ResponseEntity<>(tokenDTO,tokenDTO.getStatus());
        }
        return new ResponseEntity<>( null,HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody UserDetailsDTO userDetailsDTO,
                                           HttpServletResponse response) {
        TokenDTO tokenDTO = loginService.login(userDetailsDTO);
        if(tokenDTO.getRefreshToken()!=null && tokenDTO.getAccessToken()!=null) {
            Cookie refreshCookie = new Cookie("refreshToken", tokenDTO.getRefreshToken());
            Cookie accessCookie = new Cookie("accessToken",tokenDTO.getAccessToken());
            if (appEnv.equals("production")) {
                refreshCookie.setHttpOnly(true);
                refreshCookie.setSecure(true);
                refreshCookie.setDomain("stocksense.com");
                refreshCookie.setPath("/");
                accessCookie.setHttpOnly(true);
                accessCookie.setSecure(true);
                accessCookie.setDomain("stocksense.com");
                accessCookie.setPath("/");
            }
            response.addCookie(refreshCookie);
            response.addCookie(accessCookie);
            tokenDTO.setRefreshToken("");
            tokenDTO.setAccessToken("");
            return new ResponseEntity<>(tokenDTO,tokenDTO.getStatus());
        }
        return new ResponseEntity<>(null,tokenDTO.getStatus());
    }

    @PatchMapping({"/login/verifyOtp"})
    public ResponseEntity<TokenDTO> verifyLoginOtp(@RequestBody OtpDTO otpDTO,
                                                    HttpServletResponse response){
        OtpDTO otpServiceVerification = otpService.getVerificationCode(otpDTO.getUserEmail());
        if(Objects.equals(otpServiceVerification.getVerificationCode(), otpDTO.getVerificationCode())){
            TokenDTO tokenDTO = loginService.updateVerificationDetailsNGetToken(otpDTO.getUserEmail());
            if(tokenDTO!=null) {
                Cookie refreshCookie = new Cookie("refreshToken", tokenDTO.getRefreshToken());
                Cookie accessCookie = new Cookie("accessToken",tokenDTO.getAccessToken());
                if (appEnv.equals("production")) {
                    refreshCookie.setHttpOnly(true);
                    refreshCookie.setSecure(true);
                    refreshCookie.setDomain("stocksense.com");
                    refreshCookie.setPath("/");
                    accessCookie.setHttpOnly(true);
                    accessCookie.setSecure(true);
                    accessCookie.setDomain("stocksense.com");
                    accessCookie.setPath("/");
                }
                response.addCookie(refreshCookie);
                response.addCookie(accessCookie);
                tokenDTO.setRefreshToken("");
                tokenDTO.setAccessToken("");
                otpService.deleteOtpByEmail(otpDTO.getUserEmail());
                return new ResponseEntity<>(tokenDTO, HttpStatus.ACCEPTED);
            }
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }
        else{
                throw new InvalidOtp("Wrong Otp entered! Kindly enter the correct Otp");
        }
    }

//    @PostMapping("/refreshToken")
//    public ResponseEntity<String> refreshAccessToken(HttpServletRequest request){
//        String refreshToken = Arrays.stream(request.getCookies())
//                .filter(cookie -> "refreshToken".equals(cookie.getName()))
//                .findFirst()
//                .map(cookie -> cookie.getValue())
//                .orElseThrow(()-> new AuthenticationServiceException("Refresh server not found in the cookies"));
//        Long userId = jwtService.getUserIdFromToken(refreshToken);
//        String accessToken = jwtService.GenerateAccessTokenFromRefreshToken(userId);
//        if(accessToken!=null){
//           return new ResponseEntity<>(accessToken,HttpStatus.FOUND);
//        }
//        throw new InvalidRefreshToken("The refresh token is not valid");
//    }

    @PostMapping("/logout")
    public ResponseEntity<String> logOut(HttpServletRequest request,
                                          HttpServletResponse response){
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue).orElseThrow(()->new AuthenticationServiceException("Invalid refresh token"));
        String accessToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue).orElseThrow(()->new AuthenticationServiceException("Invalid access token"));
        Cookie refreshCookie = new Cookie("refreshToken",null);
        Cookie accessCookie = new Cookie("accessToken",null);
        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        sessionService.removeSession(refreshToken);
        return new ResponseEntity<>("Logged Out Successfully",HttpStatus.OK);
    }


}

