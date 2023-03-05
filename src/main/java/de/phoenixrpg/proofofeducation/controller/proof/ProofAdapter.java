package de.phoenixrpg.proofofeducation.controller.proof;

import de.phoenixrpg.proofofeducation.ProofOfEducation;
import de.phoenixrpg.proofofeducation.utils.DateUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;


public class ProofAdapter {
    private String name;
    private Date year;

    private LinkedHashMap<String, DailyProof> dailyProofs;

    public ProofAdapter(long discordId, LocalDate localDate) {

        LocalDate date = localDate;
        this.dailyProofs = ProofOfEducation.getControllManager().getDataManager().getWeek(discordId, date);

         this.name = ProofOfEducation.getControllManager().getDataManager().getNameById(discordId);

         LocalDate year = ProofOfEducation.getControllManager().getDataManager().getYearById(discordId);
         this.year = new Date(year.getYear()-1900, year.getMonthValue()-1, year.getDayOfMonth());
    }

    public LinkedHashMap<String, DailyProof> getDailyProofs() {
        return dailyProofs;
    }

    public File build() {
        copyFileFromResources("Vorlage.docx", "Override.docx");
         try {
            try (XWPFDocument doc = openDocument("Override.docx")) {
                if (doc != null) {
                    LocalDate start = dailyProofs.values().stream().findFirst().get().getProofDate();
                    LocalDate end = dailyProofs.values().stream().reduce((one, two) -> two).get().getProofDate();
                    Date endDate = Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant());

                    replaceText(doc, "NAME", name);
                    replaceText(doc, "FROM", DateUtils.getDateAsString(start));
                    replaceText(doc, "TO", DateUtils.getDateAsString(end));
                    replaceText(doc, "DATE", DateUtils.getDateAsString(LocalDate.now()));


                    int numOfWeeks = (int) (DateUtils.getWeeksBetweenDates(year, endDate));
                    replaceText(doc, "NR", "" + numOfWeeks);

                    replaceText(doc,"YEAR", "" + (DateUtils.getDiffYears(year, endDate)+1));

                    LocalDate cloneStart = start.minusDays(1);
                    for(int proofNumber = 0; proofNumber < 5; proofNumber++) {
                        cloneStart = cloneStart.plusDays(1);
                        DailyProof dailyProof = dailyProofs.get(cloneStart.toString());

                        replaceText(doc, "LOCATION" + proofNumber, dailyProof.getLocation());

                        String[] lines = dailyProof.getTask().split("  ");
                        for(int line = 0; line < 5; line++) {
                            if(line >= lines.length) {
                                replaceText(doc, "INF" + proofNumber + "-" + line, "");
                            } else {
                                replaceText(doc, "INF" + proofNumber + "-" + line, lines[line]);

                            }
                        }
                    }

                    String fileName = DateUtils.getDateAsString(start);
                    saveDocument(doc,numOfWeeks + "_Ausbildungsnachweis_" + fileName + ".docx");
                    doc.close();
                    deleteFile("Override.docx");

                    File file = new File(Paths.get("").toAbsolutePath() + "/" + numOfWeeks + "_Ausbildungsnachweis_" + fileName + ".docx");
                    return file;
                }
            }
            return null;
        } catch (Exception ex) {
             ex.printStackTrace();
            return null;
        }
    }

    private XWPFDocument replaceText(XWPFDocument doc, String findText, String replaceText) {
        for (XWPFParagraph p : doc.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null && text.contains(findText)) {
                        text = text.replace(findText, replaceText);
                        r.setText(text, 0);
                    }
                }
            }
        }
        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            String text = r.getText(0);
                            if (text != null && text.contains(findText)) {
                                text = text.replace(findText, replaceText);
                                r.setText(text,0);
                            }
                        }
                    }
                }
            }
        }
        return doc;
    }

    private boolean deleteFile(String file) {
        File sample = new File(Paths.get("").toAbsolutePath() + "/" + file);
        sample.delete();
        return true;
    }

    private boolean copyFile(String file, String toFile) {
        URL res = getClass().getClassLoader().getResource(file);
        File sample = new File(res.getPath());
        try {
            if(Files.exists(Paths.get(toFile))) deleteFile(toFile);
            Files.copy(sample.toPath(), Paths.get(Paths.get("").toAbsolutePath() + "/" + toFile));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean copyFileFromResources(String file, String toFile) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(file);
        try {
            if(Files.exists(Paths.get(toFile))) deleteFile(toFile);
            Files.copy(inputStream, Paths.get(Paths.get("").toAbsolutePath() + "/" + toFile));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private XWPFDocument openDocument(String file) throws Exception {
        Path res = Paths.get("").toAbsolutePath();
        XWPFDocument document = null;
        if (res != null) {
            document = new XWPFDocument(OPCPackage.open(new File(res + "/" + file)));
        }
        return document;
    }

    private void saveDocument(XWPFDocument doc, String file) {
        try (FileOutputStream out = new FileOutputStream(file)) {
            doc.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
