package ddc.http;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

public class HClientExecutorTest {

	@Test
	public void test() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, HClientException {
		HClientFactory f = new HClientFactory();
//		f.setProxyConfig(new HClientBasicHostAuth());
		HClientExecutor client = new HClientExecutor(f.createClient());
		
		HClientResponse res = client.executeGet("www.repubblica.it");
		System.out.println(res);
		System.out.println(res.getBody());
		
	}
}


