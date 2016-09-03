package org.rong.task.vimeo.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class VimeoClient {
	private static final String VIMEO_SERVER = "https://api.vimeo.com";
	private String token;
	private String tokenType;

	public VimeoClient(String token) {
		this(token, "bearer");
	}

	public VimeoClient(String token, String tokenType) {
		this.token = token;
		this.tokenType = tokenType;
	}

	public VimeoResponse searchVideos(String query, int pageNumber,
			int itemsPerPage) throws IOException {
		String apiRequestEndpoint = "/videos?fields=stats.plays&page="
				+ pageNumber + "&per_page=" + itemsPerPage + "&query=" + query;
		return apiRequest(apiRequestEndpoint, HttpGet.METHOD_NAME, null, null);
	}

	protected VimeoResponse apiRequest(String endpoint, String methodName,
			Map<String, String> params, File file) throws IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpRequestBase request = null;
		String url = null;
		if (endpoint.startsWith("http")) {
			url = endpoint;
		} else {
			url = new StringBuffer(VIMEO_SERVER).append(endpoint).toString();
		}
		if (methodName.equals(HttpGet.METHOD_NAME)) {
			request = new HttpGet(url);
		} else if (methodName.equals(HttpPost.METHOD_NAME)) {
			request = new HttpPost(url);
		} else if (methodName.equals(HttpPut.METHOD_NAME)) {
			request = new HttpPut(url);
		} else if (methodName.equals(HttpDelete.METHOD_NAME)) {
			request = new HttpDelete(url);
		} else if (methodName.equals(HttpPatch.METHOD_NAME)) {
			request = new HttpPatch(url);
		}
		request.addHeader("Accept", "application/vnd.vimeo.*+json; version=3.2");
		request.addHeader("Authorization",
				new StringBuffer(tokenType).append(" ").append(token)
						.toString());
		HttpEntity entity = null;
		if (params != null) {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			for (String key : params.keySet()) {
				postParameters
						.add(new BasicNameValuePair(key, params.get(key)));
			}
			entity = new UrlEncodedFormEntity(postParameters);
		} else if (file != null) {
			entity = new FileEntity(file, ContentType.MULTIPART_FORM_DATA);
		}
		if (entity != null) {
			if (request instanceof HttpPost) {
				((HttpPost) request).setEntity(entity);
			} else if (request instanceof HttpPatch) {
				((HttpPatch) request).setEntity(entity);
			} else if (request instanceof HttpPut) {
				((HttpPut) request).setEntity(entity);
			}
		}
		CloseableHttpResponse response = client.execute(request);
		String responseAsString = null;
		int statusCode = response.getStatusLine().getStatusCode();
		if (methodName.equals(HttpPut.METHOD_NAME)
				|| methodName.equals(HttpDelete.METHOD_NAME)) {
			JSONObject out = new JSONObject();
			for (Header header : response.getAllHeaders()) {
				out.put(header.getName(), header.getValue());
			}
			responseAsString = out.toString();
		} else if (statusCode != 204) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			response.getEntity().writeTo(out);
			responseAsString = out.toString("UTF-8");
			out.close();
		}
		JSONObject json = null;
		JSONObject headers = null;
		try {
			json = new JSONObject(responseAsString);
			headers = new JSONObject();
			for (Header header : response.getAllHeaders()) {
				headers.put(header.getName(), header.getValue());
			}
		} catch (Exception e) {
			json = new JSONObject();
			headers = new JSONObject();
		}
		VimeoResponse vimeoResponse = new VimeoResponse(json, headers,
				statusCode);
		response.close();
		client.close();
		return vimeoResponse;
	}
}
