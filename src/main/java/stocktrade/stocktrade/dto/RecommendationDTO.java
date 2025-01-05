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
public class RecommendationDTO {

    private String recommendationName;
    private String exchangeName;
    private String scriptName;
    private String scriptTokenNo;
    private String instrumentType;
    private String expiry;
    private Double entryPrice;
    private Double stopLoss;
    private Double targetPrice;
    private String entryType;
    private Double currentPrice;
    private String riskRewardRatio;
    private String planName;
    private LocalDateTime expiryDate;
    private Boolean isExpired;
}
