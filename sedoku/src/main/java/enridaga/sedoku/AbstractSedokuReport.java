package enridaga.sedoku;

import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public abstract class AbstractSedokuReport extends AbstractMavenReport {

	/**
	 * Mode can be quad or triple
	 * @parameter property = "storeMode" default-value="quad"
	 * @required
	 */
	protected String storeMode;

	/**
	 * @parameter property = "endpoint"
	 * @required
	 */
	protected String endpoint;

	/**
	 * Directory where reports will go.
	 * 
	 * @parameter expression="${project.reporting.outputDirectory}"
	 * @required
	 * @readonly
	 */
	protected String outputDirectory;

	/**
	 * Directory where reports will go.
	 * 
	 * @parameter property = "dataCacheDirectory"
	 *            default-value="${project.reporting.outputDirectory}/sedoku-cache"
	 * @readonly
	 */
	protected String dataCacheDirectory;

	/**
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	protected Renderer siteRenderer;

	@Override
	protected String getOutputDirectory() {
		return outputDirectory;
	}

	@Override
	protected MavenProject getProject() {
		return project;
	}

	@Override
	protected Renderer getSiteRenderer() {
		return siteRenderer;
	}

	protected DataProvider getDataProvider() {
		if (!(project.getContextValue("sedoku.data.provider") instanceof DataProvider)) {
			getLog().info("#init data provider");
			project.setContextValue("sedoku.data.provider",
					new CachedDataProvider(endpoint, storeMode, dataCacheDirectory));
		}
		return (DataProvider) project.getContextValue("sedoku.data.provider");
	}

	public URL getVelocityProperties() {
		return getClass().getResource("../../velocity.properties");
	}

	protected VelocityEngine getTemplateEngine() {
		if (!(project.getContextValue("sedoku.template.engine") instanceof VelocityEngine)) {
			getLog().info("#init velocity template engine");
			VelocityEngine ve;
			try {
				Properties pr = new Properties();
				pr.load(getClass().getResourceAsStream("velocity.properties"));
				ve = new VelocityEngine(pr);
				ve.init();
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			project.setContextValue("sedoku.template.engine", ve);
		}
		return (VelocityEngine) project
				.getContextValue("sedoku.template.engine");
	}

	protected VelocityContext getDefaultContext() {

		if (!(project.getContextValue("sedoku.velocity.context") instanceof VelocityContext)) {
			getLog().info("#init velocity context");
			VelocityContext velocityContext;
			velocityContext = new VelocityContext();
			velocityContext.put("endpoint", endpoint);
			velocityContext.put("data", getDataProvider());
			velocityContext.put("Utils", Utils.class);
			// velocityContext.put("endpoint", endpoint);
			project.setContextValue("sedoku.velocity.context", velocityContext);
		}
		return (VelocityContext) project
				.getContextValue("sedoku.velocity.context");
	}

	protected void render(String template, Object... bindings) {
		getLog().info("#render " + template);
		VelocityContext context = new VelocityContext(getDefaultContext());
		for (int i = 0; i < bindings.length; i = i + 2) {
			String key = (String) bindings[0];
			Object obj = bindings[1];
			context.put(key, obj);
		}
		try {
			StringWriter sw = new StringWriter();
			getTemplateEngine().getTemplate(
					"enridaga/sedoku/velocity/" + template).merge(context, sw);
			getSink().rawText(sw.toString());
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}
	}
}
