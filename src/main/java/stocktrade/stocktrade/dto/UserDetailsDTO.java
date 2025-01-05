package stocktrade.stocktrade.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import stocktrade.stocktrade.enums.Permissions;
import stocktrade.stocktrade.enums.Roles;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailsDTO {

    private String firstName;
    private String surName;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate DOB;
    private String gender;
    private String userEmail;
    private String userPassword;
    @JsonProperty("isVerified")
    private Boolean isVerified;
    private Set<Roles> roles;
    private Permissions permissions;
}
