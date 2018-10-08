package com.magento.simple;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.recording.SnapshotRecordResult;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WiremockTest {

  private WireMockServer wireMockServer;

  @BeforeClass
  public static void initLogging() {
    final InputStream inputStream = WiremockTest.class.getResourceAsStream("/logging.properties");
    try {
      LogManager.getLogManager().readConfiguration(inputStream);
    } catch (final IOException e) {
      Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
      Logger.getAnonymousLogger().severe(e.getMessage());
    }
  }

  @Before
  public void configureSystemUnderTest() {
    this.wireMockServer = new WireMockServer(
        options().dynamicPort().withRootDirectory("src/test/resources/wiremock"));
    this.wireMockServer.start();
    configureFor(this.wireMockServer.port());
    wireMockServer.startRecording("https://www.example.com");
  }

  @Test
  public void connect() throws IOException {
    Proxy proxy =
        new Proxy(Proxy.Type.HTTP, new InetSocketAddress("0.0.0.0", wireMockServer.port()));
    try (InputStream is =
        new URL("https://www.example.com").openConnection(proxy).getInputStream()) {
      String content = IOUtils.toString(is, StandardCharsets.UTF_8);
      System.out.println(content);
    }
  }

  @After
  public void shutdownWireMock() {
    SnapshotRecordResult srb = wireMockServer.stopRecording();
    System.out.println(srb.getStubMappings().size());
    for (StubMapping sb : srb.getStubMappings()) {
      System.out.println(sb);
    }
    wireMockServer.stop();
  }
}
