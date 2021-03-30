package org.abf.bps.lib.part;

import org.abf.bps.lib.dto.entry.PartSequence;

/**
 * Produces
 */
public interface PartsProducer {

    boolean hasNext();

    PartSequence next();
}
