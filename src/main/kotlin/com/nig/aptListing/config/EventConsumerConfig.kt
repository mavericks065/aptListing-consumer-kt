package com.nig.aptListing.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EventConsumerConfig {
    @Bean
    fun getAppExchange(): TopicExchange {
        return TopicExchange("apartments-listing")
    }

    @Bean("ApartmentCreated")
    fun getApartmentCreatedQueue(): Queue {
        return Queue("apartments-created")
    }

    @Bean("ApartmentCreatedBinding")
    fun declareApartmentCreatedBinding(): Binding {
        return BindingBuilder.bind(getApartmentCreatedQueue()).to(getAppExchange())
            .with("aptsListing.create")
    }
}
