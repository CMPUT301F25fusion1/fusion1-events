package com.example.fusion1_events;
import java.util.ArrayList;
import java.util.Date;
import com.google.firebase.firestore.PropertyName;


/**
 * Model class representing an event with its details and participant lists.
 * Stores event information including title, dates, attendees, waiting list, and image URL.
 */
public class EventsModel {
    String eventTitle;
    Date registrationStart;
    Date registrationEnd;
    String eventDescription;
    Date date;
    Long attendees;
    Long signups;
    ArrayList<String> waitingList;
    ArrayList<String> invitedList;
    ArrayList<String> cancelled;
    ArrayList<String> confirmed;


    String imageUrl; // Store Cloudinary URL for event image
    String eventId;
    Long maxWaitList;
    ArrayList<String> selectedTags;
    private boolean geolocationRequired;

    // Sports, Chill, Party, Seasonal, Educational,
    /**
     * Constructor to create an EventsModel with all fields.
     *
     * @param eventTitle The title of the event
     * @param registrationStart The date when registration opens
     * @param registrationEnd The date when registration closes
     * @param eventDescription A description of the event
     * @param date The date when the event takes place
     * @param attendees The maximum number of attendees allowed
     * @param signups The current number of signups
     * @param waitingList List of entrant IDs on the waiting list
     * @param imageUrl The Cloudinary URL for the event image
     * @param eventId The Firestore document ID
     * @param invitedList List of entrant IDs selected for the event
     * @param maxWaitList The maximum number of entrants on the waiting list
     * @param cancelled List of entrant IDs who were cancelled
     * @param confirmed List of entrant IDs who are in the final confirmed list
     */

    public EventsModel(String eventTitle,ArrayList<String> selectedTags, Date registrationStart, Date registrationEnd,
                       String eventDescription, Date date, Long attendees, Long signups,
                       ArrayList<String> waitingList, String imageUrl, String eventId,
                       ArrayList<String> invitedList, Long maxWaitList,ArrayList<String> cancelled, ArrayList<String> confirmed, boolean geolocationRequired) {
        this.eventTitle = eventTitle;
        this.selectedTags = selectedTags != null ? selectedTags : new ArrayList<>();
        this.registrationStart = registrationStart;
        this.registrationEnd = registrationEnd;
        this.eventDescription = eventDescription;
        this.date = date;
        this.attendees = attendees;
        this.signups = signups;
        this.waitingList = waitingList != null ? waitingList : new ArrayList<>();
        this.imageUrl = imageUrl;
        this.eventId = eventId;
        this.invitedList = invitedList != null ? invitedList : new ArrayList<>();
        this.maxWaitList = maxWaitList;
        this.cancelled = cancelled != null ? cancelled : new ArrayList<>();
        this.confirmed = confirmed != null ? confirmed : new ArrayList<>();
        this.geolocationRequired = geolocationRequired;
    }

    /**
     * Returns the list of cancelled entrant IDs.
     * If the list is null, it initializes an empty list to avoid null references.
     *
     * @return A non-null list of cancelled entrant IDs.
     */
    public ArrayList<String> getCancelled() {
        if (cancelled == null) cancelled = new ArrayList<>();
        return cancelled;
    }

    /**
     * Sets the list of cancelled entrants for the event.
     * If the provided list is null, an empty list is assigned instead.
     *
     * @param cancelled The list of cancelled entrant IDs, or null to reset to an empty list.
     */
    public void setCancelled(ArrayList<String> cancelled) {
        this.cancelled = cancelled != null ? cancelled : new ArrayList<>();
    }

    /**
     * Returns the list of confirmed entrant IDs.
     * Ensures the list is never null by creating an empty list when necessary.
     *
     * @return A non-null list of confirmed entrant IDs.
     */
    public ArrayList<String> getConfirmed() {
        if (confirmed == null) confirmed = new ArrayList<>();
        return confirmed;
    }

    /**
     * Sets the list of confirmed entrants for the event.
     * If null is passed, the method initializes an empty list instead.
     *
     * @param confirmed The list of confirmed entrant IDs, or null to reset to an empty list.
     */
    public void setConfirmed(ArrayList<String> confirmed) {
        this.confirmed = confirmed != null ? confirmed : new ArrayList<>();
    }

    /**
     * Returns the list of selected keyword tags associated with the event.
     *
     * @return A list of keyword tags.
     */
    public ArrayList<String> getSelectedTags(){
        return this.selectedTags;
    }

    /**
     * Sets the list of keyword tags associated with the event.
     *
     * @param selectedTags The list of selected tags.
     */
    public void setSelectedTags(ArrayList<String> selectedTags){
        this.selectedTags = selectedTags;
    }

    /**
     * Returns the maximum allowed size for the event's waiting list.
     *
     * @return The maximum waiting list size.
     */
    public Long getMaxWaitList() {
        return maxWaitList;
    }

    /**
     * Sets the maximum allowed size for the waiting list.
     *
     * @param maxWaitList The maximum number of people allowed in the waiting list.
     */
    public void setMaxWaitList(Long maxWaitList) {
        this.maxWaitList = maxWaitList;
    }

    /**
     * Gets the event title.
     *
     * @return The title of the event
     */
    public String getEventTitle() {
        return eventTitle;
    }

    /**
     * Sets the event title.
     *
     * @param eventTitle The title to set
     */
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    /**
     * Gets the registration start date.
     *
     * @return The date when registration opens
     */
    public Date getRegistrationStart() {
        return registrationStart;
    }

    /**
     * Sets the registration start date.
     *
     * @param registrationStart The date when registration opens
     */
    public void setRegistrationStart(Date registrationStart) {
        this.registrationStart = registrationStart;
    }

    /**
     * Gets the registration end date.
     *
     * @return The date when registration closes
     */
    public Date getRegistrationEnd() {
        return registrationEnd;
    }

    /**
     * Sets the registration end date.
     *
     * @param registrationEnd The date when registration closes
     */
    public void setRegistrationEnd(Date registrationEnd) {
        this.registrationEnd = registrationEnd;
    }

    /**
     * Gets the event description.
     *
     * @return The description of the event
     */
    public String getEventDescription() {
        return eventDescription;
    }

    /**
     * Sets the event description.
     *
     * @param eventDescription The description to set
     */
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    /**
     * Gets the event date.
     *
     * @return The date when the event takes place
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the event date.
     *
     * @param date The date when the event takes place
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets the maximum number of attendees.
     *
     * @return The maximum number of attendees allowed
     */
    public Long getAttendees() {
        return attendees;
    }

    /**
     * Sets the maximum number of attendees.
     *
     * @param attendees The maximum number of attendees allowed
     */
    public void setAttendees(Long attendees) {
        this.attendees = attendees;
    }

    /**
     * Gets the current number of signups.
     *
     * @return The current number of signups
     */
    public Long getSignups() {
        return signups;
    }

    /**
     * Sets the current number of signups.
     *
     * @param signups The current number of signups
     */
    public void setSignups(Long signups) {
        this.signups = signups;
    }

    /**
     * Gets the waiting list of entrant IDs.
     *
     * @return ArrayList of entrant IDs on the waiting list
     */
    public ArrayList<String> getWaitingList() {
        return waitingList;
    }

    /**
     * Sets the waiting list.
     *
     * @param waitingList ArrayList of entrant IDs to set as the waiting list
     */
    public void setWaitingList(ArrayList<String> waitingList) {
        this.waitingList = waitingList;
    }

    /**
     * Adds an entrant to the waiting list if they are not already present.
     * Prevents duplicate entries in the waiting list.
     *
     * @param entrantId The ID of the entrant to add
     */
    public void addToWaitingList(String entrantId) {
        if (!waitingList.contains(entrantId)) {
            waitingList.add(entrantId);
        }
    }

    /**
     * Removes an entrant from the waiting list.
     *
     * @param entrantId The ID of the entrant to remove
     */
    public void removeFromWaitingList(String entrantId) {
        waitingList.remove(entrantId);
    }

    /**
     * Checks if an entrant is in the waiting list.
     *
     * @param entrantId The ID of the entrant to check
     * @return true if the entrant is in the waiting list, false otherwise
     */
    public boolean isInWaitingList(String entrantId) {
        return waitingList.contains(entrantId);
    }

    /**
     * Gets the size of the waiting list.
     *
     * @return The number of entrants in the waiting list
     */
    public int getWaitingListSize() {
        return waitingList.size();
    }

    /**
     * Gets the final list of selected entrant IDs.
     *
     * @return ArrayList of entrant IDs selected for the event
     */
    public ArrayList<String> getInvitedList() {
        return invitedList;
    }

    /**
     * Sets the final list of selected entrants.
     *
     * @param invitedList ArrayList of entrant IDs selected for the event
     */
    public void setInvitedList(ArrayList<String> invitedList) {
        this.invitedList = invitedList;
    }

    /**
     * Gets the Cloudinary URL for the event image.
     *
     * @return The URL of the event image
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the Cloudinary URL for the event image.
     *
     * @param imageUrl The URL of the event image
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the Firestore document ID.
     *
     * @return The Firestore document ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the Firestore document ID.
     *
     * @param eventId The Firestore document ID
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Returns whether geolocation is required for this event.
     *
     * @return true if geolocation is required, false otherwise
     */
    public boolean isGeolocationRequired() {
        return geolocationRequired;
    }

    /**
     * Sets whether geolocation is required for this event.
     *
     * @param geolocationRequired true to require location verification, false otherwise
     */
    public void setGeolocationRequired(boolean geolocationRequired) {
        this.geolocationRequired = geolocationRequired;
    }
}
