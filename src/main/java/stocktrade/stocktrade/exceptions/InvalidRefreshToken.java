package stocktrade.stocktrade.exceptions;

public class InvalidRefreshToken extends RuntimeException{
    public InvalidRefreshToken(String message) {
        super(message);
    }
}
