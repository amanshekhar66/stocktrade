package stocktrade.stocktrade.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import stocktrade.stocktrade.dto.CouponDTO;
import stocktrade.stocktrade.entities.CouponEntity;
import stocktrade.stocktrade.repositories.CouponRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final ModelMapper modelMapper;
    private final CouponRepository couponRepository;

    public List<CouponDTO> getCouponByPlanName(String planName){
        List<CouponEntity> couponEntities = couponRepository.findByPlanName(planName);
        return couponEntities.stream()
                .map(couponEntity -> modelMapper.map(couponEntity, CouponDTO.class))
                .toList();
    }

    public void saveCoupon(CouponDTO coupon) {
        if(couponRepository.existsByCouponCode(coupon.getCouponCode())){
            CouponEntity couponEntity = modelMapper.map(coupon, CouponEntity.class);
            couponRepository.save(couponEntity);
        }
    }

    public void updateCoupons(String planName,List<CouponDTO> couponDTO,String op) {
        List<CouponEntity> couponEntities = couponRepository.findByPlanName(planName);
        switch (op) {
            case "update" -> couponDTO.forEach(coupon -> {
                couponEntities.forEach(couponEntity -> {
                    if (couponEntity.getCouponCode().equals(coupon.getCouponCode())) {
                        couponEntity.setCouponDiscount(coupon.getCouponDiscount());
                        couponEntity.setExpiredDate(coupon.getExpiredDate());
                        couponRepository.save(couponEntity);
                    }
                });
            });
            case "delete" -> {
                CouponDTO couponDTO1 = couponDTO.getFirst();
                couponRepository.deleteByCouponCodeAndPlanName(couponDTO1.getCouponCode(),planName);
            }
            case "add" -> couponDTO.forEach(this::saveCoupon);
        }
    }

}
