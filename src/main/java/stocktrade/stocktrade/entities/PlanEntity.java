package stocktrade.stocktrade.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="plan",indexes = @Index(name = "planId_idx",columnList = "planId"))
public class PlanEntity extends AuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @Column(unique = true)
    private String planName;
    private Long planOwnerId;
    private Double planAmount;
    private String paymentId;
    private Boolean isExpired;
    private Boolean planDuration_three;
    private Boolean planDuration_six;
    private Boolean planDuration_twelve;

}
