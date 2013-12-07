package example

import spray.routing.HttpService
import akka.actor.{ Props, Actor, ActorRef }
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import spray.httpx.SprayJsonSupport._

object Service {
  def props(model: ActorRef): Props = Props(new Service(model))
  def name = "service"
}

class Service(model: ActorRef) extends Actor with Route {
  def actorRefFactory = context
  def timeout = Timeout(1.second)
  def receive = runRoute(route(model))
}

trait Route extends HttpService with ModelJsonProtocol {
  implicit def ec = actorRefFactory.dispatcher
  implicit def timeout: Timeout

  def route(model: ActorRef) =
    get {
      path(Segment) { segment =>
        onSuccess(model ? segment) {
          case m: ModelResponse => complete(m)
        }
      }
    }

}
