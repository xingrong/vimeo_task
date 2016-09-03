package org.rong.task.entry;

import org.rong.task.util.TaskConstants;
import org.rong.task.vimeo.VimeoMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * main entry
 * 
 * @author Rong
 * 
 */
public class Main {
	final static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		if (args.length != 4) {
			logger.error("Arguments Error!");
			return;
		}
		TaskConstants.CONF_DIR = args[1];
		long mainStartTime = System.currentTimeMillis();
		VimeoMain vimeoMain = VimeoMain.getInstance();
		vimeoMain.process();
		long mainEndTime = System.currentTimeMillis();
		logger.info("TaskMain done : " + (mainEndTime - mainStartTime) / 1000
				+ " seconds");
	}
}
