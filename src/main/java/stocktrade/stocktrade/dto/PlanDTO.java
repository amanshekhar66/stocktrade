package stocktrade.stocktrade.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanDTO {

    @NotNull
    @NotBlank
    private String planName;
    private Long planOwnerId;
    private Double planAmount;
    @NotNull
    private String paymentId;
    @JsonProperty("isExpired")
    private Boolean isExpired;
    private Boolean planDuration_three;
    private Boolean planDuration_six;
    private Boolean planDuration_twelve;
}
