package com.magento;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.braintreegateway.Environment;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.recording.SnapshotRecordResult;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import java.net.InetSocketAddress;
import java.net.Proxy;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class WiremockBraintreeTest extends ProxyBraintreeTest {

  private static WireMockServer wireMockServer;

  @BeforeClass
  public static void startWiremock() {
    wireMockServer = new WireMockServer(
        options().dynamicPort().withRootDirectory("src/test/resources/wiremock"));
    wireMockServer.start();
    configureFor(wireMockServer.port());
    wireMockServer.startRecording(Environment.SANDBOX.baseURL);
  }

  @Override
  public Proxy getProxy() {
    return new Proxy(Proxy.Type.HTTP, new InetSocketAddress("0.0.0.0", wireMockServer.port()));
  }

  @Override
  public Environment getEnvironment() {
    return Environment.SANDBOX;
  }

  @AfterClass
  public static void shutdownWireMock() {
    SnapshotRecordResult srb = wireMockServer.stopRecording();
    for (StubMapping sb : srb.getStubMappings()) {
      System.out.println(sb);
    }
    wireMockServer.stop();
  }
}
