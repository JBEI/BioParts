package org.abf.bps.lib.parsers.genbank;

/**
 * @author Hector Plahar
 */
public class AccessionTag extends Tag {

    public AccessionTag(String value) {
        super(Type.ACCESSION);
        this.setValue(value);
    }
}
