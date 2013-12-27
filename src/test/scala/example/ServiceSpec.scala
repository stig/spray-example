package example

import akka.actor.ActorDSL._
import org.scalatest.FlatSpec
import spray.testkit.ScalatestRouteTest
import akka.util.Timeout
import scala.concurrent.duration._
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._
import spray.http.CacheDirectives.`max-age`
import spray.http.HttpHeaders.`Cache-Control`

class ServiceSpec extends FlatSpec with ScalatestRouteTest with ServiceJsonProtocol {

  import ModelActor._

  val data = Seq.range(1, 11).map(i => Item(i, i + 2, s"title-$i", s"desc-$i"))
  val summary = (i: Item) => ItemSummary(i.id, i.stock, i.title)

  val model = actor(new Act {
    become {
      case i: Int => sender ! data.find(_.id == i).getOrElse(ItemNotFound)
      case 'list => sender ! ItemSummaries(data.map(summary))
      case ('query, x: String) => sender ! ItemSummaries(data.filter(_.desc.contains(x)).map(summary))

    }
  })

  implicit def timeout = Timeout(3.second)

  def route = new Service {
    def actorRefFactory = system
  }.route(model)

  "The Service" should "return a list of 10 items" in {
    Get("/items") ~> route ~> check {
      assert(status === StatusCodes.OK)
      assert(header[`Cache-Control`] === Some(`Cache-Control`(`max-age`(10))))

      val res = responseAs[Seq[PublicItemSummary]]
      assert(res.size === data.size)
      assert(res.head === PublicItemSummary(summary(data.head)))
    }
  }

  it should "return a list of 2 items containing '1'" in {
    Get("/items?q=1") ~> route ~> check {
      assert(status === StatusCodes.OK)
      assert(header[`Cache-Control`] === Some(`Cache-Control`(`max-age`(10))))

      val res = responseAs[Seq[PublicItemSummary]]
      assert(res.size === 2)
      assert(res.head === PublicItemSummary(summary(data.head)))
    }
  }

  it should "return single items" in {
    Get("/items/1") ~> route ~> check {
      assert(status === StatusCodes.OK)
      assert(header[`Cache-Control`] === Some(`Cache-Control`(`max-age`(10))))
      assert(responseAs[PublicItem] === PublicItem(1, LowStock, "title-1", "desc-1"))
    }

    Get("/items/9") ~> route ~> check {
      assert(status === StatusCodes.OK)
      assert(header[`Cache-Control`] === Some(`Cache-Control`(`max-age`(30))))
      assert(responseAs[PublicItem] === PublicItem(9, InStock, "title-9", "desc-9"))
    }

  }

  it should "return 404 for non-existent items" in {
    Get("/items/404") ~> route ~> check {
      assert(status === StatusCodes.NotFound)
      assert(header[`Cache-Control`] === Some(`Cache-Control`(`max-age`(60))))
      response === "Not Found"
    }
  }

}
