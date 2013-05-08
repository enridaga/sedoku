package enridaga.sedoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.SystemDefaultHttpClient;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 
 * @author enridaga
 * 
 */
public class LiveDataProvider implements DataProvider {

	private URI endpoint;
	private Set<String> graphs = null;
	private Set<String> namespaces = null;
	private Set<String> types = null;
	private Set<String> properties = null;
	private Map<String, Set<String>> graphTypesMap = null;
	private Map<String, Set<String>> graphPropertiesMap = null;
	private Map<String, Set<String>> graphNamespacesMap = null;
	private Map<String, Set<String>> typePropertiesMap = null;
	private Map<String, Set<String>> typePropertiesToMap = null;
	/**
	 * Map<"entity", Map<"graph",Map<"property",Set<"value">>>>
	 */
	private Map<String, Map<String, Map<String, Set<String>>>> aboutMap = null;

	protected HttpClient httpClient = null;
	private DualHashBidiMap prefixes;
	private String storeMode;

	public LiveDataProvider(String endpoint) {
		this(endpoint, "quad");
	}

	public LiveDataProvider(String endpoint2, String storeMode) {
		try {
			this.endpoint = new URI(endpoint2);
			if (storeMode.equals(DataProvider.STORE_MODE_TRIPLE)
					|| storeMode.equals(DataProvider.STORE_MODE_QUAD)) {
				this.storeMode = storeMode;
			} else {
				throw new RuntimeException(
						"Bad configuration for store mode, value can be only triple or quad, was: "
								+ storeMode);
			}
			httpClient = new SystemDefaultHttpClient();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public URI getEndpointURL() {
		return endpoint;
	}

	protected InputStream getSparqlStream(String query) {
		return getSparqlStream(query, "application/sparql-results+xml");
	}

	protected InputStream getSparqlStream(String query, String accept) {
		URIBuilder ub;
		HttpGet req;
		ub = new URIBuilder(endpoint);
		ub.addParameter("query", query);
		try {
			req = new HttpGet(ub.build());
		} catch (URISyntaxException e1) {
			// this should never happen
			throw new RuntimeException(e1);
		}

		req.setHeader("Accept", accept);

		HttpResponse res;
		try {
			res = httpClient.execute(req);
			return res.getEntity().getContent();
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			// cl.getConnectionManager().shutdown();
		}
	}

	public Set<String> getNamedGraphs() {

		if (graphs == null) {
			graphs = new HashSet<String>();
			if (getStoreMode().equals(DataProvider.STORE_MODE_TRIPLE)) {
				graphs.add("");
			} else {
				Iterator<String> it = buildIterator(
						getSparqlStream("select distinct ?graph where {graph ?graph { ?a ?b ?c }}"),
						"<uri>(.*?)</uri>");
				while (it.hasNext()) {
					graphs.add(it.next());
				}
			}

		}

		return graphs;
	}

	protected Iterator<String> buildIterator(InputStream stream, String regex) {

		final Scanner scanner = new Scanner(stream);
		final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL);
		// FIXME WHEN WE CLOSE THE STREAM???
		return new Iterator<String>() {

			private boolean first = true;
			private boolean flushed = false;
			private String currentMatch = null;

			private void goAhead() {
				if (first) {
					first = false;
				}
				if (scanner.findWithinHorizon(pattern, 0) != null) {

					MatchResult m = scanner.match();
					currentMatch = m.group(1);
				} else {
					// nothing more
					currentMatch = null;
				}
				flushed = false;
			}

			public boolean hasNext() {
				if (flushed || first) {
					goAhead();
				}
				return currentMatch != null;
			}

			public String next() {
				if (flushed) {
					goAhead();
				}
				if (currentMatch == null) {
					throw new NoSuchElementException();
				}
				flushed = true;
				return currentMatch;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@SuppressWarnings("unchecked")
	public Set<String> getTypes(String graph) {
		if (graphTypesMap == null) {
			graphTypesMap = new HashMap<String, Set<String>>();
		}
		if (!graphTypesMap.containsKey(graph)) {
			Set<String> types = new HashSet<String>();
			// Get the list of types
			String query;
			if (getStoreMode().equals(DataProvider.STORE_MODE_TRIPLE)) {
				query = "select distinct ?type where { [] a ?type }";
			} else {
				query = "select distinct ?type where {graph <" + graph
						+ "> { [] a ?type }}";
			}
			Iterator<String> it = buildIterator(getSparqlStream(query),
					"<uri>(.*?)</uri>");
			while (it.hasNext()) {
				types.add(it.next());
			}
			graphTypesMap.put(graph, types);
		}
		return Collections.unmodifiableSet(graphTypesMap.get(graph));
	}

	@SuppressWarnings("unchecked")
	public Set<String> getProperties(String graph) {
		if (graphPropertiesMap == null) {
			graphPropertiesMap = new HashMap<String, Set<String>>();
		}

		if (!graphPropertiesMap.containsKey(graph)) {
			Set<String> properties = new HashSet<String>();
			// Get the list of properties
			String query;
			if (getStoreMode().equals(DataProvider.STORE_MODE_TRIPLE)) {
				query = "select distinct ?property where { [] ?property [] }";
			} else {
				query = "select distinct ?property where {graph <" + graph
						+ "> { [] ?property [] }}";
			}
			Iterator<String> it = buildIterator(getSparqlStream(query),
					"<uri>(.*?)</uri>");
			while (it.hasNext()) {
				properties.add(it.next());
			}

			graphPropertiesMap.put(graph, properties);

		}
		return Collections.unmodifiableSet(graphPropertiesMap.get(graph));
	}

	@SuppressWarnings("unchecked")
	public Set<String> getNamespaces(String graph) {
		if (graphNamespacesMap == null) {
			graphNamespacesMap = new HashMap<String, Set<String>>();
		}
		if (!graphNamespacesMap.containsKey(graph)) {
			Set<String> namespaces = new HashSet<String>();
			Set<String> types = getTypes(graph);
			Set<String> properties = getProperties(graph);
			for (String type : types) {
				namespaces.add(Utils.namespaceFromUri(type));
			}
			for (String property : properties) {
				namespaces.add(Utils.namespaceFromUri(property));
			}
			graphNamespacesMap.put(graph, namespaces);
		}
		return Collections.unmodifiableSet(graphNamespacesMap.get(graph));
	}

	public Set<String> getNamespaces() {
		if (namespaces == null) {
			namespaces = new HashSet<String>();
			// graphs
			for (String g : getNamedGraphs()) {
				namespaces.addAll(getNamespaces(g));
			}
		}
		return namespaces;
	}

	public Set<String> getTypes() {
		if (types == null) {
			types = new HashSet<String>();
			// graphs
			for (String g : getNamedGraphs()) {
				types.addAll(getTypes(g));
			}
		}
		return types;
	}

	public Set<String> getProperties() {
		if (properties == null) {
			properties = new HashSet<String>();
			// graphs
			for (String g : getNamedGraphs()) {
				properties.addAll(getProperties(g));
			}
		}
		return properties;
	}

	@SuppressWarnings("unchecked")
	public Set<String> getPropertiesOfType(String graph, String type) {
		if (typePropertiesMap == null) {
			typePropertiesMap = new HashMap<String, Set<String>>();
		}

		if (!typePropertiesMap.containsKey(type)) {
			Set<String> properties = new HashSet<String>();
			// Get the list of properties
			String query;
			if (getStoreMode().equals(DataProvider.STORE_MODE_TRIPLE)) {
				query = "select (\"\" as ?graph) ?property ?value where { ?x ?property [] . ?x a <"
						+ type + "> }";
			} else {
				query = "select distinct ?property where {graph <" + graph
						+ "> { ?x ?property [] . ?x a <" + type + "> }}";
			}
			Iterator<String> it = buildIterator(getSparqlStream(query),
					"<uri>(.*?)</uri>");
			while (it.hasNext()) {
				properties.add(it.next());
			}

			typePropertiesMap.put(type, properties);

		}
		return Collections.unmodifiableSet(typePropertiesMap.get(type));
	}

	@SuppressWarnings("unchecked")
	public Set<String> getPropertiesToType(String graph, String type) {
		if (typePropertiesToMap == null) {
			typePropertiesToMap = new HashMap<String, Set<String>>();
		}

		if (!typePropertiesToMap.containsKey(type)) {
			Set<String> properties = new HashSet<String>();
			// Get the list of properties
			String query;
			if (getStoreMode().equals(DataProvider.STORE_MODE_TRIPLE)) {
				query = "select (\"\" as ?graph) ?property ?value where { [] ?property ?x . ?x a <"
						+ type + "> }";
			} else {
				query = "select distinct ?property where {graph <" + graph
						+ "> { [] ?property ?x . ?x a <" + type + "> }}";
			}
			Iterator<String> it = buildIterator(getSparqlStream(query),
					"<uri>(.*?)</uri>");
			while (it.hasNext()) {
				properties.add(it.next());
			}

			typePropertiesToMap.put(type, properties);

		}
		return Collections.unmodifiableSet(typePropertiesToMap.get(type));
	}

	@SuppressWarnings("unchecked")
	public Set<String> getGraphsOfType(String type) {
		Set<String> graphs = new HashSet<String>();

		for (String g : getNamedGraphs()) {
			if (getTypes(g).contains(type)) {
				graphs.add(g);
			}
		}

		return Collections.unmodifiableSet(graphs);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Map<String, Set<String>>> about(String entity) {
		if (aboutMap == null) {
			aboutMap = new HashMap<String, Map<String, Map<String, Set<String>>>>();
		}

		if (!aboutMap.containsKey(entity)) {
			aboutMap.put(entity,
					new HashMap<String, Map<String, Set<String>>>());
			// Get the list of properties
			String query;
			if (getStoreMode().equals(DataProvider.STORE_MODE_TRIPLE)) {
				query = "select (\"\" as ?graph) ?property ?value where { <"
						+ entity + "> ?property ?value }";
			} else {
				query = "select ?graph ?property ?value where {graph ?graph { <"
						+ entity + "> ?property ?value }}";
			}
			Iterator<Entry<String, Map<String, Set<String>>>> it = buildAboutIterator(getSparqlStream(query));
			while (it.hasNext()) {
				Entry<String, Map<String, Set<String>>> entry = it.next();
				aboutMap.get(entity).put(entry.getKey(), entry.getValue());
			}
		}
		return Collections.unmodifiableMap(aboutMap.get(entity));
	}

	class SparqlSelectResult {
		List<String> cols = new ArrayList<String>();
		List<SparqlSelectResultRow> rows = new ArrayList<SparqlSelectResultRow>();
	}

	class SparqlSelectResultRow {
		List<String> cell = new ArrayList<String>();
	}

	class SparqlSelectResultSaxHandler extends DefaultHandler {
		String col = null;
		boolean cell = false;
		String datatype = null;
		String lang = null;
		String type = null;
		SparqlSelectResult res = new SparqlSelectResult();

		public SparqlSelectResult getResult() {
			return res;
		}

		@Override
		public void startElement(String arg0, String name, String arg2,
				Attributes attr) throws SAXException {
			if (name.equals("variable")) {
				res.cols.add(attr.getValue("name"));
			} else if (name.equals("result")) {
				res.rows.add(new SparqlSelectResultRow());
			} else if (name.equals("binding")) {
				col = attr.getValue("name");
			} else if (name.equals("uri")) {
				type = "uri";
				cell = true;
			} else if (name.equals("bnode")) {
				type = "bnode";
				cell = true;
			} else if (name.equals("literal")) {
				type = "literal";
				cell = true;
				datatype = attr.getValue("datatype");
			} else if (name.equals("lang")) {
				lang = attr.getValue("lang");
			}
		}

		@Override
		public void endElement(String arg0, String name, String arg2)
				throws SAXException {
			if (name.equals("result")) {
				cell = false;
				type = null;
				datatype = null;
				lang = null;
				col = null;
			}
		}

		@Override
		public void characters(char[] c, int arg1, int arg2)
				throws SAXException {
			if (cell) {
				StringBuilder vb = new StringBuilder();
				if (type.equals("uri")) {
					vb.append(c); // XXX add <> brackets here?
				} else if (type.equals("bnode")) {
					vb.append(c);
				} else if (type.equals("literal")) {
					vb.append(c);
					if (datatype != null) {
						vb.append("^^<").append(datatype).append(">");
					} else if (lang != null) {
						vb.append("@").append(lang);
					}
				}
				vb.append(c);
				res.rows.get(res.rows.size() - 1).cell.add(
						res.cols.indexOf(col), vb.toString());
			}
		}
	}

	protected Iterator<Entry<String, Map<String, Set<String>>>> buildAboutIterator(
			InputStream sparqlStream) {
		Map<String, Map<String, Set<String>>> data = new HashMap<String, Map<String, Set<String>>>();
		// This is a bit more complex, we change the approach
		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		try {
			SAXParser sax = saxFactory.newSAXParser();
			sax.parse(sparqlStream, new SparqlSelectResultSaxHandler());
			SparqlSelectResult res = new SparqlSelectResult();
			for (SparqlSelectResultRow r : res.rows) {
				String graph = r.cell.get(res.cols.indexOf("graph"));
				String property = r.cell.get(res.cols.indexOf("property"));
				String value = r.cell.get(res.cols.indexOf("value"));
				if (!data.containsKey(graph)) {
					data.put(graph, new HashMap<String, Set<String>>());
				}
				if (!data.get(graph).containsKey(property)) {
					data.get(graph).put(property, new HashSet<String>());
				}
				data.get(graph).get(property).add(value);
			}
		} catch (ParserConfigurationException e) {
			new RuntimeException(e);
		} catch (SAXException e) {
			new RuntimeException(e);
		} catch (IOException e) {
			new RuntimeException(e);
		}
		return data.entrySet().iterator();
	}

	public String getPrefix(String namespace) {
		if (prefixes == null) {
			prefixes = new DualHashBidiMap();
			// We load the prefixes downloaded from prefix.cc
			BufferedReader br = new BufferedReader(new InputStreamReader(
					getClass().getResourceAsStream("prefix.cc.txt")));
			String line;
			try {
				while ((line = br.readLine()) != null) {
					String[] pieces = line.split("\t");
					if (!pieces[0].startsWith("#")) {
						prefixes.put(pieces[0], pieces[1]);
					}
				}

				br.close();
			} catch (Exception e) {
				// FIXME Handle better
				e.printStackTrace();
			}
		}
		if (!prefixes.containsValue(namespace)) {

			String prefix = null;

			// if it is still null
			if (prefix == null) {
				// XXX something better then this?
				int c = 0;
				prefix = "ns0";
				while (prefixes.containsKey(prefix)) {
					c++;
					prefix = "ns" + Integer.toString(c);
				}
			}
			prefixes.put(prefix, namespace);
		}
		return (String) prefixes.inverseBidiMap().get(namespace);
	}

	public String getStoreMode() {
		return storeMode;
	}

	public boolean isModeTriple() {
		return storeMode.equals(DataProvider.STORE_MODE_TRIPLE);
	}

	public boolean isModeQuad() {
		return storeMode.equals(DataProvider.STORE_MODE_QUAD);
	}
}
