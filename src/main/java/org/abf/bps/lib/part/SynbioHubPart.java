package org.abf.bps.lib.part;

public class SynbioHubPart {

    private String type;
    private String uri;
    private String displayId;
    private String name;
    private String description;
    private String version;
    private String percentMatch;
    private String strandAlignment;

    public SynbioHubPart() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDisplayId() {
        return displayId;
    }

    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
