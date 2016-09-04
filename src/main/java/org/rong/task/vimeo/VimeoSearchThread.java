package org.rong.task.vimeo;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.rong.task.db.model.movies.MovieSearch;
import org.rong.task.file.WriterQueue;
import org.rong.task.vimeo.api.VimeoClient;
import org.rong.task.vimeo.api.VimeoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VimeoSearchThread implements Runnable {
	final static Logger logger = LoggerFactory
			.getLogger(VimeoSearchThread.class);

	private VimeoClient vimeoClient;
	private int rateLimitRemaining;
	private MovieSearch movieSearch;

	public VimeoSearchThread(String token) {
		super();
		this.vimeoClient = new VimeoClient(token);
		this.rateLimitRemaining = 100;
	}

	public void run() {
		for (this.movieSearch = VimeoMain.getNextSearch(); this.movieSearch != null; this.movieSearch = VimeoMain
				.getNextSearch()) {
			if (rateLimitRemaining <= VimeoInit.page) {
				try {
					Thread.sleep(15 * 60 * 1000);
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			}
			searchVideos(this.movieSearch.getSearch_term());
		}
	}

	private void searchVideos(String query) {
		int hour_total = 0;
		for (int page = 1; page <= VimeoInit.page; page++) {
			VimeoResponse vimeoResponse = null;
			try {
				vimeoResponse = vimeoClient.searchVideos(
						URLEncoder.encode(query, "utf-8"), page,
						VimeoInit.per_page);
				if (vimeoResponse.getStatusCode() == 429) {
					logger.error("vimeo api response status code error : "
							+ vimeoResponse.getHeaders().toString());
				} else if (vimeoResponse.getStatusCode() == 400) {
					break;
				}
				this.rateLimitRemaining = vimeoResponse.getRateLimitRemaining();
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
				Thread.sleep(100);
			} catch (Exception e) {
				logger.error(e.getMessage());
				continue;
			}
		}
		logger.info("rate remaining => " + this.rateLimitRemaining);
		VimeoMain.pushToDynamoDB(this.movieSearch, hour_total);
	}
}
