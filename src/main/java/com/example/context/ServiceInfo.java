/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.context;

import org.springframework.http.HttpHeaders;

public class ServiceInfo {

	private final long traceId;

	private final String user;

	private final HttpHeaders headers;


	public ServiceInfo(long traceId, String user, HttpHeaders headers) {
		this.traceId = traceId;
		this.user = user;
		this.headers = headers;
	}


	public long getTraceId() {
		return traceId;
	}

	public String getUser() {
		return user;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

}
