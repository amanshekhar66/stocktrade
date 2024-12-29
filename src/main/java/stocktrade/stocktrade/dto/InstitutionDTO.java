package stocktrade.stocktrade.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Access;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import stocktrade.stocktrade.enums.Permissions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstitutionDTO {
    @NotNull
    @NotBlank
    @Size(max = 50,message = "The length of the name of the institution cannot be greater than 50 characters")
    private String institutionName;
    @Email
    private String institutionEmail;
    @NotNull
    @NotBlank
    private String ownerName;
    @NotNull
    @NotBlank
    private String phoneNo;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate DOB;
//    @NotNull
//    @NotBlank
//    private String address1;
//    private String address2;
    @NotNull
    @NotBlank
    private String city;
    @NotNull
    @NotBlank
    private String state;
    private String description;
    private String disclaimer;
    private String companyCertificate;
    @NotNull
    @NotBlank
    private String regNumber;
    @JsonProperty("isVerified")
    private Boolean isVerified;
    @JsonProperty("isApproved")
    private Boolean isApproved;
    private LocalDateTime approvalDate;
    private String aadharNo;
    private String panNo;
    private String instagram;
    private String linkedIn;
    private String twitter;
    private String facebook;
}
