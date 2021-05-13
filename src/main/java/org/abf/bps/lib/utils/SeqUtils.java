package org.abf.bps.lib.utils;

import org.abf.bps.lib.common.logging.Logger;
import org.abf.bps.lib.dto.DNAFeature;
import org.abf.bps.lib.dto.DNAFeatureLocation;
import org.abf.bps.lib.dto.DNAFeatureNote;
import org.abf.bps.lib.dto.FeaturedDNASequence;
import org.apache.commons.lang3.StringUtils;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.features.FeatureInterface;
import org.biojava.nbio.core.sequence.features.Qualifier;
import org.biojava.nbio.core.sequence.io.GenbankReaderHelper;
import org.biojava.nbio.core.sequence.location.template.AbstractLocation;
import org.biojava.nbio.core.sequence.template.AbstractSequence;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SeqUtils {

    /**
     * Converts a dna sequence to system internal model
     *
     * @param dnaSequence sequence to convert
     * @return converted sequence or null on exception
     */
    public static FeaturedDNASequence convert(DNASequence dnaSequence) {
        if (dnaSequence == null)
            return null;

        FeaturedDNASequence featuredDNASequence = new FeaturedDNASequence();
        featuredDNASequence.setSequence(dnaSequence.getSequenceAsString());
        featuredDNASequence.setDescription(dnaSequence.getDescription());
        featuredDNASequence.setName(dnaSequence.getAccession().toString());

        // parse header
        String[] split = dnaSequence.getOriginalHeader().split("\\s+");
        if (split.length == 7) {
            featuredDNASequence.setIdentifier(split[0].trim());
            featuredDNASequence.setIsCircular(split[4].trim().equalsIgnoreCase("circular"));
            featuredDNASequence.setDate(split[6]);
        }

        // convert features
        List<FeatureInterface<AbstractSequence<NucleotideCompound>, NucleotideCompound>> features = dnaSequence.getFeatures();
        for (FeatureInterface<AbstractSequence<NucleotideCompound>, NucleotideCompound> feature : features) {
//            feature.getDescription(); // ?
            DNAFeature dnaFeature = new DNAFeature();
            dnaFeature.setType(feature.getType());          // type

            // location
            AbstractLocation locations = feature.getLocations();
            DNAFeatureLocation location = new DNAFeatureLocation();
            location.setGenbankStart(locations.getStart().getPosition());
            location.setEnd(locations.getEnd().getPosition());
            dnaFeature.getLocations().add(location);

            // strand
            dnaFeature.setStrand(locations.getStrand().getNumericRepresentation());

            // notes (same as qualifiers)
            Map<String, List<Qualifier>> qualifiers = feature.getQualifiers();
            for (String key : qualifiers.keySet()) {
                List<Qualifier> list = qualifiers.get(key);
                if (list != null) {
                    for (Qualifier qualifier : list) {
                        if ("label".equalsIgnoreCase(qualifier.getName()) && StringUtils.isBlank(dnaFeature.getName()))
                            dnaFeature.setName(qualifier.getValue());

                        // todo : else ??
                        DNAFeatureNote note = new DNAFeatureNote(qualifier.getName(), qualifier.getValue());
                        note.setQuoted(qualifier.needsQuotes());
                        dnaFeature.getNotes().add(note);
                    }
                }
            }

            featuredDNASequence.getFeatures().add(dnaFeature);
        }

        return featuredDNASequence;
    }

    public static DNASequence parseGenbank(InputStream inputStream) {
        try (inputStream) {
            LinkedHashMap<String, DNASequence> sequences = GenbankReaderHelper.readGenbankDNASequence(inputStream);
            for (DNASequence sequence : sequences.values())
                return sequence;
        } catch (Exception e) {
            Logger.error(e);
        }
        return null;
    }
}
