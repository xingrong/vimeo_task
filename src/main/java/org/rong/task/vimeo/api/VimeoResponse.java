package org.rong.task.vimeo.api;

import org.json.JSONObject;

/**
 * Vimeo API response
 * 
 * @author Rong
 * 
 */
public class VimeoResponse {
	private JSONObject json;
	private JSONObject headers;
	private int statusCode;

	public VimeoResponse(JSONObject json, JSONObject headers, int statusCode) {
		this.json = json;
		this.headers = headers;
		this.statusCode = statusCode;
	}

	public JSONObject getJson() {
		return json;
	}

	public JSONObject getHeaders() {
		return headers;
	}

	public int getRateLimit() {
		return getHeaders().getInt("X-RateLimit-Limit");
	}

	public int getRateLimitRemaining() {
		return getHeaders().getInt("X-RateLimit-Remaining");
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String toString() {
		return new StringBuffer("HTTP Status Code: \n").append(getStatusCode())
				.append("\nJson: \n").append(getJson().toString(2))
				.append("\nHeaders: \n").append(getHeaders().toString(2))
				.toString();
	}
}
