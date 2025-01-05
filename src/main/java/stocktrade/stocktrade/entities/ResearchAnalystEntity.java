package stocktrade.stocktrade.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktrade.stocktrade.enums.Permissions;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "research_Analyst")
public class ResearchAnalystEntity {

    @Id
    private Long id;
    @Column(updatable = false,nullable = false)
    private String firstName;
    private String surName;
    private String gender;
    @Column(unique = true,nullable = false)
    private String email;
    private LocalDate DOB;
    private String city;
    private String state;
    private String certificate;
    private String disclaimer;
    private Boolean isVerified;
    private Boolean isApproved;
    private LocalDateTime approvalDate;
    private String aadhar_No;
    private String pan_No;
    private String profileUrl;
    private String description;
    @Column(updatable = false,nullable = false)
    private String regNo;

    @ManyToOne(fetch = FetchType.EAGER)
    private InstitutionEntity institutionEntity;
    private Permissions permission;

}
