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
		List<String> properties = new ArrayList<String>(getDataProvider()
				.getProperties());
		Collections.sort(properties);
		render("properties.vt", "properties", properties);
	}

	@Override
	public void generate(Sink aSink, SinkFactory aSinkFactory, Locale aLocale)
			throws MavenReportException {
		super.generate(aSink, aSinkFactory, aLocale);

		for (Entry<String, String> page : getPropertiesPageMap().entrySet()) {
			Sink s = aSinkFactory.createSink(getReportOutputDirectory(),
					page.getValue());

			s.rawText("<h1>" + page.getKey() + "</h1>");
			s.sectionTitle_();

			s.paragraph();
			s.text("This page illustrates the property ");
			s.bold();
			s.text(page.getKey());
			s.bold_();
			s.paragraph_();
			/* TODO
			for (String graph : getDataProvider()
					.getGraphsOfType(page.getKey())) {
				s.paragraph();
				s.text("Used in graph ");
				// >> link
				s.text(graph);
				s.text(" ");
				s.link(Utils.uriToPage(graph));
				s.text(">>");
				s.link_();
				// link <<
				s.paragraph_();

				// Properties of instances
				List<String> properties = new ArrayList<String>(
						getDataProvider().getPropertiesOfType(graph,
								page.getKey()));
				Collections.sort(properties);
				if (!properties.isEmpty()) {
					// printing
					s.section1();
					s.paragraph();
					s.text("Instances in this graph may have the following properties:");
					s.paragraph_();
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
				} else {
					s.paragraph();
					s.text("Instances in this graph don't have any property.");
					s.paragraph_();

				}
			}
			*/
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
