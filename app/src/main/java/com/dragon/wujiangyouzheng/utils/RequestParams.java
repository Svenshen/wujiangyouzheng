package com.dragon.wujiangyouzheng.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ConcurrentHashMap;

public class RequestParams {
	protected ConcurrentHashMap<String, String> urlParams;

	public RequestParams() {
		urlParams = new ConcurrentHashMap<String, String>();
	}

	public ConcurrentHashMap<String, String> getParams() {
		return urlParams;
	}

	public void put(String key, String value) {
		if (key != null && value != null) {
			urlParams.put(key, value);
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (ConcurrentHashMap.Entry<String, String> entry : urlParams
				.entrySet()) {
			if (result.length() > 0)
				result.append("&");

			result.append(encode(entry.getKey()));
			result.append("=");
			result.append(encode(entry.getValue()));
		}
		return result.toString();
	}

	private String encode(String value) {
		String encode = null;
		try {
			encode = URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encode;
	}
}
