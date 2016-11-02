# IQ notes

[![CircleCI](https://circleci.com/gh/rockjam/iq-notes/tree/master.svg?style=svg)](https://circleci.com/gh/rockjam/iq-notes/tree/master)

This is test assignment. It provides REST API to manage notes.

## How to run

To run server locally, without docker, execute `sbt run`. You need to have [MongoDB](https://www.mongodb.com/) installed to run server.

## How to run tests

You can check current build status on Circe CI. Click on badge on top of README. 

To run test locally, execute `sbt test`. You need to have [MongoDB](https://www.mongodb.com/) installed to run tests.


## Examples

Assuming that you run server on localhost, base uri will be http://localhost:3000

```bash
# To create new user with specified password:
$ curl  -v -H "Content-Type: application/json" -XPOST -d '{ "username":"rockjam", "password":"ohmyholly" }' http://localhost:3000/api/register
# Response: 201 Created

# To get access token:
$ curl  -v -H "Content-Type: application/json" -XPOST -d '{ "username":"rockjam", "password":"ohmyholly" }' http://localhost:3000/api/login
# Response: {"accessToken":"262a43cb-16ba-485c-9104-e52902285c9e"}

# With token obtained on previous step you can access notes API

# To create new note:
$ curl -v -H "Content-Type: application/json" -XPUT -d '{ "title": "Shopping list", "body": "Milk, eggs and candies" }' http://localhost:3000/api/note\?access_token\=262a43cb-16ba-485c-9104-e52902285c9e
# Response: {"_id":"5819e5aa4e00005d00e66344"}

# To list all created notes:
$ curl -v -H "Content-Type: application/json" -XGET http://localhost:3000/api/note\?access_token\=262a43cb-16ba-485c-9104-e52902285c9e
# Response: [
              {
                "_id": "5819e5aa4e00005d00e66344",
                "title": "Shopping list",
                "body": "Milk, eggs and candies"
              },
              {
                "_id": "5819e5e64e00006d00e66345",
                "title": "TODO list",
                "body": "Finish assignment; make docker image"
              }
            ]
            
# To get specific note by its id:
$ curl -v -H "Content-Type: application/json" -XGET http://localhost:3000/api/note/5819e5aa4e00005d00e66344\?access_token\=262a43cb-16ba-485c-9104-e52902285c9e
# Response: {
              "_id": "5819e5aa4e00005d00e66344",
              "title": "Shopping list",
              "body": "Milk, eggs and candies"
            }

# To update notes content(you can update title or body, or both):
$ curl -v -H "Content-Type: application/json" -XPOST -d '{ "body": "Milk[Done], eggs[Done], candies[Done]" }' http://localhost:3000/api/note/5819e5aa4e00005d00e66344\?access_token\=262a43cb-16ba-485c-9104-e52902285c9e
# Response: 204 No Content

# To delete specific note by id:
$ curl -v -H "Content-Type: application/json" -XDELETE http://localhost:3000/api/note/5819e5aa4e00005d00e66344\?access_token\=262a43cb-16ba-485c-9104-e52902285c9e
# Response: 204 No Content
```


### License

This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).
