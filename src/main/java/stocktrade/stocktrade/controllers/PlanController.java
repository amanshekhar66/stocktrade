package stocktrade.stocktrade.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import stocktrade.stocktrade.dto.PlanDTO;
import stocktrade.stocktrade.services.PlanService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plan")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;

    @PreAuthorize("hasAnyRole('INSTITUTION','RESEARCH_ANALYST','ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<String> createNewPlan(@RequestBody @Valid PlanDTO planDTO,
                                                HttpServletRequest request){
        planService.createNewPlan(planDTO,request);
        return new ResponseEntity<>("Plan created successfully", HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('INSTITUTION',RESEARCH_ANALYST','ADMIN')")
    @PatchMapping("/update")
    public ResponseEntity<String> updatePlanById(@RequestBody Map<String,Object> planUpdates,
                                                 HttpServletRequest request){
        planService.updatePlanById(planUpdates,request);
        return new ResponseEntity<>("Plan updated successfully",HttpStatus.OK);

    }

    @GetMapping("/viewAllPlans")
    public ResponseEntity<List<PlanDTO>> getAllPlans(){
        return new ResponseEntity<>(planService.getAllPlans(),HttpStatus.FOUND);
    }

    @GetMapping("/viewPlanPerUser")
    public ResponseEntity<List<PlanDTO>> getAllPlansPerUser(HttpServletRequest request){
        return new ResponseEntity<>(planService.getAllPlansPerUser(request),HttpStatus.FOUND);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deletePlan(@RequestBody Map<String,Object> ownerName,
                                             HttpServletRequest request){
        planService.deletePlan(ownerName,request);
        return new ResponseEntity<>("Plan deleted successfully",HttpStatus.OK);
    }
}
