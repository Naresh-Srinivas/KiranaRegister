package example.kirana.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class FxRateUpi {

    // Use WebClient in a production scenario
    static RestTemplate restTemplate = new RestTemplate();

    /**
     * Fetch data from the API and cache the response using Spring's caching mechanism.
     *
     * @param url The URL to fetch data from.
     * @return A Map containing the API response.
     */
    @Cacheable(value = "cache") // Cache the result of this method using the specified cache name
    public Map<String, Object> getApi(String url) {
        // Make the API call using RestTemplate
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        // Return the API response
        return response;
    }
}
