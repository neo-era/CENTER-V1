package vn.center.mock;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateBuilder;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;

/**
 * Sinh/đọc chứng chỉ X.509 self-signed cho mock server (CHỈ dùng dev).
 * Production: chứng chỉ do CA/đơn vị quản lý cấp, quản lý tập trung (rules/20, rules/70).
 */
final class KeyStoreLoader {

  static final String APPLICATION_URI = "urn:center-v1:mock:server";
  private static final String ALIAS = "center-mock";
  private static final char[] PASSWORD = "center_dev_pw".toCharArray();

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  private X509Certificate certificate;
  private X509Certificate[] certificateChain;
  private KeyPair keyPair;

  KeyStoreLoader load(Path baseDir) throws Exception {
    KeyStore keyStore = KeyStore.getInstance("PKCS12");
    Path keyStorePath = baseDir.resolve("center-mock-server.pfx");

    if (!Files.exists(keyStorePath)) {
      keyStore.load(null, PASSWORD);

      KeyPair kp = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);
      X509Certificate cert =
          new SelfSignedCertificateBuilder(kp)
              .setCommonName("CENTER-V1 OPC-UA Mock Server")
              .setOrganization("CENTER-V1")
              .setOrganizationalUnit("dev")
              .setLocalityName("Ho Chi Minh City")
              .setStateName("HCM")
              .setCountryCode("VN")
              .setApplicationUri(APPLICATION_URI)
              .addDnsName("localhost")
              .addIpAddress("127.0.0.1")
              .build();

      keyStore.setKeyEntry(ALIAS, kp.getPrivate(), PASSWORD, new X509Certificate[] {cert});
      try (OutputStream out = Files.newOutputStream(keyStorePath)) {
        keyStore.store(out, PASSWORD);
      }
    } else {
      try (InputStream in = Files.newInputStream(keyStorePath)) {
        keyStore.load(in, PASSWORD);
      }
    }

    Key privateKey = keyStore.getKey(ALIAS, PASSWORD);
    if (privateKey instanceof PrivateKey pk) {
      certificate = (X509Certificate) keyStore.getCertificate(ALIAS);
      certificateChain =
          Arrays.stream(keyStore.getCertificateChain(ALIAS))
              .map(X509Certificate.class::cast)
              .toArray(X509Certificate[]::new);
      PublicKey publicKey = certificate.getPublicKey();
      keyPair = new KeyPair(publicKey, pk);
    } else {
      throw new IllegalStateException("Không đọc được private key từ keystore");
    }
    return this;
  }

  X509Certificate getCertificate() {
    return certificate;
  }

  X509Certificate[] getCertificateChain() {
    return certificateChain;
  }

  KeyPair getKeyPair() {
    return keyPair;
  }
}
