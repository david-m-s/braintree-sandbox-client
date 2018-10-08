package com.magento.simple;

import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit.core.HoverflyConfig;
import io.specto.hoverfly.junit.core.HoverflyMode;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class HoverflyTest {

  private static Hoverfly hoverfly;

  @BeforeClass
  public static void setupHoverFly() throws Exception {
    // trustAllSSL();
    System.out.println("start hoverfly");
    hoverfly = new Hoverfly(HoverflyConfig.localConfigs(), HoverflyMode.CAPTURE);
    hoverfly.start();
  }

  public static void trustAllSSL() throws NoSuchAlgorithmException, KeyManagementException {
    System.out.println("trust all");
    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

      @Override
      public X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      @Override
      public void checkClientTrusted(X509Certificate[] certs, String authType) {
      }

      @Override
      public void checkServerTrusted(X509Certificate[] certs, String authType) {
      }
    }};
    // Create all-trusting trust manager
    SSLContext sc = SSLContext.getInstance("SSL");
    sc.init(null, trustAllCerts, new java.security.SecureRandom());
    // Create all-trusting host name verifier
    HostnameVerifier allHostsValid = new HostnameVerifier() {
      @Override
      public boolean verify(String hostname, SSLSession session) {
        return true;
      }
    };
    // Install the all-trusting trust manager
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    // Install the all-trusting host verifier
    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
  }

  @Test
  public void connect() throws IOException {
    // Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("0.0.0.0",
    // wireMockServer.port()));
    String postData = "";
    HttpURLConnection myURLConnection =
        (HttpURLConnection) new URL("https://api.sandbox.braintreegateway.com").openConnection();
    myURLConnection.setRequestMethod("POST");
    myURLConnection.setRequestProperty("Content-Type", "application/json");
    myURLConnection.setRequestProperty("Accept", "application/json");
    myURLConnection.setRequestProperty("Content-Length", "" + postData.getBytes().length);
    myURLConnection.setRequestProperty("api_key", "en-US");
    myURLConnection.setUseCaches(false);
    myURLConnection.setDoInput(true);
    myURLConnection.setDoOutput(true);
    try (InputStream is =
        new URL("https://api.sandbox.braintreegateway.com").openConnection().getInputStream()) {
      String content = IOUtils.toString(is, StandardCharsets.UTF_8);
      System.out.println(content);
    }
  }

  @AfterClass
  public static void shutdownHoverfly() {
    hoverfly.exportSimulation(Paths.get("./src/test/resources/hoverfly/mappings"));
    hoverfly.close();
  }
}
