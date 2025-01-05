package stocktrade.stocktrade.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import stocktrade.stocktrade.dto.PlanCustomerDTO;
import stocktrade.stocktrade.entities.PlanCustomerEntity;

import java.util.List;

@Repository
public interface PlanCustomerRepository extends JpaRepository<PlanCustomerEntity,Long> {
    //void deleteByPlanNameAndCustomerEmailId(String planName, String customerEmailId);

    List<PlanCustomerDTO> findByPlanName(String planName);

    void deleteByPlanNameAndCustomerEmail(String planName, String customerEmail);
}
