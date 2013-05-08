package enridaga.sedoku;

import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author enridaga
 * 
 */
public interface DataProvider {

	public final String STORE_MODE_TRIPLE = "triple";
	public final String STORE_MODE_QUAD = "quad";
	
	public Set<String> getNamedGraphs();

	public URI getEndpointURL();

	public Set<String> getTypes(String graph);

	public Set<String> getProperties(String graph);

	public Set<String> getNamespaces(String graph);

	public Set<String> getTypes();

	public Set<String> getTypesInNamespace(String namespace);

	public Set<String> getPropertiesInNamespace(String namespace);

	public Set<String> getProperties();

	public Set<String> getNamespaces();

	public Set<String> getPropertiesOfType(String graph, String type);

	public Set<String> getGraphsOfType(String type);

	public Set<String> getGraphsOfProperty(String property);

	public Set<String> getPropertiesToType(String graph,
			String type);
	
	public Map<String, Map<String, Set<String>>> about(String entity);
	
	public String getPrefix(String namespace);
	
	public String getStoreMode();

	public boolean isModeTriple();
	
	public boolean isModeQuad();

	public Set<String> getSubjectTypes(String graph, String property);

	public Set<String> getObjectTypes(String graph, String property);
}
