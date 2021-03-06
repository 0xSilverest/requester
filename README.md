# Requester

A terminal app to benchmark and stress-test a server for handling requests.

## App concept

```yaml
link: localhost:8080
endpoints:
    name1:
      path: /api/test
      type: (Auth|NoAuth)

regex:
  customeId: "[0-9]{2}[a-zA-Z]"

models:
  User:
    username: String
    password: String
    role: Admin | Client | Operator # Basically takes one of the three
    email: String@regex:email # Email regex predefined

login:
    requestType: POST
    parameters:
        username: username
        password: password
        # any other variables
    # For the moment will just handler json for auth response
    responseType: json
    token: key-name

flow:
    endpointName1:
      requestType: PUT
      bodyType: json
      body: ./file.json
      repeat: 3 # Optional

    endpointName2:
      requestType: POST
      bodyType: json 
      body:
        param1: value
        param2: value

    endpointName3:
      requestType: PATCH
      bodyType: binary
      file: ./filename # path from directory you're in or absolute path starting with /

    endpointName4:
      requestType: POST
      bodyType: json@User
      # if no body passed it will auto generate a random user
      # else it will validate your model before passing it
      
    endpointName5:
      requestType: GET
```
