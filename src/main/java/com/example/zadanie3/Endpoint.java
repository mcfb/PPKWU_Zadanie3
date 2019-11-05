package com.example.zadanie3;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/")
public class Endpoint {

    private String version = "VERSION:1.0 \n";
    private String prodid = "PRODID://test \n";
    private String calBegin = "BEGIN:VCALENDAR \n";
    private String calEnd = "END:VCALENDAR \n";
    private String eventBegin = "BEGIN:VEVENT \n";
    private String eventEnd = "END:VEVENT \n";
    private String calscale = "CALSCALE:GREGORIAN \n";
    private String method = "METHOD:PUBLISH \n";
    private String dateStart = "DTSTART:2019";
    private String dateEnd = "DTEND:2019";
    private String summary = "SUMMARY:";
    private String newLine = "\n";

    @GetMapping("convert-calendar")
    public ResponseEntity<Resource> returnCalendar() throws IOException {
        Document doc = Jsoup.connect("http://www.weeia.p.lodz.pl/pliki_strony_kontroler/kalendarz.php?rok=2019&miesiac=11&lang=1").get();
        Elements elements = doc.select("a.active");

        List<String> dates = new ArrayList<>();
        String something = "11";
        for (Element e : elements) {
            dates.add(e.text());
        }
        write(dates, something);

        File file = new File("mycalendar.ics");
        Resource fileSystemResource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/calendar"))
                .body(fileSystemResource);
    }


    public void write(List<String> dates, String month) {
        StringBuilder builder = new StringBuilder();
        builder.append("mycalendar");
        builder.append(".ics");

        try {

            File file = new File(builder.toString());
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(calBegin);
            bw.write(version);
            bw.write(prodid);
            bw.write(calscale);
            bw.write(method);

            for (int i = 0; i < dates.size(); i++) {
                bw.write(eventBegin);
                bw.write(summary + "test" + i + newLine);
                bw.write(dateStart + month + dates.get(i) + newLine);
                bw.write(dateEnd + month + dates.get(i) + newLine);
                bw.write(eventEnd);
            }

            bw.write(calEnd);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}







