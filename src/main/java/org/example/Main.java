package org.example;

import org.example.eds.file.pkcs.KeyStorePKCS12;
import org.example.signature.ECDSA.SignatureECDSA;
import java.security.PrivateKey;

public class Main {
    public static void main(String[] args) throws Exception {
        String fileName1 = "test_for_doc.docx";
        String fileSignName1 = "sign_doc_file.docx";
        String passwordKeyStore1 = "java";
        String ellipticCurve = "secp521r1";
        String alias1 = "first name: ivan\n last name: ivanov\n " +
                "patronymic: ivanovich\n mail: example@mail.ru\n tel: +79000000000";
        String fileKeyStore1 = "DOCXkey.p12";
        RequestEDS.signDocxFile(fileName1, fileSignName1, passwordKeyStore1, ellipticCurve, alias1, fileKeyStore1);

        System.out.println(RequestEDS.verifySignDocxFile(fileSignName1,fileKeyStore1, passwordKeyStore1));




        String fileName = "test_for_pdf.pdf";
        String fileSignName = "sign_pdf_file.pdf";
        String passwordKeyStore = "java";
        String alias = "first name: ivan\n last name: ivanov\n " +
                "patronymic: ivanovich\n mail: example@mail.ru\n tel: +79000000000";
        String fileKeyStore = "PDFkey.p12";
        String fileSignName2 = "sign_pdf_file2.pdf";

//        RequestEDS.signPDFFile(fileName, fileSignName, passwordKeyStore, fileKeyStore);
//        System.out.println(RequestEDS.verifySignPDFFile(fileSignName, fileKeyStore, passwordKeyStore));
//
        RequestEDS.signPDFFile(fileName, fileSignName, passwordKeyStore, ellipticCurve, alias, fileKeyStore);



        System.out.println(RequestEDS.verifySignPDFFile(fileSignName, fileKeyStore, passwordKeyStore));
//
//        RequestEDS.signPDFFile(fileSignName, fileSignName2, passwordKeyStore, ellipticCurve, alias, fileKeyStore);
//
//
//
//        System.out.println(RequestEDS.verifySignPDFFile(fileSignName2, fileKeyStore, passwordKeyStore));

    }
}