package enridaga.sedoku;

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

	public static String namespaceFromUri(String uri) {
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
		return uri.substring(namespaceFromUri(uri).length());
	}

	public static String prefixed(String prefix, String uri) {
		return prefix + ":" + localName(uri);
	}
}
