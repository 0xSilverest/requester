package requester

import org.scalatest.*
import flatspec.AnyFlatSpec
import matchers.should.Matchers

import com.github.silverest.requester.dsl.RequesterSpecifications.RequestSpec.*
import com.github.silverest.requester.dsl.RequesterSpecifications.*

import com.github.silverest.requester.enums.*

class ParserSpec extends AnyFlatSpec with Matchers {
  "A Link" should "be parsed correctly" in {
    val link = "link: https://www.google.com"
    val parsedLink = fromYaml(link).head
    parsedLink shouldBe a [Link]
    parsedLink.asInstanceOf[Link].link shouldBe "https://www.google.com"
  }

  "A list of Endpoints" should "be parsed correctly" in {
    val endpoints = """
      endpoints:
        endPoint1:
          path: /api/endPoint1
          auth: true
        
        endPoint2:
          path: /api/endPoint2
          auth: false
    """
    val parsedEndpoints = fromYaml(endpoints)
    parsedEndpoints shouldBe a [List[Endpoint]]
    val firstEndpoint = parsedEndpoints.head.asInstanceOf[Endpoint]
    val secondEndpoint = parsedEndpoints(1).asInstanceOf[Endpoint]

    firstEndpoint.path shouldBe Seq("api","endPoint1")
    firstEndpoint.isAuth shouldBe true

    secondEndpoint.path shouldBe Seq("api","endPoint2")
    secondEndpoint.isAuth shouldBe false
  }

  "A Login endpoint" should "be parsed correctly" in {
    val loginEndpoint = """
      login:
        endpoint: /api/login
        parameters:
          username: username
          password: password
        responseType: json
        key: token
    """

    val parsedLoginEndpoint = fromYaml(loginEndpoint).head
    parsedLoginEndpoint shouldBe a [Login]
    val login = parsedLoginEndpoint.asInstanceOf[Login]
    login.parameters shouldBe Map("username" -> "username", "password" -> "password")
    login.responseType shouldBe ResponseType.JSON
    login.key shouldBe Option("token")
  }

  "A list of Actions" should "be parsed correctly" in {
    val listOfActions = """
      actions:
        action1:
          endpoint: endPoint1
          bodyType: json 
          body: ./file.json
      
        action2:
          endpoint: endPoint2
          bodyType: json 
          body:
            param1: value
            param2: value
      
        action3:
          endpoint: endPoint3
          bodyType: binary
          body: ./filename # path from directory you're in or absolute path starting with /
      
        action4:
          endpoint: endPoint4
          bodyType: json@User
          # if no body passed it will auto generate a random user
          # else it will validate your model before passing it
    """

    val parsedListOfActions = fromYaml(listOfActions)
    parsedListOfActions shouldBe a [List[Action]]
    val firstAction = parsedListOfActions.head.asInstanceOf[Action]
    val secondAction = parsedListOfActions(1).asInstanceOf[Action]
    val thirdAction = parsedListOfActions(2).asInstanceOf[Action]
    val fourthAction = parsedListOfActions(3).asInstanceOf[Action]
    
    firstAction.endpointName shouldBe "endPoint1"
    firstAction.bodyType shouldBe BodyType.JSON
    firstAction.body shouldBe Option("./file.json")
    
    secondAction.endpointName shouldBe "endPoint2"
    secondAction.bodyType shouldBe BodyType.JSON
    secondAction.body shouldBe Option(Map("param1" -> "value", "param2" -> "value"))

    thirdAction.endpointName shouldBe "endPoint3"
    thirdAction.bodyType shouldBe BodyType.Binary
    thirdAction.body shouldBe Option("./filename")
    
    fourthAction.endpointName shouldBe "endPoint4"
    fourthAction.bodyType shouldBe BodyType.CustomBody("User", BodyType.JSON)
  }
  
}
