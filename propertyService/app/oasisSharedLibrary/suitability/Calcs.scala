package oasisSharedLibrary.suitability

import oasisSharedLibrary.domain.{LocationLists, PublicTransportDetails, Region, RoomRequirements, Rooms, ScalingSettings, StationDetail, SuitabilityCalculatorPartialResult, StationType}
import oasisSharedLibrary.suitability.CalcsHelper.Scaling.linearScaleValue

object Calcs {
  val defaultScalingFactors: Map[String, Double] = Map(
    "price" -> 30_000.0,
    "transport.trains" -> 1.2,
    "transport.trams" -> 0.9,
    "transport.buses" -> 0.5,
    "size.baseValueToMinusFrom" -> 2500,
    "weighting.price" -> 1.0,
    "weighting.location" -> 1.0,
    "weighting.transport" -> 1.0,
    "weighting.rooms" -> 1.0,
    "weighting.size" -> 1.0,
    "weighting.garden" -> 1.0,
  )

  private def getScaling(scalingFactors: Map[String, Double])(identifier: String) = {
    scalingFactors.getOrElse(identifier, defaultScalingFactors(identifier))
  }

  def getPriceSuitability(scalingFactors: Map[String, Double], budget: Int)(cost: Int): Double = {
    val scaling = getScaling(scalingFactors)("price")
    val diff: Double = budget - cost
    val scaledScore = 1.0 / (1.0 + Math.exp(-diff / scaling))
    if scaledScore > 1.0 then println(s">> Hitting limit in [Price] calc, value: $scaledScore")
    println(s"value: $scaledScore")
    Math.max(0.0, Math.min(1.0, scaledScore))
  }

  def getLocationSuitability(locLists: Option[LocationLists])(propertyRegion: Region): Double = {
    import CalcsHelper.LocLists.*
    locLists match {
      case None => 0.5
      case Some(locLists) if bothEmpty(locLists) => 0.5
      case Some(locLists) if inBothLists(locLists)(propertyRegion) => 0.5
      case Some(locLists) if inWhitelist(locLists)(propertyRegion) => 0.75
      case Some(locLists) if inBlacklist(locLists)(propertyRegion) => 0.25
      case Some(_) => 0.5
    }
  }

  def getPublicTransportSuitability(scalingFactors: Map[String, Double])(publicTransportDetails: PublicTransportDetails): Double = {
    def handleStation(scaling: Double)(details: StationDetail): Double = {
      val distanceScore = (2.5 - details.distanceFromProperty) * scaling
      linearScaleValue(ScalingSettings(0.0, 2.5))(distanceScore)
    }

    val scaledValues = publicTransportDetails.stations.map {
      case station if station.stationType == StationType.Train => handleStation(getScaling(scalingFactors)("transport.trains"))(station)
      case station if station.stationType == StationType.Tram => handleStation(getScaling(scalingFactors)("transport.trams"))(station)
      case station if station.stationType == StationType.Bus => handleStation(getScaling(scalingFactors)("transport.buses"))(station)
      case _ => 0.5
    }
    linearScaleValue(ScalingSettings(0.0, 3.0))(scaledValues.sum)
  }

  def getRoomsSuitability(roomReqs: Option[RoomRequirements])(rooms: Rooms): Double = {
    import CalcsHelper.Rooms.*
    roomReqs match {
      case None => 0.5
      case Some(unwrappedRooms) =>
        val bedsSuitability = handleBedsDif(rooms.bedrooms - unwrappedRooms.minBedrooms.getOrElse(rooms.bedrooms))
        val bathsSuitability = handleBathsDif(rooms.bathrooms - unwrappedRooms.minBathrooms.getOrElse(rooms.bathrooms))
        val ensuiteSuitability = handleEnsuiteDif(unwrappedRooms.needsEnSuite, rooms.hasEnsuite)
        val x = linearScaleValue(ScalingSettings(0.0, 3.0, 0.0, 1.0))(bedsSuitability + bathsSuitability + ensuiteSuitability)
        println(s"$unwrappedRooms <> value: $x")
        x
    }
  }

  // TODO: Make this actually do something - can I get the size of a house from elsewhere? Land Registry?
  def getSizeSuitability(size: Option[Int]): Double = size match {
    case None => 0.5
    case Some(value) => 0.5
  }

  def getGardenSuitability(needsGarden: Boolean)(hasGarden: Option[Boolean]): Double = hasGarden match {
    case None => 0.5
    case Some(hasGarden) => needsGarden match {
      case needsGarden if needsGarden && !hasGarden => 0.3
      case needsGarden if !needsGarden && !hasGarden => 0.5
      case needsGarden if !needsGarden && hasGarden => 0.7
      case needsGarden if needsGarden && hasGarden => 1.0
      case _ => 0.5
    }
  }

  def getWeightedAverage(scalingFactors: Map[String, Double])(partialResult: SuitabilityCalculatorPartialResult): Double = {
    val priceWeight = getScaling(scalingFactors)("weighting.price") / partialResult.numberOfFields
    val locationWeight = getScaling(scalingFactors)("weighting.location") / partialResult.numberOfFields
    val transportWeight = getScaling(scalingFactors)("weighting.transport") / partialResult.numberOfFields
    val roomsWeight = getScaling(scalingFactors)("weighting.rooms") / partialResult.numberOfFields
    val sizeWeight = getScaling(scalingFactors)("weighting.size") / partialResult.numberOfFields
    val gardenWeight = getScaling(scalingFactors)("weighting.garden") / partialResult.numberOfFields

    val weightedPrice = partialResult.priceSuitability * priceWeight
    val weightedLocation = partialResult.locationSuitability * locationWeight
    val weightedTransport = partialResult.publicTransportSuitability * transportWeight
    val weightedRooms = partialResult.roomsSuitability * roomsWeight
    val weightedSize = partialResult.sizeSuitability * sizeWeight
    val weightedGarden = partialResult.gardenSuitability * gardenWeight

    (weightedPrice + weightedLocation + weightedTransport + weightedRooms + weightedSize + weightedGarden)
      / (priceWeight + locationWeight + transportWeight + roomsWeight + sizeWeight + gardenWeight)
  }
}
