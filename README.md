# alloc-ui

generated using Luminus version "3.28"

FIXME

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein run 
    
    
## Deplying to Docker

first, form the root project folder, build the uberjar. run:

    lein uberjar 
  
then, build the Docker image. run (the dot _is_ important):

    docker build -t alloc-ui . 

> thanks, Dmitri! (he pre-build the docker file as part of [Luminus](http://www.luminusweb.net))


then, run the image in Dock. run:

    docker run -p 3500:3000 alloc-ui 

check to see that it is running. run:

    docker run -p 3500:3000 alloc-ui 

and look for your image in the list.

finally, connect to the running image, but point the browser at:

    localhost:3500 
      

## License

Copyright © 2019 FIXME
