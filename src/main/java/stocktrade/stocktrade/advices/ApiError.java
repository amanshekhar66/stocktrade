package stocktrade.stocktrade.advices;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@Builder
public class ApiError {
    private HttpStatus status;
    private String errMsg;
    private List<String> subErrors;
}

