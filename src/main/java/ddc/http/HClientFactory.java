package ddc.http;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

public class HClientFactory {
	private int maxTotalConnection = 5;
	private int maxPerRoute = 5;
	private PoolingHttpClientConnectionManager connectionManager = null;
	private HClientBasicHostAuth proxyConfig = null;
	private KeyStore clientCert;
	private HClientBasicHostAuth basicAuth = null;

	public int getMaxTotalConnection() {
		return maxTotalConnection;
	}

	public void setMaxTotalConnection(int maxTotalConnection) {
		this.maxTotalConnection = maxTotalConnection;
	}

	public int getMaxPerRoute() {
		return maxPerRoute;
	}

	public KeyStore getClientCert() {
		return clientCert;
	}

	public void setClientCert(KeyStore clientCert) {
		this.clientCert = clientCert;
	}

	public void setMaxPerRoute(int maxPerRoute) {
		this.maxPerRoute = maxPerRoute;
	}

	public HClientBasicHostAuth getProxyConfig() {
		return proxyConfig;
	}

	public void setProxyConfig(HClientBasicHostAuth proxyConfig) {
		this.proxyConfig = proxyConfig;
	}

	public HClientBasicHostAuth getBasicAuth() {
		return basicAuth;
	}

	public void setBasicAuth(HClientBasicHostAuth basicAuth) {
		this.basicAuth = basicAuth;
	}

	public CloseableHttpClient createClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		HttpClientBuilder builder = HttpClients.custom();
		if (connectionManager == null) {
			if (clientCert != null) {
				SSLConnectionSocketFactory sslConnectionFactory = buildSSLConnectionFactory(clientCert);
				Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create().register("https", sslConnectionFactory).register("http", new PlainConnectionSocketFactory()).build();
				builder.setSSLSocketFactory(sslConnectionFactory);
				connectionManager = new PoolingHttpClientConnectionManager(registry);
			} else {
				connectionManager = new PoolingHttpClientConnectionManager();
			}
			connectionManager.setMaxTotal(maxTotalConnection);
			connectionManager.setDefaultMaxPerRoute(maxPerRoute);
		}
		builder.setConnectionManager(connectionManager);
		CredentialsProvider credsProvider = null;
		if (proxyConfig != null) {
			if (credsProvider == null)
				credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(proxyConfig.getHost(), proxyConfig.getPort()), new UsernamePasswordCredentials(proxyConfig.getUsername(), proxyConfig.getPassword()));
			builder.setProxy(new HttpHost(proxyConfig.getHost(), proxyConfig.getPort()));
		}
		if (basicAuth != null) {
			if (credsProvider == null)
				credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(basicAuth.getHost(), basicAuth.getPort()), new UsernamePasswordCredentials(basicAuth.getUsername(), basicAuth.getPassword()));
		}
		if (credsProvider != null) {
			builder.setDefaultCredentialsProvider(credsProvider);
		}

		return builder.build();
	}

	private SSLConnectionSocketFactory buildSSLConnectionFactory(KeyStore keyStore) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(keyStore, new TrustSelfSignedStrategy()).build();
		SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslContext, new DefaultHostnameVerifier());
		return sslConnectionFactory;
	}

}
