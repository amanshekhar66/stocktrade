package stocktrade.stocktrade.dto;

import jakarta.validation.constraints.Email;
import lombok.*;
import stocktrade.stocktrade.enums.Roles;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtpDTO {
    @Email
    private String userEmail;
    private Integer verificationCode;
    private LocalDateTime createdAt;
    private LocalDateTime expriryDate;
    private Integer otpCount;
    private Boolean otpVerified;
}
