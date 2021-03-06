package lib

import java.net.{URLDecoder, URI}
import scala.concurrent.Future
import _root_.play.api.mvc.{Results, Result, RequestHeader, Filter}
import com.gu.mediaservice.syntax._

object ForceHTTPSFilter extends Filter {

  /** Uses the X-Forwarded-Proto header (added by Amazon's ELB) to determine whether
    * the client used HTTPS, and redirect if not.
    *
    * Assumes untrusted clients can only connect via the ELB!
    */
  def apply(f: (RequestHeader) => Future[Result])(request: RequestHeader): Future[Result] =
    if (request.forwardedProtocol.exists(_ != "https")) {
      val queryString = Some(URLDecoder.decode(request.rawQueryString, "UTF-8")).filter(_.nonEmpty)
      val uri = new URI("https", request.host, request.path, queryString.orNull, null)
      Future.successful(Results.MovedPermanently(uri.toString))
    }
    else f(request)

}
