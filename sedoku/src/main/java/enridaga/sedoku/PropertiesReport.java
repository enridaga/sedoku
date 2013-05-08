package enridaga.sedoku;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;
import org.apache.maven.reporting.MavenReportException;

/**
 * 
 * @author enridaga
 * 
 * @goal report-properties
 * @phase site
 */
public class PropertiesReport extends AbstractSedokuReport {

	private Map<String, String> propertyPageMap = null;

	public String getOutputName() {
		return "properties";
	}

	public String getName(Locale locale) {
		return "Properties";
	}

	public String getDescription(Locale locale) {
		return "List of properties";
	}

	@Override
	protected void executeReport(Locale arg0) throws MavenReportException {
		render("properties.vt", "properties", getDataProvider().getProperties());
	}

	@Override
	public void generate(Sink aSink, SinkFactory aSinkFactory, Locale aLocale)
			throws MavenReportException {
		super.generate(aSink, aSinkFactory, aLocale);

		for (Entry<String, String> page : getPropertiesPageMap().entrySet()) {
			Sink s = aSinkFactory.createSink(getReportOutputDirectory(),
					page.getValue());
			render(s, "property.vt", "property", page.getKey());
		}
	}

	protected Map<String, String> getPropertiesPageMap() {
		if (propertyPageMap == null) {
			propertyPageMap = new HashMap<String, String>();
			for (String nproperty : getDataProvider().getProperties()) {
				propertyPageMap.put(nproperty, Utils.uriToPage(nproperty));
			}
		}
		return propertyPageMap;
	}
}
