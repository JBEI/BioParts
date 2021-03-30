package org.abf.bps.lib.scrape.igem;

import org.abf.bps.lib.common.logging.Logger;
import org.abf.bps.lib.dto.entry.PartSequence;
import org.abf.bps.lib.scrape.IgemParts;
import org.abf.bps.lib.search.blast.Constants;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

public class IGemTeams implements Closeable {

    private final int STARTING_YEAR = 2005;
    private PartIDsFile partIDsFile;

    public IGemTeams() throws IOException {
        partIDsFile = new PartIDsFile();
    }

    private Map<String, String> getUrlParams(String url) {
        Map<String, String> params = new HashMap<>();
        try {
            List<NameValuePair> teamParams = new URIBuilder(url).getQueryParams();
            for (NameValuePair pair : teamParams) {
                params.put(pair.getName(), pair.getValue());
            }
        } catch (Exception e) {
            Logger.error(e);
        }
        return params;
    }

    private void parse() throws Exception {
        String iGemTeamsUrl = Constants.IGEM_TEAMS_URL;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int start = STARTING_YEAR;

        while (start < year) {
            // generate url for iGems team for specified year
            final String url = iGemTeamsUrl + start;

            // parse the teams page
            parseTeams(url);

            start++;
        }
    }

    // parse teams from difference countries by specified year (in URL)
    private void parseTeams(String url) throws Exception {
        System.out.println("Processing page " + url);
        Document document = Jsoup.connect(url).get();
        Elements ulElements = document.select("table tbody tr td div a");

        // get links to each part
        for (String attr : ulElements.eachAttr("href")) {

            // process list of parts page
            getPageOfResults(attr);
            Thread.sleep(1000);
        }
    }

    // retrieve list of details of team parts (for specified year)
    // eg link is http://parts.igem.org/cgi/partsdb/pgroup.cgi?pgroup=iGEM2005&group=Cambridge
    private void getPageOfResults(String url) throws Exception {
        url = url.replaceAll("\\s+", "_");
        Map<String, String> params = this.getUrlParams(url);
        String iGemYear = params.get("pgroup");
        String group = params.get("group");
        if (group != null)
            group = group.replaceAll("_", " ");

        System.out.println("Processing '" + iGemYear + "' for group '" + group + "'");

        List<String> attrs = Jsoup.connect(url).get().select("table a.part_link").eachAttr("href");
        int count = 0;
        for (String attribute : attrs) {
            Map<String, String> titleParams = this.getUrlParams(attribute); // title=Part:BBa_...
            String title = titleParams.get("title");
            String[] split = title.split(":");
            if (split.length < 2) {
                Logger.info("Skipping title: " + title);
                continue;
            }

            String partId = split[1];
            writePartId(partId, iGemYear, group);
            count += 1;
        }

        System.out.println("Processing " + count + " parts ");
    }

    private void writePartId(String partId, String... otherInfo) {
        try {
            partIDsFile.writePartId(partId, otherInfo);
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    public static void main(String[] args) throws Exception {
//        try (IGemTeams teams = new IGemTeams()) {
//            teams.parse();
//        }

        try (PartIDsFile file = new PartIDsFile()) {
            Iterator<String[]> iterator = file.iterator();
            iterator.forEachRemaining(strings -> {
                String partId = strings[0];
                try {
                    PartSequence partSequence = IgemParts.parseIgemPart(partId);
                    System.out.println(partId);
                } catch (Exception e) {
                    Logger.error(e);
                }
            });
        }
    }

    @Override
    public void close() throws IOException {
        if (partIDsFile == null)
            return;

        partIDsFile.close();
    }
}
