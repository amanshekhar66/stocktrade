package stocktrade.stocktrade.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {

    private String userEmail;
    private String userPassword;
    private String firstName;
    private String surName;
    private LocalDate DOB;
    private String gender;
    private Boolean isVerified;
}
