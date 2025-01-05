package stocktrade.stocktrade.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanCustomerDTO {

    private String planName;
    private String customerEmail;
    private String customerName;
}
