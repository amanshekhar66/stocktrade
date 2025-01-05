package stocktrade.stocktrade.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import stocktrade.stocktrade.entities.RecommendationEntity;

import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<RecommendationEntity,Long> {

    @Query("SELECT MAX(e.id) FROM RecommendationEntity e")
    Long getLastRecordId();

    Optional<RecommendationEntity> findByRecommendationNameAndPlanName(String recommendationName, String planName);
}
