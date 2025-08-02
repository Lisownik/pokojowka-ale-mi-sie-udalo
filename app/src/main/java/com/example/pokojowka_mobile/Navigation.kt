package com.example.pokojowka_mobile

object AppDestinations {
    const val FORM_SCREEN = "form_screen"


//    Główne Ekrany - SCREEN's
    const val HOME_SCREEN = "home_screen"
    const val ROOMS_SCREEN = "rooms_screen"
    const val PLANTS_SCREEN = "plants_screen"
    const val BULBS_SCREEN = "bulbs_screen"
    const val PROFILE_SCREEN = "profile_screen"

//     Widoki - View's
    const val ROOM_VIEW_SCREEN_ROUTE = "room_view"
    const val ROOM_ID_ARG = "roomId"
    const val ROOM_VIEW_SCREEN = "$ROOM_VIEW_SCREEN_ROUTE/{$ROOM_ID_ARG}"

    const val PLANT_VIEW_SCREEN_ROUTE = "plant_view"
    const val PLANT_ID_ARG = "plantId"
    const val PLANT_VIEW_SCREEN = "$PLANT_VIEW_SCREEN_ROUTE/{$PLANT_ID_ARG}"

    const val BULB_VIEW_SCREEN_ROUTE = "bulb_view"
    const val BULB_ID_ARG = "bulbId"
    const val BULB_VIEW_SCREEN = "$BULB_VIEW_SCREEN_ROUTE/{$BULB_ID_ARG}"

}

