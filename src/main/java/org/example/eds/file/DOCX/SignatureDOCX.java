package org.example.eds.file.DOCX;

import java.io.*;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class SignatureDOCX {


    private final String SLASH_BACK = "/";
    private StringBuilder content;
    private String insertPath = "";

    // конструктор класса
    public SignatureDOCX() {
        content = new StringBuilder();
    }

    // получение содержимого файла ".docx" без ЭП
//
    public String getContentWithoutSignature(String signature) {
        return content.toString().replace(signature, "");
    }

    // Получение содержимого файла ".docx"
    public String getContent() {
        return content.toString();
    }

    // получение ЭП из подписанного файла ".docx"
//    fileName - имя подписанного файла
    public String getSignatureFromFile(String fileName) throws IOException {
        ZipInputStream zin = new ZipInputStream(new FileInputStream(fileName));
        ZipEntry entry;
        String name;
        StringBuilder signature = new StringBuilder();
        while ((entry = zin.getNextEntry()) != null) {
            name = entry.getName();
            if (name.equals("word\\document.xml")) {
                Scanner scanner = new Scanner(zin);
                while (scanner.hasNextLine()) {
                    String str = scanner.nextLine();
                    content.append(str);
                    String[] temp1 = str.split("</w:body>");
                    String[] df = temp1[temp1.length - 1].split("</w:document>");
                    for (String s : df) {
                        signature.append(s);
                    }
                }
                break;
            }
        }
        return signature.toString();
    }

    // подписание файла ".docx"
//    signature - ЭП
    public void signFile(String signature) {
        int ind = content.indexOf("</w:body>") + "</w:body>".length();
        content.insert(ind, signature);

        try {
            FileWriter fw = new FileWriter(insertPath);
            fw.write(content.toString());
            fw.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // чтение сордержимого файла ".docx"
//    zipName - имя открытого файла
    public void readFile(String zipName) throws Exception {
        zipName = zipName.replace(".docx", "");
        // открытия содержимого файла
        File file = new File(zipName + "/word/document.xml");
//      запоминание пути, куда вставляется ЭП
        insertPath = zipName + "/word/document.xml";

        try {
//            чтение содержимого файла
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {

                content.append(line);

                line = br.readLine();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // открытие файла
//   fileName - имя открываемого файла
    public void unArchive(final String fileName) throws Exception {
//       открытие файла ".docx", как архива
        ZipFile zipFile = new ZipFile(fileName);
//      получение списка элементов полученного архива
        Enumeration<?> entries = zipFile.entries();
//      создание временного архива
        String zipName = zipFile.getName().replace(".docx", "");
        createDirrectory(zipName);
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String entryName = entry.getName();
            if (entryName.endsWith(SLASH_BACK)) {
                createFolder(entryName);
                continue;
            } else {
                checkFolder(entryName, zipName);
            }
            InputStream fis = zipFile.getInputStream(entry);

            FileOutputStream fos = new FileOutputStream(zipName + "/" + entryName);
            byte[] buffer = new byte[1024];
            int len;
            while
            ((len = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }

            fis.close();
            fos.close();
        }
        zipFile.close();
    }

    // создание нового файла ".docx"
//    fileName - имя старого файла
//    zipFile - имя нового файла
    public void archive(String fileName, String zipFile) throws Exception {
        fileName = fileName.replace(".docx", "");
        FileOutputStream fout = new FileOutputStream(zipFile);
        ZipOutputStream zout = new ZipOutputStream(fout);
        File fileSource = new File(fileName);
        addDirectory(zout, fileSource, fileName);
        zout.close();

    }

    //  Добавление каталогов в новый файл ".docx"
//      zout - поток для записи данных
//      fileSource - открытый архив
//      sourceDirectory - имя открытого архива
    public void addDirectory(ZipOutputStream zout, File fileSource, String sourceDirectory) throws Exception {
        File[] files = fileSource.listFiles();
        for (File file : files) {
//          Если file является директорией, то рекурсивно вызываем
//          метод addDirectory
            if (file.isDirectory()) {
                addDirectory(zout, file, sourceDirectory);
                continue;
            }
            FileInputStream fis = new FileInputStream(file);
            String path = file.getPath().substring((sourceDirectory + "/").length());
            zout.putNextEntry(new ZipEntry(path));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0)
                zout.write(buffer, 0, length);
            zout.closeEntry();
            fis.close();
        }
    }

    //  Создание директории
//      dir - имя создаваемой директории
    private void createDirrectory(final String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    //  Создание папки
//      dirName - имя создаваемой папки
    private void createFolder(final String dirName) {
        if (dirName.endsWith(SLASH_BACK)) {
            createDirrectory(dirName.substring(0, dirName.length() - 1));
        }

    }

    //  Создание директории, если filePath - не папка
//      filePath - имя проверяемого пути
//      zipName - имя архива
    private void checkFolder(final String filePath, final String zipName) {
        if (!filePath.endsWith(SLASH_BACK) && filePath.contains(SLASH_BACK)) {
            String dir = filePath.substring(0, filePath.lastIndexOf(SLASH_BACK));
            createDirrectory(zipName + "/" + dir);
        }

    }

}