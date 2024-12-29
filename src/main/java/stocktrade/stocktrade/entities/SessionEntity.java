package stocktrade.stocktrade.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String refreshToken;
    @ManyToOne()
    private UserDetailsEntity user;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;
}
