package org.example;



public class Main {
    public static void main(String[] args) throws Exception {
        String ellipticCurve = "secp521r1";
        String fileName = "test_for_pdf.pdf";
        String fileSignName = "sign_pdf_file.pdf";
        String passwordKeyStore = "java";
        String alias = "first name: ivan\n last name: ivanov\n " +
                "patronymic: ivanovich\n mail: example@mail.ru\n tel: +79000000000";
        String fileKeyStore = "PDFkey.p12";

        RequestEDS.signPDFFile(fileName, fileSignName, passwordKeyStore, ellipticCurve, alias, fileKeyStore);
        System.out.println(RequestEDS.verifySignPDFFile(fileSignName, fileKeyStore, passwordKeyStore));

    }
}