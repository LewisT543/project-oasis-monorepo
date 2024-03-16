package controllers

import javax.inject._
import play.api._
import play.api.libs.json.Json
import play.api.mvc._
import oasisSharedLibrary.domain.{MortgageSettings, Property}
import decoders.PropertyJsonFormats.*
import repos.MockPropertyRepository

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  def getProperties: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val propertyRepository = new MockPropertyRepository()
    val properties: List[Property] = propertyRepository.getAllProperties
    properties match {
      case properties if properties.isEmpty => NotFound("No Properties found")
      case properties => Ok(Json.toJson[List[Property]](properties))
    }
  }

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
}
