package com.msa.eshop.utils.map.osm

import org.osmdroid.util.GeoPoint

object Coordinates {

    val iranCenter = GeoPoint(32.4279, 53.6880)

    val tehran = GeoPoint(35.6892, 51.3890)

    val defaultLocation: GeoPoint
        get() = iranCenter
}