package example.kirana.ratelimit;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.grid.jcache.JCacheProxyManager;
import org.redisson.config.Config;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.cache.CacheManager;
import javax.cache.Caching;

/**
 * Configuration class for setting up Redis as a cache manager for rate limiting in the Kirana application.
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * Configures Redisson client with a single server.
     *
     * @return Configured Redisson client.
     */
    @Bean
    public Config config() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        return config;
    }

    /**
     * Creates and configures a cache manager for Redisson with the specified Redisson client configuration.
     *
     * @param config Redisson client configuration.
     * @return CacheManager for Redisson.
     */
    @Bean(name = "mymanager")
    public CacheManager cacheManager(Config config) {
        CacheManager manager = Caching.getCachingProvider().getCacheManager();
        manager.createCache("cache", RedissonConfiguration.fromConfig(config));
        return manager;
    }

    /**
     * Creates a ProxyManager for rate limiting using JCache and Redisson.
     *
     * @param cacheManager CacheManager for Redisson.
     * @return ProxyManager for rate limiting.
     */
    @Bean
    ProxyManager<String> proxyManager(CacheManager cacheManager) {
        return new JCacheProxyManager<>(cacheManager.getCache("cache"));
    }

}
