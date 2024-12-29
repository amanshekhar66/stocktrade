package stocktrade.stocktrade.services;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import stocktrade.stocktrade.dto.PlanDTO;
import stocktrade.stocktrade.entities.PlanEntity;
import stocktrade.stocktrade.exceptions.ResourceNotFound;
import stocktrade.stocktrade.repositories.PlanRepository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    public void createNewPlan(PlanDTO planDTO, HttpServletRequest request) {
        Long planOwnerId = getUserIdFromToken(request);
        planDTO.setPlanOwnerId(planOwnerId);
        modelMapper.typeMap(PlanDTO.class, PlanEntity.class)
                .addMappings(mapper -> {
                    mapper.skip(PlanEntity::setPlanId);
                });
        PlanEntity planEntity = modelMapper.map(planDTO, PlanEntity.class);
        planRepository.save(planEntity);

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

    public void updatePlanById(Map<String, Object> planUpdates, HttpServletRequest request) {
        Long ownerId = getUserIdFromToken(request);
        String planName = (String) planUpdates.get("planName");
        PlanEntity planEntity = planRepository.findByPlanOwnerIdAndPlanName(ownerId,planName).orElseThrow(()->new BadCredentialsException("You are not authorized to update this plan"));
        planUpdates.forEach((field,value)->{
            Field fieldToBeUpdated = ReflectionUtils.findField(PlanEntity.class,field);
            if(fieldToBeUpdated!=null){
                fieldToBeUpdated.setAccessible(true);
                ReflectionUtils.setField(fieldToBeUpdated,planEntity,value);
            }
        });
        planRepository.save(planEntity);
    }

    public List<PlanDTO> getAllPlans() {
        List<PlanEntity> planEntities = planRepository.findAll();
        return planEntities.stream()
                .map(planEntity -> modelMapper.map(planEntity, PlanDTO.class))
                .toList();
    }

    public List<PlanDTO> getAllPlansPerUser(HttpServletRequest request) {
        Long ownerId = getUserIdFromToken(request);
        List<PlanEntity> planEntities = planRepository.findByPlanOwnerId(ownerId).orElseThrow(()-> new ResourceNotFound("No plan found by this user"));
        return planEntities.stream()
                .map(planEntity -> modelMapper.map(planEntity, PlanDTO.class))
                .toList();
    }

    public void deletePlan(Map<String, Object> userPlanName, HttpServletRequest request) {
        Long ownerId = getUserIdFromToken(request);
        String planName = (String) userPlanName.get("planName");
        planRepository.deleteByPlanOwnerIdAndPlanName(ownerId,planName);
    }
}

