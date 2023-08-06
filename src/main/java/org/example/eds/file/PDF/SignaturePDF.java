package org.example.eds.file.PDF;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import java.io.*;

public class SignaturePDF {

    static PdfName PIECE_INFO = new PdfName("PieceInfo");
    static PdfName LAST_MODIFIED = new PdfName("LastModified");
    static PdfName PRIVATE = new PdfName("Private");

    static PdfName app = new PdfName("APP");

    static PdfName name = new PdfName("Signature");

    //  получение содержимого файла ".pdf"
//    file - имя файла ".pdf"
    public String getContent(String file) throws IOException {
        PdfReader reader = new PdfReader(file);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i <= reader.getNumberOfPages(); ++i) {
            TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
            String text = PdfTextExtractor.getTextFromPage(reader, i, strategy);
            stringBuilder.append(text);
        }
        reader.close();
        return stringBuilder.toString();
    }

    //  подписание файла
//    file - имя изначального файла ".pdf"
//    fileSign - имя подписанного файла ".pdf"
//    sig - ЭП
    public void signPDF(String file, String fileSign, String sig) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(file);
        addSign(reader, new PdfString(sig));
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(fileSign));
        stamper.close();
        reader.close();
    }

    //  добавление ЭП в файл
//    reader - поток для чтения файла ".pdf"
//    signature - ЭП
    void addSign(PdfReader reader, PdfObject signature) {
        PdfDictionary catalog = reader.getCatalog();
        PdfDictionary pieceInfo = catalog.getAsDict(PIECE_INFO);
        if (pieceInfo == null) {
            pieceInfo = new PdfDictionary();
            catalog.put(PIECE_INFO, pieceInfo);
        }

        PdfDictionary appData = pieceInfo.getAsDict(app);
        if (appData == null) {
            appData = new PdfDictionary();
            pieceInfo.put(app, appData);
        }

        PdfDictionary privateData = appData.getAsDict(PRIVATE);
        if (privateData == null) {
            privateData = new PdfDictionary();
            appData.put(PRIVATE, privateData);
        }

        appData.put(LAST_MODIFIED, new PdfDate());
        privateData.put(name, signature);
    }

    //  получение ЭП из файла
//    file - имя файла ".pdf"
    public String getSign(String file) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(file);
        String sign = getSign(reader);
        reader.close();
        return sign;
    }

    //  получение ЭП из файла
//    reader - поток для чтения файла ".pdf"
    public String getSign(PdfReader reader) {
        PdfDictionary catalog = reader.getCatalog();

        PdfDictionary pieceInfo = catalog.getAsDict(PIECE_INFO);
        if (pieceInfo == null)
            return null;

        PdfDictionary appData = pieceInfo.getAsDict(app);
        if (appData == null)
            return null;

        PdfDictionary privateData = appData.getAsDict(PRIVATE);
        if (privateData == null)
            return null;

        return privateData.get(name).toString();
    }


}
