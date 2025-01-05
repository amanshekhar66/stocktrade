package stocktrade.stocktrade.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanDTO {

    @NotNull
    @NotBlank
    private String planName;
    private Long planOwnerId;
    private String planOwnerEmail;
    private Double planAmount_three;
    private Double planAmount_six;
    private Double planAmount_twelve;
    @NotNull
    private String paymentId;
    @JsonProperty("isExpired")
    private Boolean isExpired;
    private Boolean planDuration_three;
    private Boolean planDuration_six;
    private Boolean planDuration_twelve;
    @JsonProperty("isCouponDetailsUpdated")
    private Boolean isCouponDetailsUpdated;

    private List<CouponDTO> coupons;
    private List<PlanCustomerDTO> planCustomerDTOS;
}
