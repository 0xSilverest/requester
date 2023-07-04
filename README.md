# Requester

A terminal app to benchmark and stress-test a server for handling requests.

## Input example

```yaml
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

```
