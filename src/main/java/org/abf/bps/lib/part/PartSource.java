package org.abf.bps.lib.part;

/**
 * Source of a biological part on the interwebs e.g. <code>Add Gene</code>
 *
 * @author Hector Plahar
 */
public class PartSource {

    private final String url;
    private final String name;
    private final String id;

    public PartSource(String url, String name, String id) {
        this.url = url;
        this.name = name;
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
