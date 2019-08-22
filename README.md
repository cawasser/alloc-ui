# alloc-ui

generated using Luminus version "3.28"

Alloc-UI

An experiment in:

1. Clojure
2. Clojurescript
3. Rules-based constraint solvers
4. Docker container deployment

(still to do...)
5. Kubernetes

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed. [Docker][2] will also be needed

[1]: https://github.com/technomancy/leiningen
[2]: https://www.docker.com

## Running

To start a web server for the application, run:

    lein run 
    
    
## Deploying to Docker

first, from the root project folder, build the uberjar. run:

    lein uberjar 
  
next, you need to modify the Dockerfile in the root folder. add:

    ENV DATABASE_URL="jdbc:sqlite:./alloc_ui_dev.db"
    COPY alloc_ui_dev.db alloc_ui_dev.db

> thanks, Dmitri! (he pre-build the Dockerfile as part of [Luminus](http://www.luminusweb.net), although
we need to tweak it a little).


then, build the Docker image. run (the dot _is_ important):

    docker build -t alloc-ui . 


then, run the image in Dock. run:

    docker run -p 3500:3000 alloc-ui 

check to see that it is running. run:

    docker ps 

and look for your image in the list.

finally, connect to the running image, but point the browser at:

    localhost:3500 
    
even the Swagger UI page is available at:

    localhost:3500/swagger-ui
      

## License

Copyright © 2019 Northrop Grumman.
