package org.abf.bps.lib.index;

import org.abf.bps.lib.common.logging.Logger;
import org.abf.bps.lib.dto.entry.EntryType;
import org.abf.bps.lib.dto.entry.PartData;
import org.abf.bps.lib.dto.search.BlastQuery;
import org.abf.bps.lib.dto.search.SearchQuery;
import org.abf.bps.lib.dto.search.SearchResult;
import org.abf.bps.lib.dto.search.SearchResults;
import org.abf.bps.lib.dto.web.RegistryPartner;
import org.abf.bps.lib.search.QueryType;
import org.abf.bps.lib.search.blast.BlastException;
import org.abf.bps.lib.search.blast.BlastPlus;
import org.abf.bps.lib.search.blast.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the sequence and text search indexes
 *
 * @author Hector Plahar
 */
public class SearchIndex {

    protected static String cleanQuery(String query) {
        if (query == null)
            return null;

        String cleanedQuery = query;
        cleanedQuery = cleanedQuery.replace(":", " ");
        cleanedQuery = cleanedQuery.replace(";", " ");
        cleanedQuery = cleanedQuery.replace("\\", " ");
        cleanedQuery = cleanedQuery.replace("/", " ");
        cleanedQuery = cleanedQuery.replace("!", " ");
        cleanedQuery = cleanedQuery.replace("[", "\\[");
        cleanedQuery = cleanedQuery.replace("]", "\\]");
        cleanedQuery = cleanedQuery.replace("{", "\\{");
        cleanedQuery = cleanedQuery.replace("}", "\\}");
        cleanedQuery = cleanedQuery.replace("(", "\\(");
        cleanedQuery = cleanedQuery.replace(")", "\\)");
        cleanedQuery = cleanedQuery.replace("+", "\\+");
        cleanedQuery = cleanedQuery.replace("-", "\\-");
        cleanedQuery = cleanedQuery.replace("'", "\\'");
//        cleanedQuery = cleanedQuery.replace("\"", "\\\"");
        cleanedQuery = cleanedQuery.replace("^", "\\^");
        cleanedQuery = cleanedQuery.replace("&", "\\&");

        cleanedQuery = cleanedQuery.endsWith("'") ? cleanedQuery.substring(0, cleanedQuery.length() - 1) : cleanedQuery;
        cleanedQuery = (cleanedQuery.endsWith("\\") ? cleanedQuery.substring(0, cleanedQuery.length() - 1) : cleanedQuery);
        return cleanedQuery;
    }

    private final Path indexPath;
    private static final String LUCENE_INDEX_FOLDER_NAME = "lucene";

    public SearchIndex() throws IOException {
        this.indexPath = Paths.get(Constants.DATA_DIR, LUCENE_INDEX_FOLDER_NAME);
        Logger.info("Using index path: " + this.indexPath);
        if (!Files.exists(this.indexPath)) {
            Files.createDirectory(this.indexPath);
        }
    }

    public SearchResult getByRecordId(String recordId) {
        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(this.indexPath));
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs results = searcher.search(new TermQuery(new Term(IndexField.RECORD_ID.toString(), recordId)), 5);

            ScoreDoc[] hits = results.scoreDocs;
            int numTotalHits = Math.toIntExact(results.totalHits.value);
            if (numTotalHits == 0)
                return null;

            Document doc = searcher.storedFields().document(hits[0].doc);
            return getResultFromDocument(doc);
        } catch (Exception e) {
            Logger.error(e);
        }
        return null;
    }

    private SearchResults runBlast(BlastQuery blastQuery, int limit) {
        if (blastQuery == null || blastQuery.getSequence().isEmpty())
            throw new IllegalArgumentException("Cannot run blast with no sequence in query");

        try {
            BlastPlus blastPlus = new BlastPlus();
            HashMap<String, SearchResult> results = blastPlus.runBlast(blastQuery);
            SearchResults searchResults = new SearchResults();
            searchResults.setResultCount(results.size());

            for (Map.Entry<String, SearchResult> entry : results.entrySet()) {
                searchResults.getResults().add(entry.getValue());
                if (--limit <= 0)
                    break;
            }

            return searchResults;
        } catch (IOException | BlastException e) {
            Logger.error(e);
            return null;
        }
    }

    public SearchResults find(SearchQuery query) {
        SearchResults searchResults = new SearchResults();
        int start = query.getParameters().getStart();
        int limit = query.getParameters().getRetrieveCount();

        if (query.hasBlastQuery())
            return runBlast(query.getBlastQuery(), limit);

        try {
            HashMap<String, QueryType> queries = parseQueryString(query.getQueryString());
            if (queries.isEmpty()) {
                return searchResults;
            }

            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            String queryString;

            for (Map.Entry<String, QueryType> entry : queries.entrySet()) {
                queryString = cleanQuery(entry.getKey()).toLowerCase();
                if (queryString.trim().isEmpty())
                    continue;

                builder.add(new TermQuery(new Term(IndexField.PART_ID.toString(), queryString)), BooleanClause.Occur.SHOULD);
                builder.add(new TermQuery(new Term(IndexField.NAME.toString(), queryString)), BooleanClause.Occur.SHOULD);
                builder.add(new TermQuery(new Term(IndexField.ALIAS.toString(), queryString)), BooleanClause.Occur.SHOULD);
                builder.add(new TermQuery(new Term(IndexField.KEYWORDS.toString(), queryString)), BooleanClause.Occur.SHOULD);
                builder.add(new TermQuery(new Term(IndexField.REFERENCES.toString(), queryString)), BooleanClause.Occur.SHOULD);
                builder.add(new TermQuery(new Term(IndexField.SOURCE_URL.toString(), queryString)), BooleanClause.Occur.SHOULD);
                builder.add(new TermQuery(new Term(IndexField.SOURCE_NAME.toString(), queryString)), BooleanClause.Occur.SHOULD);
                builder.add(new TermQuery(new Term(IndexField.PART_ID.toString(), queryString)), BooleanClause.Occur.SHOULD);
                builder.add(new TermQuery(new Term(IndexField.SUMMARY + "_token", queryString)), BooleanClause.Occur.SHOULD);
                builder.add(new TermQuery(new Term(IndexField.NAME + "_token", queryString)), BooleanClause.Occur.SHOULD);
            }

            if (query.getParameters().getHasSequence()) {
                builder.add(new TermQuery(new Term(IndexField.HAS_SEQUENCE.toString(), "true")), BooleanClause.Occur.MUST);
            }

            // retrieve via source
            if (query.getSourceFilters() != null && !query.getSourceFilters().isEmpty()) {
                // todo : use "or" for entire list
                builder.add(new TermQuery(new Term(IndexField.SOURCE_URL.toString(), query.getSourceFilters().get(0))), BooleanClause.Occur.SHOULD);
            }

            IndexReader reader = DirectoryReader.open(FSDirectory.open(this.indexPath));
            IndexSearcher searcher = new IndexSearcher(reader);

            // return top "n" (start + limit); provide hard limit ??
            TopDocs results = searcher.search(builder.build(), start + limit);

            ScoreDoc[] hits = results.scoreDocs;
            int numTotalHits = Math.toIntExact(results.totalHits.value);
            searchResults.setResultCount(results.totalHits.value);

            if (numTotalHits > 0) {
                int end = Math.min(start + limit, numTotalHits);
                for (int i = start; i < end; i += 1) {
                    Document doc = searcher.storedFields().document(hits[i].doc);
                    SearchResult result = getResultFromDocument(doc);
                    result.setMaxScore(hits[0].score);
                    result.setScore(hits[i].score);
                    searchResults.getResults().add(result);
                }
            }
        } catch (Exception e) {
            Logger.error(e);
            return null;
        }

        return searchResults;
    }

    /**
     * Creates the search result from the stored information from the index document
     *
     * @param doc Index document
     * @return search result information including part data and registry where the information is stored
     */
    protected SearchResult getResultFromDocument(Document doc) {
        SearchResult result = new SearchResult();

        //get part data
        PartData partData = new PartData(EntryType.nameToType(doc.get(IndexField.TYPE.toString())));
        partData.setId(Long.decode(doc.get(IndexField.ID.toString())));
        partData.setRecordId(doc.get(IndexField.RECORD_ID.toString()));
        partData.setName(doc.get(IndexField.NAME.toString()));
        partData.setAlias(doc.get(IndexField.ALIAS.toString()));
        partData.setPartId(doc.get(IndexField.PART_ID.toString()));
        partData.setShortDescription(doc.get(IndexField.SUMMARY.toString()));
        partData.setCreationTime(Long.decode(doc.get(IndexField.CREATED.toString())));
        partData.setHasSample("true".equalsIgnoreCase(doc.get(IndexField.HAS_SAMPLE.toString())));
        partData.setHasSequence("true".equalsIgnoreCase(doc.get(IndexField.HAS_SEQUENCE.toString())));
        result.setEntryInfo(partData);

        // get partner
        RegistryPartner partner = new RegistryPartner();
        partner.setUrl(doc.get(IndexField.SOURCE_URL.toString()));
        partner.setName(doc.get(IndexField.SOURCE_NAME.toString()));
        String id = doc.get(IndexField.SOURCE_ID.toString());

        if (!StringUtils.isEmpty(id)) {
            try {
                partner.setId(Long.decode(id));
            } catch (NumberFormatException nfe) {
                // skip id
            }
        }

        result.setPartner(partner);
        return result;
    }

    /**
     * Parses the query string checking for terms and phrases. A quote is used to indicate
     * the boundaries of a phrase
     * <p>e.g. <code>"quick brown" fox "jumped"</code> is parsed into two phrases and one term
     *
     * @param queryString query string to be parsed for phrases and terms
     * @return a mapping of the phrases and terms to clauses that indicate how the matches should appear
     * in the document. Phrases must appear in the result document
     */
    HashMap<String, QueryType> parseQueryString(String queryString) {
        HashMap<String, QueryType> terms = new HashMap<>();

        if (queryString == null || queryString.trim().isEmpty())
            return terms;

        StringBuilder builder = new StringBuilder();
        boolean startedPhrase = false;
        for (int i = 0; i < queryString.trim().length(); i += 1) {
            char c = queryString.charAt(i);
            if (c == '\"' || c == '\'') {
                if (startedPhrase) {
                    terms.put(builder.toString(), QueryType.PHRASE);
                    builder = new StringBuilder();
                    startedPhrase = false;
                } else {
                    startedPhrase = true;
                }
                continue;
            }

            // check for space
            if (c == ' ') {
                if (builder.isEmpty())
                    continue;

                if (!startedPhrase) {
                    terms.put(builder.toString(), QueryType.TERM);
                    builder = new StringBuilder();
                    continue;
                }
            }

            builder.append(c);
        }
        if (!builder.isEmpty()) {
            if (startedPhrase)
                terms.put(builder.toString(), QueryType.PHRASE);
            else
                terms.put(builder.toString(), QueryType.TERM);
        }

        return terms;
    }
}
