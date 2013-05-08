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

			String namespace = page.getKey();

			s.rawText("<h1>" + namespace + "</h1>");
			s.sectionTitle_();

			s.paragraph();
			s.text("This page illustrates the usage of namespace ");
			s.bold();
			s.text(namespace);
			s.bold_();
			s.paragraph_();

			// Types
			List<String> types = new ArrayList<String>(getDataProvider()
					.getTypes());
			Collections.sort(types);
			// printing
			s.section1();
			s.sectionTitle1();
			s.text("Types");
			s.sectionTitle1_();

			s.list();
			for (String type : types) {
				if (!Utils.namespaceFromUri(type).equals(namespace)) {
					continue;
				}
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
					.getProperties());
			Collections.sort(properties);
			// printing
			s.section1();
			s.sectionTitle1();
			s.text("Properties");
			s.sectionTitle1_();

			s.list();
			for (String property : properties) {
				if (!Utils.namespaceFromUri(property).equals(namespace)) {
					continue;
				}
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
