package stocktrade.stocktrade.services;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import stocktrade.stocktrade.dto.PlanCustomerDTO;
import stocktrade.stocktrade.entities.PlanCustomerEntity;
import stocktrade.stocktrade.repositories.PlanCustomerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanCustomerService {
    private final PlanCustomerRepository planCustomerRepository;
    private final ModelMapper modelMapper;
    private final SendEmailService sendEmailService;

    public PlanCustomerDTO addCustomerToPlan(PlanCustomerDTO planCustomerDTO, boolean isUserPermitted) throws MessagingException {
        if (isUserPermitted) {
            PlanCustomerEntity planCustomerEntity = modelMapper.map(planCustomerDTO, PlanCustomerEntity.class);
            sendEmailService.sendEmail(
                    planCustomerDTO.getCustomerEmail(),
                    "Welcome to stocksense plan!",
                    "Dear customer,/n You have been successfully added to the plan with plan name - "+planCustomerDTO.getPlanName()+" ./n Regards, /n Team Stocksense"
                    );
            return modelMapper.map(planCustomerRepository.save(planCustomerEntity), PlanCustomerDTO.class);
        }
        throw new AuthorizationDeniedException("You are not authorized to add customers to this plan - "+planCustomerDTO.getPlanName());
    }

    public void removeCustomerFromPlan(PlanCustomerDTO planCustomerDTO, boolean permissionToRemoveCustomer) throws MessagingException {
        if(permissionToRemoveCustomer){
            planCustomerRepository.deleteByPlanNameAndCustomerEmail(planCustomerDTO.getPlanName(),planCustomerDTO.getCustomerEmail());
            sendEmailService.sendEmail(
                    planCustomerDTO.getCustomerEmail(),
                    "Removal from plan - "+planCustomerDTO.getPlanName(),
                    "Dear customer,/n You have been removed from the plan with plan name - "+planCustomerDTO.getPlanName()+" ./n Regards, /n Team Stocksense"
            );
        }
    }

    public List<PlanCustomerDTO> getCustomersByPlan(String planName) {
        return planCustomerRepository.findByPlanName(planName);
    }
}
