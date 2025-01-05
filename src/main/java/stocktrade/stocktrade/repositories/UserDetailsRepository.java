package stocktrade.stocktrade.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stocktrade.stocktrade.entities.UserDetailsEntity;

import java.util.Optional;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetailsEntity,Long> {
    Optional<UserDetailsEntity> findByUserEmail(String username);

    boolean existsByUserEmail(String userEmail);

    Optional<String> findEmailByUserId(Long ownerId);
}
