package example

import spray.json.DefaultJsonProtocol

trait ModelJsonProtocol extends DefaultJsonProtocol {

  implicit val itemFmt = jsonFormat4(Item)
  implicit val itemSummaryFmt = jsonFormat3(ItemSummary)

}
