package org.rong.task.mybatis;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.rong.task.util.TaskConstants;
import org.rong.task.util.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MybatisSessionFactory {
	final static Logger logger = LoggerFactory
			.getLogger(MybatisSessionFactory.class);
	private static HashMap<String, SqlSessionFactory> sqlSessionFactories = new HashMap<String, SqlSessionFactory>();
	static String defaultEnvironment = "";
	static boolean initialized = false;

	public static void init(State state) throws Exception {
		if (initialized) {
			return;
		}
		Properties props = state.props;
		if (props.getProperty("mybatis.confFile") != null
				&& !props.getProperty("mybatis.confFile").trim().equals("")) {

			String mybatisFile = TaskConstants.CONF_DIR + "/"
					+ props.getProperty("mybatis.confFile").trim();

			String environmentStr = "";
			if (props.getProperty("mybatis.environments") != null
					&& !props.getProperty("mybatis.environments").trim()
							.equals("")) {
				environmentStr = props.getProperty("mybatis.environments")
						.trim();
			} else {
				logger.error("invalid mybatis.confFile: " + mybatisFile);
				throw new Exception("invalid mybatis.confFile: " + mybatisFile);
			}

			String[] environments = environmentStr.split("[;,]");
			defaultEnvironment = environments[0];
			for (int i = 0; i < environments.length; i++) {
				InputStream in = new FileInputStream(mybatisFile);
				String environment = environments[i];
				sqlSessionFactories.put(environment,
						new SqlSessionFactoryBuilder().build(in, environment));
				in.close();
			}
		} else {
			logger.error("invalid mybatis.confFile: " + state.configFile);
			throw new Exception("invalid mybatis.confFile: " + state.configFile);
		}
		initialized = true;
	}

	public static SqlSession openSession() {
		return getSqlSessionFactory().openSession();

	}

	public static SqlSession openSession(String environment) {
		return getSqlSessionFactory(environment).openSession();

	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactories.get(defaultEnvironment);
	}

	public static SqlSessionFactory getSqlSessionFactory(String environment) {
		return sqlSessionFactories.get(environment);
	}
}
