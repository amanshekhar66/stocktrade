package stocktrade.stocktrade.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktrade.stocktrade.enums.Permissions;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResearchAnalystDTO {
    private String firstName;
    private String surName;
    private String gender;
    private String password;
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
    @NotNull
    @NotBlank
    private String regNo;
    @JsonIgnoreProperties("researchAnalystDTOList")
    private InstitutionDTO institutionDTO;
    private Permissions permission;
}
