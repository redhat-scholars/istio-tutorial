
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {

	val httpProtocol = http
		.baseURL("http://customer-istiocb.192.168.99.102.nip.io")
		.inferHtmlResources()
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-GB,en;q=0.5")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:57.0) Gecko/20100101 Firefox/57.0")

	val headers_0 = Map("Upgrade-Insecure-Requests" -> "1")



	val scn = scenario("RecordedSimulation")
		.exec(http("request_0")
			.get("/")
			.headers(headers_0)
			.resources(http("request_1")
			.get("/favicon.ico")))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}