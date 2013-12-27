package example

import spray.routing.HttpService
import akka.actor.{ Props, Actor, ActorRef }
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import spray.httpx.SprayJsonSupport._
import spray.http.StatusCodes
import spray.http.CacheDirectives.`max-age`
import spray.http.HttpHeaders.`Cache-Control`
import spray.http.StatusCodes._

object ServiceActor {
  def props(model: ActorRef)(implicit askTimeout: Timeout): Props = Props(classOf[ServiceActor], model, askTimeout)
  def name = "service"
}

class ServiceActor(model: ActorRef, implicit val askTimeout: Timeout) extends Actor with Service {
  def actorRefFactory = context
  def receive = runRoute(route(model))
}

trait Service extends HttpService with ServiceJsonProtocol {

  import ModelActor._

  import scala.language.postfixOps // for 'q ? in parameter() below

  implicit def ec = actorRefFactory.dispatcher

  val cacheHeader = (maxAge: Long) => `Cache-Control`(`max-age`(maxAge)) :: Nil

  // To avoid leaking exact stock levels via cache control header's max-age
  // use a formula to obfuscate the levels a bit. Because this is an example I'm
  // hard-coding cache multipliers here.
  val cacheTime = (stockLevel: Int) =>
    if (stockLevel > 0)
      10 * math.sqrt(stockLevel).toLong
    else
      100L

  def route(model: ActorRef)(implicit askTimeout: Timeout) =
    get {
      path("items") {
        parameter('q ?) { term =>
          val msg = term.map('query -> _).getOrElse('list)
          onSuccess(model ? msg) {
            case ItemSummaries(Nil) =>
              // Cache an empty list for 60 seconds (similar to 404 below)
              complete(OK, cacheHeader(60), List.empty[PublicItem])

            case ItemSummaries(summaries) =>
              // Use the smallest stock value in the returned list as a max-age decider
              val maxAge = cacheTime(summaries.map(_.stock).min)
              complete(OK, cacheHeader(maxAge), summaries map { PublicItemSummary(_) })
          }
        }
      } ~
        path("items" / IntNumber) { id =>
          onSuccess(model ? id) {
            case item: Item =>
              // Cache items in stock by a function of their stock level
              val maxAge = cacheTime(item.stock)
              complete(OK, cacheHeader(maxAge), PublicItem(item))

            case ItemNotFound =>
              // Cache 404 for 60 seconds
              complete(StatusCodes.NotFound, cacheHeader(60), "Not Found")
          }
        }
    }

}
