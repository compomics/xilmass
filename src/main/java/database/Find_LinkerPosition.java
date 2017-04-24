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
 * This class finds cross-linking sites
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
     * @param s sequence of a given protein
     * @param firstPart is first group or not (carboxyl-to-amine is the first
     * part is assumed to carboxyl)
     * @param crossLinker
     * @param doesContainProteinNtermini true: a protein has n-termini; false: a
     * protein has NOT n-termini
     * @param doesContainProteinCTermini true: a protein has c-termini; false: a
     * protein has NOT c-termini
     * @param isSideReactionConsidered_S introducing S as linkeable due to
     * consideration of side-reactions
     * @param isSideReactionConsidered_T introducing T as linkeable due to
     * consideration of side-reactions
     * @param isSideReactionConsidered_Y introducing Y as linkeable due to
     * consideration of side-reactions
     * @return
     */
    public static ArrayList<LinkedResidue> find_cross_linking_sites(Protein protein, char[] s, boolean firstPart, CrossLinker crossLinker,
            boolean doesContainProteinNtermini, boolean doesContainProteinCTermini,
            boolean isSideReactionConsidered_S, boolean isSideReactionConsidered_T, boolean isSideReactionConsidered_Y) {
        ArrayList<LinkedResidue> crossLinker_and_indices = new ArrayList<LinkedResidue>();
        switch (crossLinker.getName()) {
            case BS3d0:
                crossLinker_and_indices = get_amine_groups(protein, s, doesContainProteinNtermini, doesContainProteinCTermini,
                        isSideReactionConsidered_S, isSideReactionConsidered_T, isSideReactionConsidered_Y);
                break;
            case BS3d4:
                crossLinker_and_indices = get_amine_groups(protein, s, doesContainProteinNtermini, doesContainProteinCTermini,
                        isSideReactionConsidered_S, isSideReactionConsidered_T, isSideReactionConsidered_Y);
                break;
            case DSSd0:
                crossLinker_and_indices = get_amine_groups(protein, s, doesContainProteinNtermini, doesContainProteinCTermini,
                        isSideReactionConsidered_S, isSideReactionConsidered_T, isSideReactionConsidered_Y);
                break;
            case DSSd12:
                crossLinker_and_indices = get_amine_groups(protein, s, doesContainProteinNtermini, doesContainProteinCTermini,
                        isSideReactionConsidered_S, isSideReactionConsidered_T, isSideReactionConsidered_Y);
                break;
            case GA:
                crossLinker_and_indices = get_amine_groups(protein, s, doesContainProteinNtermini, doesContainProteinCTermini,
                        isSideReactionConsidered_S, isSideReactionConsidered_T, isSideReactionConsidered_Y);
                break;
            case EDC:
                if (firstPart) {
                    crossLinker_and_indices = get_carboxyl_groups(protein, s, doesContainProteinNtermini, doesContainProteinCTermini);
                } else {
                    crossLinker_and_indices = get_amine_groups(protein, s,  doesContainProteinNtermini, doesContainProteinCTermini,
                            isSideReactionConsidered_S, isSideReactionConsidered_T, isSideReactionConsidered_Y);
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
     * @param sequence
     * @param doesContainProteinNTermini
     * @param doesContainProteinCTermini
     * @return
     */
    public static ArrayList<LinkedResidue> get_carboxyl_groups(Protein protein, char[] sequence, boolean doesContainProteinNTermini, boolean doesContainProteinCTermini) {
//        char[] sequence = protein.getSequence().getSequence().toCharArray();
        ArrayList<LinkedResidue> linkedRes = new ArrayList<LinkedResidue>();
        for (int position = 0; position < sequence.length; position++) {
            LinkedResidue r = null;
            char charAt = sequence[position];
            if (charAt == 'E' && position != sequence.length - 1) {
                r = new LinkedResidue(protein, position, LinkedResidueType.E, doesContainProteinNTermini, doesContainProteinCTermini);
            } else if (charAt == 'D' && position != sequence.length - 1) {
                r = new LinkedResidue(protein, position, LinkedResidueType.D, doesContainProteinNTermini, doesContainProteinCTermini);
            } else if (position == sequence.length - 1) {
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
     * conjugate: These are lysin (K) and protein N-termini, and if side
     * reaction is allowed STY also allowed
     *
     * @param protein
     * @param s a char array of sequence of given protein
     * @param doesContainProteinNTermini
     * @param doesContainProteinCTermini
     * @param isSideReactionConsidered_S introducing S as linkeable due to
     * consideration of side-reactions
     * @param isSideReactionConsidered_T introducing T as linkeable due to
     * consideration of side-reactions
     * @param isSideReactionConsidered_Y introducing Y as linkeable due to
     * consideration of side-reactions
     * @return
     */
    public static ArrayList<LinkedResidue> get_amine_groups(Protein protein, char[] s, boolean doesContainProteinNTermini, boolean doesContainProteinCTermini,
    boolean isSideReactionConsidered_S, boolean isSideReactionConsidered_T, boolean isSideReactionConsidered_Y ) {
        boolean isMethionineFirstResidue = false;
//        char[] s = protein.getSequence().getSequence().toCharArray();
//        String sequence = protein.getSequence().getSequence();
        if (s[0] == 'M') {
            isMethionineFirstResidue = true;
        }
        ArrayList<LinkedResidue> linkedRes = new ArrayList<LinkedResidue>();
        for (int position = 0; position < s.length; position++) {
            LinkedResidue r = null;
            char charAt = s[position];
            if (position == 0 && doesContainProteinNTermini && !isMethionineFirstResidue) {
                r = new LinkedResidue(protein, position, LinkedResidueType.NTerminus, doesContainProteinNTermini, doesContainProteinCTermini);
            } else if (position == 0 && doesContainProteinNTermini && isMethionineFirstResidue) {
                r = new LinkedResidue(protein, position, LinkedResidueType.M, doesContainProteinNTermini, doesContainProteinCTermini);
                // nterminal m excision is essential and most of the time M is cleaved, the second residue is checked.. (Frottin, 2006)
            } else if (position == 1 && doesContainProteinNTermini && isMethionineFirstResidue) {
                r = new LinkedResidue(protein, position, LinkedResidueType.NTerminiIncludesM, doesContainProteinNTermini, doesContainProteinCTermini);
                // / not including c-terminus.. (Rinner et al, 2008)
            } else if (position != s.length - 1 && charAt == 'K') {
                r = new LinkedResidue(protein, position, LinkedResidueType.K, doesContainProteinNTermini, doesContainProteinCTermini);
            } else if (position != s.length - 1 && charAt == 'S' && isSideReactionConsidered_S) {
                r = new LinkedResidue(protein, position, LinkedResidueType.S, doesContainProteinNTermini, doesContainProteinCTermini);
            } else if (position != s.length - 1 && charAt == 'T' && isSideReactionConsidered_T) {
                r = new LinkedResidue(protein, position, LinkedResidueType.T, doesContainProteinNTermini, doesContainProteinCTermini);
            } else if (position != s.length - 1 && charAt == 'Y' && isSideReactionConsidered_Y) {
                r = new LinkedResidue(protein, position, LinkedResidueType.Y, doesContainProteinNTermini, doesContainProteinCTermini);
            }
            if (r != null) {
                linkedRes.add(r);
            }
        }
        return linkedRes;
    }
}
