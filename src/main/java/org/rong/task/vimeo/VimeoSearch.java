package org.rong.task.vimeo;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.ibatis.session.SqlSession;
import org.rong.task.db.dao.movies.MovieSearchDao;
import org.rong.task.db.model.movies.MovieSearch;
import org.rong.task.mybatis.MybatisSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VimeoSearch {
	final static Logger logger = LoggerFactory.getLogger(VimeoSearch.class);
	public static ArrayList<MovieSearch> movieSearchList;
	static int index = 0;

	public synchronized static String getNextQuery() {
		String query = null;
		if (index < movieSearchList.size()) {
			query = movieSearchList.get(index++).getSearch_term();
		}
		logger.info("index : " + index);
		return query;
	}

	public void process() {
		getMovieSearchList();
		searchMovie();
	}

	private void getMovieSearchList() {
		SqlSession session = MybatisSessionFactory.getSqlSessionFactory(
				"vimeo_task").openSession();
		MovieSearchDao dataDao = session.getMapper(MovieSearchDao.class);
		movieSearchList = dataDao.getAll();
		session.commit();
		session.close();
	}

	private void searchMovie() {
		ExecutorService exec = Executors
				.newFixedThreadPool(VimeoConf.threadCount);
		VimeoSearchThread[] threadList = new VimeoSearchThread[VimeoConf.threadCount];
		for (int i = 0; i < VimeoConf.threadCount; ++i) {
			threadList[i] = new VimeoSearchThread(VimeoConf.tokenList[i]);
		}
		long threadStartTime = System.currentTimeMillis();
		for (int i = 0; i < VimeoConf.threadCount; ++i) {
			exec.execute(threadList[i]);
		}
		exec.shutdown();
		try {
			exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long threadEndTime = System.currentTimeMillis();
		logger.info("VimeoSearch => " + VimeoConf.threadCount
				+ " Threads all done with " + (threadEndTime - threadStartTime)
				/ 1000 + " seconds");
	}
}
