package com.example.fusion1_events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Event implements Serializable {
    private String id;
    private String name;
    private String date;
    private String time;
    private String location;
    private String description;
    private List<String> waitingList;
    private int waitingListMax;
    private int imageResId;
    private String price;
    private String registrationDeadline;
    private String organizer;

    public Event(){
        this.waitingList = new ArrayList<>();
        this.waitingListMax = 0;
    }

    public Event(String id, String name, String date, String time, String location,
                 String description, List<String> waitingList, int waitingListMax, int imageResId,
                 String price, String registrationDeadline, String organizer) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
        this.waitingList = waitingList;
        this.waitingListMax = waitingListMax;
        this.imageResId = imageResId;
        this.price = price;
        this.registrationDeadline = registrationDeadline;
        this.organizer = organizer;
    }

    //getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getWaitingList() { return waitingList; }
    public void setWaitingList(List<String> waitingList) { this.waitingList = waitingList; }

    public int getWaitingListMax() { return waitingListMax; }

    public void setWaitingListMax(int waitingListMax) { this.waitingListMax = waitingListMax; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getRegistrationDeadline() { return registrationDeadline; }
    public void setRegistrationDeadline(String registrationDeadline) { this.registrationDeadline = registrationDeadline; }

    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }

    public int getTotalEntrants() { return waitingList.size(); }

    // get the size of the waiting list
    public int getWaitingListSize() {
        if (waitingList != null) {
            return waitingList.size();
        } else {
            return 0;
        }
    }
    public void addEntrant(String name) {
        if (!waitingList.contains(name) && waitingList.size() < getWaitingListMax()) {
            waitingList.add(name);
        }
    }
}
