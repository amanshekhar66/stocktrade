package stocktrade.stocktrade.controllers;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import stocktrade.stocktrade.dto.PlanCustomerDTO;
import stocktrade.stocktrade.dto.PlanDTO;
import stocktrade.stocktrade.dto.ResearchAnalystDTO;
import stocktrade.stocktrade.services.ResearchAnalystService;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/researchAnalyst")
@RequiredArgsConstructor
public class ResearchAnalystController {
    private final ResearchAnalystService researchAnalystService;
    @PostMapping("/create")
    public ResponseEntity<String> createANewResearchAnalyst(@RequestBody @Valid ResearchAnalystDTO researchAnalystDTO,
                                                            HttpServletRequest request) throws AuthenticationException {
        researchAnalystService.createANewResearchAnalyst(researchAnalystDTO,request,false);
        return new ResponseEntity<>("Research Analyst created  successfully", HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('INSTITUTION','ADMIN','RESEARCH_ANALYST')")
    @PatchMapping("/update")
    public ResponseEntity<String> updateResearchAnalyst(Map<String,Object> analystUpdates){
        researchAnalystService.updateResearchAnalyst(analystUpdates);
        return new ResponseEntity<>("Details of research analyst has been updated successfully",HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('INSTITUTION','ADMIN','RESEARCH_ANALYST')")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteResearchAnalyst(@RequestBody String email){
        researchAnalystService.deleteResearchAnalyst(email);
        return new ResponseEntity<>("Research Analyst deleted successfully !",HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RESEARCH_ANALYST')")
    @GetMapping("/getAllPlans")
    public ResponseEntity<List<PlanDTO>> getAllPlans(HttpServletRequest request){
        return new ResponseEntity<>(researchAnalystService.getAllPlans(request),HttpStatus.FOUND);
    }

    @PostMapping("/addCustomerToPlan")
    public ResponseEntity<PlanCustomerDTO> addCustomerToPlan(@RequestBody PlanCustomerDTO planCustomerDTO,
                                                             HttpServletRequest request) throws MessagingException {
        return new ResponseEntity<>(researchAnalystService.addCustomerToPlan(planCustomerDTO,request),HttpStatus.OK);
    }

    @DeleteMapping("/removeCustomerFromPlan")
    public ResponseEntity<String> removeCustomerFromPlan(@RequestBody PlanCustomerDTO planCustomerDTO,
                                                         HttpServletRequest request) throws MessagingException {
        researchAnalystService.removeCustomerFromPlan(planCustomerDTO,request);
        return new ResponseEntity<>("Customer removed from plan successfully",HttpStatus.OK);

    }
}
