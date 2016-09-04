package org.rong.task.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class State {
	final static Logger logger = LoggerFactory.getLogger(State.class);

	public String configFile = "";
	public Properties props = new Properties();

	public State(String[] args) throws Exception {
		logger.info("Command line arguments: " + Arrays.asList(args));

		for (int i = 0; i < args.length; i++) {
			if ("-c".equals(args[i])) {
				if (++i == args.length) {
					throw new IllegalStateException(
							"Missing argument for config file");
				}
				configFile = args[i];
				logger.info("loading config file: " + configFile);
				try {
					if (configFile.endsWith(".properties")) {
						InputStream in = new BufferedInputStream(
								new FileInputStream(configFile));
						props.load(in);
					} else if (configFile.endsWith(".json")) {
						// TODO
					}
				} catch (IOException e) {
					logger.error("can't load config file: " + configFile);
					throw new IOException(e);
				}
			} else {
				// other args
			}
		}
	}
}
