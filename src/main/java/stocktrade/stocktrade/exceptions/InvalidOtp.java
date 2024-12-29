package stocktrade.stocktrade.exceptions;

public class InvalidOtp extends RuntimeException{
    public InvalidOtp(String message) {
        super(message);
    }
}
