package stocktrade.stocktrade.services;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import stocktrade.stocktrade.dto.InstitutionDTO;
import stocktrade.stocktrade.entities.InstitutionEntity;
import stocktrade.stocktrade.entities.UserDetailsEntity;
import stocktrade.stocktrade.enums.Roles;
import stocktrade.stocktrade.exceptions.ResourceNotFound;
import stocktrade.stocktrade.repositories.InstitutionRepository;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InstitutionService {
    private final InstitutionRepository institutionRepository;
    private final SignUpService signUpService;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;

    public void createNewInstitution(InstitutionDTO institutionDTO, HttpServletRequest request) {
        InstitutionEntity institutionEntity = modelMapper.map(institutionDTO, InstitutionEntity.class);
        Long userId = getUserIdFromToken(request);
        UserDetailsEntity userDetails = signUpService.getUserById(userId);
        if(userDetails!=null){
            institutionEntity.setInstitutionEmail(userDetails.getUserEmail());
            institutionEntity.setId(userDetails.getUserId());
            institutionRepository.save(institutionEntity);
            Set<Roles> roles = new HashSet<>();
            roles.add(Roles.INSTITUTION);
            signUpService.updateUserRole(userId,roles);
        }
        else{
            throw new ResourceNotFound("No login id found for this user, kindly register on the platform first in order to create an institution");
        }
    }

    public List<InstitutionDTO> getAllInstitutions() {
        List<InstitutionEntity> institutionEntityList = institutionRepository.findAll();
        return institutionEntityList.stream()
                .map(institutionEntity -> modelMapper.map(institutionEntity, InstitutionDTO.class))
                .toList();
    }

    public InstitutionDTO getInstitutionById(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        InstitutionEntity institutionEntity = institutionRepository.findById(userId).orElseThrow(
                ()->new ResourceNotFound("No institution found,kindly create a new institution"));
        return modelMapper.map(institutionEntity, InstitutionDTO.class);

    }

    public void updateInstituteById(Map<String,Object> institutionUpdates,
                                    HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        InstitutionEntity institutionEntity = institutionRepository.findById(userId)
                .orElseThrow(()->new ResourceNotFound("No institution found! Kindly create a new institution"));
        institutionUpdates.forEach((field,value) ->{
            if(field.equals("DOB") || field.equals("approvalDate")){
                value = parseValues(value,field);
            }
            Field fieldToBeUpdated = ReflectionUtils.findField(InstitutionEntity.class,field);
            if(fieldToBeUpdated!=null){
                fieldToBeUpdated.setAccessible(true);
                ReflectionUtils.setField(fieldToBeUpdated,institutionEntity,value);
            }
        });
        institutionRepository.save(institutionEntity);
    }
    private Object parseValues(Object value,String field){
        if(field.equals("DOB")){
            return LocalDate.parse(value.toString());
        }
        return LocalDateTime.parse(value.toString());
    }

    public void deleteInstitutionById(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        if(institutionRepository.existsById(userId)){
            institutionRepository.deleteById(userId);
        }
    }
    private Long getUserIdFromToken(HttpServletRequest request){
        String requestToken = request.getHeader("Authorization");
        String accessToken = requestToken.split("Bearer ")[1];
        Long userId = jwtService.getUserIdFromToken(accessToken);
        if(userId.equals(0L)){
            throw new JwtException("Invalid token, kindly login again");
        }
        return userId;
    }
}
