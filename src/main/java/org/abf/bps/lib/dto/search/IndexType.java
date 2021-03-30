package org.abf.bps.lib.dto.search;

import org.abf.bps.lib.dto.IDataTransferModel;

/**
 * Represents the different kinds of indexes available
 *
 * @author Hector Plahar
 */
public enum IndexType implements IDataTransferModel {

    BLAST,
    LUCENE
}
