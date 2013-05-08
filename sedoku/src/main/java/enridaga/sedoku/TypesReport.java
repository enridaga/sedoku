package enridaga.sedoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;
import org.apache.maven.reporting.MavenReportException;

import edu.emory.mathcs.backport.java.util.Collections;

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
		
		List<String> types = new ArrayList<String>(getDataProvider()
				.getTypes());
		Collections.sort(types);
		render("types.vt", "types", types);
	}

	@Override
	public void generate(Sink aSink, SinkFactory aSinkFactory, Locale aLocale)
			throws MavenReportException {
		super.generate(aSink, aSinkFactory, aLocale);

		for (Entry<String, String> page : getTypePageMap().entrySet()) {
			Sink s = aSinkFactory.createSink(getReportOutputDirectory(),
					page.getValue());

			s.rawText("<h1>" + page.getKey() + "</h1>");
			s.sectionTitle_();

			s.paragraph();
			s.text("This page illustrates the type ");
			s.bold();
			s.text(page.getKey());
			s.bold_();
			s.paragraph_();

			Map<String, Map<String, Set<String>>> about = getDataProvider()
					.about(page.getKey());
			// about
			for (Entry<String, Map<String, Set<String>>> ingraph : about
					.entrySet()) {

				s.table();
				s.tableCaption();
				s.text(ingraph.getKey());
				s.tableCaption_();
				for (Entry<String, Set<String>> property : ingraph.getValue()
						.entrySet()) {
					s.tableRow();
					s.tableCell();
					s.text(property.getKey());
					s.tableCell_();
					s.tableCell();
					s.list();
					for (String value : property.getValue()) {
						s.listItem();
						s.text(value);
						s.listItem_();
					}
					s.list_();
					s.tableCell_();
					s.tableRow_();
				}
				s.table_();
			}
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

				//
				// Properties of instances
				List<String> propertiesInv = new ArrayList<String>(
						getDataProvider().getPropertiesToType(graph,
								page.getKey()));
				Collections.sort(propertiesInv);
				if (!propertiesInv.isEmpty()) {
					// printing
					s.section1();
					s.paragraph();
					s.text("Instances in this graph may be values of the following properties:");
					s.paragraph_();
					s.list();
					for (String property : propertiesInv) {
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
					s.text("Instances in this graph are not values of any property.");
					s.paragraph_();

				}
			}
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
