package example

import spray.json.DefaultJsonProtocol

trait ModelJsonProtocol extends DefaultJsonProtocol {

  implicit val itemFmt = jsonFormat3(Item)
  implicit val itemSummaryFmt = jsonFormat2(ItemSummary)

}
