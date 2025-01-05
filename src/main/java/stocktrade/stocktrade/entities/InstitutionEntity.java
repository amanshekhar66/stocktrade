package stocktrade.stocktrade.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "institution",
        indexes = @Index(name= "institution_email_idx",columnList = "institutionEmail"))
public class InstitutionEntity extends AuditableEntity{
    @Id
    private Long id;
    @Column(nullable = false,updatable = false)
    private String institutionName;
    @Column(unique = true,nullable = false)
    private String institutionEmail;
    @Column(updatable = false)
    private String ownerName;
    private String phoneNo;
    private LocalDate DOB;
//    private String address1;
//    private String address2;
    private String city;
    private String state;
    private String description;
    private String disclaimer;
    private String companyCertificate;
    @Column(updatable = false)
    private String regNumber;
    private Boolean isVerified;
    private Boolean isApproved;
    private LocalDateTime approvalDate;
    private String aadharNo;
    private String panNo;
    private String instagram;
    private String linkedIn;
    private String twitter;
    private String facebook;
    @OneToMany(mappedBy = "institutionEntity",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnoreProperties("institutionEntity")
    private List<ResearchAnalystEntity> reseachAnalystEntityList;

}
