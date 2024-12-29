package stocktrade.stocktrade.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stocktrade.stocktrade.entities.OtpEntity;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity,Long> {
     Optional<OtpEntity> findByUserEmail(String email);

    boolean existsByUserEmail(String email);
    @Transactional
    void deleteByUserEmail(String email);
}
