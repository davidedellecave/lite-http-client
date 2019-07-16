package ddc.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;

/**
 * HttpLiteClient Executor
 * 
 * @author davide
 *
 */
public class HClientExecutor {
	public static final String POST_CONTENTTYPE = "application/x-www-form-urlencoded"; // "text/html;
																						// charset=ISO-8859-1";
	public static final String JSON_CONTENTTYPE = "application/json"; // "text/html;
																		// charset=ISO-8859-1";
	private CloseableHttpClient client = null;
	private Map<String, String> headers = null;

	public HClientExecutor(CloseableHttpClient client) {
		this.client = client;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	/**
	 * Execute Http POST verb
	 * 
	 * @param url:
	 *            Plain (not url encoded) url
	 * @param data
	 * @return
	 * @throws HClientException
	 */
	public HClientResponse executePostForm(String url, Map<String, String> data) throws HClientException {
		try {
			return doExecutePostForm(url, data);
		} catch (URISyntaxException | IOException e) {
			throw new HClientException(e);
		}
	}

	public HClientResponse executePostJson(URI uri, String json) throws HClientException {
		try {
			return doExecutePostJson(uri, json);
		} catch (URISyntaxException | IOException e) {
			throw new HClientException(e);
		}
	}

	public HClientResponse executePutJson(URI uri, String json) throws HClientException {
		try {
			return doExecutePutJson(uri, json);
		} catch (URISyntaxException | IOException e) {
			throw new HClientException(e);
		}
	}

	public HClientResponse executePostJson(String url, String json) throws HClientException {
		try {
			return doExecutePostJson(url, json);
		} catch (URISyntaxException | IOException e) {
			throw new HClientException(e);
		}
	}

	public HClientResponse executeGet(URI uri) throws HClientException {
		try {
			return doExecuteGet(uri);
		} catch (IOException e) {
			throw new HClientException(e);
		}
	}

	public HClientResponse executeGet(String url) throws HClientException {
		try {
			URI uri = doParseURI(url, null);
			return doExecuteGet(uri);
		} catch (IOException | URISyntaxException e) {
			throw new HClientException(e);
		}
	}

	public HClientResponse executeGet(String url, Map<String, String> params) throws HClientException {
		try {
			URI uri = doParseURI(url, params);
			return doExecuteGet(uri);
		} catch (IOException | URISyntaxException e) {
			throw new HClientException(e);
		}
	}

	public HClientResponse executeGet(String schema, String hostname, int port, String path, Map<String, String> params) throws HClientException {
		try {
			URI uri = new URIBuilder().setScheme(schema).setHost(hostname).setPort(port).setPath(path).setParameters(mapToNvp(params)).build();
			return doExecuteGet(uri);
		} catch (Exception e) {
			throw new HClientException(e);
		}
	}

	public HClientResponse executePutStream(String url, InputStream is) throws HClientException {
		try {
			return doExecutePutStream(url, is);
		} catch (URISyntaxException | IOException e) {
			throw new HClientException(e);
		}
	}

	private HClientResponse doExecutePutStream(String url, InputStream is) throws URISyntaxException, HClientException, ClientProtocolException, IOException {

		URI uri = doParseURI(url, null);
		HttpPut request = new HttpPut(uri);
		addHeaders(request);
		// RequestConfig requestConfig = RequestConfig.custom()
		// .setConnectionRequestTimeout(1000 * 60 * 10)
		// .setConnectTimeout(1000 * 60 * 10)
		// .setSocketTimeout(1000 * 60 * 10)
		// .build();
		//
		// httpRequest.setConfig(requestConfig);

		HttpEntity entity = new InputStreamEntity(is, ContentType.APPLICATION_OCTET_STREAM);
		request.setEntity(entity);
		HttpClientContext context = HttpClientContext.create();
		client.execute(request, context);
		return new HClientResponse(context);
	}

	private HClientResponse doExecutePostForm(String url, Map<String, String> data) throws HClientException, URISyntaxException, ClientProtocolException, IOException {
		URI uri = doParseURI(url, null);
		HttpPost request = new HttpPost(uri);
		addHeaders(request);
		request.setHeader("Content-Type", POST_CONTENTTYPE);
		List<NameValuePair> params = mapToNvp(data);
		request.setEntity(new UrlEncodedFormEntity(params));
		HttpClientContext context = HttpClientContext.create();
		client.execute(request, context);
		return new HClientResponse(context);
	}

	private HClientResponse doExecutePostJson(String url, String json) throws URISyntaxException, ClientProtocolException, IOException, HClientException {
		URI uri = doParseURI(url, null);
		return doExecutePostJson(uri, json);
	}

	private HClientResponse doExecutePutJson(URI uri, String json) throws URISyntaxException, ClientProtocolException, IOException {
		HttpPut request = new HttpPut(uri);
		addHeaders(request);
		request.setHeader("Content-type", JSON_CONTENTTYPE);
		request.setEntity(new StringEntity(json));
		HttpClientContext context = HttpClientContext.create();
		client.execute(request, context);
		return new HClientResponse(context);
	}

	private HClientResponse doExecutePostJson(URI uri, String json) throws URISyntaxException, ClientProtocolException, IOException {
		HttpPost request = new HttpPost(uri);
		addHeaders(request);
		request.setHeader("Content-type", JSON_CONTENTTYPE);
		request.setEntity(new StringEntity(json));
		HttpClientContext context = HttpClientContext.create();
		client.execute(request, context);
		return new HClientResponse(context);
	}


	private HClientResponse doExecuteGet(URI uri) throws ClientProtocolException, IOException {
		System.out.println(uri);
		HttpGet request = new HttpGet(uri);
		addHeaders(request);
		HttpClientContext context = HttpClientContext.create();
		client.execute(request, context);
		return new HClientResponse(context);
	}
	
	private void addHeaders(AbstractHttpMessage request) {
		if (headers != null) {
			for (Map.Entry<String, String> h : headers.entrySet()) {
				request.addHeader(h.getKey(), h.getValue());
			}
		}
	}

	/**
	 * Parse the url to get the URI
	 * 
	 * @param url:
	 *            the uri without the query string (parameter after ?)
	 * @param params:
	 *            the parameters to build the query string (parameter after ?)
	 * @throws HClientException
	 */
	public static URI parseURI(String url, Map<String, String> params) throws HClientException {
		try {
			return doParseURI(url, params);
		} catch (URISyntaxException e) {
			throw new HClientException(e);
		}
	}

	/**
	 * Parse the url to get the URI
	 * 
	 * @param url:
	 *            the url to parse. The query string might be url-encoded or
	 *            not-url-enconded
	 * @throws HClientException
	 */
	public static URI parseURI(String url) throws HClientException {
		try {
			return doParseURI(url, null);
		} catch (URISyntaxException e) {
			throw new HClientException(e);
		}
	}

	private static URI doParseURI(String url, Map<String, String> params) throws URISyntaxException, HClientException {
		if (url.indexOf('?') > 0 && params != null && params.size() > 0) {
			throw new HClientException("URI already contains parameters as query string");
		}
		if (!url.toLowerCase().startsWith("http")) {
			url = "http://" + url;
		}
		URI uri = URI.create(url);
		if (params != null) {
			URIBuilder b = new URIBuilder();
			b.setScheme(uri.getScheme());
			b.setHost(uri.getHost());
			b.setPort(uri.getPort());
			b.setPath(uri.getPath());
			b.setParameters(mapToNvp(params));
			uri = b.build();
		}
		return uri;
	}

	private static ArrayList<NameValuePair> mapToNvp(Map<String, String> data) {
		ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
		if (data == null)
			return nvp;
		for (Iterator<?> it = data.entrySet().iterator(); it.hasNext();) {
			@SuppressWarnings("unchecked")
			Map.Entry<Object, Object> el = (Map.Entry<Object, Object>) it.next();
			NameValuePair nvp1 = new BasicNameValuePair(el.getKey() + "", el.getValue() + "");
			nvp.add(nvp1);
		}
		return nvp;
	}
}
