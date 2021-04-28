package org.abf.bps.lib.search.blast;

// todo : move to properties file / database
public class Constants {

    // location of data directory
    public static final String DATA_DIR = "/var/lib/bp";

    // blast installation location (must point to the "bin" folder)
    public static final String BLAST_INSTALL = "/usr/local/ncbi/blast/bin";  // = ";

    public static final String REGISTRY_TOKEN = "";

    public static final String REGISTRY_TOKEN_CLIENT = "bioparts.org";

    public static final String REGISTRY_TOKEN_OWNER = "haplahar@lbl.gov";

    public static final String ADDGENE_URL_PREFIX = "https://www.addgene.org";

    // url prefix for retrieving the part information from IGEM in xml format
    public static final String IGEM_XML_PART_URL_PREFIX = "http://parts.igem.org/cgi/xml/part.cgi?part=";

    public static final String IGEM_PART_URL_PREFIX = "http://parts.igem.org";

    public static final String NCBI_SEARCH_URL = "https://www.ncbi.nlm.nih.gov/search/api/sequence"; ///AC163214/?report=fasta

    public static final String MASTER_REGISTRY_URL = "registry.jbei.org";
}
