package enridaga.sedoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;
import org.apache.maven.reporting.MavenReportException;

import java.util.Collections;

/**
 * 
 * @author enridaga
 * 
 * @goal report-namespaces
 * @phase site
 */
public class NamespacesReport extends AbstractSedokuReport {

	private Map<String, String> namespacePageMap = null;

	public String getDescription(Locale locale) {
		return "List of namespaces";
	}

	public String getOutputName() {
		return "Namespaces";
	}

	@Override
	protected void executeReport(Locale locale) throws MavenReportException {
		List<String> namespaces = new ArrayList<String>(getDataProvider().getNamespaces());
		Collections.sort(namespaces);
		render("namespaces.vt", "namespaces", namespaces);
	}

	@Override
	public void generate(Sink aSink, SinkFactory aSinkFactory, Locale aLocale)
			throws MavenReportException {
		super.generate(aSink, aSinkFactory, aLocale);

		for (Entry<String, String> page : getNamespacePageMap().entrySet()) {
			Sink s = aSinkFactory.createSink(getReportOutputDirectory(),
					page.getValue());
			render(s, "namespace.vt", "namespace", page.getKey());	
		}
	}

	public String getName(Locale locale) {
		return "Namespaces";
	}

	protected Map<String, String> getNamespacePageMap() {
		if (namespacePageMap == null) {
			namespacePageMap = new HashMap<String, String>();
			for (String ngraph : getDataProvider().getNamespaces()) {
				namespacePageMap.put(ngraph, Utils.uriToPage(ngraph));
			}
		}
		return namespacePageMap;
	}

}
