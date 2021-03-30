package org.abf.bps.lib.dto;

import java.util.LinkedList;
import java.util.List;

/**
 * Data transfer object for features associated with a sequence
 *
 * @author Hector Plahar
 */
public class DNAFeature implements IDataTransferModel {

    private long id;
    private String type;
    private String name;
    private int strand;
    private String uri;
    private String identifier;
    private List<DNAFeatureNote> notes = new LinkedList<>();
    private List<DNAFeatureLocation> locations = new LinkedList<>();
    private String sequence;

    public DNAFeature() {
        this.type = "";
        this.name = "";
        this.strand = 1;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DNAFeatureNote> getNotes() {
        return notes;
    }

    public void setNotes(List<DNAFeatureNote> notes) {
        this.notes = notes;
    }

    public int getStrand() {
        return strand;
    }

    public void setStrand(int strand) {
        this.strand = strand;
    }

    public void addNote(DNAFeatureNote dnaFeatureNote) {
        notes.add(dnaFeatureNote);
    }

    public void setLocations(List<DNAFeatureLocation> locations) {
        this.locations = locations;
    }

    public List<DNAFeatureLocation> getLocations() {
        return locations;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
}
