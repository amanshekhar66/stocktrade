package stocktrade.stocktrade.entities;

import jakarta.persistence.*;
import lombok.*;
import stocktrade.stocktrade.enums.Permissions;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "plan_RA",uniqueConstraints = @UniqueConstraint(name = "plan_ra_name",
            columnNames = {"planName","researchAnalystEmailId"}),
        indexes = {
                  @Index(name = "planName_idx",columnList = "planName"),
                  @Index(name = "researchAnalystEmail_idx",columnList = "researchAnalystEmailId")})
public class PlanRAEntity extends AuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String planName;
    private String researchAnalystEmailId;
    private Long researchAnalystId;
    private Permissions planPermission;
    private Permissions recommendationPermission;
}
