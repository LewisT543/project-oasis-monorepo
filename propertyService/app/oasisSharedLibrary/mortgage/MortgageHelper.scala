package oasisSharedLibrary.mortgage

import oasisSharedLibrary.domain.{Applicant, CalcResult, MortgageSettings}

object MortgageHelper {
  def roundBigDec(value: BigDecimal, dp: Int): Double = value.setScale(dp, BigDecimal.RoundingMode.HALF_UP).toDouble

  def zipToMap[A <: Product](instance: A): Map[String, Any] = instance.productElementNames.zip(instance.productIterator).toMap

  def prettyPrintResult(applicants: List[Applicant], settings: MortgageSettings, res: CalcResult): Unit = {
    println(s"Settings: ")
    println(s"    ${zipToMap(settings)}")
    println("Given applicants: ")
    applicants.foreach(ap => println(s"    ${zipToMap(ap)}"))
    println("Results: ")
    println(s"    LOW result: ${zipToMap(res.lowResult)}")
    println(s"    HIGH result: ${zipToMap(res.highResult)}")
    println("Affordability Breakdown: ")
    println(s"    LOW result: ${zipToMap(res.lowResult.affordability)}")
    println(s"    HIGH result: ${zipToMap(res.highResult.affordability)}")
  }
}
