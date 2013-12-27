package example

import akka.actor.{ Props, Actor }

object ModelActor {
  def props: Props = Props[ModelActor]
  def name = "model"

  case object ItemNotFound
  case class ItemSummaries(items: Seq[ItemSummary])
}

class ModelActor extends Actor with Model {
  import ModelActor._

  def receive = {
    case id: Int =>
      sender ! get(id).getOrElse(ItemNotFound)

    case 'list =>
      sender ! ItemSummaries(list)

    case ('query, term: String) =>
      sender ! ItemSummaries(query(term))

  }

}

