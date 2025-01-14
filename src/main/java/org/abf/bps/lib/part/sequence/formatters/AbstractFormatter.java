package org.abf.bps.lib.part.sequence.formatters;

import org.abf.bps.lib.dto.FeaturedDNASequence;
import org.biojavax.Namespace;
import org.biojavax.SimpleNamespace;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Abstract formatter implementing {@link IFormatter} interface.
 *
 * @author Zinovii Dmytriv
 */
public class AbstractFormatter implements IFormatter {
    public static final String DEFAULT_NAMESPACE = "org.jbei";

    @Override
    /**
     * Format the {@link Sequence} and output to the {@link OutputStream}.
     */
    public void format(FeaturedDNASequence sequence, OutputStream outputStream) throws FormatterException, IOException {
        throw new UnsupportedOperationException("Unsupported");
    }

    public Namespace getNamespace() {
        return new SimpleNamespace(DEFAULT_NAMESPACE);
    }
}
