package stocktrade.stocktrade.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stocktrade.stocktrade.entities.CouponEntity;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity,Long> {
    boolean existsByCouponCode(String couponCode);

    List<CouponEntity> findByPlanName(String planName);

    CouponEntity findByCouponCode(String couponCode);


    void deleteByCouponCodeAndPlanName(String couponCode,String planName);
}
