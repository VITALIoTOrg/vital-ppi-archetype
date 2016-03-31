package eu.vital.ppi.utils;

/**
 * Created by f.deceglia on 26/03/2015.
 */

public class EventHelper {

    private int id;
    private String resource;

    public EventHelper(int id, String resource) {
        this.id = id;
        this.resource = resource;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override
    public boolean equals(Object o) {
        EventHelper eventHelper;

        if(o.getClass() == EventHelper.class) {
            eventHelper = (EventHelper) o;
            return this.id == eventHelper.getId() && this.resource.equals(eventHelper.getResource());
        }

        return false;
    }
}
