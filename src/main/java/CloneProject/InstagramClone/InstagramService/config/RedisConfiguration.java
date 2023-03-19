package CloneProject.InstagramClone.InstagramService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfiguration {
    private final String redisHost;
    private final int redisPort;

    public RedisConfiguration(@Value("${spring.redis.host}") String redisHost,
                              @Value("${spring.redis.port}") int redisPort) {
        this.redisHost = redisHost;
        this.redisPort = redisPort;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(this.redisHost,this.redisPort);
    }

    @Bean
    public RedisTemplate<String,String> redisTemplate() {
        RedisTemplate<String,String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        //Spring과 Redis간 데이터 직렬화 및 역직렬화 시에 사용하는 방식을 설정. -> redis-cli로 데이터 확인시 알아볼 수 있도록 표시해준다.
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}
