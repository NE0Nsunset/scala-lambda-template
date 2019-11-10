package lambda.pagelevel

import com.thoughtworks.binding.{Binding, FutureBinding, dom}
import lambda.{ClientConfig, SharedClass, UsesAjaxClient}
import lambda.api.{AnotherApiExample, BlogApi, SharedApi}
import org.scalajs.dom.document
import org.scalajs.dom.raw.{Event, HTMLInputElement, Node}
import com.thoughtworks.binding.Binding.Var
import lambda.models.{BlogItem, Movie}

import scala.util.{Failure, Success}
import lambda.serialization.Picklers._
import wvlet.airframe._
import autowire._
import lambda.routing.{RouteName, UsesSimpleRouter}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.std.option._
import scalaz.std.list._

import scala.concurrent.Future

class Home extends PageComponent with UsesAjaxClient with UsesSimpleRouter {

  val latestBlogs: FutureBinding[List[BlogItem]] = FutureBinding {
    ajaxClient[BlogApi].getNBlogs(10).call()
  }

  val clientConfig = bind[ClientConfig]
  val staticUrl = clientConfig.getStaticUrl

  @dom def banner: Binding[Node] = {
    <div id="index-banner" class="parallax-container" style="height:350px;">
      <div class="section no-pad-bot">
        <div class="container">
          <br /><br />
          <br /><br />
          <div class="row center">
            <h5 class="header col s12 white-text">A modern single page application template for AWS</h5>
            <h5 class="header col s12 white-text">Powered by a Scala backend and <a href="https://github.com/ThoughtWorksInc/Binding.scala" target="_blank" class="grey-text">Binding.scala</a> frontend</h5>
          </div>
          <br /><br />
        </div>
      </div>
      <div class="parallax"><img src={s"${staticUrl}static/img/traintracks.jpg"} alt="Unsplashed background img 1" style="transform: translate3d(-50%, 240.88px, 0px); opacity: 1;" /></div>
    </div>
  }

  @dom def bulletPoints: Binding[Node] = {
    <div class="container">
      <div class="row">
        <div class="col s12 m4">
          <div class="icon-block">
            <h2 class="center brown-text"><i class="material-icons">share</i></h2>
            <h5 class="center">Shared Objects</h5>
            <p class="light">
              Utilizing Scala.js, API contracts and objects are defined once (in the shared sub-project) between the frontend and backend.
              Additionally, all frontend requests are routed through just one Lambda via <a href="https://github.com/lihaoyi/autowire" target="_blank">Autowire</a> saving you time in development.
            </p>
          </div>
        </div>

        <div class="col s12 m4">
          <div class="icon-block">
            <h2 class="center brown-text"><i class="material-icons">cloud</i></h2>
            <h5 class="center">AWS and Local Development</h5>
            <p class="light">
              Iterate faster by developing locally with the included Akka Http server that simulates a close representation of how the Scala Lambda template would work once deployed to AWS.
              Pre-defined Terraform config files allow you to easily deploy a basic infrastructure to AWS when ready.
            </p>
          </div>
        </div>

        <div class="col s12 m4">
          <div class="icon-block">
            <h2 class="center brown-text"><i class="material-icons">settings</i></h2>
            <h5 class="center">Easy to Extend</h5>
            <p class="light">
              This template includes examples for getting objects into and out of Amazon's DynamoDb, a simple document store.
              With type-safety and a common language across the frontend and backend, it's easy to extend to your needs.
            </p>
          </div>
        </div>
      </div>
    </div>
  }

  @dom def blogTitle(blogItems: List[BlogItem]): Binding[Node] = {
    <ul>
      {blogItems map { blogItem =>
      <li>
        <a href="" onclick={event: Event => {event.preventDefault(); simpleRouter.changeToRouteByName(RouteName.BlogDetail.entryName, BlogDetail.props(new scalajs.js.Date(blogItem.rangeKey.split("#")(0)), blogItem.slug))  } }>
          {blogItem.title}
        </a>

      </li>
      }}
    </ul>
  }

  @dom def showMeTheCode: Binding[Node] = {
    <div class="container">
        <div class="section">
          <div class="row">
            <div class="col s12 center">
              <h3><i class="mdi-content-send brown-text"></i></h3>
              <h4>Getting Started</h4>
              <p>Checkout the <a href="https://bitbucket.org/jkapple/scala-lambda-template/" target="_blank">bitbucket repository</a> and read through the README.md to get a deployable copy of this website. Then, dive into the code to see how the examples work!</p>
              <p>This website is also a working example of the template. Open the developer console and head to the <a href="/dynamo-examples" onclick={e: Event => {
                e.preventDefault();
                simpleRouter.changeToRouteByName(RouteName.DynamoExample.entryName)}}>examples</a> page to see requests and responses in action.</p>
            </div>
          </div>
        </div>
      </div>
  }

  @dom def render: Binding[Node] =
    <div>
      {banner.bind}
      {bulletPoints.bind}
      {showMeTheCode.bind}
      <div class="no-pad-bot" id="index-banner">
        <div class="container">
          <div class="row">
          </div>
          <div class="row">
            {latestBlogs.bind match {
            case Some(Success(xs @ head :: tail)) => blogTitle(xs).bind
            case Some(Success(Nil)) => <!-- -->
            case _ => <p>loading ...</p>
            }}
          </div>
        </div>
      </div>
    </div>
}
