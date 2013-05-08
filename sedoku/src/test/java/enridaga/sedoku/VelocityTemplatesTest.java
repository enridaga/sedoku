package enridaga.sedoku;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocityTemplatesTest {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Test
	public void getNamedGraphs() throws IOException, URISyntaxException {
	TypesReport tr = new TypesReport();
	log.info("{}",tr.getVelocityProperties());
	}
}
