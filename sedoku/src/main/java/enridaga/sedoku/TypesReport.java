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
 * @goal report-types
 * @phase site
 */
public class TypesReport extends AbstractSedokuReport {

	private Map<String, String> typePageMap = null;

	public String getOutputName() {
		return "types";
	}

	public String getName(Locale locale) {
		return "Types";
	}

	public String getDescription(Locale locale) {
		return "List of types";
	}

	@Override
	protected void executeReport(Locale arg0) throws MavenReportException {
		render("types.vt", "types", getDataProvider().getTypes());
	}

	@Override
	public void generate(Sink aSink, SinkFactory aSinkFactory, Locale aLocale)
			throws MavenReportException {
		super.generate(aSink, aSinkFactory, aLocale);

		for (Entry<String, String> page : getTypePageMap().entrySet()) {
			Sink s = aSinkFactory.createSink(getReportOutputDirectory(),
					page.getValue());
			render(s, "type.vt", "type", page.getKey());
		}
	}

	protected Map<String, String> getTypePageMap() {
		if (typePageMap == null) {
			typePageMap = new HashMap<String, String>();
			for (String ntype : getDataProvider().getTypes()) {
				typePageMap.put(ntype, Utils.uriToPage(ntype));
			}
		}
		return typePageMap;
	}
}
