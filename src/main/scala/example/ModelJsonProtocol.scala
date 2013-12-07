package example

import spray.json.DefaultJsonProtocol

trait ModelJsonProtocol extends DefaultJsonProtocol {

  implicit val modelResponseFmt = jsonFormat2(ModelResponse)

}
