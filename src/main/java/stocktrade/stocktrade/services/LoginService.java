package stocktrade.stocktrade.services;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import stocktrade.stocktrade.dto.TokenDTO;
import stocktrade.stocktrade.dto.UserDetailsDTO;
import stocktrade.stocktrade.entities.OtpEntity;
import stocktrade.stocktrade.entities.UserDetailsEntity;
import stocktrade.stocktrade.enums.Roles;
import stocktrade.stocktrade.exceptions.ResourceNotFound;
import stocktrade.stocktrade.repositories.UserDetailsRepository;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final UserDetailsRepository userDetailsRepository;
    private final JwtService jwtService;
    private final SessionService sessionService;

    public TokenDTO login(UserDetailsDTO userDetailsDTO) {
        UserDetailsEntity user = modelMapper.map(userDetailsDTO,UserDetailsEntity.class);
        if(!userDetailsRepository.existsByUserEmail(user.getUserEmail())){
            throw new ResourceNotFound("User does not exist by this email - "+userDetailsDTO.getUserEmail()+" kindly register first and then try to log in");
        }
        Authentication authenticationToken = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDetailsDTO.getUserEmail(),userDetailsDTO.getUserPassword())
        );
        UserDetailsEntity userDetails = (UserDetailsEntity) authenticationToken.getPrincipal();
        UserDetailsEntity existingUser = userDetailsRepository.findByUserEmail(userDetails.getUserEmail()).orElse(null);
        assert existingUser != null;
        if(existingUser.getIsVerified()){
            TokenDTO tokenDTO = TokenDTO.builder()
                    .accessToken(jwtService.generateAccessToken(existingUser))
                    .refreshToken(jwtService.generateRefreshToken(existingUser))
                    .userStatus("Login Successful")
                    .status(HttpStatus.ACCEPTED)
                    .build();
            sessionService.createNewSession(existingUser, tokenDTO.getRefreshToken());
            return tokenDTO;
        }
        else{
            return TokenDTO.builder()
                    .userStatus("User Not Verified")
                    .status(HttpStatus.UNAUTHORIZED)
                    .accessToken(null)
                    .refreshToken(null)
                    .build();
        }

    }

    public TokenDTO updateVerificationDetailsNGetToken(String email) {
        UserDetailsEntity userDetails = userDetailsRepository.findByUserEmail(email).orElse(null);
        if(userDetails!=null){
            userDetails.setIsVerified(true);
            userDetailsRepository.save(userDetails);
            TokenDTO tokenDTO = TokenDTO.builder()
                    .accessToken(jwtService.generateAccessToken(userDetails))
                    .refreshToken(jwtService.generateRefreshToken(userDetails))
                    .build();
            sessionService.createNewSession(userDetails, tokenDTO.getRefreshToken());
            return tokenDTO;
        }
        return null;
    }
    public TokenDTO generateTokensAfterRegisteration(UserDetailsDTO userDetailsDTO){
        UserDetailsEntity user = userDetailsRepository.findByUserEmail(userDetailsDTO.getUserEmail()).orElseThrow(()->new ResourceNotFound("User did not got saved properly"));
        TokenDTO tokenDTO = TokenDTO.builder()
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .status(HttpStatus.ACCEPTED)
                .userStatus("User registered and logged in successfully")
                .build();
        sessionService.createNewSession(user,tokenDTO.getRefreshToken());
        return tokenDTO;
    }
}
