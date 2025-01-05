package stocktrade.stocktrade.services;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import stocktrade.stocktrade.dto.CouponDTO;
import stocktrade.stocktrade.dto.CustomerDTO;
import stocktrade.stocktrade.dto.PlanCustomerDTO;
import stocktrade.stocktrade.entities.CustomerEntity;
import stocktrade.stocktrade.entities.PlanCustomerEntity;
import stocktrade.stocktrade.enums.Permissions;
import stocktrade.stocktrade.enums.Roles;
import stocktrade.stocktrade.exceptions.ResourceNotFound;
import stocktrade.stocktrade.repositories.CustomerRepository;
import stocktrade.stocktrade.repositories.PlanCustomerRepository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final PlanCustomerRepository planCustomerRepository;
    private final JwtService jwtService;
    public void createNewCustomer(CustomerEntity customer) {
        customerRepository.save(customer);
    }

    public void deleteCustomer(Long userId) {
        customerRepository.deleteById(userId);
    }

    public void updateCustomer(Map<String, Object> customerUpdates, HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        CustomerEntity customerEntity = customerRepository.findById(userId).orElseThrow(
                ()-> new ResourceNotFound("User not found,kindly register again")
        );
        customerUpdates.forEach((field,value)->{
            Field fieldToBeUpdated = ReflectionUtils.findField(CustomerEntity.class,field);
            if(fieldToBeUpdated!=null){
                fieldToBeUpdated.setAccessible(true);
                ReflectionUtils.setField(fieldToBeUpdated,customerEntity,value);
            }
        });
        customerRepository.save(customerEntity);
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

    public CustomerDTO getCustomerDetails(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        CustomerEntity customerEntity = customerRepository.findById(userId).orElseThrow(()->
                new ResourceNotFound("No customer found by this id"));
        return modelMapper.map(customerEntity, CustomerDTO.class);
    }
}
