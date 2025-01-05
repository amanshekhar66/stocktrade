package stocktrade.stocktrade.services;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import stocktrade.stocktrade.dto.PlanRADTO;
import stocktrade.stocktrade.entities.PlanRAEntity;
import stocktrade.stocktrade.enums.Permissions;
import stocktrade.stocktrade.repositories.PlanRARepository;
import stocktrade.stocktrade.repositories.ResearchAnalystRepository;
import stocktrade.stocktrade.utils.PermissionMapping;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanRAService {
    private final PlanRARepository planRARepository;
    private final ModelMapper modelMapper;
    private final ResearchAnalystRepository researchAnalystRepository;
    private final SignUpService signUpService;
    private final SendEmailService sendEmailService;
    PermissionMapping permissionMapping = new PermissionMapping();
    public void assignRAToPlan(PlanRADTO planRADTO) throws MessagingException {
        Permissions planPermission = permissionMapping.getSuperPermission(planRADTO.getPlanPermission());
        Permissions recommendationPermission = permissionMapping.getSuperPermission(planRADTO.getRecommendationPermission());
        Long researchAnalystId = signUpService.getUserId(planRADTO.getResearchAnalystEmailId());
        PlanRAEntity planRAEntity = PlanRAEntity.builder()
                .researchAnalystEmailId(planRADTO.getResearchAnalystEmailId())
                .planName(planRADTO.getPlanName())
                .planPermission(planPermission)
                .recommendationPermission(recommendationPermission)
                .researchAnalystId(researchAnalystId)
                .build();
        Optional<PlanRAEntity> planRA = planRARepository.findByPlanNameAndResearchAnalystEmailId(
                planRADTO.getPlanName(),
                planRADTO.getResearchAnalystEmailId()
        );
        planRA.ifPresent(planRAEntity1 -> planRAEntity.setId(planRAEntity1.getId()));
        planRARepository.save(planRAEntity);
        if(planRA.isEmpty()) {
            sendEmailService.sendEmail(
                    planRADTO.getResearchAnalystEmailId(),
                    "Congratulations!",
                    "Dear Research Analyst, /n You have been successfully assigned to the plan with plan name - " + planRADTO.getPlanName() + " ./n Regards, /n Team Stocksense");
        }
    }

    public void deleteRAFromPlan(PlanRADTO planRADTO) throws MessagingException {
        planRARepository.deleteByPlanNameAndResearchAnalystEmailId(planRADTO.getPlanName(),planRADTO.getResearchAnalystEmailId());
        sendEmailService.sendEmail(
                planRADTO.getResearchAnalystEmailId(),
                "Notice!",
                "Dear Research Analyst, /n You have been removed from the plan with plan name - "+planRADTO.getPlanName()+" ./n Regards, /n Team Stocksense");
    }

    public List<PlanRADTO> getRAByPlan(String planName) {
        List<PlanRAEntity> planRAEntities = planRARepository.findByPlanName(planName);
        return planRAEntities.stream()
                .map(planRAEntity -> modelMapper.map(planRAEntity,PlanRADTO.class))
                .toList();
    }

    public Permissions getPlanPermissionByUser(String planName,Long researchAnalystId) {
        PlanRAEntity planRAEntity = planRARepository.findByPlanNameAndResearchAnalystId(planName,researchAnalystId).
                orElseThrow(()->  new AuthorizationDeniedException("You are not assigned to this plan"));
        return planRAEntity.getPlanPermission();
    }
    public Permissions getRecommendationPermissionByUser(String planName,Long researchAnalystId) {
        PlanRAEntity planRAEntity = planRARepository.findByPlanNameAndResearchAnalystId(planName,researchAnalystId).
                orElseThrow(()->  new AuthorizationDeniedException("You are not assigned to this plan"));
        return planRAEntity.getRecommendationPermission();
    }
}
