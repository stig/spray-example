My Spray Example
================

I intend this as a example project for creating Spray APIs that goes a bit beyond just the routing layer. In particular, it shows how to wire together an API that uses a separate service and model actor.

To start the example service, cd into the directory and run sbt. Once SBT starts, run:

    $ sbt
    # Once sbt has compiled the build system, run the service:
    > re-start

In a different terminal (or a browser), call the service:

    $ curl localhost:8080/items

You should see:

    [{
      "id": 1,
      "title": "foo"
    }, {
      "id": 2,
      "title": "bar"
    }, {
      "id": 3,
      "title": "qux"
    }, {
      "id": 4,
      "title": "quux"
    }, {
      "id": 5,
      "title": "quuux"
    }]

Now try getting a single item:

    $ curl localhost:8080/items/2

You should see:

    {
      "id": 2,
      "title": "bar",
      "desc": "More information about Bar"
    }

Finally, try getting an item that doesn't exist:

    $ curl localhost:8080/items/23

You should get a "Not Found" message, and the status code 404.
