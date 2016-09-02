package org.rong.task.entry;

import java.util.ArrayList;

import org.apache.ibatis.session.SqlSession;
import org.rong.task.db.dao.default_task.DataDao;
import org.rong.task.db.model.default_task.Data;
import org.rong.task.mybatis.MybatisSessionFactory;
import org.rong.task.util.State;
import org.rong.task.util.TaskConstants;
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
			logger.error("Arguments error, missing argument.");
			return;
		}
		TaskConstants.CONF_DIR = args[1];
		long mainStartTime = System.currentTimeMillis();
		// db test
		State state = null;
		args[1] = TaskConstants.CONF_DIR + "/task.properties";
		try {
			state = new State(args);
			MybatisSessionFactory.init(state);
			SqlSession session_app_data = MybatisSessionFactory
					.getSqlSessionFactory("default_task").openSession();
			DataDao dataDao = session_app_data.getMapper(DataDao.class);
			ArrayList<Data> dataList = dataDao.getAllName();
			for (int i = 0; i < dataList.size(); ++i) {
				logger.info("data name => " + dataList.get(i).getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long mainEndTime = System.currentTimeMillis();
		logger.info("TaskMain done : " + (mainEndTime - mainStartTime) / 1000
				+ " seconds");
	}
}
