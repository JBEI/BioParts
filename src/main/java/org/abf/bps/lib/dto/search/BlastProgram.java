package org.abf.bps.lib.dto.search;

import org.abf.bps.lib.dto.IDataTransferModel;

/**
 * Types of blast programs that this system supports for nucleotide search
 *
 * @author Hector Plahar
 */
public enum BlastProgram implements IDataTransferModel {

    BLAST_N("blastn"),
    TBLAST_X("tblastx");

    private String name;

    BlastProgram(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
