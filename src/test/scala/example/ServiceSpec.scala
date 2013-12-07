package example

import akka.actor.ActorDSL._
import org.scalatest.FlatSpec
import spray.testkit.ScalatestRouteTest
import akka.util.Timeout
import scala.concurrent.duration._
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._

class ServiceSpec extends FlatSpec with ScalatestRouteTest with ModelJsonProtocol {

  val model = actor(new Act {
    become {
      case i: Int if i < 10 => sender ! Item(i, s"title-$i", "desc")
      case i: Int => sender ! None
      case 'list => sender ! Seq.range(0, 10).map(i => ItemSummary(i, s"title-$i"))
    }
  })

  def route = new Service {
    def actorRefFactory = system
    def timeout = Timeout(1.second)
  }.route(model)

  "The Service" should "return a list" in {
    Get("/items") ~> route ~> check {
      assert(status === StatusCodes.OK)
      assert(responseAs[Seq[ItemSummary]].size === 10)
    }

  }

  it should "return single items" in {
    Get("/items/1") ~> route ~> check {
      assert(status === StatusCodes.OK)
      assert(responseAs[Item] === Item(1, "title-1", "desc"))
    }
  }

  it should "return 404 for non-existent items" in {
    Get("/items/404") ~> route ~> check {
      assert(status === StatusCodes.NotFound)
    }
  }

}
