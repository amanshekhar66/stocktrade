package stocktrade.stocktrade.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stocktrade.stocktrade.entities.PlanEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<PlanEntity,Long> {
    Optional<List<PlanEntity>> findByPlanOwnerId(Long ownerId);

    Optional<PlanEntity> findByPlanOwnerIdAndPlanName(Long ownerId, String planName);

    void deleteByPlanOwnerIdAndPlanName(Long ownerId, String planName);
}
