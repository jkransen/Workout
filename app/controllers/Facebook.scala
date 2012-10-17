package controllers

import play.api.Play.current
import play.api.libs.ws.WS
import play.api.mvc._;
import models.User
import scala.util.matching.Regex
import com.restfb.DefaultFacebookClient

object Facebook extends Controller {

  val config = play.api.Play.configuration
  val appId = config.getString("facebook.app_id").get
  val appSecret = config.getString("facebook.app_secret").get
  val redirectUrl = config.getString("facebook.redirect_url").get

  val url = "https://www.facebook.com/dialog/oauth?client_id=" + appId + "&redirect_uri=" + redirectUrl + "&scope=email"

  def login = Action {
    Redirect(url)
  }

  // we don't care about the ref parameter, but we need to match it
  def loginWithParams(ref: Option[String], code: Option[String]) = Action {
    if (code.isDefined) {
      loginWithCode(code.get)
    } else {
      Redirect(url)
    }
  }

  def canvas(trash: Option[String]) = Action {
    request =>
      println(request.rawQueryString)
      Redirect(controllers.routes.Facebook.login)
  }

  def login2(code: String) = Action {
    if (!code.isEmpty) {
      loginWithCode(code)
    } else {
      Redirect(controllers.routes.Facebook.login)
    }
  }

  def loginWithCode(code: String): Result = {
    val accessTokenUrl = "https://graph.facebook.com/oauth/access_token?client_id=" + appId + "&client_secret=" + appSecret + "&code=" + code + "&redirect_uri=" + redirectUrl
    val accessTokenBody = WS.url(accessTokenUrl).get().value.get.body
    val regex = new Regex("access_token=(.*)&expires=(.*)")
    accessTokenBody match {
      case regex(accessToken, expires) => {
        val facebookClient = new DefaultFacebookClient(accessToken)
        val fbUser = facebookClient.fetchObject("me", classOf[com.restfb.types.User])
        val user = getOrCreateUser(fbUser)
        Redirect(controllers.routes.Application.index).withSession("connected" -> user.email)
      }
    }
  }

  def getOrCreateUser(fbUser: com.restfb.types.User): User = {
    val facebookUsername = fbUser.getUsername()
    val user = User(fbUser.getEmail);
    if (user == null) {
      createFacebookUser(fbUser.getEmail, facebookUsername, fbUser.getName());
    } else {
      user
    }
  }

  def createFacebookUser(email: String, facebookUsername: String, fullName: String): User = {
    User.create(User(0, email, fullName, facebookUsername))
    User(email)
  }
}
