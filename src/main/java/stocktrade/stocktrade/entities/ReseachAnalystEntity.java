package stocktrade.stocktrade.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "research_Analyst")
public class ReseachAnalystEntity {

    @Id
    private Long id;
    @Column(updatable = false,nullable = false)
    private String name;
    private String email;
    private LocalDate DOB;
    private String city;
    private String state;
    private String certificate;
    private String disclaimer;
    private Boolean isVerified;
    private Boolean isApproved;
    @Column(updatable = false)
    private LocalDateTime approvalDate;
    private String aadhar_No;
    private String pan_No;
    private String profileUrl;
    private String description;
    @Column(updatable = false,nullable = false)
    private String regNo;

}
