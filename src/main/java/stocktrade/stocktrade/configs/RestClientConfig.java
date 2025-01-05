package stocktrade.stocktrade.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    public RestClient restClient(){
        return RestClient.builder()
                .baseUrl("https://apiconnect.angelone.in/rest/secure/angelbroking/market/v1/quote/")
                .defaultHeader("")
                .build();
    }
}
