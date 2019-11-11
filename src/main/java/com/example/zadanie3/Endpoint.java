package com.example.zadanie3;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/")
public class Endpoint {

    private static final String VERSION = "VERSION:2.0\n";
    private static final String PRODID = "PRODID:-//hacksw/handcal//NONSGML v1.0//EN\n";
    private static final String CAL_BEGIN = "BEGIN:VCALENDAR\n";
    private static final String CAL_END = "END:VCALENDAR\n";
    private static final String EVENT_BEGIN = "BEGIN:VEVENT\n";
    private static final String EVENT_END = "END:VEVENT\n";
    private static final String CALSCALE_GREGORIAN = "CALSCALE:GREGORIAN\n";
    private static final String METHOD = "METHOD:PUBLISH\n";
    private static final String DATE_START = "DTSTART:";
    private static final String DATE_END = "DTEND:";
    private static final String SUMMARY = "SUMMARY:";
    private int year;
    private String month;
    private String nextMonth;
    private BufferedWriter bw;

    @GetMapping("convert-calendar")
    public ResponseEntity<Resource> returnCalendar() throws IOException {

        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        year = localDate.getYear();
        month = String.valueOf(localDate.getMonthValue());
        nextMonth = String.valueOf(localDate.getMonthValue() + 1);

        List<Event> events = new ArrayList<>();
        events.add(fetchCalendar(month, String.valueOf(year)));
        events.add(fetchCalendar(nextMonth, String.valueOf(year)));
        write(events);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mycalendar.ics");

        File file = new File("mycalendar.ics");
        Resource fileSystemResource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/calendar"))
                .body(fileSystemResource);
    }

    private Event fetchCalendar(String month, String year) throws IOException {
        Document doc = Jsoup.connect("http://www.weeia.p.lodz.pl/pliki_strony_kontroler/kalendarz.php?rok=" + year + "&miesiac=" + month + "&lang=1").get();
        Elements elements = doc.select("a.active");
        Elements elementsNames = doc.select("p");

        List<String> dates = new ArrayList<>();
        List<String> eventNames = new ArrayList<>();

        for (Element e : elements) {
            dates.add(e.text());
        }

        for (Element e : elementsNames) {
            eventNames.add(e.text());
        }
        return new Event(dates, eventNames, month);
    }


    private void write(List<Event> events) {

        StringBuilder builder = new StringBuilder();
        builder.append("mycalendar");
        builder.append(".ics");

        try {
            File file = new File(builder.toString());
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
            bw.write(CAL_BEGIN);
            bw.write(VERSION);
            bw.write(PRODID);
            bw.write(CALSCALE_GREGORIAN);
            bw.write(METHOD);
            for (int i = 0; i < events.size(); i++) {
                writeSingleEvent(events.get(i));
            }
            bw.write(CAL_END);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeSingleEvent(Event event) throws IOException {
        event.setMonth(checkMonthValue(event.getMonth()));
        for (int i = 0; i < event.getDates().size(); i++) {
            bw.write(EVENT_BEGIN);
            bw.write(DATE_START + year + event.getMonth() + event.getDates().get(i) + System.lineSeparator());
            bw.write(DATE_END + year + event.getMonth() + event.getDates().get(i) + System.lineSeparator());
            bw.write(SUMMARY + event.getEventNames().get(i) + System.lineSeparator());
            bw.write(EVENT_END);
        }
    }

    private String checkMonthValue(String month) {
        int monthNum = Integer.valueOf(month);
        if (monthNum < 10) {
            return new StringBuilder("0").append(month).toString();
        } else {
            return month;
        }
    }
}