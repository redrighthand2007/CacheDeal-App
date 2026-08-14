package com.kush.cachedeal.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute

@Serializable
data object AuthRoute

@Serializable
data object OnboardingRoute

@Serializable
data object HomeRoute

@Serializable
data object PostItemRoute

@Serializable
data class ItemDetailRoute(val itemId: String)

@Serializable
data object MyListingsRoute

@Serializable
data class OffersRoute(val itemId: String)

@Serializable
data object DealsRoute

@Serializable
data object ProfileRoute
