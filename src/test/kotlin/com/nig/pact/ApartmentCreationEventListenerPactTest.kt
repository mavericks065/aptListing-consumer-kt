package com.nig.pact

import au.com.dius.pact.consumer.MessagePactBuilder
import au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody
import au.com.dius.pact.consumer.dsl.LambdaDslJsonBody
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.consumer.junit5.ProviderType
import au.com.dius.pact.core.model.PactSpecVersion
import au.com.dius.pact.core.model.V4Pact
import au.com.dius.pact.core.model.annotations.Pact
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.nig.aptListing.config.EventSerdeConfig
import com.nig.aptListing.model.Apartment
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*


@ExtendWith(PactConsumerTestExt::class)
class ApartmentCreationEventListenerPactTest {

    private val eventSerdeConfig = EventSerdeConfig()

    @Pact(
        provider = "apartment-listing-api.web.apartment-created-provider",
        consumer = "apartments.analytics.apartment-created-listener"
    )
    fun createPactForSuccessApartmentCreation(builder: MessagePactBuilder): V4Pact {
        val bodySuccess = newJsonBody { o: LambdaDslJsonBody ->
            o.stringType("id", "fc02a23a-c711-45ab-97c4-00c00c327fd2")
            o.stringType("name", "Apartment name")
            o.stringType("address", "123 rue rivoli")
            o.stringType("description", "large T4")
            o.stringType("city", "Paris")
            o.integerType("postcode", 75001)
            o.stringValue("energyRating", "E")
        }
        return builder
            .given("successful_apartment_creation")
            .expectsToReceive("New Apartment created")
            .withContent(bodySuccess.build())
            .toPact()
    }

    @Test
    @PactTestFor(
        pactMethod = "createPactForSuccessApartmentCreation",
        providerType = ProviderType.ASYNCH,
        providerName = "apartment-listing-api.web.apartment-created-provider",
        pactVersion = PactSpecVersion.V4
    )
    fun successApartmentCreation(pact: V4Pact) {
        // given
        val expectedEvent = Apartment(
            id = UUID.fromString("fc02a23a-c711-45ab-97c4-00c00c327fd2"),
            name = "Apartment name",
            description = "large T4",
            address = "123 rue rivoli",
            city = "Paris",
            postCode = 75001,
            energyRating = "E"
        )
        val msg = pact.interactions.first()
        val objectMapper = eventSerdeConfig.jackson2ObjectMapper()
        // when
        val event = objectMapper.treeToValue<Apartment>(
            objectMapper.readTree(msg.asAsynchronousMessage()!!.contents.contents.value!!)
        )

        // then
        Assertions.assertEquals(expectedEvent, event)
    }
}
//        val stateMap = msg.providerStates[0].params
//        val expectedEvent = Apartment(
//            id = stateMap["id"] as UUID,
//            name = stateMap["name"] as String,
//            description = stateMap["description"] as String,
//            address = stateMap["address"] as String,
//            city = stateMap["city"] as String,
//            postCode = stateMap["postCode"] as Int,
//            energyRating = stateMap["energyRating"] as String?,
//        )
