package example

import akka.actor._
import spray.can.Http
import akka.io.IO
import akka.actor.Terminated
import akka.actor.SupervisorStrategy.{ Restart, Stop }

object TopLevel {
  def props: Props = Props(new TopLevel with ProductionTopLevelConfig)
  def name = "top-level"
}

trait ProductionTopLevelConfig extends TopLevelConfig {
  this: Actor =>

  private def c = context.system.settings.config
  def interface = c.getString("example-app.service.interface")
  def port = c.getInt("example-app.service.port")
}

trait TopLevelConfig {
  def interface: String
  def port: Int
}

class TopLevel extends Actor with ActorLogging {
  this: TopLevelConfig =>

  import context._

  val model = actorOf(Model.props, Model.name)
  context watch model

  val service = actorOf(ServiceActor.props(model), ServiceActor.name)
  context watch service

  IO(Http) ! Http.Bind(service, interface, port)

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
    case _ if model == sender => Stop
    case _ if service == sender => Restart
  }

  def receive = {
    case Terminated(`model`) => context stop self
  }

}
