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

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 
 * @author enridaga
 * 
 * @goal report-graphs
 * @phase site
 */
public class GraphsReport extends AbstractSedokuReport {

	private Map<String, String> graphPageMap = null;

	public String getOutputName() {
		return "graphs";
	}

	public String getName(Locale locale) {
		return "Graphs";
	}

	public String getDescription(Locale locale) {
		return "List of named graphs";
	}

	@Override
	protected void executeReport(Locale arg0) throws MavenReportException {
		List<String> graphs = new ArrayList<String>(getDataProvider()
				.getNamedGraphs());
		Collections.sort(graphs);
		render("graphs.vt", "graphs", graphs);
	}

	@Override
	public void generate(Sink aSink, SinkFactory aSinkFactory, Locale aLocale)
			throws MavenReportException {
		super.generate(aSink, aSinkFactory, aLocale);

		for (Entry<String, String> page : getGraphPageMap().entrySet()) {
			Sink s = aSinkFactory.createSink(getReportOutputDirectory(),
					page.getValue());
			render(s, "graph.vt", "graph", page.getKey());
		}
	}

	protected Map<String, String> getGraphPageMap() {
		if (graphPageMap == null) {
			graphPageMap = new HashMap<String, String>();
			for (String ngraph : getDataProvider().getNamedGraphs()) {
				graphPageMap.put(ngraph, Utils.uriToPage(ngraph));
			}
		}
		return graphPageMap;
	}
}
