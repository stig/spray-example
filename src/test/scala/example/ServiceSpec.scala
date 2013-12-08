package example

import akka.actor.ActorDSL._
import org.scalatest.FlatSpec
import spray.testkit.ScalatestRouteTest
import akka.util.Timeout
import scala.concurrent.duration._
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._

class ServiceSpec extends FlatSpec with ScalatestRouteTest with ModelJsonProtocol {

  val data = Seq.range(1, 11).map(i => Item(i, s"title-$i", s"desc-$i"))
  val summary = (i: Item) => ItemSummary(i.id, i.title)

  val model = actor(new Act {
    become {
      case i: Int => sender ! data.find(_.id == i).getOrElse(None)
      case 'list => sender ! data.map(summary)
      case ('query, x: String) => sender ! data.filter(_.desc.contains(x)).map(summary)

    }
  })

  implicit def timeout = Timeout(3.second)

  def route = new Service {
    def actorRefFactory = system
  }.route(model)

  "The Service" should "return a list of 10 items" in {
    Get("/items") ~> route ~> check {
      assert(status === StatusCodes.OK)
      assert(responseAs[Seq[ItemSummary]].size === 10)
    }
  }

  it should "return a list of 2 items containing '1'" in {
    Get("/items?q=1") ~> route ~> check {
      assert(status === StatusCodes.OK)
      assert(responseAs[Seq[ItemSummary]].size === 2)
    }
  }

  it should "return single items" in {
    Get("/items/1") ~> route ~> check {
      assert(status === StatusCodes.OK)
      assert(responseAs[Item] === Item(1, "title-1", "desc-1"))
    }

    Get("/items/9") ~> route ~> check {
      assert(status === StatusCodes.OK)
      assert(responseAs[Item] === Item(9, "title-9", "desc-9"))
    }

  }

  it should "return 404 for non-existent items" in {
    Get("/items/404") ~> route ~> check {
      assert(status === StatusCodes.NotFound)
    }
  }

}
