package stocktrade.stocktrade.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import stocktrade.stocktrade.dto.TokenDTO;
import stocktrade.stocktrade.dto.UserDetailsDTO;
import stocktrade.stocktrade.entities.UserDetailsEntity;
import stocktrade.stocktrade.enums.Roles;
import stocktrade.stocktrade.exceptions.UnverifiedUserException;
import stocktrade.stocktrade.repositories.UserDetailsRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SignUpService implements UserDetailsService {
    private final UserDetailsRepository userDetailsRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailsRepository.findByUserEmail(username).orElseThrow(()->new BadCredentialsException("No user exists by this email id - "+ username));
    }
    public UserDetailsEntity getUserById(Long userId){
        return userDetailsRepository.findById(userId).orElse(null);
    }
    
    public UserDetailsEntity createNewUserThroughOAuth2(UserDetailsEntity userDetails){
        if(!userDetailsRepository.existsByUserEmail(userDetails.getUserEmail())) {
            return userDetailsRepository.save(userDetails);
        }
        return userDetailsRepository.findByUserEmail(userDetails.getUserEmail()).orElseThrow(
                ()->new RuntimeException("User exists in the database but its value is not getting retrieved")
        );
    }
    public UserDetailsDTO getUserByEmail(String email){
        return modelMapper.map(userDetailsRepository.findByUserEmail(email).orElse(null),UserDetailsDTO.class);
    }

    public UserDetailsDTO createNewUser(UserDetailsDTO userDetailsDTO) {
        if(!otpService.isUserVerified(userDetailsDTO.getUserEmail())){
            throw new UnverifiedUserException("User is not verified, kindly verify your email before registration");
        }
        otpService.deleteOtpByEmail(userDetailsDTO.getUserEmail());
        UserDetailsEntity user = modelMapper.map(userDetailsDTO,UserDetailsEntity.class);
        user.setUserPassword(passwordEncoder.encode(userDetailsDTO.getUserPassword()));
        return modelMapper.map(userDetailsRepository.save(user),UserDetailsDTO.class);
    }

    public boolean ifVerifiedUserAlreadyRegistered(String email) {
        if(userDetailsRepository.existsByUserEmail(email)){
            UserDetailsEntity userDetails = userDetailsRepository.findByUserEmail(email).orElse(null);
            if(userDetails!=null){
                return userDetails.getIsVerified();
            }
        };
        return false;
    }

    public void updateUserRole(Long userId, Set<Roles> roles) {
        UserDetailsEntity userDetails = userDetailsRepository.findById(userId).orElse(null);
        if(userDetails!=null) {
            userDetails.setRoles(roles);
            userDetailsRepository.save(userDetails);
        }
    }
}

