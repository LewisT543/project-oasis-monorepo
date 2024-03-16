package repos

import oasisSharedLibrary.domain.*

import java.util.Date

class MockPropertyRepository {
  private val property: Property = Property(
    location = Location("test", "test", "test", "test"),
    price = 250000,
    publicTransport = PublicTransportDetails(List(
      StationDetail("test station 1", StationType.Bus, 0.7),
      StationDetail("test station 2", StationType.Tram, 0.9),
      StationDetail("test station 3", StationType.Train, 0.7)
    )),
    addedOn = new Date(),
    propertyType = PropertyType.Semi_Detached,
    tenure = Tenure.Freehold,
    rooms = Rooms(bedrooms = 5, bathrooms = 2, hasEnsuite = false),
    size = Some(2500),
    hasGarden = None
  )

  def getAllProperties: List[Property] = List(property)
}
