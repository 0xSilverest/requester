package requester

import org.scalatest.*
import flatspec.AnyFlatSpec
import matchers.should.Matchers

import com.github.silverest.requester.parser.RequesterSpecifications.*

import com.github.silverest.requester.enums.*

class ParserSpec extends AnyFlatSpec with Matchers {
  def fromYaml(yaml: String): List[RequesterSpec] =
    RequestSpec.fromYaml(yaml)

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
    
    firstAction.name shouldBe "action1"
    firstAction.endpointName shouldBe "endPoint1"
    firstAction.bodyType shouldBe BodyType.JSON
    firstAction.body shouldBe Option("./file.json")
    
    secondAction.name shouldBe "action2"
    secondAction.endpointName shouldBe "endPoint2"
    secondAction.bodyType shouldBe BodyType.JSON
    secondAction.body shouldBe Option(Map("param1" -> "value", "param2" -> "value"))

    thirdAction.name shouldBe "action3"
    thirdAction.endpointName shouldBe "endPoint3"
    thirdAction.bodyType shouldBe BodyType.Binary
    thirdAction.body shouldBe Option("./filename")
    
    fourthAction.name shouldBe "action4"
    fourthAction.endpointName shouldBe "endPoint4"
    fourthAction.bodyType shouldBe BodyType.CustomBody("User", BodyType.JSON)
  }

  "A list of models" should "be loaded correctly" in {
    val listOfModels = """
      models:
        User:
          username: String
          password: String@range:8:20 # String with length between 8 and 20
          role: Admin | Client | Operator # Basically takes one of the three
          email: String@regex:email # Email regex predefined
      
        Product:
          name: String@max:20 # String with length between 0 and 20
          price: Double@range:100:1000 # Double with value between 0 and 1000
          quantity: Int
    """

    val parsedYaml = fromYaml(listOfModels)
    parsedYaml shouldBe a [List[Model]]
    val firstModel = parsedYaml.head.asInstanceOf[Model]
    val secondModel = parsedYaml(1).asInstanceOf[Model]

    firstModel.name shouldBe "User"
    firstModel.fields shouldBe Map(
      "username" -> FieldType.String,
      "password" -> FieldType.StringOfLength(8, 20),
      "role" -> FieldType.Enum(List("Admin", "Client", "Operator")),
      "email" -> FieldType.StringWithRegex("email")
    )

    secondModel.name shouldBe "Product"
    secondModel.fields shouldBe Map(
      "name" -> FieldType.StringOfLength(0, 20),
      "price" -> FieldType.DoubleInRange(100, 1000),
      "quantity" -> FieldType.Int
    )
  }

  "A list of Regexes" should "be parsed correctly" in {
    val ListOfRegexes = """
      regex:
        customId: "[0-9]{2}[a-zA-Z]"
        email: (?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])
    """

    val parsedYaml = fromYaml(ListOfRegexes)
    parsedYaml shouldBe a [List[Regex]]
    val firstRegex = parsedYaml.head.asInstanceOf[Regex]
    firstRegex.name shouldBe "customId"
    firstRegex.pattern shouldBe "[0-9]{2}[a-zA-Z]"
    
    val secondRegex = parsedYaml(1).asInstanceOf[Regex]
    secondRegex.name shouldBe "email"
    secondRegex.pattern shouldBe """(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])"""
  }

  "A full yaml file" should "be parsed correctly" in {
    val yamlString = """
    link: localhost:8080
    
    endpoints:
      endPoint1:
        path: /api/endPoint1
        auth: true
      
      endPoint2:
        path: /api/endPoint2
        auth: false
    
    login:
      endpoint: /api/login
      parameters:
        username: username
        password: password
        # any other variables to be included within the json request
      responseType: json
      key: token # key-name inside the json
    
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
        
    models:
      User:
        username: String
        password: String@range:8:20 # String with length between 8 and 20
        role: Admin | Client | Operator # Basically takes one of the three
        email: String@regex:email # Email regex predefined
    
      Product:
        name: String@max:20 # String with length between 0 and 20
        price: Double@range:100:1000 # Double with value between 0 and 1000
        quantity: Int
    
    regex:
      customId: "[0-9]{2}[a-zA-Z]"
      email: (?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])
    """

    val parsedYaml = fromYaml(yamlString)
    parsedYaml shouldBe a [List[RequesterSpec]]

    val parsedLink = parsedYaml.filter(_.isInstanceOf[Link]).head.asInstanceOf[Link]

    parsedLink.link shouldBe "localhost:8080"

    val parsedEndpoints = parsedYaml.filter(_.isInstanceOf[Endpoint]).map(_.asInstanceOf[Endpoint])

    parsedEndpoints.map(_.path) shouldBe List(Seq("api","endPoint1"), Seq("api","endPoint2"))
    parsedEndpoints.map(_.isAuth) shouldBe List(true, false)

    val login = parsedYaml.filter(_.isInstanceOf[Login]).head.asInstanceOf[Login]

    login.parameters shouldBe Map("username" -> "username", "password" -> "password")
    login.responseType shouldBe ResponseType.JSON
    login.key shouldBe Option("token")
 
    val parsedActions = parsedYaml.filter(_.isInstanceOf[Action]).map(_.asInstanceOf[Action])

    val firstAction = parsedActions(0)
    val secondAction = parsedActions(1)
    val thirdAction = parsedActions(2)
    val fourthAction = parsedActions(3)
   
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

    val parsedModels = parsedYaml.filter(_.isInstanceOf[Model]).map(_.asInstanceOf[Model])

    val firstModel = parsedModels(0)
    val secondModel = parsedModels(1)

    firstModel.name shouldBe "User"
    firstModel.fields shouldBe Map(
      "username" -> FieldType.String,
      "password" -> FieldType.StringOfLength(8, 20),
      "role" -> FieldType.Enum(List("Admin", "Client", "Operator")),
      "email" -> FieldType.StringWithRegex("email")
    )

    secondModel.name shouldBe "Product"
    secondModel.fields shouldBe Map(
      "name" -> FieldType.StringOfLength(0, 20),
      "price" -> FieldType.DoubleInRange(100, 1000),
      "quantity" -> FieldType.Int
    )

    val parsedRegex = parsedYaml.filter(_.isInstanceOf[Regex]).map(_.asInstanceOf[Regex])

    val firstRegex = parsedRegex(0)
    val secondRegex = parsedRegex(1)

    firstRegex.name shouldBe "customId"
    firstRegex.pattern shouldBe "[0-9]{2}[a-zA-Z]"
    
    secondRegex.name shouldBe "email"
    secondRegex.pattern shouldBe """(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])"""
  }
}
