package stocktrade.stocktrade.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "plan_customer",uniqueConstraints = @UniqueConstraint(name = "plan_customer_name",
        columnNames = {"planName","customerEmailId"}),
        indexes = {
                @Index(name = "planName_idx",columnList = "planName"),
                @Index(name = "customerEmailId_idx",columnList = "customerEmailId")})

public class PlanCustomerEntity extends AuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String planName;
    private String customerEmail;
    private String customerName;

}
