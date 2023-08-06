package org.example.signature.ECDSA;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.*;


public class SignatureECDSA {
//    создание ЭП
//    plaintext - текст для подписания
//    key - секретный ключ для формирования ЭП
    public byte[] generateSignature(String plaintext, PrivateKey key)
            throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {
        Signature ecdsaSign = Signature
                .getInstance("SHA512withECDSA");
        ecdsaSign.initSign(key);
        ecdsaSign.update(plaintext.getBytes(StandardCharsets.UTF_8));
        return ecdsaSign.sign();
    }
//  проверка ЭП
//  plaintext - подписанный текст
//  signature - ЭП
    public boolean validateSignature(String plaintext, PublicKey pubKey, byte[] signature) throws SignatureException,
            InvalidKeyException,
            NoSuchAlgorithmException, NoSuchProviderException {
        try {
            Signature ecdsaVerify = Signature.getInstance("SHA512withECDSA",
                    "BC");
            ecdsaVerify.initVerify(pubKey);
            ecdsaVerify.update(plaintext.getBytes(StandardCharsets.UTF_8));
            return ecdsaVerify.verify(signature);
        }catch(Exception e){
            System.out.println("the data was changed after signing");
        }
        return false;
    }
//  создание ключевой пары
//  curve - название эллиптической кривой (P-256, P-384, P-521)
    public KeyPair generateKeys(String curve) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException {
        ECParameterSpec ecSpec = ECNamedCurveTable
                .getParameterSpec(curve);

        KeyPairGenerator g = KeyPairGenerator.getInstance("EC", "BC");

        g.initialize(ecSpec);

        return g.generateKeyPair();
    }
//  добавление криптопровайдера Bouncy castle
    public void setSecurityProvider() {
        Security.addProvider(new BouncyCastleProvider());
    }
}
