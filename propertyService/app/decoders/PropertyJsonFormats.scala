package decoders
import play.api.libs.json.*
import oasisSharedLibrary.domain.{Location, Property, PropertyType, PublicTransportDetails, StationDetail, StationType, Tenure, Rooms}

object PropertyJsonFormats {
  implicit val propertyWrites: Writes[Property] = Json.writes[Property]
  implicit val locationWrites: Writes[Location] = Json.writes[Location]
  implicit val publicTransportDetailsWrites: Writes[PublicTransportDetails] = Json.writes[PublicTransportDetails]
  implicit val stationDetailWrites: Writes[StationDetail] = Json.writes[StationDetail]
  implicit val stationTypeWrites: Writes[StationType] = Writes { stationType => JsString(stationType.toString) }
  implicit val propertyTypeWrites: Writes[PropertyType] = Writes { propertyType => JsString(propertyType.toString) }
  implicit val tenureTypeWrites: Writes[Tenure] = Writes { tenure => JsString(tenure.toString) }
  implicit val roomsWrites: Writes[Rooms] = Json.writes[Rooms]
}
