package stocktrade.stocktrade.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = @Index(name = "userEmail_idx",columnList = "userEmail"))
public class OtpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;
    private Integer verificationCode;
    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;
    private Integer otpCount;
    private Boolean otpVerified;
    @PrePersist
    private void setExpiryDate() {
        this.expiryDate = createdAt.plusMinutes(1);
    }
}

