package pact

import au.com.dius.pact.consumer.MessagePactBuilder
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
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
    companion object {
        const val PROVIDER = "apartment-listings"
        const val CONSUMER = "apartment-price-analytics"
    }

    private val eventSerdeConfig = EventSerdeConfig()

    @Pact(provider = PROVIDER, consumer = CONSUMER )
    fun createPactForSuccessApartmentCreation(builder: MessagePactBuilder): V4Pact {
        val bodySuccess = PactDslJsonBody()
            .stringType("id", "fc02a23a-c711-45ab-97c4-00c00c327fd2")
            .stringType("name", "Apartment name")
            .stringType("address", "123 rue rivoli")
            .stringType("description", "large T4")
            .stringType("city", "Paris")
            .numberType("postcode", 75001)
            .stringType("energyRating", "E")

        return builder
            .given("successful_apartment_creation")
            .expectsToReceive("New Apartment created")
            .withContent(bodySuccess)
            .toPact()
    }

    @Test
    @PactTestFor(
        pactMethod = "createPactForSuccessApartmentCreation",
        providerType = ProviderType.ASYNCH,
        providerName = PROVIDER,
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

        val msg = pact.interactions.firstOrNull()
        val objectMapper = eventSerdeConfig.jackson2ObjectMapper()
        // when
        val event = objectMapper.treeToValue<Apartment>(
            objectMapper.
            readTree(msg!!.asAsynchronousMessage()!!.contents.contents.value)
        )

        // then
        Assertions.assertEquals(expectedEvent.id, event.id)
        Assertions.assertEquals(expectedEvent.name, event.name)
        Assertions.assertEquals(expectedEvent.description, event.description)
        Assertions.assertEquals(expectedEvent.address, event.address)
        Assertions.assertEquals(expectedEvent.city, event.city)
//        Assertions.assertEquals(expectedEvent.postCode, event.postCode)
        Assertions.assertEquals(expectedEvent.energyRating, event.energyRating)
    }
}