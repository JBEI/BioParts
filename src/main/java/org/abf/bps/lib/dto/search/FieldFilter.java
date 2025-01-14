package org.abf.bps.lib.dto.search;

import org.abf.bps.lib.dto.IDataTransferModel;
import org.abf.bps.lib.dto.entry.EntryField;

/**
 * Filter a specified entry field
 *
 * @author Hector Plahar
 */
public class FieldFilter implements IDataTransferModel {

    private EntryField field;
    private String filter;

    public EntryField getField() {
        return field;
    }

    public void setField(EntryField field) {
        this.field = field;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
