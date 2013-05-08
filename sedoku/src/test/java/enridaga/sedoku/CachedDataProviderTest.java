package enridaga.sedoku;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import enridaga.sedoku.CachedDataProvider;

public class CachedDataProviderTest {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Test
	public void getNamedGraphs() throws IOException, URISyntaxException {
		log.info("#getNamedGraphs()");
		URL dir = getClass().getClassLoader().getResource(".");
		File cacheDir = new File(dir.toURI());
		cacheDir = new File(cacheDir.getAbsoluteFile().getAbsolutePath()
				+ System.getProperty("file.separator")
				+ "cached-data-provider-test");
		CachedDataProvider dp = new CachedDataProvider(
				"http://dbpedia.org/sparql", DataProvider.STORE_MODE_QUAD, cacheDir.toString());
		Set<String> ng = dp.getNamedGraphs();
		for (String s : ng) {
			log.info(" - {} - ", s);
		}
	}
}
