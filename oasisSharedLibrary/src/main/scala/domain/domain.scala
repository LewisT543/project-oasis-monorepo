import java.util.Date

package object domain {
  // Mortgage
  case class MortgageSettings(repaymentTerm: Int, interestRate: Double, loanToSalaryRange: (Double, Double), usingNet: Boolean)
  case class Expenses(loans: Int, rent: Int, bills: Int, food: Int, other: Int) {
    val total: Int = loans + rent + bills + other
  }
  case class Applicant(salary: Int, netMonthly: Int, expenses: Expenses)
  case class AffordabilityResult(loanPctOfNet: Double, leftOverPct: Double, monthlyCosts: Int, leftoverAmount: Int)
  case class SingleResult(loanAmount: Int, repaymentTerm: Int, rate: Double, monthlyPayments: Int, affordability: AffordabilityResult)
  case class CalcResult(lowResult: SingleResult, highResult: SingleResult)

  // Suitability
  type Region = String
  case class RoomRequirements(minBedrooms: Option[Int] = None, minBathrooms: Option[Int] = None, needsEnSuite: Boolean = false)
  case class LocationLists(whitelist: List[Region] = List.empty, blacklist: List[Region] = List.empty)
  case class SuitabilityRequirements(targetBudget: Int, roomRequirements: Option[RoomRequirements] = None, locationLists: Option[LocationLists] = None, needsGarden: Boolean = false)
  case class SuitabilityCalculatorPartialResult(priceSuitability: Double, locationSuitability: Double, publicTransportSuitability: Double, roomsSuitability: Double, sizeSuitability: Double, gardenSuitability: Double) {
    def numberOfFields: Int = 6
  }
  case class SuitabilityCalculatorResult(suitabilities: SuitabilityCalculatorPartialResult, overallSuitability: Double)
  
  // Property
  private enum PropertyType {case Detached, Semi_Detached, Terraced, Bungalow, Flat}
  private enum Tenure {case Freehold, Leasehold, Shared_Freehold}
  enum StationType {case Train, Tram, Bus}
  case class Location(road: String, region: String, city: String, postcode: String)
  case class Rooms(bedrooms: Int, bathrooms: Int, hasEnsuite: Boolean)
  case class StationDetail(name: String, stationType: StationType, distanceFromProperty: Double)
  case class PublicTransportDetails(stations: List[StationDetail])
  case class Property(location: Location, price: Int, publicTransport: PublicTransportDetails, addedOn: Date, propertyType: PropertyType, tenure: Tenure, rooms: Rooms, size: Option[Int], hasGarden: Option[Boolean])

  // Utils
  case class ScalingSettings(minOriginal: Double, maxOriginal: Double, minScaled: Double = 0.0, maxScaled: Double = 1.0)
}
