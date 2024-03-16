import oasisSharedLibrary.domain.{PublicTransportDetails, RoomRequirements, Rooms, StationDetail, StationType, SuitabilityCalculatorPartialResult}


object TestData {
  object PublicTransportSuitability {
    val train1: StationDetail = StationDetail("Salford Quays Train Station", StationType.Train, 0.4)
    val train2: StationDetail = StationDetail("Heaton Chapel Station", StationType.Train, 0.2)
    val train3: StationDetail = StationDetail("Reddish South Station", StationType.Train, 0.9)
    val train4: StationDetail = StationDetail("Levenshulme Station", StationType.Train, 1.2)
    val train5: StationDetail = StationDetail("Salford Quays Train Station 2", StationType.Train, 0.6)
    val train6: StationDetail = StationDetail("Salford Quays Train Station 3", StationType.Train, 0.5)
    val tram1: StationDetail = StationDetail("Anchorage Tram Station", StationType.Tram, 0.4)
    val tram2: StationDetail = StationDetail("Other Tram Station", StationType.Tram, 0.3)
    val tram3: StationDetail = StationDetail("Other2 Tram Station", StationType.Tram, 0.5)
    val tram4: StationDetail = StationDetail("other3 Tram Station", StationType.Tram, 0.6)
    val bus1: StationDetail = StationDetail("Harbour City Bus Stop", StationType.Bus, 0.4)

    val mixed1: PublicTransportDetails = PublicTransportDetails(List(tram1, train1, bus1))
    val mixed2: PublicTransportDetails = PublicTransportDetails(List(tram1, train6, tram4))
    val trains: PublicTransportDetails = PublicTransportDetails(List(train1, train1, train1))
    val trains2: PublicTransportDetails = PublicTransportDetails(List(train2, train3, train4))
    val trams: PublicTransportDetails = PublicTransportDetails(List(tram1, tram1, tram1))
    val trams2: PublicTransportDetails = PublicTransportDetails(List(tram1, tram2, tram3))
    val buses: PublicTransportDetails = PublicTransportDetails(List(bus1, bus1, bus1))

  }
  object RoomsSuitability {
    val bed5Bath3Rooms: Rooms = Rooms(5, 3, false)
    val bigHouseRooms: Rooms = Rooms(5, 3, false)
    val bigishHouseRooms: Rooms = Rooms(5, 2, false)
    val smallHouseRooms: Rooms = Rooms(2, 1, false)
    val palace: Rooms = Rooms(11, 10, false)

    val noneFilledRoomReqs: Option[RoomRequirements] = Some(RoomRequirements(None, None))
    val lowRoomReqs: Option[RoomRequirements] = Some(RoomRequirements(Some(3), Some(1)))
    val highRoomReqs: Option[RoomRequirements] = Some(RoomRequirements(Some(5), Some(2)))
    val massiveRoomReqs: Option[RoomRequirements] = Some(RoomRequirements(Some(12), Some(9)))
  }

  object WeightedAverage {
    val partialResult_zeros: SuitabilityCalculatorPartialResult = SuitabilityCalculatorPartialResult(
      priceSuitability = 0,
      locationSuitability = 0,
      publicTransportSuitability = 0,
      roomsSuitability = 0,
      sizeSuitability = 0,
      gardenSuitability = 0
    )
    
    val partialResult_perfect: SuitabilityCalculatorPartialResult = SuitabilityCalculatorPartialResult(
      priceSuitability = 1,
      locationSuitability = 1,
      publicTransportSuitability = 1,
      roomsSuitability = 1,
      sizeSuitability = 1,
      gardenSuitability = 1
    )
    
    val partialResult_realLike_good: SuitabilityCalculatorPartialResult = SuitabilityCalculatorPartialResult(
      priceSuitability = 0.8,
      locationSuitability = 0.9,
      publicTransportSuitability = 0.7,
      roomsSuitability = 0.85,
      sizeSuitability = 0.75,
      gardenSuitability = 0.8
    )
    
    val partialResult_realLike_bad: SuitabilityCalculatorPartialResult = SuitabilityCalculatorPartialResult(
      priceSuitability = 0.3,
      locationSuitability = 0.4,
      publicTransportSuitability = 0.5,
      roomsSuitability = 0.2,
      sizeSuitability = 0.3,
      gardenSuitability = 0.6
    )
  }
}
