
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {
    // val baseURL = System.getProperty("endpoint.url")
	val httpProtocol = http
		.baseURL("http://customer-tutorial.192.168.99.103.nip.io")
		.inferHtmlResources()
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-GB,en;q=0.5")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:57.0) Gecko/20100101 Firefox/57.0")

	val headers_0 = Map("Upgrade-Insecure-Requests" -> "1")

    val scn = scenario("IstioCBServiceSimulation")
		.exec(http("request_0")
			.get("/")
			.headers(headers_0)
		    .check(status.is(200)))
    
    // to see it in real action worth adding some Load to the system earlier and then fire this test 
	// making sure my load gets the response in right expected responseTime
	setUp(scn.inject(atOnceUsers(4)))
        .assertions(global.responseTime.max.lt(3000))
        .protocols(httpProtocol)
}