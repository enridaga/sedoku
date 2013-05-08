package enridaga.sedoku;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveDataProviderTest {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Test
	public void getNamedGraphs() throws IOException, URISyntaxException {
		log.info("#getNamedGraphs()");
		Set<String> ng = new LiveDataProvider("http://dbpedia.org/sparql")
				.getNamedGraphs();
		for (String s : ng) {
			log.info(" - {} - ", s);
		}
	}

	@Test
	public void getPrefix() {
		log.info("#getPrefix()");
		DataProvider dp = new LiveDataProvider("http://dbpedia.org/sparql");
		Assert.assertEquals("rdf",
				dp.getPrefix("http://www.w3.org/1999/02/22-rdf-syntax-ns#"));
		Assert.assertEquals("freebase", dp.getPrefix("http://rdf.freebase.com/ns/"));
		Assert.assertEquals("ns0",
				dp.getPrefix("http://unexististent/namespace/"));
		Assert.assertEquals("ns1",
				dp.getPrefix("http://unexististent/namespace2/"));
		Assert.assertEquals("ns2",
				dp.getPrefix("http://unexististent/namespace3/"));
	}
}
