
# Vision
Build an static documentation web site from a sparql endpoint. The documentation must rely on what the endpoint actually contains.
The aim is to provide support to developers and to minimize the effort needed to design the query they need.
The documentation will distinguish information that is IN the endpoint from information that can be provided externally (specifications, vocabularies) by giving links.

# TODO
* Use sub-folders to prevent side effect of punning
* List of namespaces as SPARQL prefix declarations
* Implement a way of attaching snippets relevant to the item documented
* Create a goal for building the knowledge base, separating it from the site building
* Add information about the default graph
* Types: add properties that has instances of that type as range
* Provide statistics about:
** Number of instances of types
** Number of distinct subjects of properties
** Number of distinct objects of properties
** Number of triples of each graph
* Provide always a sparql query which will return the currently displayed information