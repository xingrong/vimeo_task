package org.rong.task.entry;

import org.rong.task.util.TaskConstants;
import org.rong.task.vimeo.VimeoMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * main entry
 * 
 * use -c [conf_dir] -d [data_dir] to run main, or you will use default conf_dir
 * and data_dir
 * 
 * @author Rong
 * 
 */
public class Main {
	final static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {

		if (args.length == 4) {
			TaskConstants.CONF_DIR = args[1];
			TaskConstants.DATA_DIR = args[3];
			logger.info("Use customized conf_dir : " + TaskConstants.CONF_DIR
					+ " and data_dir : " + TaskConstants.DATA_DIR);
		} else {
			logger.info("Use default conf_dir : " + TaskConstants.CONF_DIR
					+ " and data_dir : " + TaskConstants.DATA_DIR);
			logger.info("If you wanna customize conf_dir, please use -c [conf_dir] -d [data_dir]");
		}

		long mainStartTime = System.currentTimeMillis();

		VimeoMain vimeoMain = VimeoMain.getInstance();
		vimeoMain.runTask();

		long mainEndTime = System.currentTimeMillis();
		logger.info("TaskMain done : " + (mainEndTime - mainStartTime) / 1000
				+ " seconds");
		System.exit(0);
	}
}
