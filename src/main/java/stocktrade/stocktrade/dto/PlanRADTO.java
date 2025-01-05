package stocktrade.stocktrade.dto;

import lombok.*;
import stocktrade.stocktrade.enums.Permissions;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanRADTO {

    private String planName;
    private String researchAnalystEmailId;
    private Set<Permissions> planPermission;
    private Set<Permissions> recommendationPermission;
}
