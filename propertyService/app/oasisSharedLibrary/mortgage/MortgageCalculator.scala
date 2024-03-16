package oasisSharedLibrary.mortgage

import oasisSharedLibrary.domain.{AffordabilityResult, Applicant, CalcResult, MortgageSettings, SingleResult}
import oasisSharedLibrary.mortgage.MortgageHelper.roundBigDec

import scala.math.pow

object MortgageCalculator {

  private def getMonthlyPayments(loan: Int, monthlyRate: Double, loanLengthInMonths: Int): Int = {
    ((loan * monthlyRate) / (1 - pow(1 + monthlyRate, -loanLengthInMonths))).toInt
  }

  private def getAffordability(expenses: Int, netIn: Int, monthlyPayment: Int): AffordabilityResult = {
    val totalCosts = expenses + monthlyPayment
    val loanPctOfNet = roundBigDec(BigDecimal(totalCosts) / BigDecimal(netIn), 2)
    val leftovers = netIn - totalCosts
    val leftoverPct = roundBigDec(BigDecimal(1 - loanPctOfNet), 2)
    AffordabilityResult(loanPctOfNet, leftoverPct, totalCosts, leftovers)
  }

  private def getResult(settings: MortgageSettings)(lendingRatio: Double, totalSalaries: Int, monthlyRate: Double, lengthInMonths: Int, expenses: Int, income: Int): SingleResult = {
    val loanResult: Int = if settings.usingNet then (((income - expenses) * 12) * lendingRatio).toInt else (totalSalaries * lendingRatio).toInt
    val monthlyPayments: Int = getMonthlyPayments(loanResult, monthlyRate, lengthInMonths)
    val lowAffordability: AffordabilityResult = getAffordability(expenses, income, monthlyPayments)
    SingleResult(loanResult, settings.repaymentTerm, settings.interestRate, monthlyPayments, lowAffordability)
  }

  def calculateMortgage(settings: MortgageSettings)(applicants: List[Applicant]): CalcResult = {
    val totalSalaries: Int = applicants.map(_.salary).sum
    val totalIncomes: Int = applicants.map(_.netMonthly).sum
    val totalExpenses: Int = applicants.map(_.expenses.total).sum
    val monthlyRate: Double = settings.interestRate / 12
    val lengthInMonths: Int = settings.repaymentTerm * 12

    val lowResult = getResult(settings)(settings.loanToSalaryRange._1, totalSalaries, monthlyRate, lengthInMonths, totalExpenses, totalIncomes)
    val highResult = getResult(settings)(settings.loanToSalaryRange._2, totalSalaries, monthlyRate, lengthInMonths, totalExpenses, totalIncomes)

    CalcResult(lowResult, highResult)
  }

}
