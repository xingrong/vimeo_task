package org.rong.task.vimeo;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.session.SqlSession;
import org.rong.task.db.dao.movies.MovieSearchDao;
import org.rong.task.db.model.movies.MovieSearch;
import org.rong.task.file.OutputTask;
import org.rong.task.mybatis.MybatisSessionFactory;
import org.rong.task.util.DateUtil;
import org.rong.task.util.TaskConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * vimeo task main entry
 * 
 * @author Rong
 * 
 */
public class VimeoMain {
	final static Logger logger = LoggerFactory.getLogger(VimeoMain.class);
	private static VimeoMain vimeoMain = null;
	private static LinkedList<MovieSearch> movieSearchListQueue = new LinkedList<MovieSearch>();

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
		new VimeoInit(TaskConstants.CONF_DIR + "/vimeo.properties");
	}

	public void runTask() {
		getMovieSearchList();
		startOutputTask();
		searchMovies();
		stopOutputTask();
		uploadToS3();
	}

	private void getMovieSearchList() {
		SqlSession session = MybatisSessionFactory.getSqlSessionFactory(
				"vimeo_task").openSession();
		MovieSearchDao dataDao = session.getMapper(MovieSearchDao.class);
		ArrayList<MovieSearch> movieSearchList = dataDao.getAll();
		for (int i = 0; i < movieSearchList.size(); i++) {
			movieSearchListQueue.add(movieSearchList.get(i));
		}
		session.commit();
		session.close();
	}

	private void startOutputTask() {
		OutputTask outputTask = new OutputTask(TaskConstants.DATA_DIR + "/"
				+ VimeoInit.dataFile);
		new Thread(outputTask).start();
	}

	private void stopOutputTask() {
		OutputTask.loop = false;
	}

	private void searchMovies() {
		ExecutorService exec = Executors
				.newFixedThreadPool(VimeoInit.threadCount);
		VimeoSearchThread[] threadList = new VimeoSearchThread[VimeoInit.threadCount];
		for (int i = 0; i < VimeoInit.threadCount; ++i) {
			threadList[i] = new VimeoSearchThread(VimeoInit.tokenList[i]);
		}
		long threadStartTime = System.currentTimeMillis();
		for (int i = 0; i < VimeoInit.threadCount; ++i) {
			exec.execute(threadList[i]);
		}
		exec.shutdown();
		try {
			exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long threadEndTime = System.currentTimeMillis();
		logger.info("searchMovies => " + VimeoInit.threadCount
				+ " Threads all done with " + (threadEndTime - threadStartTime)
				/ 1000 + " seconds");
	}

	private void uploadToS3() {
		AmazonS3 s3client = new AmazonS3Client(new BasicAWSCredentials(
				VimeoInit.accessKeyID, VimeoInit.secretAccessKey));
		try {
			logger.info("Uploading to S3");
			File file = new File(TaskConstants.DATA_DIR + "/"
					+ VimeoInit.dataFile);
			s3client.putObject(new PutObjectRequest(VimeoInit.s3BucketName,
					VimeoInit.dataFile, file));
		} catch (AmazonServiceException ase) {
			logger.info("Caught an AmazonServiceException, which "
					+ "means your request made it "
					+ "to Amazon S3, but was rejected with an error response"
					+ " for some reason.");
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			logger.info("Caught an AmazonClientException, which "
					+ "means the client encountered "
					+ "an internal error while trying to "
					+ "communicate with S3, "
					+ "such as not being able to access the network.");
			logger.info("Error Message: " + ace.getMessage());
		}
	}

	public synchronized static MovieSearch getMovieSearch() {
		return movieSearchListQueue.poll();
	}

	public synchronized static void returnMovieSearch(MovieSearch movieSearch) {
		movieSearchListQueue.add(movieSearch);
	}

	public static void pushToDynamoDB(MovieSearch movieSearch, int hour_total) {
		Item item = null;
		try {
			String title = movieSearch.getMovie_title();
			String timestamp = DateUtil.format(DateUtil.getNowHour());
			String lastTimestamp = DateUtil.format(DateUtil.getLastHour());
			String source = "Vimeo";
			String variable = "Views";
			String search_item = movieSearch.getSearch_term();
			int last_hour_total = 0;
			Item lastHourRecord = VimeoInit.vimeoDynamoTable.getItem("title",
					title, "timestamp", lastTimestamp);
			if (lastHourRecord != null) {
				last_hour_total = lastHourRecord.getInt("hour_total");
			}
			int delta_last_hour = hour_total - last_hour_total;
			item = new Item()
					.withPrimaryKey("title", title, "timestamp", timestamp)
					.withString("source", source)
					.withString("variable", variable)
					.withString("search_item", search_item)
					.withInt("hour_total", hour_total)
					.withInt("delta_last_hour", delta_last_hour);
			VimeoInit.vimeoDynamoTable.putItem(item);
		} catch (Exception e) {
			logger.error("Unable to add item: " + item.toJSONPretty());
			logger.error(e.getMessage());
		}
	}
}
