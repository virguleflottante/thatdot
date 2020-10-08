# REST API

The REST API is accompanied by a [Swagger UI](https://swagger.io/tools/swagger-ui/)
served up at `/docs` that lists out all of the endpoints, how to call them, and some inline
descriptions of what they do. The UI even makes it possible to interactively try out endpoints
by filling in text fields and clicking buttons.

For instance, on that page, just under the **Cypher query language** section, you can click on
the green box containing `POST /api/v1/query/cypher issue a Cypher query` to expand out the
interactive panel. Now, you can see more details about this endpoint as well as try it out
interactively by clicking on the **Try it out** button, optionally editting the query, and then
clicking on the **Execute** button.

This entire page is powered by a programmatically generated OpenAPI specification of our API (the
raw version which is accessible at `/docs/documentation.json`). This specification can also be used
by users to programatically generate client programs that call the API in the correct expected
manner, see [this page](https://openapi-generator.tech/) for more details about that.

