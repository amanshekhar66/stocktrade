package stocktrade.stocktrade.controllers;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import stocktrade.stocktrade.dto.*;
import stocktrade.stocktrade.services.CustomerService;
import stocktrade.stocktrade.services.InstitutionService;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/institution")
@RequiredArgsConstructor
public class InstitutionController {

    private final InstitutionService institutionService;
    private final CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<String> createNewInstitution(@RequestBody @Valid InstitutionDTO institutionDTO,
                                                       HttpServletRequest request){
        institutionService.createNewInstitution(institutionDTO,request);
        return new ResponseEntity<>("Institution created successfully", HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/viewAll")
    public ResponseEntity<List<InstitutionDTO>> getAllInstitutions(){
        return new ResponseEntity<>(institutionService.getAllInstitutions(),HttpStatus.FOUND);
    }

    @PreAuthorize("hasAnyRole('ADMIN','INSTITUTION')")
    @GetMapping("/getInstitution")
    public ResponseEntity<InstitutionDTO> getInstitutionById(HttpServletRequest request){
        return new ResponseEntity<>(institutionService.getInstitutionById(request),HttpStatus.FOUND);
    }

    @PreAuthorize("hasAnyRole('ADMIN','INSTITUTION')")
    @PatchMapping("/update")
    public ResponseEntity<String> updateInstituteById(@RequestBody Map<String,Object> institutionUpdates,
                                                      HttpServletRequest request){
        institutionService.updateInstituteById(institutionUpdates,request);
        return new ResponseEntity<>("Details of Institution updated successfully",
                HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','INSTITUTION')")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteInstitutionById(HttpServletRequest request){
        institutionService.deleteInstitutionById(request);
        return new ResponseEntity<>("Institution deleted successfully",HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('ADMIN','INSTITUTION')")
    @PostMapping("/addRA")
    public ResponseEntity<String> addResearchAnalyst(@RequestBody @Valid ResearchAnalystDTO researchAnalystDTO,
                                                     HttpServletRequest request) throws AuthenticationException {
        institutionService.addResearchAnalyst(researchAnalystDTO,request);
        return new ResponseEntity<>("Research Analyst created successfully",HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN','INSTITUTION')")
    @PostMapping("/assignRAToPlan")
    private ResponseEntity<String> assignRAToPlan(@RequestBody @Valid PlanRADTO planRADTO) throws MessagingException {
        institutionService.assignRAToPlan(planRADTO);
        return new ResponseEntity<>("Research Analyst assigned to plan successfully",HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('ADMIN','INSTITUTION')")
    @PatchMapping("/updateRAInPlan")
    private ResponseEntity<String> updateRAInPlan(@RequestBody @Valid PlanRADTO planRADTO) throws MessagingException {
        institutionService.updateRAInPlan(planRADTO);
        return new ResponseEntity<>("Research Analyst details updated in plan successfully",HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('ADMIN','INSTITUTION')")
    @DeleteMapping("/removeRAFromPlan")
    private ResponseEntity<String> removeRAFromPlan(@RequestBody @Valid PlanRADTO planRADTO) throws MessagingException {
        institutionService.removeRAFromPlan(planRADTO);
        return new ResponseEntity<>("Research Analyst removed from plan successfully",HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('ADMIN','INSTITUTION')")
    @GetMapping("/getAllRA")
    public ResponseEntity<List<ResearchAnalystDTO>> getAllResearchAnalysts(HttpServletRequest request){
        return new ResponseEntity<>(institutionService.getAllResearchAnalysts(request),HttpStatus.FOUND);
    }

//    @PreAuthorize("hasAnyRole('ADMIN','INSTITUTION')")
    @GetMapping("/getAllPlans")
    public ResponseEntity<List<PlanDTO>> getAllPlans(HttpServletRequest request){
        return new ResponseEntity<>(institutionService.getAllPlans(request),HttpStatus.FOUND);
    }

    @PreAuthorize("hasAnyRole('ADMIN','INSTITUTION')")
    @GetMapping("/getRAByPlan")
    public ResponseEntity<List<PlanRADTO>> getRAByPlan(@RequestBody String planName){
        return new ResponseEntity<>(institutionService.getRAByPlan(planName),HttpStatus.FOUND);
    }

    @PostMapping("/addCustomerToPlan")
    public ResponseEntity<PlanCustomerDTO> addCustomerToPlan(@RequestBody PlanCustomerDTO planCustomerDTO,
                                                    HttpServletRequest request) throws MessagingException {
        return new ResponseEntity<>(institutionService.addCustomerToPlan(planCustomerDTO,request),HttpStatus.OK);
    }

    @DeleteMapping("/removeCustomerFromPlan")
    public ResponseEntity<String> removeCustomerFromPlan(@RequestBody PlanCustomerDTO planCustomerDTO,
                                                         HttpServletRequest request) throws MessagingException {
        institutionService.removeCustomerFromPlan(planCustomerDTO,request);
        return new ResponseEntity<>("Customer removed from plan successfully",HttpStatus.OK);

    }
}
