import oasisSharedLibrary.domain.{LocationLists, PublicTransportDetails, RoomRequirements, Rooms, StationDetail, StationType, SuitabilityCalculatorPartialResult}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import oasisSharedLibrary.suitability.Calcs
import oasis.suitability.Calcs.{defaultScalingFactors, getGardenSuitability, getLocationSuitability, getPriceSuitability, getPublicTransportSuitability, getRoomsSuitability, getSizeSuitability, getWeightedAverage}


class SuitabilityCalculatorTest extends AnyFunSpec with Matchers {
  describe("getPriceSuitability") {
    val budget = 300_000
    val cost = 250_000
    val lowCost = 150_000
    it("should return a score") { getPriceSuitability(defaultScalingFactors, budget)(cost) > 0 shouldEqual true }
    it("should return a high score for positive differences") { getPriceSuitability(defaultScalingFactors, budget)(cost) > 0.5 shouldEqual true }
    it("should return a low score for negative differences") { getPriceSuitability(defaultScalingFactors, cost)(budget) < 0.5 shouldEqual true }
    it("should return a VERY high score for LARGE positive differences") { getPriceSuitability(defaultScalingFactors, budget)(lowCost) > 0.95 shouldEqual true }
    it("should return a VERY low score for LARGE negative differences") { getPriceSuitability(defaultScalingFactors, lowCost)(budget) < 0.05 shouldEqual true }
    it("should return 0.99+ for a -100% saving") { getPriceSuitability(defaultScalingFactors, budget)(0) > 0.99 shouldEqual true }
    it("should return 0.01- for a +100% cost") { getPriceSuitability(defaultScalingFactors, budget)(600_000) < 0.01 shouldEqual true }

    it("should return 0.6+ for a -10% saving") { getPriceSuitability(defaultScalingFactors, budget)(270_000) > 0.7 shouldEqual true }
    it("should return 0.4- for a +10% cost") { getPriceSuitability(defaultScalingFactors, budget)(330_000) < 0.3 shouldEqual true }

    it("should return 0.75+ for a -20% saving") { getPriceSuitability(defaultScalingFactors, budget)(240_000) > 0.85 shouldEqual true }
    it("should return 0.25- for a +20% cost") { getPriceSuitability(defaultScalingFactors, budget)(360_000) < 0.15 shouldEqual true }

    it("should return 0.85+ for a -30% saving") { getPriceSuitability(defaultScalingFactors, budget)(210_000) > 0.95 shouldEqual true }
    it("should return 0.15- for a +30% cost") { getPriceSuitability(defaultScalingFactors, budget)(390_000) < 0.05 shouldEqual true }

    it("should return 0.9+ for a -40% saving") { getPriceSuitability(defaultScalingFactors, budget)(180_000) > 0.9 shouldEqual true }
    it("should return 0.1- for a +40% cost") { getPriceSuitability(defaultScalingFactors, budget)(420_000) < 0.1 shouldEqual true }
  }

  describe("getLocationSuitability") {
    val happy = "HAPPY_TEST_LOCATION"
    val sad = "SAD_TEST_LOCATION"
    val other = "OTHER_TEST_LOCATION"

    it("should return 0.5 when no config passed in") {getLocationSuitability(None)(other) shouldEqual 0.5}
    val empties: Option[LocationLists] = Some(LocationLists(List.empty, List.empty))
    it("should return 0.5 when both lists empty") { getLocationSuitability(empties)( other) shouldEqual 0.5 }

    val withList: Option[LocationLists] = Some(LocationLists(List(happy), List(sad)))
    val getLocWithLists = getLocationSuitability(withList)
    it("should return 0.75 when in whitelist") { getLocWithLists(happy) shouldEqual 0.75 }
    it("should return 0.25 when in blacklist") { getLocWithLists(sad) shouldEqual 0.25 }
    it("should return 0.5 when not in either list") { getLocWithLists(other) shouldEqual 0.5 }

    val withBothLists: Option[LocationLists] = Some(LocationLists(List(other), List(other)))
    it("should return 0.5 when in both lists") { getLocationSuitability(withBothLists)(other) shouldEqual 0.5 }
  }

  describe("getPublicTransportSuitability") {
    import TestData.PublicTransportSuitability.*
    val getPublicTransportSuitability = Calcs.getPublicTransportSuitability(defaultScalingFactors)

    // Absolutes
    it("should return something greater than 0.0") { getPublicTransportSuitability(mixed1) > 0.0 shouldEqual true }
    it("should return a larger score for trams than buses of equal distance") { getPublicTransportSuitability(trams) > getPublicTransportSuitability(buses) shouldEqual true }
    it("should return a larger score for trains than trams of equal distance") { getPublicTransportSuitability(trains) > getPublicTransportSuitability(trams) shouldEqual true }

    // Subjective
    it("should return a higher score for a \'subjectively\' better set of transport links: 1") { getPublicTransportSuitability(trains) > getPublicTransportSuitability(trams) shouldEqual true }
    it("should return a higher score for a \'subjectively\' better set of transport links: 2") { getPublicTransportSuitability(mixed2) > getPublicTransportSuitability(mixed1) shouldEqual true }
  }

  describe("getRoomsSuitability") {
    import TestData.RoomsSuitability.*

    it("should return 0.5 when RoomRequirements == None passed in") { getRoomsSuitability(None)(bed5Bath3Rooms) == 0.5 shouldEqual true }
    it("should return 0.5 when RoomRequirements contains two None(s)") { getRoomsSuitability(noneFilledRoomReqs)(bed5Bath3Rooms) == 0.5 shouldEqual true }

    it("should return a higher score when it beats the requirements") { getRoomsSuitability(lowRoomReqs)(bed5Bath3Rooms) > 0.5 shouldEqual true }
    it("should return a lower score when it falls short of the requirements") { getRoomsSuitability(highRoomReqs)(smallHouseRooms) < 0.5 shouldEqual true }

    it("should not exceed 1 with big values") { getRoomsSuitability(lowRoomReqs)(palace) <= 1 shouldEqual true }
    it("should not fall below 0 with small values") { getRoomsSuitability(massiveRoomReqs)(smallHouseRooms) >= 0 shouldEqual true }

    it("should be tuned so that a difference of 1 bathroom is noticeable") { getRoomsSuitability(lowRoomReqs)(bed5Bath3Rooms) > getRoomsSuitability(lowRoomReqs)(bigishHouseRooms) shouldEqual true }
    it("should be tuned so that a difference of 1 bedroom is noticeable") { getRoomsSuitability(lowRoomReqs)(bed5Bath3Rooms) > getRoomsSuitability(lowRoomReqs)(bigishHouseRooms) shouldEqual true }
  }

  describe("getSizeSuitability") {
    it("should return 0.5 with no requirement passed in") { getSizeSuitability(None) == 0.5 shouldEqual true }
  }

  describe("getGardenSuitability") {
    it("should return 0.5 when requirements == None && has == false") { getGardenSuitability(false)(None) == 0.5 shouldEqual true }
    it("should return 0.5 when requirements == None && has == true") { getGardenSuitability(true)(None) == 0.5 shouldEqual true }

    it("should return 0.3 when requirements == true && has == false") { getGardenSuitability(true)(Some(false)) == 0.3 shouldEqual true }
    it("should return 0.5 when requirements == false && has == false") { getGardenSuitability(false)(Some(false)) == 0.5 shouldEqual true }
    it("should return 0.7 when requirements == false && has == true") { getGardenSuitability(false)(Some(true)) == 0.7 shouldEqual true }
    it("should return 1.0 when requirements == true && has == true") { getGardenSuitability(true)(Some(true)) == 1.0 shouldEqual true }
  }

  describe("getWeightedAverage") {
    import TestData.WeightedAverage.*
    val precision: Double = 0.05
    val emptyScalingFactors = Map.empty[String, Double]

    it("should return a 0.0 for 1.0 scaling and 0.0 results") { getWeightedAverage(emptyScalingFactors)(partialResult_zeros) shouldEqual 0.0 }
    it("should return a 1.0 for 1.0 scaling and 1.0 results") { getWeightedAverage(emptyScalingFactors)(partialResult_perfect) shouldEqual 1.0 }
    it("should return a low result for 1.0 scaling and low results") { getWeightedAverage(emptyScalingFactors)(partialResult_realLike_bad) shouldEqual 0.38 +- precision }
    it("should return a high result for 1.0 scaling and high results") { getWeightedAverage(emptyScalingFactors)(partialResult_realLike_good) shouldEqual 0.82 +- precision }

    // TODO: Add more tests for this once I have tried a non-zero scaling input
  }

}
