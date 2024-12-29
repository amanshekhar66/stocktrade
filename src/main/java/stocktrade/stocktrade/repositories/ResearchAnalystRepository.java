package stocktrade.stocktrade.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stocktrade.stocktrade.entities.ReseachAnalystEntity;

@Repository
public interface ResearchAnalystRepository extends JpaRepository<ReseachAnalystEntity,Long> {
}
