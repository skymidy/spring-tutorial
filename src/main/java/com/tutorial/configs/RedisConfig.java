package com.tutorial.configs;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tutorial.model.dto.CachedHttpResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.io.IOException;

@Configuration
public class RedisConfig {

    @Bean
    @Qualifier("cachedResponseTemplate")
    public RedisTemplate<String, CachedHttpResponse> cachedResponseTemplate(
            RedisConnectionFactory factory) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        mapper.registerModule(new SimpleModule()
                .addSerializer(byte[].class, new ByteArraySerializer())
                .addDeserializer(byte[].class, new ByteArrayDeserializer())
        );

        Jackson2JsonRedisSerializer<CachedHttpResponse> serializer =
                new Jackson2JsonRedisSerializer<>(mapper, CachedHttpResponse.class);

        RedisTemplate<String, CachedHttpResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(StringRedisSerializer.UTF_8);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(StringRedisSerializer.UTF_8);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @Qualifier("rateLimitTemplate")
    public RedisTemplate<String, Long> rateLimitTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Keys
        template.setKeySerializer(StringRedisSerializer.UTF_8);
        template.setHashKeySerializer(StringRedisSerializer.UTF_8);

        // Values (Long → String → Redis)
        GenericToStringSerializer<Long> longToStringSerializer =
                new GenericToStringSerializer<>(Long.class);

        template.setValueSerializer(longToStringSerializer);
        template.setHashValueSerializer(longToStringSerializer);

        template.afterPropertiesSet();
        return template;
    }

    // Custom serializers for byte[] in JSON (base64)
    public static class ByteArraySerializer extends JsonSerializer<byte[]> {
        @Override
        public void serialize(byte[] value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeBinary(value);
            }
        }
    }

    public static class ByteArrayDeserializer extends JsonDeserializer<byte[]> {
        @Override
        public byte[] deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
            return p.getBinaryValue();
        }
    }
}