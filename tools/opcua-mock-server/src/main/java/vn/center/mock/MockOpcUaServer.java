package vn.center.mock;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.sdk.server.identity.UsernameIdentityValidator;
import org.eclipse.milo.opcua.stack.core.security.DefaultCertificateManager;
import org.eclipse.milo.opcua.stack.core.security.DefaultTrustListManager;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.transport.TransportProfile;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;
import org.eclipse.milo.opcua.stack.core.types.structured.BuildInfo;
import org.eclipse.milo.opcua.stack.core.util.CertificateUtil;
import org.eclipse.milo.opcua.stack.server.EndpointConfiguration;
import org.eclipse.milo.opcua.stack.server.security.DefaultServerCertificateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mock OPC-UA Server cho tủ chiếu sáng CENTER-V1. Bật 2 endpoint:
 * <ul>
 *   <li><b>None</b> — CHỈ dùng dev/test (rules/20 cấm dùng ở môi trường thật).</li>
 *   <li><b>Basic256Sha256 + SignAndEncrypt</b> — đúng yêu cầu bảo mật Mục III.a.</li>
 * </ul>
 * Tài khoản dev: center / center_dev_pw (hoặc Anonymous).
 */
public final class MockOpcUaServer {

  public static final int TCP_BIND_PORT = 12686;
  public static final String ENDPOINT_PATH = "/center-v1";
  public static final String HOSTNAME = "localhost";

  private static final Logger log = LoggerFactory.getLogger(MockOpcUaServer.class);

  private final OpcUaServer server;
  private final CabinetNamespace namespace;

  public MockOpcUaServer() throws Exception {
    Path security =
        Files.createDirectories(
            Path.of(System.getProperty("java.io.tmpdir"), "center-v1-mock", "security"));
    Path pki = Files.createDirectories(security.resolve("pki"));

    KeyStoreLoader loader = new KeyStoreLoader().load(security);
    X509Certificate certificate = loader.getCertificate();

    DefaultCertificateManager certificateManager =
        new DefaultCertificateManager(loader.getKeyPair(), loader.getCertificateChain());
    DefaultTrustListManager trustListManager = new DefaultTrustListManager(pki.toFile());
    DefaultServerCertificateValidator certificateValidator =
        new DefaultServerCertificateValidator(trustListManager);

    String applicationUri =
        CertificateUtil.getSanUri(certificate).orElse(KeyStoreLoader.APPLICATION_URI);

    // Cho phép Anonymous (tiện test) + Username/Password (dev). Production: thêm X.509 + RBAC.
    UsernameIdentityValidator identityValidator =
        new UsernameIdentityValidator(
            true,
            challenge ->
                "center".equals(challenge.getUsername())
                    && "center_dev_pw".equals(challenge.getPassword()));

    OpcUaServerConfig config =
        OpcUaServerConfig.builder()
            .setApplicationUri(applicationUri)
            .setApplicationName(LocalizedText.english("CENTER-V1 OPC-UA Mock Server"))
            .setEndpoints(createEndpoints(certificate))
            .setBuildInfo(
                new BuildInfo(
                    "urn:center-v1:mock",
                    "CENTER-V1",
                    "OPC-UA Mock Server",
                    "0.1.0",
                    "",
                    DateTime.now()))
            .setCertificateManager(certificateManager)
            .setTrustListManager(trustListManager)
            .setCertificateValidator(certificateValidator)
            .setIdentityValidator(identityValidator)
            .setProductUri("urn:center-v1:mock")
            .build();

    this.server = new OpcUaServer(config);
    this.namespace = new CabinetNamespace(server);
    this.namespace.startup();
  }

  private Set<EndpointConfiguration> createEndpoints(X509Certificate certificate) {
    Set<EndpointConfiguration> endpoints = new LinkedHashSet<>();

    EndpointConfiguration.Builder base =
        EndpointConfiguration.newBuilder()
            .setBindAddress("0.0.0.0")
            .setHostname(HOSTNAME)
            .setPath(ENDPOINT_PATH)
            .setCertificate(certificate)
            .addTokenPolicies(
                OpcUaServerConfig.USER_TOKEN_POLICY_ANONYMOUS,
                OpcUaServerConfig.USER_TOKEN_POLICY_USERNAME);

    // None — dev/test only
    endpoints.add(
        tcp(base.copy().setSecurityPolicy(SecurityPolicy.None).setSecurityMode(MessageSecurityMode.None)));

    // Basic256Sha256 + SignAndEncrypt — đúng yêu cầu bảo mật
    endpoints.add(
        tcp(
            base.copy()
                .setSecurityPolicy(SecurityPolicy.Basic256Sha256)
                .setSecurityMode(MessageSecurityMode.SignAndEncrypt)));

    return endpoints;
  }

  private EndpointConfiguration tcp(EndpointConfiguration.Builder builder) {
    return builder
        .copy()
        .setTransportProfile(TransportProfile.TCP_UASC_UABINARY)
        .setBindPort(TCP_BIND_PORT)
        .build();
  }

  public CompletableFuture<OpcUaServer> startup() {
    log.info(
        "OPC-UA Mock Server: opc.tcp://{}:{}{} (None + Basic256Sha256/SignAndEncrypt)",
        HOSTNAME,
        TCP_BIND_PORT,
        ENDPOINT_PATH);
    return server.startup();
  }

  public CompletableFuture<OpcUaServer> shutdown() {
    return server.shutdown();
  }

  public OpcUaServer getServer() {
    return server;
  }
}
