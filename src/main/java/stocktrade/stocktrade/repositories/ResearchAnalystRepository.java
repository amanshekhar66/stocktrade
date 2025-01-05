package stocktrade.stocktrade.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stocktrade.stocktrade.entities.InstitutionEntity;
import stocktrade.stocktrade.entities.ResearchAnalystEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResearchAnalystRepository extends JpaRepository<ResearchAnalystEntity,Long> {
    Optional<ResearchAnalystEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    void deleteByEmail(String email);

    List<ResearchAnalystEntity> findByInstitutionEntity(InstitutionEntity institutionEntity);

    Optional<Long> findIdByEmail(String researchAnalystEmailId);
}
