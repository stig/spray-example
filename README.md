Spray Example
=============

I want this to be an example of a Spray API that goes a bit beyond just the
routing layer. In particular, it shows how to wire together an API that uses a
separate service and model actor. It also shows off a few things I consider good
practice:

* "Intelligent" cache control. Tailor the upstream cache time per resource.

* Separate on-the-wire protocol. I often see code bases where the domain objects
contain more annotations than code---often for both JSON and ORM mappings.
I think this is bad practice and prefer to use different objects.

* Use [sbt-revolver][]. This is great plugin by the Spray guys to simplify and
speed up the dev/build/test cycle.

* Stub out child actors for testing using the *Cake Pattern*. See the
`TopLevelConfig` trait and its corresponding `ProductionTopLevelConfig`
implementation for more details.

[sbt-revolver]: https://github.com/spray/sbt-revolver

Running the example service
---------------------------

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
      "stockLevel": "LowStock",
      "title": "foo"
    }, {
      "id": 2,
      "stockLevel": "LowStock",
      "title": "bar"
    }, {
      "id": 3,
      "stockLevel": "InStock",
      "title": "qux"
    }, {
      "id": 4,
      "stockLevel": "InStock",
      "title": "quux"
    }, {
      "id": 5,
      "stockLevel": "InStock",
      "title": "quuux"
    }]

You can also try getting only the items where the description contains 'Qu':

    $ curl 'localhost:8080/items?q=Qu'

The quotes are necessary because `?` would otherwise be interpreted by the
shell. Anyway, you should see this:

    [{
      "id": 3,
      "stockLevel": "InStock",
      "title": "qux"
    }, {
      "id": 4,
      "stockLevel": "InStock",
      "title": "quux"
    }, {
      "id": 5,
      "stockLevel": "InStock",
      "title": "quuux"
    }]

Now try getting a single item:

    $ curl localhost:8080/items/2

You should see:

    {
      "id": 2,
      "stockLevel": "LowStock",
      "title": "bar",
      "desc": "More information about Bar"
    }

Finally, try getting an item that doesn't exist:

    $ curl localhost:8080/items/23

You should get a "Not Found" message, and the status code 404.

