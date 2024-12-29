package stocktrade.stocktrade.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stocktrade.stocktrade.entities.SessionEntity;
import stocktrade.stocktrade.entities.UserDetailsEntity;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity,Long> {
    boolean existsByUser(UserDetailsEntity userDetails);

    List<SessionEntity> findByUser(UserDetailsEntity userDetails);
    @Transactional
    void deleteByRefreshToken(String refreshToken);
}
