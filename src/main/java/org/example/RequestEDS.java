package org.example;

import com.itextpdf.text.DocumentException;
import org.bouncycastle.operator.OperatorCreationException;
import org.example.eds.file.PDF.SignaturePDF;
import org.example.eds.file.pkcs.KeyStorePKCS12;
import org.example.signature.ECDSA.SignatureECDSA;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;

public class RequestEDS {

    //    Подписание файла в формате ".pdf" с генерацией нового сертификата электронной подписи
//    fileName - имя подписываемого файла ".docx"
//    fileSignName - имя создаваемого файла ".docx" с ЭП
//    passwordKeyStore - пароль для сертификата ЭП
//    ellipticCurve - эллиптическая кривая (P-256, P-384, P-521), на основе которой будет создаваться ЭП
//    alias - информация о владельце ЭП
//    fileKeyStore - файл для сохранения сертификата электронной подписи ".р12"
    public static void signPDFFile(String fileName, String fileSignName, String passwordKeyStore, String ellipticCurve, String alias, String fileKeyStore) throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidKeyException, UnrecoverableKeyException, CertificateException, KeyStoreException, OperatorCreationException, DocumentException {
//      создание объекта класса SignaturePDF
        SignaturePDF signaturePDF = new SignaturePDF();
//      формирование информации, которую нужно подписать
        String content = signaturePDF.getContent(fileName) + alias;
//      создание объекта класса SignatureECDSA
        SignatureECDSA sig = new SignatureECDSA();
//      добавление криптопровайдера Bouncy castle
        sig.setSecurityProvider();
//      создание ключевой пары
        KeyPair kp = sig.generateKeys(ellipticCurve);
//      создание ЭП
        byte[] s = sig.generateSignature(content, kp.getPrivate());
//      создание сертификата ЭП
        KeyStorePKCS12.savePKCS12(KeyStorePKCS12.generatePKCS12KeyStore(passwordKeyStore, kp, alias), fileKeyStore, passwordKeyStore);
//      формирование подписанного файла ".pdf"
        signaturePDF.signPDF(fileName, fileSignName, Base64.getEncoder().encodeToString(s));
    }

    //    Подписание файла в формате ".pdf" на основе существующего сертификата ЭП ".р12"
//    fileName - имя подписываемого файла ".docx"
//    fileSignName - имя создаваемого файла ".docx" с ЭП
//    passwordKeyStore - пароль для сертификата ЭП
//    fileKeyStore - файл для сохранения сертификата электронной подписи ".р12"
    public static void signPDFFile(String fileName, String fileSignName, String passwordKeyStore, String fileKeyStore) throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidKeyException, UnrecoverableKeyException, CertificateException, KeyStoreException, OperatorCreationException, DocumentException {
//      создание объекта класса keyStorePKCS12
        KeyStorePKCS12 keyStorePKCS12 = new KeyStorePKCS12(fileKeyStore, passwordKeyStore);
//      проверка сертификата ЭП на дествительность по дате
        if (keyStorePKCS12.validData()) {
//          создание объекта класса SignaturePDF
            SignaturePDF signaturePDF = new SignaturePDF();
//          получение информации о владельце сертификата ЭП
            String alias = keyStorePKCS12.getAlias();
//          формирование информации, которую нужно подписать
            String content = signaturePDF.getContent(fileName) + alias;
//          создание объекта класса SignatureECDSA
            SignatureECDSA sig = new SignatureECDSA();
//          добавление криптопровайдера Bouncy castle
            sig.setSecurityProvider();
//          получение секретного ключа из сертификата ЭП
            PrivateKey privateKey = keyStorePKCS12.getPrivateKey();
//          создание ЭП
            byte[] s = sig.generateSignature(content, privateKey);
//          формирование подписанного файла ".pdf"
            signaturePDF.signPDF(fileName, fileSignName, Base64.getEncoder().encodeToString(s));
        } else {
//          сообщение о том, что сертификат ЭП не действителен по времени
            System.out.println("Certificate is is not valid by date");
        }
    }

    //  Проверка ЭП в файле ".pdf"
//  fileName - подписанный файл
//  keyStore - файл с ертификатом ЭП
//  password - пароль от сертификата ЭП
    public static String verifySignPDFFile(String fileName, String keyStore, String password) throws DocumentException, IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException, UnrecoverableKeyException {
//      создание объекта класса keyStorePKCS12
        KeyStorePKCS12 keyStorePKCS12 = new KeyStorePKCS12(keyStore, password);
//      проверка сертификата ЭП на дествительность по дате
        if (keyStorePKCS12.validData()) {
//          создание объекта класса SignaturePDF
            SignaturePDF signaturePDF = new SignaturePDF();
//          получение ЭП из подписанного файла ".pdf"
            String sign = signaturePDF.getSign(fileName);
            byte[] signature = Base64.getDecoder().decode(sign);
//          получение информации о владельце сертификата ЭП
            String alias = keyStorePKCS12.getAlias();
//          формирование информации, которую подписали данной ЭП
            String content = signaturePDF.getContent(fileName) + alias;
//          получение открытого ключа из сертификата ЭП
            PublicKey publicKey = keyStorePKCS12.getPublicKeyFrom();
//          создание объекта класса SignatureECDSA
            SignatureECDSA signatureECDSA = new SignatureECDSA();
//          добавление криптопровайдера Bouncy castle
            signatureECDSA.setSecurityProvider();
//          получение результата проверки ЭП
            boolean result = signatureECDSA.validateSignature(content, publicKey, signature);
//          формирование сообщения о результате проверки
            StringBuilder res = new StringBuilder();
            if (result) {
                res.append(alias);
                res.append("\nSignature is valid");
            } else {
                res.append("\nSignature is invalid or data in the document has been changed");
            }
            return res.toString();
        } else {
//      сообщение о том, что сертификат ЭП не действителен по времени
            return "The signature is not valid by date";
        }
    }
}
