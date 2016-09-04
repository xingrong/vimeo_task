package org.rong.task.vimeo;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.rong.task.db.model.movies.MovieSearch;
import org.rong.task.file.WriterQueue;
import org.rong.task.util.TaskConstants;
import org.rong.task.vimeo.api.VimeoClient;
import org.rong.task.vimeo.api.VimeoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Multithreaded approach to search movie using vimeo API
 * 
 * @author Rong
 * 
 */
public class VimeoSearchThread implements Runnable {
	final static Logger logger = LoggerFactory
			.getLogger(VimeoSearchThread.class);

	private VimeoClient vimeoClient;
	private int rateLimitRemaining;
	private MovieSearch movieSearch;

	public VimeoSearchThread(String token) {
		super();
		this.vimeoClient = new VimeoClient(token);
		this.rateLimitRemaining = TaskConstants.DEFAULT_RATELIMIT;
	}

	public void run() {
		for (movieSearch = VimeoMain.getMovieSearch(); movieSearch != null; movieSearch = VimeoMain
				.getMovieSearch()) {
			if (rateLimitRemaining <= VimeoInit.page) {
				VimeoMain.returnMovieSearch(movieSearch);
				sleep(TaskConstants.RETRY_WAIT_TIME);
				rateLimitRemaining = TaskConstants.DEFAULT_RATELIMIT;
			} else {
				search(movieSearch.getSearch_term());
			}
		}
	}

	private void search(String query) {
		int hour_total = 0;
		for (int page = 1; page <= VimeoInit.page; page++) {
			VimeoResponse vimeoResponse = null;
			try {
				vimeoResponse = vimeoClient.searchVideos(
						URLEncoder.encode(query, "utf-8"), page,
						VimeoInit.per_page);
				if (vimeoResponse.getStatusCode() == 429) {
					VimeoMain.returnMovieSearch(movieSearch);
					movieSearch = null;
					sleep(TaskConstants.RETRY_WAIT_TIME);
					rateLimitRemaining = TaskConstants.DEFAULT_RATELIMIT;
					break;
				} else if (vimeoResponse.getStatusCode() == 400) {
					// no content in current page
					break;
				}
				rateLimitRemaining = vimeoResponse.getRateLimitRemaining();
				JSONArray statsArray = vimeoResponse.getJson().getJSONArray(
						"data");
				for (int i = 0; i < statsArray.length(); i++) {
					String playsString = statsArray.getJSONObject(i)
							.getJSONObject("stats").get("plays").toString();
					int playsCount = 0;
					if (!playsString.equals("null")) {
						playsCount = Integer.parseInt(playsString);
					}
					hour_total += playsCount;
				}
				WriterQueue.getQueue().put(vimeoResponse.toString());
				sleep(100);
			} catch (Exception e) {
				logger.error(e.getMessage());
				continue;
			}
		}
		if (movieSearch != null) {
			logger.info("[movieSearch=>" + movieSearch.getMovie_title()
					+ ", hour_total=>" + hour_total + ", rate remaining=>"
					+ rateLimitRemaining + "]");
			VimeoMain.pushToDynamoDB(movieSearch, hour_total);
		}
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
