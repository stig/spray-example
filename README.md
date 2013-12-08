Spray Example
=============

I intend this as a example project for creating Spray APIs that goes a bit
beyond just the routing layer. In particular, it shows how to wire together an
API that uses a separate service and model actor.

To start the example service, launch a terminal and cd into the directory and
run sbt:

    $ sbt

Once sbt starts the prompt will change. You can start the example service in the background using the `re-start` command, provided by the `sbt-revolver` plugin:

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
