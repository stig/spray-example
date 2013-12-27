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

  import scala.language.postfixOps // for 'q ? in parameter() below

  implicit def ec = actorRefFactory.dispatcher

  /*
   One of the main benefits of a custom API (rather than just serving from a dumb data store)
   is that one can add customised caching. In this case we instruct upstream caches to cache
   an item for 10 second per item in stock.
   */
  val cacheSecondsPerItemInStock = 10
  val cacheHeader = (maxAge: Long) => `Cache-Control`(`max-age`(maxAge)) :: Nil
  val scaledCacheHeader = (maxAge: Long) => cacheHeader(maxAge * cacheSecondsPerItemInStock)

  def route(model: ActorRef)(implicit askTimeout: Timeout) =
    get {
      path("items") {
        parameter('q ?) { term =>
          val msg = term.map('query -> _).getOrElse('list)
          onSuccess(model ? msg) {
            case ItemSummaries(summaries) =>
              // Use the smallest stock value in the returned list as a max-age decider
              complete(OK, scaledCacheHeader(summaries.map(_.stock).min + 1), summaries map toPublicItemSummary)
          }
        }
      } ~
        path("items" / IntNumber) { id =>
          onSuccess(model ? id) {
            case item: Item => complete(OK, scaledCacheHeader(item.stock + 1), toPublicItem(item))

            // Cache 404 for 60 seconds
            case ItemNotFound => complete(StatusCodes.NotFound, cacheHeader(60), "Not Found")
          }
        }
    }

}
