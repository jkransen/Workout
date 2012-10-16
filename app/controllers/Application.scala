package controllers

import play.api.GlobalSettings
import play.api.mvc.Action
import play.api.mvc.Controller

import models.User
import models.Gym

object Application extends Controller {

  def index = Action {
    request =>
      val session = request.session
      session.get("connected").map { email =>
        val user = User(email);
        Ok(views.html.index("Hello " + user.fullname))
      }.getOrElse {
        Ok(views.html.index("Not logged in"))
      }
  }

  def login = Action {
    Ok(views.html.login())
  }

  def logout = Action {
    Redirect(controllers.routes.Application.index).withNewSession
  }
}

