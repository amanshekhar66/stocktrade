package stocktrade.stocktrade.controllers;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import stocktrade.stocktrade.dto.CouponDTO;
import stocktrade.stocktrade.dto.PlanDTO;
import stocktrade.stocktrade.dto.RecommendationDTO;
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
                                                HttpServletRequest request) throws MessagingException {
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

    @PreAuthorize("hasAnyRole('ADMIN','INSTITUTION','RESEARCH_ANALYST')")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deletePlan(@RequestBody Map<String,Object> planName,
                                             HttpServletRequest request){
        planService.deletePlan(planName,request);
        return new ResponseEntity<>("Plan deleted successfully",HttpStatus.OK);
    }

    @PatchMapping("/updateCoupon")
    public ResponseEntity<String> updateCouponsByPlan(@RequestBody @Valid List<CouponDTO> couponDTO,
                                                      @RequestParam(defaultValue = "")String planName,
                                                      @RequestParam(defaultValue = "")String op){
        planService.updateCouponsInAPlan(planName,couponDTO,op);
        return new ResponseEntity<>("Coupons updated successfully!",HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('INSTITUTION','ADMIN','RESEARCH_ANALYST')")
    @PostMapping("/addRecommendation")
    public ResponseEntity<String> addRecommendationInAPlan(@RequestBody @Valid RecommendationDTO recommendationDTO,
                                                           HttpServletRequest request){
        planService.addRecommendationInAPlan(recommendationDTO,request);
        return new ResponseEntity<>("Recommendation created successfully in the plan - "+recommendationDTO.getPlanName(),HttpStatus.CREATED);
    }
    @PatchMapping("/updateRecommendation")
    public ResponseEntity<String> updateRecommendation(@RequestBody Map<String,Object> recommendationUpdates,
                                                       HttpServletRequest request){
        planService.updateRecommendation(recommendationUpdates,request);
        return new ResponseEntity<>("Recommendation Updated Successfully",HttpStatus.OK);
    }

    @DeleteMapping("/deleteRecommendation")
    public ResponseEntity<String> deleteRecommendation(@RequestBody RecommendationDTO recommendationDTO,
                                                       HttpServletRequest request){
        planService.deleteRecommendation(recommendationDTO,request);
        return new ResponseEntity<>("Recommendation Updated Successfully",HttpStatus.OK);
    }
}
