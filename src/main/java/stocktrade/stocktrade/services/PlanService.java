package stocktrade.stocktrade.services;

import io.jsonwebtoken.JwtException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import stocktrade.stocktrade.dto.CouponDTO;
import stocktrade.stocktrade.dto.PlanDTO;
import stocktrade.stocktrade.dto.PlanRADTO;
import stocktrade.stocktrade.dto.RecommendationDTO;
import stocktrade.stocktrade.entities.CouponEntity;
import stocktrade.stocktrade.entities.PlanEntity;
import stocktrade.stocktrade.entities.ResearchAnalystEntity;
import stocktrade.stocktrade.enums.Permissions;
import stocktrade.stocktrade.enums.Roles;
import stocktrade.stocktrade.exceptions.ResourceNotFound;
import stocktrade.stocktrade.repositories.CouponRepository;
import stocktrade.stocktrade.repositories.PlanRepository;
import stocktrade.stocktrade.repositories.ResearchAnalystRepository;

import java.lang.reflect.Field;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final CouponRepository couponRepository;
    private final CouponService couponService;
    private final RecommendationService recommendationService;
    private final SignUpService signUpService;
    private final ResearchAnalystRepository researchAnalystRepository;
    private final PlanRAService planRAService;
    private final PlanCustomerService planCustomerService;

    public void createNewPlan(PlanDTO planDTO, HttpServletRequest request) throws MessagingException {
        Long planOwnerId = getUserIdFromToken(request);
        List<Roles> userRoles = signUpService.getRoleOfUser(planOwnerId).stream().toList();
        if(userRoles.getFirst().equals(Roles.INSTITUTION) || userRoles.getFirst().equals(Roles.RESEARCH_ANALYST)) {
            boolean isPlanOwner = true ;
            if(userRoles.getFirst().equals(Roles.RESEARCH_ANALYST)) {
                if(!getPermissionForUser(planOwnerId).equals(Permissions.CREATE_PLAN)){
                    isPlanOwner = false;
                }
            }
            if(isPlanOwner) {
                planDTO.setPlanOwnerId(planOwnerId);
                String planOwnerEmail = signUpService.getUserEmail(planOwnerId);
                planDTO.setPlanOwnerEmail(planOwnerEmail);
                List<CouponDTO> couponDTOS = planDTO.getCoupons();
                couponDTOS.forEach(couponDTO -> {
                    couponDTO.setPlanName(planDTO.getPlanName());
                    CouponEntity couponEntity = modelMapper.map(couponDTO, CouponEntity.class);
                    couponRepository.save(couponEntity);
                });
                modelMapper.typeMap(PlanDTO.class, PlanEntity.class)
                        .addMappings(mapper -> {
                            mapper.skip(PlanEntity::setPlanId);
                        });
                PlanEntity planEntity = modelMapper.map(planDTO, PlanEntity.class);
                planRepository.save(planEntity);
                PlanRADTO planRADTO = PlanRADTO.builder()
                        .planName(planEntity.getPlanName())
                        .researchAnalystEmailId(planOwnerEmail)
                        .planPermission(Collections.singleton(Permissions.PLAN_MASTER))
                        .recommendationPermission(Collections.singleton(Permissions.RECOMMENDATION_MASTER))
                        .build();
                planRAService.assignRAToPlan(planRADTO);
            }
        }
    }

    private Permissions getPermissionForUser(Long planOwnerId) {
        ResearchAnalystEntity reseachAnalystEntity = researchAnalystRepository.findById(planOwnerId).orElseThrow(
                ()-> new ResourceNotFound("User is not registered as Research Analyst")
        );
        return reseachAnalystEntity.getPermission();
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
        String planOwnerEmail = (String) planUpdates.get("planOwnerEmail");
        Permissions permissions = planRAService.getPlanPermissionByUser(planName,ownerId);
        if(permissions.equals(Permissions.UPDATE_PLAN) || permissions.equals(Permissions.PLAN_MASTER) || permissions.equals(Permissions.PLAN_UPDATE_DELETE) || permissions.equals(Permissions.PLAN_UPDATE_ADD_CUSTOMER)) {
            PlanEntity planEntity = planRepository.findByPlanOwnerIdAndPlanName(ownerId, planName).orElseThrow(() -> new BadCredentialsException("You are not authorized to update this plan"));
            planUpdates.forEach((field, value) -> {
                Field fieldToBeUpdated = ReflectionUtils.findField(PlanEntity.class, field);
                if (fieldToBeUpdated != null) {
                    fieldToBeUpdated.setAccessible(true);
                    ReflectionUtils.setField(fieldToBeUpdated, planEntity, value);
                }
            });
            planRepository.save(planEntity);
        }
        else {
            throw new AuthenticationServiceException("You are not allowed to update this plan");
        }
    }

    public List<PlanDTO> getAllPlans() {
        List<PlanEntity> planEntities = planRepository.findAll();
        List<PlanDTO> planDTOS = planEntities.stream()
                .map(planEntity -> modelMapper.map(planEntity, PlanDTO.class))
                .toList();
        return getCouponDetailsAndCustomerDetails(planDTOS);
    }

    public List<PlanDTO> getAllPlansPerUser(HttpServletRequest request) {
        Long ownerId = getUserIdFromToken(request);
        List<PlanEntity> planEntities = planRepository.findByPlanOwnerId(ownerId).orElseThrow(()-> new ResourceNotFound("No plan found by this user"));
        List<PlanDTO> planDTOS = planEntities.stream()
                .map(planEntity -> modelMapper.map(planEntity, PlanDTO.class))
                .toList();
        return getCouponDetailsAndCustomerDetails(planDTOS);
    }

    public void deletePlan(Map<String, Object> userPlanName, HttpServletRequest request) {
        Long ownerId = getUserIdFromToken(request);
        String planName = (String) userPlanName.get("planName");
        Permissions permissions = planRAService.getPlanPermissionByUser(planName,ownerId);
        if(permissions.equals(Permissions.DELETE_PLAN) || permissions.equals(Permissions.PLAN_MASTER) || permissions.equals(Permissions.PLAN_DELETE_ADD_CUSTOMER) || permissions.equals(Permissions.PLAN_UPDATE_DELETE)) {
            List<CouponDTO> coupons = couponService.getCouponByPlanName(planName);
            coupons.forEach(coupon -> {
                coupon.setIsExpired(true);
                couponService.saveCoupon(coupon);
            });
            planRepository.deleteByPlanOwnerIdAndPlanName(ownerId, planName);
        }
    }
    private List<PlanDTO> getCouponDetailsAndCustomerDetails(List<PlanDTO> planDTOS){
        planDTOS.forEach(planDTO -> planDTO.setCoupons(couponService.getCouponByPlanName(planDTO.getPlanName())));
        planDTOS.forEach(planDTO -> planDTO.setPlanCustomerDTOS(planCustomerService.getCustomersByPlan(planDTO.getPlanName())));
        return planDTOS;
    }

    public void updateCouponsInAPlan(String planName,List<CouponDTO> couponDTO,String op) {
        couponService.updateCoupons(planName,couponDTO,op);
    }

    public void addRecommendationInAPlan(RecommendationDTO recommendationDTO,HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        List<Roles> roles = signUpService.getRoleOfUserInList(userId);
        Permissions permissions = planRAService.getRecommendationPermissionByUser(recommendationDTO.getPlanName(),userId);
        if(roles.getFirst().equals(Roles.INSTITUTION) || (permissions.equals(Permissions.CREATE_RECOMMENDATION) || permissions.equals(Permissions.RECOMMENDATION_CREATE_UPDATE) || permissions.equals(Permissions.RECOMMENDATION_CREATE_DELETE) || permissions.equals(Permissions.RECOMMENDATION_MASTER))) {
            recommendationService.createNewRecommendationInAPlan(recommendationDTO,userId);
        }
        else{
            throw new AuthorizationDeniedException("You are not allowed to add recommendation in this plan - "+recommendationDTO.getPlanName());
        }
    }

    public void updateRecommendation(Map<String, Object> recommendationUpdates,HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        String planName = (String) recommendationUpdates.get("planName");
        Permissions permissions = planRAService.getRecommendationPermissionByUser(planName, userId);
        if (permissions.equals(Permissions.UPDATE_RECOMMENDATION) || permissions.equals(Permissions.RECOMMENDATION_CREATE_UPDATE) || permissions.equals(Permissions.RECOMMENDATION_UPDATE_DELETE) || permissions.equals(Permissions.RECOMMENDATION_MASTER)) {
            recommendationService.updateRecommendation(recommendationUpdates);
        }
        else{
            throw new AuthorizationDeniedException("You are not allowed to update recommendation in this plan - "+planName);
        }
    }


    public void deleteRecommendation(RecommendationDTO recommendationDTO,HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        Permissions permissions = planRAService.getRecommendationPermissionByUser(recommendationDTO.getPlanName(),userId);
        if(permissions.equals(Permissions.DELETE_RECOMMENDATION) || permissions.equals(Permissions.RECOMMENDATION_CREATE_DELETE) || permissions.equals(Permissions.RECOMMENDATION_UPDATE_DELETE) || permissions.equals(Permissions.RECOMMENDATION_MASTER)) {
            recommendationService.deleteRecommendation(recommendationDTO,userId);
        }
        else{
            throw new AuthorizationDeniedException("You are not allowed to delete recommendation in this plan - "+recommendationDTO.getPlanName());
        }
    }

    public List<PlanDTO> getAllPlansByInstitution(ArrayList<Long> planOwnerIdList) {
        List<PlanEntity> planEntities = planRepository.findByPlanOwnerIdIn(planOwnerIdList);
        List<PlanDTO> planDTOS = planEntities.stream()
                .map(planEntity -> modelMapper.map(planEntity, PlanDTO.class))
                .toList();
        return getCouponDetailsAndCustomerDetails(planDTOS);
    }
}

