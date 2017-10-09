package ddc.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.util.EntityUtils;

public class HClientResponse {
	private String url="";
	private int statusCode=0;
	private String statusPhrase="";
	private byte[] data=null;
	private long dataLength=0;
	private Map<String, String> header;
	
	public HClientResponse(HttpClientContext context) {
		super();
		this.url = context.getRequest().getRequestLine().getUri().toString();
		this.statusPhrase=context.getResponse().getStatusLine().getReasonPhrase();
		this.statusCode=context.getResponse().getStatusLine().getStatusCode();
		
		header =  new HashMap<String, String>();
		Header[] headers = context.getResponse().getAllHeaders();
		for (Header h : headers) {
			header.put(h.getName(), h.getValue());
		}
		try {			
			data = EntityUtils.toByteArray(context.getResponse().getEntity());
			dataLength = context.getResponse().getEntity().getContentLength();
		} catch (IOException e) { 
			data=null;
			dataLength=0;
		}
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public boolean isStatusCodeOk() {
		return statusCode==200;
	}

	public String getStatusPhrase() {
		return statusPhrase;
	}

	public String getBody() {		
		return new String(data);
	}
	
	public byte[] getData() {		
		return data;
	}
	
	public long getBodyLength() {
		return dataLength;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}
	
	@Override
	public String toString() {
		return getStatusCode() + " " + getStatusPhrase();
	}
	
	
}
