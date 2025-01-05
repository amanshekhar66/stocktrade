package stocktrade.stocktrade.services;

import io.jsonwebtoken.JwtException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import stocktrade.stocktrade.dto.PlanCustomerDTO;
import stocktrade.stocktrade.dto.RecommendationDTO;
import stocktrade.stocktrade.entities.PlanCustomerEntity;
import stocktrade.stocktrade.entities.RecommendationEntity;
import stocktrade.stocktrade.enums.Roles;
import stocktrade.stocktrade.exceptions.ResourceNotFound;
import stocktrade.stocktrade.repositories.PlanCustomerRepository;
import stocktrade.stocktrade.repositories.RecommendationRepository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final ModelMapper modelMapper;
    private final SignUpService signUpService;
    private final JwtService jwtService;
    private final SendEmailService sendEmailService;
    private final PlanCustomerService planCustomerService;


    public void createNewRecommendationInAPlan(RecommendationDTO recommendationDTO,
                                               Long userId){
        Long id = recommendationRepository.getLastRecordId();
        if(id == null && recommendationRepository.count()==0){
            id =1L;
        }
        else{
            id+=1;
        }
        recommendationDTO.setRecommendationName("R"+id);
        RecommendationEntity recommendationEntity = modelMapper.map(recommendationDTO,RecommendationEntity.class);
        recommendationEntity.setOwnerId(userId);
        recommendationRepository.save(recommendationEntity);
        List<PlanCustomerDTO> planCustomerDTOS = planCustomerService.getCustomersByPlan(recommendationDTO.getPlanName());
        planCustomerDTOS.forEach(planCustomerDTO -> {
            try {
                sendEmailService.sendEmail(
                        planCustomerDTO.getCustomerEmail(),
                        "New recommendation at stocksense",
                        "New recommendation has been added in your plan - "+recommendationDTO.getPlanName()+"/n Kindly check out. /n Regards /n Stocksense team"
                );
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void updateRecommendation(Map<String,Object> recommendationUpdates){
        String recommendationName = (String) recommendationUpdates.get("recommendationName");
        String planName = (String) recommendationUpdates.get("planName");
        RecommendationEntity recommendationEntity = recommendationRepository.findByRecommendationNameAndPlanName(recommendationName,planName)
                .orElseThrow(()-> new ResourceNotFound("Recommendation Not found"));
        if(!recommendationEntity.getIsExpired()) {
            recommendationUpdates.forEach((field, value) -> {
                Field fieldToBeUpdated = ReflectionUtils.findField(RecommendationEntity.class, field);
                if (fieldToBeUpdated != null) {
                    fieldToBeUpdated.setAccessible(true);
                    ReflectionUtils.setField(fieldToBeUpdated, recommendationEntity, value);
                }
            });
            recommendationRepository.save(recommendationEntity);
            List<PlanCustomerDTO> planCustomerDTOS = planCustomerService.getCustomersByPlan(recommendationEntity.getPlanName());
            planCustomerDTOS.forEach(planCustomerDTO -> {
                try {
                    sendEmailService.sendEmail(
                            planCustomerDTO.getCustomerEmail(),
                            "Recommendation has been updated!",
                            "Recommendation with recommendation name - "+recommendationName +" has been added in your plan - "+recommendationEntity.getPlanName()+"/n Kindly check out. /n Regards /n Stocksense team"
                    );
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void deleteRecommendation(RecommendationDTO recommendationDTO,Long userId){
        RecommendationEntity recommendationEntity = recommendationRepository.
                findByRecommendationNameAndPlanName(recommendationDTO.getRecommendationName(),recommendationDTO.getPlanName())
                .orElseThrow(()-> new ResourceNotFound("No recommendation found !"));
        List<Roles> roles = signUpService.getRoleOfUserInList(userId);
        if(roles.getFirst().equals(Roles.INSTITUTION) || (!recommendationEntity.getIsExpired() && recommendationEntity.getOwnerId().equals(userId))){
            recommendationRepository.delete(recommendationEntity);
            List<PlanCustomerDTO> planCustomerDTOS = planCustomerService.getCustomersByPlan(recommendationDTO.getPlanName());
            planCustomerDTOS.forEach(planCustomerDTO -> {
                try {
                    sendEmailService.sendEmail(
                            planCustomerDTO.getCustomerEmail(),
                            "Recommendation has been deleted!",
                            "Recommendation with recommendation name - "+recommendationEntity.getRecommendationName() +" has been added in your plan - "+recommendationEntity.getPlanName()+"/n Kindly check out. /n Regards /n Stocksense team"
                    );
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            });
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
