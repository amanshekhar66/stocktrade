package stocktrade.stocktrade.services;

import io.jsonwebtoken.JwtException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import stocktrade.stocktrade.dto.PlanCustomerDTO;
import stocktrade.stocktrade.dto.PlanDTO;
import stocktrade.stocktrade.dto.ResearchAnalystDTO;
import stocktrade.stocktrade.dto.UserDetailsDTO;
import stocktrade.stocktrade.entities.InstitutionEntity;
import stocktrade.stocktrade.entities.ResearchAnalystEntity;
import stocktrade.stocktrade.enums.Permissions;
import stocktrade.stocktrade.enums.Roles;
import stocktrade.stocktrade.exceptions.ResourceNotFound;
import stocktrade.stocktrade.repositories.InstitutionRepository;
import stocktrade.stocktrade.repositories.ResearchAnalystRepository;

import javax.naming.AuthenticationException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ResearchAnalystService{
    private final ResearchAnalystRepository researchAnalystRepository;
    private final SignUpService signUpService;
    private final ModelMapper modelMapper;
    private final PlanService planService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final InstitutionRepository institutionRepository;
    private final CustomerService customerService;
    private final PlanRAService planRAService;
    private final PlanCustomerService planCustomerService;


    public void createANewResearchAnalyst(ResearchAnalystDTO researchAnalystDTO,
                                          HttpServletRequest request,
                                          boolean isAddedByInstitution) throws AuthenticationException {
        ResearchAnalystEntity reseachAnalystEntity = modelMapper.map(researchAnalystDTO, ResearchAnalystEntity.class);
        Long currentUserId = getUserIdFromToken(request);
        if(!isAddedByInstitution){
            reseachAnalystEntity.setId(currentUserId);
            researchAnalystRepository.save(reseachAnalystEntity);
            customerService.deleteCustomer(currentUserId);
        }
        else{
            UserDetailsDTO userDetails = UserDetailsDTO.builder()
                    .firstName(researchAnalystDTO.getFirstName())
                    .surName(researchAnalystDTO.getSurName())
                    .DOB(researchAnalystDTO.getDOB())
                    .userEmail(researchAnalystDTO.getEmail())
                    .roles(Set.of(Roles.RESEARCH_ANALYST))
                    .isVerified(false)
                    .gender(researchAnalystDTO.getGender())
                    .userPassword(passwordEncoder.encode(researchAnalystDTO.getPassword()))
                    .build();
            signUpService.createNewUser(userDetails);
            Long userId = signUpService.getUserId(researchAnalystDTO.getEmail());
            reseachAnalystEntity.setId(userId);
            reseachAnalystEntity.setInstitutionEntity(institutionRepository.findById(currentUserId)
                    .orElseThrow(()-> new ResourceNotFound("System error: cannot find the current user as institution")));
            researchAnalystRepository.save(reseachAnalystEntity);
        }

    }


    public void updateResearchAnalyst(Map<String, Object> analystUpdates) {
        String email = (String) analystUpdates.get("email");
        ResearchAnalystEntity reseachAnalystEntity = researchAnalystRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFound("No research analyst found by the email provided - "+email));
        analystUpdates.forEach((field,value)->{
            if(field.equals("DOB") || field.equals("approvalDate")){
                value = parseValues(value,field);
            }
            Field fieldToBeUpdated = ReflectionUtils.findField(ResearchAnalystEntity.class,field);
            if(fieldToBeUpdated!=null && !field.equals("regNo")){
                fieldToBeUpdated.setAccessible(true);
                ReflectionUtils.setField(fieldToBeUpdated,reseachAnalystEntity,value);
            }
        });
        researchAnalystRepository.save(reseachAnalystEntity);
    }


    private Object parseValues(Object value,String field){
        if(field.equals("DOB")){
            return LocalDate.parse(value.toString());
        }
        return LocalDateTime.parse(value.toString());
    }

    public void deleteResearchAnalyst(String email) {
        if(researchAnalystRepository.existsByEmail(email)){
            researchAnalystRepository.deleteByEmail(email);
        }
        throw new ResourceNotFound("No analyst found by this email - "+email);
    }

    public List<ResearchAnalystDTO> getAllResearchAnalysts(InstitutionEntity institutionEntity) {
        List<ResearchAnalystEntity> reseachAnalystEntityList = researchAnalystRepository.findByInstitutionEntity(institutionEntity);
        return reseachAnalystEntityList.stream()
                .map(reseachAnalystEntity -> modelMapper.map(reseachAnalystEntity,ResearchAnalystDTO.class))
                .toList();
    }
    public List<ResearchAnalystEntity> getResearchAnalystByInstitution(InstitutionEntity institutionEntity) {
        return researchAnalystRepository.findByInstitutionEntity(institutionEntity);
    }

    public List<PlanDTO> getAllPlans(HttpServletRequest request) {
       return planService.getAllPlansPerUser(request);
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
    public void removeCustomerFromPlan(PlanCustomerDTO planCustomerDTO, HttpServletRequest request) throws MessagingException {
        Long userId = getUserIdFromToken(request);
        boolean isUserPermitted = false;
        List<Roles> roles = signUpService.getRoleOfUserInList(userId);
        if(roles.getFirst().equals(Roles.INSTITUTION)){
            isUserPermitted = true;
        }
        else {
            Permissions permissions = planRAService.getPlanPermissionByUser(planCustomerDTO.getPlanName(), userId);
            if (permissions.equals(Permissions.PLAN_MASTER) || permissions.equals(Permissions.ADD_CUSTOMER_TO_PLAN)) {
                isUserPermitted = true;
            }
        }
        planCustomerService.removeCustomerFromPlan(planCustomerDTO,isUserPermitted);
    }

    public PlanCustomerDTO addCustomerToPlan(PlanCustomerDTO planCustomerDTO, HttpServletRequest request) throws MessagingException {
        Long userId = getUserIdFromToken(request);
        boolean isUserPermitted = false;
        List<Roles> roles = signUpService.getRoleOfUserInList(userId);
        if(roles.getFirst().equals(Roles.INSTITUTION)){
            isUserPermitted = true;
        }
        else {
            Permissions permissions = planRAService.getPlanPermissionByUser(planCustomerDTO.getPlanName(), userId);
            if (permissions.equals(Permissions.PLAN_MASTER) || permissions.equals(Permissions.ADD_CUSTOMER_TO_PLAN)) {
                isUserPermitted = true;
            }
        }
        return planCustomerService.addCustomerToPlan(planCustomerDTO,isUserPermitted);
    }
}
