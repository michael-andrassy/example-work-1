package demo.xmlassembly.service.setup;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {

    public final static String ALL_DF_CACHE_NAME = "DFBorrowerCache";

    @Bean
    @Qualifier(ALL_DF_CACHE_NAME)
    public CacheManager cacheManager4Borrower() {

        final CaffeineCacheManager cacheManager = new CaffeineCacheManager(ALL_DF_CACHE_NAME);

        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                    .expireAfterWrite(10, TimeUnit.SECONDS)
                    .maximumSize(1000)
        );

        return cacheManager;
    } //m

} //class

/* For Quarkus

<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-cache</artifactId>
</dependency>

import io.quarkus.cache.CacheResult;
import io.quarkus.cache.CacheKey;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MessageService {

    @CacheResult(cacheName = "expensiveMessageCache")
    public Message process(@CacheKey String param1,
                           @CacheKey String param2,
                           @CacheKey Data data) {
        // Perform expensive operations, e.g., calling multiple REST services.
        Message message = new Message();
        // Populate message as needed.
        return message;
    }
}

 */