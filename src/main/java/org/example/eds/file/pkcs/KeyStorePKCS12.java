package org.example.eds.file.pkcs;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;


public class KeyStorePKCS12 {

//  информация о владельце сертификата ЭП
    private String alias;
//  название файла сертификата ЭП
    private String keyStoreFile;
//  пароль от сертификата ЭП
    private String password;
//  хранилище ключей
    private KeyStore keyStore = null;

//  конструктор класса
    public KeyStorePKCS12(String keyStore, String password) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        this.keyStoreFile = keyStore;
        this.password = password;
        openKeyStore();
        this.alias = getAlias();
    }

//  открытие файла сертификата ЭП
    private void openKeyStore() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(keyStoreFile), password.toCharArray());
    }

//  создание сертификата ЭП
    public static KeyStore generatePKCS12KeyStore(final String password, KeyPair kp, String alias)
            throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, OperatorCreationException{
        final KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, password.toCharArray());

        final KeyStore.PrivateKeyEntry privateKey =
                new KeyStore.PrivateKeyEntry(
                        kp.getPrivate(),
                        new X509Certificate[]{generateX509Certificate(kp)});
        final KeyStore.ProtectionParameter privateKeyPassword =
                new KeyStore.PasswordProtection(password.toCharArray());
        keyStore.setEntry(alias, privateKey, privateKeyPassword);

        return keyStore;
    }
//  создание самоподписанного сертификата
    private static X509Certificate generateX509Certificate(final KeyPair keyPair)
            throws OperatorCreationException, CertificateException, CertIOException
    {
//      создание дат действительности сертификата ЭП (15 месяцев)
        final Instant now = Instant.now();
        final Date notBefore = Date.from(now);
        final Date notAfter = Date.from(now.plus(Duration.ofDays(455)));
//      создание службы подписи контента
        final ContentSigner contentSigner = new JcaContentSignerBuilder("SHA512withECDSA").build(keyPair.getPrivate());
        final String dn = "CN=asymm-cn";
//      создание самоподписанного сертификата
        final X500Name x500Name = new X500Name(RFC4519Style.INSTANCE, dn);
        final X509v3CertificateBuilder certificateBuilder =
                new JcaX509v3CertificateBuilder(x500Name,
                        BigInteger.valueOf(now.toEpochMilli()),
                        notBefore,
                        notAfter,
                        x500Name,
                        keyPair.getPublic())
                        .addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

        return new JcaX509CertificateConverter()
                .setProvider(new BouncyCastleProvider()).getCertificate(certificateBuilder.build(contentSigner));
    }
//  сохранение сертификата ЭП в файл
    public static void savePKCS12(KeyStore keyStore, String file, String password) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        final FileOutputStream fos = new FileOutputStream(file);
        keyStore.store(fos, password.toCharArray());
    }

//  получение информации о владельце сертификата ЭП
    public String getAlias() throws KeyStoreException{
        return keyStore.aliases().nextElement();
    }
//  проверка является ли сертификат ЭП действительным по времени
    public boolean validData() throws  KeyStoreException{
        Date validSign = ((X509Certificate) keyStore.getCertificate(alias)).getNotAfter();
        Date nowDate = new Date();
        return nowDate.before(validSign);
    }
//  получение открытого ключа из сертификата ЭП
    public PublicKey getPublicKeyFrom() throws KeyStoreException{
        return keyStore.getCertificate(alias).getPublicKey();
    }
//  получение секретного ключа из сертификата ЭП
    public PrivateKey getPrivateKey() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        return (PrivateKey) keyStore.getKey(alias, password.toCharArray());
    }

}

