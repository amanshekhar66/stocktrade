package stocktrade.stocktrade.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import stocktrade.stocktrade.entities.PlanRAEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRARepository extends JpaRepository<PlanRAEntity,Long> {
    Optional<PlanRAEntity> findByPlanNameAndResearchAnalystEmailId(String planName, String researchAnalystEmailId);

    void deleteByPlanNameAndResearchAnalystEmailId(String planName, String researchAnalystEmailId);

    List<PlanRAEntity> findByPlanName(String planName);

    Optional<PlanRAEntity> findByPlanNameAndResearchAnalystId(String planName, Long researchAnalystId);
}
