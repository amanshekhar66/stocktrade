package stocktrade.stocktrade.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stocktrade.stocktrade.entities.InstitutionEntity;

@Repository
public interface InstitutionRepository extends JpaRepository<InstitutionEntity,Long> {
}
