package org.abf.bps.lib.parsers.genbank;

/**
 * @author Hector Plahar
 */
public class OriginTag extends Tag {

    public OriginTag() {
        super(Type.ORIGIN);
    }

    private String sequence;

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
}
