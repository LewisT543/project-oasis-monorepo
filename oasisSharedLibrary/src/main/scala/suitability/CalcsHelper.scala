package suitability

import domain.{LocationLists, Region, ScalingSettings}

object CalcsHelper {
  object LocLists {
    def bothEmpty(locLists: LocationLists): Boolean = locLists.whitelist.isEmpty && locLists.blacklist.isEmpty

    def inBothLists(locLists: LocationLists)(propertyRegion: Region): Boolean = locLists.whitelist.contains(propertyRegion) && locLists.blacklist.contains(propertyRegion)

    def inWhitelist(locLists: LocationLists)(propertyRegion: Region): Boolean = locLists.whitelist.contains(propertyRegion)

    def inBlacklist(locLists: LocationLists)(propertyRegion: Region): Boolean = locLists.blacklist.contains(propertyRegion)
  }

  object Scaling {
    def linearScaleValue(scaling: ScalingSettings)(value: Double): Double = {
      val scaledValue = (value - scaling.minOriginal) / (scaling.maxOriginal - scaling.minOriginal) * (scaling.maxScaled - scaling.minScaled) + scaling.minScaled
      Math.max(scaling.minScaled, Math.min(scaling.maxScaled, scaledValue))
    }
  }

  object Rooms {
    def handleBedsDif(diff: Int): Double = diff match {
      case diff if diff < 0 => 0.0
      case diff if diff == 0 => 0.5
      case diff if diff == 1 => 0.8
      case diff if diff == 2 => 0.9
      case diff if diff > 2 => 1.0
    }

    def handleBathsDif(diff: Int): Double = diff match {
      case diff if diff < 0 => 0.2
      case diff if diff == 0 => 0.5
      case diff if diff == 1 => 0.85
      case diff if diff >= 2 => 1.0
    }

    def handleEnsuiteDif(req: Boolean, hasEnsuite: Boolean): Double = req match {
      case req if req && !hasEnsuite => 0.3
      case req if !req && !hasEnsuite => 0.5
      case req if !req && hasEnsuite => 0.7
      case req if req && hasEnsuite => 1.0
      case _ => 0.5
    }
  }
}
