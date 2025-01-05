package stocktrade.stocktrade.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CouponDTO {

    private String couponCode;
    private Double couponDiscount;
    private String planName;
    private Boolean isExpired;
    private LocalDateTime expiredDate;
}
