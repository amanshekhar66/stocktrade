package stocktrade.stocktrade.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "recommendation",uniqueConstraints =
@UniqueConstraint(name = "reco_plan",columnNames = {"recommendationName","planName"}))
public class RecommendationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String recommendationName;
    private Long ownerId;
    private String exchangeName;
    private String scriptTokenNo;
    private String scriptName;
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
