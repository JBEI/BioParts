package org.abf.bps.lib.index;

/**
 * Fields that can be indexed
 */
public enum IndexField {
    ID,
    RECORD_ID,
    TYPE,
    PART_ID,
    NAME,
    SUMMARY,
    CREATED,
    REFERENCES,

    ALIAS,
    KEYWORDS,
    SOURCE_NAME,
    SOURCE_URL,
    SOURCE_ID,

    HAS_SEQUENCE,
    HAS_SAMPLE;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
