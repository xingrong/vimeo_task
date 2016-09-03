package org.rong.task.vimeo;

import org.rong.task.util.TaskConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * vimeo task main entry
 * 
 * @author Rong
 * 
 */
public class VimeoMain {
	final static Logger logger = LoggerFactory.getLogger(VimeoMain.class);
	private static VimeoMain vimeoMain = null;

	/**
	 * singleton schema
	 * 
	 * @return
	 */
	public synchronized static VimeoMain getInstance() {
		if (null == vimeoMain) {
			vimeoMain = new VimeoMain();
		}
		return vimeoMain;
	}

	private VimeoMain() {
		new VimeoConf(TaskConstants.CONF_DIR + "/vimeo.properties");
	}

	public void process() {
		VimeoSearch vimeoSearch = new VimeoSearch();
		vimeoSearch.process();
	}
}
