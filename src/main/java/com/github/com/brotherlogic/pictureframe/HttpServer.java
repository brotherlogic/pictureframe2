package com.github.brotherlogic.pictureframe;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {

	private Config conf;
	private Frame parent;

	public HttpServer(Config c, Frame f) throws IOException {
		super(8085);
		start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
		conf = c;
		parent = f;
	}

	protected Properties getProperties(InputStream stream) {
		if (stream == null) {
			return new Properties();
		}
		try {
			Properties props = new Properties();
			props.load(stream);
			return props;
		} catch (IOException e) {
			return new Properties();
		}
	}

	public String getVersion() {
		Properties props = getProperties(HttpServer.class.getResourceAsStream("properties.txt"));
		return props.getProperty("version", "UNKNOWN_VERSION");
	}

	@Override
	public Response serve(IHTTPSession session) {
		String msg = "<html><body><h1>Version: " + getVersion() + "</h1>\n";
		Map<String, String> parms = session.getParms();
		if (parms.get("pictures") != null) {
			conf.setNumberOfPhotos(Integer.parseInt(parms.get("pictures")));
			parent.saveConfig();
		}

		msg += "<form action='?' method='get'>\n  <p>Number of Photos (currently " + conf.getConfig().getRecentImages()
				+ "): <input type='text' name='pictures'></p>\n" + "</form>\n";
		return newFixedLengthResponse(msg + "</body></html>\n");
	}
}
