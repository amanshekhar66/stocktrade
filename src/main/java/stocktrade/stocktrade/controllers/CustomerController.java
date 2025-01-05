package stocktrade.stocktrade.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stocktrade.stocktrade.dto.CustomerDTO;
import stocktrade.stocktrade.services.CustomerService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;

    @PatchMapping("/update")
    public ResponseEntity<String> updateCustomer(@RequestBody Map<String,Object> customerUpdates,
                                                      HttpServletRequest request){
        customerService.updateCustomer(customerUpdates,request);
        return new ResponseEntity<>("Customer details updated successfully", HttpStatus.OK);
    }
    @GetMapping("/getCustomer")
    public ResponseEntity<CustomerDTO> getCustomerDetails(HttpServletRequest request){
        return new ResponseEntity<>(customerService.getCustomerDetails(request),HttpStatus.FOUND);
    }
}
