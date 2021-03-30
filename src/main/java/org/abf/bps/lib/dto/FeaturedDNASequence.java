package org.abf.bps.lib.dto;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a DNA sequence that has features
 *
 * @author Hector Plahar
 */
public class FeaturedDNASequence implements IDataTransferModel {

    private List<DNAFeature> features;
    private String name;
    private boolean isCircular;
    private String description;
    private long length;
    private String organism;
    private String sequence;
    private String authors;
    private String identifier;
    private String date;

    public FeaturedDNASequence() {
        this.features = new LinkedList<>();
        this.name = "";
        this.isCircular = true;
    }

    public List<DNAFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<DNAFeature> features) {
        this.features = features;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCircular() {
        return isCircular;
    }

    public void setIsCircular(boolean isCircular) {
        this.isCircular = isCircular;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        this.organism = organism;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
