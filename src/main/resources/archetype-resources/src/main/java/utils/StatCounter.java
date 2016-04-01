package eu.vital.ppi.utils;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by f.deceglia on 23/03/2015.
 */

public class StatCounter {

    private static AtomicInteger requestNumber = new AtomicInteger(0);
    private static AtomicInteger errorNumber = new AtomicInteger(0);
    private static ArrayList<EventHelper> eventHelpers = new ArrayList<>();


    public static AtomicInteger getRequestNumber() {
        return requestNumber;
    }

    public static AtomicInteger getErrorNumber() {
        return errorNumber;
    }

    public static void setRequestedNumber(int value) {
        requestNumber.set(value);
    }

    public static void incrementErrorNumber() {

        int currentErrorNumber = errorNumber.get();
        errorNumber.set(currentErrorNumber + 1);
    }

    public static synchronized void addEventHelper(EventHelper eventHelper) {
        eventHelpers.add(eventHelper);
    }

    public static synchronized boolean deleteEventHelper(EventHelper eventHelper) {
        try {
            eventHelpers.remove(eventHelper);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static synchronized int getPendingRequest() {
        return eventHelpers.size();
    }
}
