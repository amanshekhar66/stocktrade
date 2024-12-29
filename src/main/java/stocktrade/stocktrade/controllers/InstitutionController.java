package stocktrade.stocktrade.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import stocktrade.stocktrade.dto.InstitutionDTO;
import stocktrade.stocktrade.services.InstitutionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/institution")
@RequiredArgsConstructor
public class InstitutionController {

    private final InstitutionService institutionService;

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


}
