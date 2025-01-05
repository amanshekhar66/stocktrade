package stocktrade.stocktrade.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerEntity {
    @Id
    private Long userId;

    @Column(unique = true)
    private String userEmail;
    private String userPassword;
    private String firstName;
    private String surName;
    private LocalDate DOB;
    private String gender;
    private Boolean isVerified;
}
