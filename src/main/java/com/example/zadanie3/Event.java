package com.example.zadanie3;

import java.util.List;

public class Event {
    private List<String> dates;
    private List<String> eventNames;
    private String month;


    public Event(List<String> dates, List<String> eventNames, String month) {
        this.dates = dates;
        this.eventNames = eventNames;
        this.month = month;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public List<String> getEventNames() {
        return eventNames;
    }

    public void setEventNames(List<String> eventNames) {
        this.eventNames = eventNames;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
