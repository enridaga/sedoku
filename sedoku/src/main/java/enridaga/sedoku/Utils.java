package enridaga.sedoku;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 
 * @author enridaga
 * 
 */
public class Utils {

	public static String uriToPage(String uri) {
		if (uri.equals("")) {
			return "default.html";
		}
		// replace non alpha chars with _
		return uri.replaceAll("[^0-9a-zA-Z]", "_") + ".html";
	}

	public static String namespace(String uri) {
		int index = uri.lastIndexOf('#');
		if (index == -1) {
			index = uri.lastIndexOf('/');
		}

		if (index == -1 || index == uri.length() - 1) {
			return uri; // no local name
		} else {
			return uri.substring(0, index + 1);
		}
	}

	public static String localName(String uri) {
		return uri.substring(namespace(uri).length());
	}

	public static String prefixed(String prefix, String uri) {
		return prefix + ":" + localName(uri);
	}

	private static Utils instance = null;

	/**
	 * This is needed to be used in velocity&lt;1.6 templates
	 * 
	 * @return
	 */
	public static final Utils instance() {
		if (instance == null) {
			instance = new Utils();
		}
		return instance;
	}

	public static final List<?> sorted(Collection<?> collection) {
		if(collection instanceof List){
			Collections.sort((List<?>)collection);
			return (List<?>) collection;
		}else{
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<?> list = new ArrayList(collection);
			Collections.sort(list);
			return list;
		}
	}
}
