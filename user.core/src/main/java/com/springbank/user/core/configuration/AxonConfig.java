package com.springbank.user.core.configuration;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.MongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.serialization.Serializer;
import org.axonframework.spring.config.AxonConfiguration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig
{
    @Value("${spring.data.mongodb.host:127.0.0.1}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port:27017}")
    private int mongoPort;

    @Value("${spring.data.mongodb.database:user}")
    private String mongoDatabase;

    @Bean
    public MongoClient mongo()
    {
        MongoClient mongoClient = new MongoClient(mongoHost, mongoPort);

        return mongoClient;
    }

    @Bean
    public MongoTemplate axonMongoTemplate()
    {
        return DefaultMongoTemplate
                .builder()
                .mongoDatabase((com.mongodb.client.MongoClient) mongo(), mongoDatabase)
                .build();
    }

    @Bean
    public TokenStore tokenStore(Serializer serializer)
    {
        return MongoTokenStore
                .builder()
                .mongoTemplate(axonMongoTemplate())
                .serializer(serializer)
                .build();
    }

    @Bean
    public EventStorageEngine EventStorageEngine(MongoClient mongoClient)
    {
        return MongoEventStorageEngine
                .builder()
                .mongoTemplate(DefaultMongoTemplate
                                .builder()
                                .mongoDatabase((com.mongodb.client.MongoClient) mongoClient)
                                .build())
                .build();
    }

    @Bean
    public EmbeddedEventStore embeddedEventStore(EventStorageEngine eventStorageEngine, AxonConfiguration axonConfiguration)
    {
        return EmbeddedEventStore
                .builder()
                .storageEngine(eventStorageEngine)
                .messageMonitor(axonConfiguration.messageMonitor(EventStore.class,"eventStore"))
                .build();
    }
}
