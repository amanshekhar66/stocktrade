package stocktrade.stocktrade.exceptions;

public class ExpiredOtp extends RuntimeException{
    public ExpiredOtp(String message) {
        super(message);
    }
}
