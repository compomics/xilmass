/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import com.compomics.util.protein.Protein;
import crossLinker.CrossLinker;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author Sule
 */
public class Find_LinkerPosition {

    private static final Logger LOGGER = Logger.getLogger(Find_LinkerPosition.class);

    /**
     * This method finds a list of possible linking sites for given peptide
     * according to the given cross-linker
     *
     * @param protein
     * @param firstPart is first group or not (carboxyl-to-amine is the first
     * part is assumed to carboxyl)
     * @param crossLinker
     * @param doesContainProteinNtermini true: a protein has n-termini; false: a
     * protein has NOT n-termini
     * @param doesContainProteinCTermini true: a protein has c-termini; false: a
     * protein has NOT c-termini
     * @return
     */
    public static ArrayList<LinkedResidue> find_cross_linking_sites(Protein protein, boolean firstPart, CrossLinker crossLinker, boolean doesContainProteinNtermini, boolean doesContainProteinCTermini) {
        ArrayList<LinkedResidue> crossLinker_and_indices = new ArrayList<LinkedResidue>();
        switch (crossLinker.getName()) {
            case BS3d0:
                crossLinker_and_indices = get_amine_groups(protein, doesContainProteinNtermini, doesContainProteinCTermini);
                break;
            case BS3d4:
                crossLinker_and_indices = get_amine_groups(protein, doesContainProteinNtermini, doesContainProteinCTermini);
                break;
            case DSSd0:
                crossLinker_and_indices = get_amine_groups(protein, doesContainProteinNtermini, doesContainProteinCTermini);
                break;
            case DSSd12:
                crossLinker_and_indices = get_amine_groups(protein, doesContainProteinNtermini, doesContainProteinCTermini);
                break;
            case GA:
                crossLinker_and_indices = get_amine_groups(protein, doesContainProteinNtermini, doesContainProteinCTermini);
                break;
            case EDC:
                if (firstPart) {
                    crossLinker_and_indices = get_carboxyl_groups(protein, doesContainProteinNtermini, doesContainProteinCTermini);
                } else {
                    crossLinker_and_indices = get_amine_groups(protein, doesContainProteinNtermini, doesContainProteinCTermini);
                }
                break;
        }
        return crossLinker_and_indices;
    }

    /**
     * A list of linked residues that contains carboxyl groups in order to
     * conjugate: These are glutamate (E), aspartate (D) and protein C-termini
     *
     * @param protein
     * @param doesContainProteinNTermini
     * @param doesContainProteinCTermini
     * @return
     */
    public static ArrayList<LinkedResidue> get_carboxyl_groups(Protein protein, boolean doesContainProteinNTermini, boolean doesContainProteinCTermini) {
        String sequence = protein.getSequence().getSequence();
        ArrayList<LinkedResidue> linkedRes = new ArrayList<LinkedResidue>();
        for (int position = 0; position < sequence.length(); position++) {
            LinkedResidue r = null;
            char charAt = sequence.charAt(position);
            if (charAt == 'E' && position != sequence.length() - 1) {
                r = new LinkedResidue(protein, position, LinkedResidueType.E, doesContainProteinNTermini, doesContainProteinCTermini);
            } else if (charAt == 'D' && position != sequence.length() - 1) {
                r = new LinkedResidue(protein, position, LinkedResidueType.D, doesContainProteinNTermini, doesContainProteinCTermini);
            } else if (position == sequence.length() - 1) {
                r = new LinkedResidue(protein, position, LinkedResidueType.CTerminus, doesContainProteinNTermini, doesContainProteinCTermini);
            }
            if (r != null) {
                linkedRes.add(r);
            }
        }
        LOGGER.info("This function is not fully checked, yet!");
        return linkedRes;
    }

    /**
     * A list of linked residues that contains amine groups in order to
     * conjugate: These are lysin (K) and protein N-termini
     *
     * @param protein
     * @param doesContainProteinNTermini
     * @param doesContainProteinCTermini
     * @return
     */
    public static ArrayList<LinkedResidue> get_amine_groups(Protein protein, boolean doesContainProteinNTermini, boolean doesContainProteinCTermini) {
        boolean isMethionineFirstResidue = false;
        String sequence = protein.getSequence().getSequence();
        if (sequence.startsWith("M")) {
            isMethionineFirstResidue = true;
        }
        ArrayList<LinkedResidue> linkedRes = new ArrayList<LinkedResidue>();
        for (int position = 0; position < sequence.length(); position++) {
            LinkedResidue r = null;
            char charAt = sequence.charAt(position);
            // not including c-terminus.. (Rinner et al, 2008)
            if (charAt == 'K' && position != sequence.length() - 1) {
                r = new LinkedResidue(protein, position, LinkedResidueType.K, doesContainProteinNTermini, doesContainProteinCTermini);
            } else if (position == 0 && doesContainProteinNTermini && !isMethionineFirstResidue) {
                r = new LinkedResidue(protein, position, LinkedResidueType.NTerminus, doesContainProteinNTermini, doesContainProteinCTermini);
            } else if (position == 0 && doesContainProteinNTermini && isMethionineFirstResidue) {
                r = new LinkedResidue(protein, position, LinkedResidueType.M, doesContainProteinNTermini, doesContainProteinCTermini);
                // nterminal m excision is essential and most of the time M is cleaved, the second residue is checked.. (Frottin, 2006)
            } else if (position == 1 && doesContainProteinNTermini && isMethionineFirstResidue) {
                r = new LinkedResidue(protein, position, LinkedResidueType.NTerminiIncludesM, doesContainProteinNTermini, doesContainProteinCTermini);
            }
            if (r != null) {
                linkedRes.add(r);
            }
        }
        return linkedRes;
    }
}
