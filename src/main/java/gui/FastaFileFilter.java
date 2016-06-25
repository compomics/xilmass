package gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.io.FilenameUtils;

/**
 * A file filter for FASTA files.
 *
 * @author Niels Hulstaert
 */
public class FastaFileFilter extends FileFilter {

    public static final String FASTA_EXTENSION = "fasta";
    public static final String FASTA_EXTENSION_CAPS = "FASTA";
    private static final String DESCRIPTION = "*.fasta, *.FASTA";

    @Override
    public boolean accept(File file) {
        boolean accept = false;

        if (file.isFile()) {
            String extension = FilenameUtils.getExtension(file.getName());
            if (!extension.isEmpty() && (extension.equals(FASTA_EXTENSION) || extension.equals(FASTA_EXTENSION_CAPS))) {
                accept = true;
            }
        } else {
            accept = true;
        }

        return accept;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

}
