package example

import akka.actor.{ Props, Actor }

case class Item(id: Int, title: String, desc: String)
case class ItemSummary(id: Int, title: String)

object ModelActor {
  def props: Props = Props(new ModelActor)
  def name = "model"
}

class ModelActor extends Actor with Model {

  def receive = {
    case id: Int =>
      sender ! get(id).getOrElse(None)

    case 'list =>
      sender ! list

    case ('query, term: String) =>
      sender ! query(term)

  }

}

trait Model {
  private val items = Item(1, "foo", "More information about Foo") ::
    Item(2, "bar", "More information about Bar") ::
    Item(3, "qux", "More information about Qux") ::
    Item(4, "quux", "More information about Quux") ::
    Item(5, "quuux", "More information about Quuux") ::
    Nil

  def get(id: Int) = items find (_.id == id)

  def list = items map { i => ItemSummary(i.id, i.title) }

  def query(s: String) = items filter (_.desc.contains(s))

}