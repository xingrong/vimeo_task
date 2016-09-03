package org.rong.task.vimeo;

import java.util.Properties;
import org.rong.task.mybatis.MybatisSessionFactory;
import org.rong.task.util.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VimeoConf {
	final static Logger logger = LoggerFactory.getLogger(VimeoConf.class);
	
	public static String[] tokenList;
	public static int threadCount;
	public static int page;
	public static int per_page;

	public VimeoConf(String confFile) {
		String[] args = new String[2];
		args[0] = "-c";
		args[1] = confFile;
		State state = null;
		Properties props;
		String property = "";
		try {
			state = new State(args);
			props = state.props;
			MybatisSessionFactory.init(state);

			property = "tokenList";
			if (props.getProperty(property) != null
					&& !props.getProperty(property).trim().equals("")) {
				VimeoConf.tokenList = props.getProperty(property).split(",");
				VimeoConf.threadCount = tokenList.length;
				logger.info("token list : " + props.getProperty(property));
				logger.info("thread count : " + VimeoConf.threadCount);
			}else {
				logger.error("missing token config");
				System.exit(-1);
			}
			
			property = "page";
			if (props.getProperty(property) != null
					&& !props.getProperty(property).trim().equals("")) {
				VimeoConf.page = Integer.parseInt(props.getProperty(property));
				logger.info("page : " + VimeoConf.page);
			}else {
				logger.error("missing page config");
				System.exit(-1);
			}
			
			property = "per_page";
			if (props.getProperty(property) != null
					&& !props.getProperty(property).trim().equals("")) {
				VimeoConf.per_page = Integer.parseInt(props.getProperty(property));
				logger.info("per_page : " + VimeoConf.per_page);
			}else {
				logger.error("missing per_page config");
				System.exit(-1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
