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

			s.rawText("<h1>" + page.getKey() + "</h1>");
			s.sectionTitle_();

			s.paragraph();
			s.text("This page illustrates the content of graph ");
			s.bold();
			s.text(page.getKey());
			s.bold_();
			s.paragraph_();

			// Namespaces
			List<String> namespaces = new ArrayList<String>(getDataProvider()
					.getNamespaces(page.getKey()));
			Collections.sort(namespaces);
			// printing
			s.section1();
			s.sectionTitle1();
			s.text("Namespaces");
			s.sectionTitle1_();
			s.list();
			for (String namespace : namespaces) {
				s.listItem();
				// >> link
				s.text(namespace);
				s.text(" ");
				s.link(Utils.uriToPage(namespace));
				s.text(">>");
				s.link_();
				// link <<
				s.listItem_();
			}
			s.list_();
			s.section1_();

			// Types
			List<String> types = new ArrayList<String>(getDataProvider()
					.getTypes(page.getKey()));
			Collections.sort(types);
			// printing
			s.section1();
			s.sectionTitle1();
			s.text("Types");
			s.sectionTitle1_();

			s.list();
			for (String type : types) {
				s.listItem();
				// >> link
				s.text(type);
				s.text(" ");
				s.link(Utils.uriToPage(type));
				s.text(">>");
				s.link_();
				// link <<
				s.listItem_();
			}
			s.list_();
			s.section1_();

			// Properties
			List<String> properties = new ArrayList<String>(getDataProvider()
					.getProperties(page.getKey()));
			Collections.sort(properties);
			// printing
			s.section1();
			s.sectionTitle1();
			s.text("Properties");
			s.sectionTitle1_();

			s.list();
			for (String property : properties) {
				s.listItem();
				// >> link
				s.text(property);
				s.text(" ");
				s.link(Utils.uriToPage(property));
				s.text(">>");
				s.link_();
				// link <<
				s.listItem_();
			}
			s.list_();
			s.section1_();
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
