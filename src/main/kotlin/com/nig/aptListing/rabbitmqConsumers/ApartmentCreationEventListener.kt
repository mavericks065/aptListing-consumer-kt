package com.nig.aptListing.rabbitmqConsumers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.nig.aptListing.model.Apartment
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class ApartmentCreationEventListener(
    val jsonObjectMapper: ObjectMapper
) {

    @RabbitListener(
        queues = ["apartments-created"]
    )
    fun processEvent(event:ByteArray) {
        val raw = jsonObjectMapper.readTree(event)
        val newApartment = jsonObjectMapper.treeToValue<Apartment>(raw)
        println("Should probably store it in some analytics DB but this is a demo...s")
        println(newApartment)
    }
}
