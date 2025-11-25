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
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.io.IOException;

@Configuration
public class RedisConfig {

    // Template for caching HTTP responses (JSON)
    @Bean
    @Qualifier("cachedResponseTemplate")
    public ReactiveRedisTemplate<String, CachedHttpResponse> cachedResponseTemplate(
            ReactiveRedisConnectionFactory factory) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        mapper.registerModule(new SimpleModule()
                .addSerializer(byte[].class, new ByteArraySerializer())
                .addDeserializer(byte[].class, new ByteArrayDeserializer()));

        Jackson2JsonRedisSerializer<CachedHttpResponse> serializer =
                new Jackson2JsonRedisSerializer<>(mapper, CachedHttpResponse.class);

        RedisSerializationContext<String, CachedHttpResponse> context =
                RedisSerializationContext.<String, CachedHttpResponse>newSerializationContext()
                        .key(StringRedisSerializer.UTF_8)
                        .value(serializer)
                        .hashKey(StringRedisSerializer.UTF_8)
                        .hashValue(serializer)
                        .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    @Qualifier("rateLimitTemplate")
    ReactiveRedisTemplate<String, Long> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {

        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();

        StringRedisSerializer stringRedisSerializer = StringRedisSerializer.UTF_8;

        GenericToStringSerializer<Long> longToStringSerializer = new GenericToStringSerializer<>(Long.class);

        return new ReactiveRedisTemplate<>(factory,
                RedisSerializationContext
                        .<String, Long>newSerializationContext(jdkSerializationRedisSerializer)
                        .key(stringRedisSerializer).value(longToStringSerializer).build());
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