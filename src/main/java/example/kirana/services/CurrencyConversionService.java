package example.kirana.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CurrencyConversionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyConversionService.class);

    private final FxRateUpi fxRateUpi;

    @Value("${currency.api.url}")
    private String apiUrl;

    /**
     * Constructor for CurrencyConversionService.
     *
     * @param fxRateUpi FxRateUpi instance to fetch currency rates.
     */
    public CurrencyConversionService(FxRateUpi fxRateUpi) {
        this.fxRateUpi = fxRateUpi;
    }

    /**
     * Get the currency conversion rate between source and target currencies.
     *
     * @param sourceCurrency Source currency code.
     * @param targetCurrency Target currency code.
     * @return The conversion rate.
     */
    public BigDecimal getConversionRate(String sourceCurrency, String targetCurrency) {
        // Construct the API URL with the source currency
        String url = apiUrl.replace("{base}", sourceCurrency);

        // Use RestTemplate to make the API call
        RestTemplate restTemplate = new RestTemplate();

        try {
            // Make the API call using the provided FxRateUpi instance
            Map<String, Object> response = fxRateUpi.getApi(url);

            // Check if the response contains currency rates
            if (response != null && response.containsKey("rates")) {
                // Extract the rates from the response
                Map<String, Object> rates = (Map<String, Object>) response.get("rates");

                // Ensure both source and target currencies are present in the rates
                if (rates.containsKey(sourceCurrency) && rates.containsKey(targetCurrency)) {
                    // Convert values to BigDecimal before performing operations
                    BigDecimal sourceRate = convertToBigDecimal(rates.get(sourceCurrency));
                    BigDecimal targetRate = convertToBigDecimal(rates.get(targetCurrency));

                    // Calculate the conversion rate
                    return targetRate.divide(sourceRate, 4, BigDecimal.ROUND_HALF_UP);
                }
            }

            // Log invalid response or missing currencies
            LOGGER.error("Invalid response or missing currencies from the API: {}", response);
            throw new RuntimeException("Unable to fetch valid conversion rates from the API.");
        } catch (Exception e) {
            // Log exceptions and rethrow
            LOGGER.error("Error while fetching conversion rates from the API", e);
            throw new RuntimeException("Error fetching conversion rates from the API", e);
        }
    }

    /**
     * Convert an Object to BigDecimal.
     *
     * @param value The Object to convert.
     * @return The converted BigDecimal.
     */
    private BigDecimal convertToBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Number) {
            // Handle various Number types, including Integer
            return new BigDecimal(((Number) value).doubleValue());
        } else {
            throw new RuntimeException("Unexpected type encountered when converting to BigDecimal: " + value.getClass());
        }
    }
}
