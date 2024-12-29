package stocktrade.stocktrade.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {

    private String accessToken;
    private String refreshToken;
    private String verificationToken;
    private String userStatus;
    private HttpStatus status;

}
