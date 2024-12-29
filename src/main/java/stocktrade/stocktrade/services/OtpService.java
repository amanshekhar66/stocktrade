package stocktrade.stocktrade.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import stocktrade.stocktrade.dto.OtpDTO;
import stocktrade.stocktrade.dto.TokenDTO;
import stocktrade.stocktrade.entities.OtpEntity;
import stocktrade.stocktrade.exceptions.ExpiredOtp;
import stocktrade.stocktrade.exceptions.InvalidOtp;
import stocktrade.stocktrade.exceptions.ResourceNotFound;
import stocktrade.stocktrade.repositories.OtpRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final OtpRepository otpRepository;
    private final ModelMapper modelMapper;
    private final Random random = new Random();
    public OtpDTO getVerificationCode(String email){
        OtpEntity otpEntity = otpRepository.findByUserEmail(email).orElse(null);
        if(otpEntity != null){
            if(otpEntity.getExpiryDate().isBefore(LocalDateTime.now())){
                otpRepository.deleteByUserEmail(email);
                throw new ExpiredOtp("Otp has been expired,kindly register again");
            }
            return modelMapper.map(otpEntity,OtpDTO.class);
        }
        else{
            throw new ResourceNotFound("Could Not find user Email, kindly try to resend the otp");
        }
    }

    public void deleteOtpByEmail(String email){
        if(otpRepository.existsByUserEmail(email)) {
            otpRepository.deleteByUserEmail(email);
        }
    }

    public void deleteAllOtp(){
        if(otpRepository.count()>0){
            otpRepository.deleteAll();
        }
    }
    public Integer createOtp(){
        return 100000 + random.nextInt(899999);
    }

    public void saveOtpDetails(OtpDTO otpDTO) {
        OtpEntity otpEntity = otpRepository.findByUserEmail(otpDTO.getUserEmail()).orElse(null);
        if(otpEntity==null){
            otpDTO.setOtpCount(1);
        }
        else {
            otpDTO.setOtpCount(otpEntity.getOtpCount()+1);
            otpRepository.deleteByUserEmail(otpDTO.getUserEmail());
        }
        otpRepository.save(modelMapper.map(otpDTO,OtpEntity.class));
    }

    public OtpEntity findOtpByEmail(String email) {
        return otpRepository.findByUserEmail(email).orElseThrow(()->new BadCredentialsException("Invalid email id - "+email));
    }

    public boolean isUserVerified(String userEmail) {
        OtpEntity otpEntity = otpRepository.findByUserEmail(userEmail).orElse(null);
        if(otpEntity!=null){
            return otpEntity.getOtpVerified();
        }
        return false;
    }

    public void updateVerificationOfOtp(OtpDTO otpServiceVerification) {
        OtpEntity otpEntity = otpRepository.findByUserEmail(otpServiceVerification.getUserEmail()).orElse(null);
        if(otpEntity!=null){
            otpEntity.setOtpVerified(true);
            otpRepository.save(otpEntity);
        }
    }
}

