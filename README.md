# alloc-ui

generated using Luminus version "3.28"

---
An experiment in:

1. [Clojure][14]
1. [Clojurescript][15], using [Reagent][10] and [Re-frame][11]
1. Rules-based constraint solvers using [Loco][12]
1. WebApi, including [Swagger][16] and [Swagger-UI][17]
1. SQL for the service, using [SQLite][18] and [HugSQL][19]
1. ...all composed using the [Luminus][20] clojure template for [Leiningen][21]
1. [Docker][13] container deployment



[10]: https://holmsand.github.io/reagent/
[11]: https://github.com/Day8/re-frame
[12]: https://github.com/aengelberg/loco
[13]: https://www.docker.com
[14]: https://clojure.org
[15]: https://clojurescript.org
[16]: https://swagger.io
[17]: https://swagger.io/tools/swagger-ui/
[18]: https://sqlite.org/index.html
[19]: https://www.hugsql.org
[20]: http://www.luminusweb.net
[21]: https://leiningen.org


(still to do...)
5. [Kubernetes][14] orchestration

[14]: https://kubernetes.io

---

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed. [Docker][2] will also need to be installed beforehand.

[1]: https://github.com/technomancy/leiningen
[2]: https://www.docker.com


## Starting form an Empty Folder

The project was originally built form the Luminus template, specifically:

    lein new luminus alloc-ui +re-frame +sqlite +swagger 

## Running

To start a web server for the application, run:

    lein run 
    
    
## Deploying to Docker

First, from the root project folder, build the uberjar. run:

    lein uberjar 
  
Next, you need to modify the Dockerfile in the root folder. add:

    ENV DATABASE_URL="jdbc:sqlite:./alloc_ui_dev.db"
    COPY alloc_ui_dev.db alloc_ui_dev.db

> thanks, Dmitri! (he pre-build the Dockerfile as part of [Luminus](http://www.luminusweb.net), although
we need to tweak it a little).


Then, build the Docker image. run (the dot _is_ important):

    docker build -t alloc-ui . 


Then, run the image in Dock. run:

    docker run -p 3500:3000 alloc-ui 

Check to see that it is running. run:

    docker ps 

...and look for your image in the list.

Finally, connect to the running image, but point the browser at:

    localhost:3500 
    
Even the Swagger UI page is available at:

    localhost:3500/swagger-ui
      
      
## Where to learn more
      
A collection of documentation, tutorials, and other resources

#### Reagent

- ["ClojureScript + Reagent Tutorial with Code Examples"](https://purelyfunctional.tv/guide/reagent/)

- ["Reagent deep dive"](https://timothypratley.blogspot.com/2017/01/reagent-deep-dive-part-1.html)

- ["Transforming Enterprise Development With Clojure" (video)](https://www.youtube.com/watch?v=nItR5rwP4mY)

#### Re-frame

- ["Re-Frame - Functional Reactive Programming With Clojurescript"](https://dhruvp.github.io/2015/03/07/re-frame/)

- ["re-frame interactive demo"](https://blog.klipse.tech/clojure/2019/02/17/reframe-tutorial.html)


#### Loco

- ["Appointment scheduling in Clojure with Loco"](https://programming-puzzler.blogspot.com/2014/03/appointment-scheduling-in-clojure-with.html)

- ["Optimization with Loco"](https://programming-puzzler.blogspot.com/2014/03/optimization-with-loco.html)

#### Docker

- ["Docker for Beginners"](https://docker-curriculum.com)

- ["Get Going From Scratch"](https://stackify.com/docker-tutorial/)

- ["Introduction To Docker & Containerization"](https://www.edureka.co/blog/docker-tutorial)

#### Kubernetes

Under construction
    
    
    

## License

Copyright © 2019 Northrop Grumman.
