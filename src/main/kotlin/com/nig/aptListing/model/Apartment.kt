package com.nig.aptListing.model

import java.util.UUID

data class Apartment(
    val id: UUID,
    val name: String,
    val description: String,
    val address: String,
    val city: String,
    val postCode: Int,
    val energyRating: String?,
)
