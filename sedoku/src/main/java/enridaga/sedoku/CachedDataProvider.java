package enridaga.sedoku;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author enridaga
 * 
 */
public class CachedDataProvider extends LiveDataProvider {

	private String cachedir;

	public CachedDataProvider(String endpoint, String storeMode, String cachedir) {
		super(endpoint, storeMode);
		this.cachedir = cachedir;

		File f = new File(this.cachedir);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	@Override
	protected InputStream getSparqlStream(String query, String accept) {
		String key = query + accept;
		if (!exists(key)) {
			put(key, super.getSparqlStream(query, accept));
		}
		return get(key);
	}

	private String key(String arg) {
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		m.reset();
		m.update(arg.getBytes());
		byte[] digest = m.digest();
		BigInteger bigInt = new BigInteger(1, digest);
		return bigInt.toString(16);
	}

	public File file(String arg) {
		String key = key(arg);
		return new File(cachedir + System.getProperty("file.separator") + key);
	}

	public boolean exists(String url) {
		return file(url).exists();
	}

	public void put(String url, InputStream stream) {

		try {
			OutputStream out = new FileOutputStream(file(url));
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = stream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			stream.close();
			out.flush();
			out.close();
			stream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public InputStream get(String url) {
		try {
			return new FileInputStream(file(url));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
