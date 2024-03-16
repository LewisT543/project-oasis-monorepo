package oasisSharedLibrary.suitability

import oasisSharedLibrary.domain.{Property, SuitabilityCalculatorPartialResult, SuitabilityCalculatorResult, SuitabilityRequirements}
import Calcs.*

object SuitabilityCalculator {
  // FACTORS TO CONSIDER FOR SUITABILITY:
  // Price less than budget
  // Location
  // Distance from public transport
  // Number of rooms
  // Cost of rooms in that Location
  // Number of bathrooms
  // Has en-suite
  // Size of house sqft
  // Average house price in area
  // Garden

  //https://www.rightmove.co.uk/property-for-sale/map.html?locationIdentifier=REGION%5E904&minBedrooms=4&maxPrice=300000&numberOfPropertiesPerPage=499&propertyTypes=bungalow%2Cdetached%2Csemi-detached%2Cterraced&viewType=MAP&mustHave=&dontShow=&furnishTypes=&viewport=-2.39906%2C-2.07651%2C53.4341%2C53.5254&keywords=


  def calculateSuitability(scalingFactors: Map[String, Double], requirements: SuitabilityRequirements)(property: Property): SuitabilityCalculatorResult = {
    // all of these will return a value between 0-1.0
    val partialResult: SuitabilityCalculatorPartialResult = SuitabilityCalculatorPartialResult(
      getPriceSuitability(scalingFactors, requirements.targetBudget)(property.price),
      getLocationSuitability(requirements.locationLists)(property.location.region),
      getPublicTransportSuitability(scalingFactors)(property.publicTransport),
      getRoomsSuitability(requirements.roomRequirements)(property.rooms),
      getSizeSuitability(property.size),
      getGardenSuitability(requirements.needsGarden)(property.hasGarden)
    )
    val totalSuitability = getWeightedAverage(scalingFactors)(partialResult)
    SuitabilityCalculatorResult(partialResult, totalSuitability)
  }
}
