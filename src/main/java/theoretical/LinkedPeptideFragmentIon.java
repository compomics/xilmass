/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.AminoAcid;
import com.compomics.util.experiment.biology.Atom;
import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.biology.IonFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * This class holds information on a linked (or attached) peptide with two
 * possible ways to generate fragment ions derived from a linked peptide:
 *
 * Attaching: Simply generating all backbone ions for a linked peptide
 *
 * Branching: Fragment ion generation starts from a node (linked aminoacid) and
 * then go to the right while adding amino acid masses (if it is C-termini
 * included ion, adding c-termini as well). Then, going to the left with the
 * same strategy (adding N-termini mass shift to an ion including N-termini).
 *
 * Note that none of the ions with index of a peptide length is not generated at
 * all!
 *
 *
 * @author Sule
 */
public final class LinkedPeptideFragmentIon {

    private Peptide linkedPeptide; //a linked or attached peptide 
    private int linker_position_on_linkedPeptide; // An index on where a linker is bond to that linkedPeptide
    private boolean isLinkedPepA; // Used for naming...
    private double intensity; // intensity for a theoretical ion intensity
    private IonFactory fragmentFactory = IonFactory.getInstance();
    private HashMap<Integer, ArrayList<Ion>> product_ions_linkedPeptide;// peptide backbon ion of a linked peptide

    public LinkedPeptideFragmentIon(Peptide linkedPeptide, int linker_position_on_linkedPeptide, boolean isLinkedPepA, double intensity) {
        this.linkedPeptide = linkedPeptide;
        this.linker_position_on_linkedPeptide = linker_position_on_linkedPeptide;
        this.isLinkedPepA = isLinkedPepA;
        this.intensity = intensity;
        product_ions_linkedPeptide = fragmentFactory.getFragmentIons(linkedPeptide).get(0); // only peptide fragment ions
    }

    /**
     * This method generates linked peptide ions including C-termini according
     * to fragmentIonType.
     *
     *
     * @param fragmentIonType Must be
     * PeptideFragmentIon.X_ION/PeptideFragmentIon.Y_ION/PeptideFragmentIon.Z_ION
     * @return a list of generated CPeptideIons for a linked peptide
     *
     */
    public ArrayList<CPeptideIon> getCTerminiMasses(int fragmentIonType) {
        // make sure that only correctly given fragmentIonType is used!
        if (fragmentIonType == PeptideFragmentIon.X_ION
                || fragmentIonType == PeptideFragmentIon.Y_ION
                || fragmentIonType == PeptideFragmentIon.Z_ION) {
            ArrayList<CPeptideIon> cTerminiMasses = calculateTerminisAttaching(fragmentIonType);
            return cTerminiMasses;
        } else {
            System.err.print("N-termini including fragment ion type is selected to retrieve C-termini ones!");
        }
        return null;
    }

    /**
     * This method generates linked peptide ions including N-termini according
     * to fragmentIonType.
     *
     * @param fragmentIonType (PeptideFragmentIon.A_ION/B_ION/C_ION)
     * @return a list of generated CPeptideIons for a linked peptide
     */
    public ArrayList<CPeptideIon> getNTerminiMasses(int fragmentIonType) {
        // make sure that only correctly given fragmentIonType is used!
        if (fragmentIonType == PeptideFragmentIon.A_ION
                || fragmentIonType == PeptideFragmentIon.B_ION
                || fragmentIonType == PeptideFragmentIon.C_ION) {
            ArrayList<CPeptideIon> nTerminiMasses = calculateTerminisAttaching(fragmentIonType);
            return nTerminiMasses;
        } else {
            System.err.print("C-termini including fragment ion type is selected to retrieve N-termini ones!");
        }
        return null;
    }

    /**
     * This method selects a list of fragment ions based according to the
     * "Attaching approach"
     *
     * @param fragmentIonType - any (call as PeptideFragmentIon.XXX_ION)
     * @return
     */
    private ArrayList<CPeptideIon> calculateTerminisAttaching(int fragmentIonType) {
        // prepare naming.. 
        String lepName = "lepA";
        CPeptideIonType cPeptideIonType = CPeptideIonType.LinkedPeptideBackbone_PepA;
        if (!isLinkedPepA) {
            cPeptideIonType = CPeptideIonType.LinkedPeptideBackbone_PepB;
            lepName = "lepB";
        }
        String abbrIonType = getAbbrIonType(fragmentIonType);
        String rootName = lepName + "_" + abbrIonType + "_";
        // select given fragment ion types and retrieve them..
        ArrayList<CPeptideIon> terminiCPepIons = new ArrayList<CPeptideIon>();
        ArrayList<Ion> tmp_ions = product_ions_linkedPeptide.get(fragmentIonType);
        if (fragmentIonType == PeptideFragmentIon.A_ION) {
            Ion a2 = tmp_ions.get(1);
            tmp_ions = new ArrayList<Ion>();
            tmp_ions.add(a2);
        }
        for (int ion_index = 0; ion_index < tmp_ions.size(); ion_index++) {
            int index_for_user = ion_index + 1;
            if (fragmentIonType == PeptideFragmentIon.A_ION) {
                index_for_user++;
            }
            String cPepIonTypeName = rootName + "_" + index_for_user;
            Ion ion = tmp_ions.get(ion_index);
            CPeptideIon cIon = new CPeptideIon(intensity, ion.getTheoreticMass(), cPeptideIonType, fragmentIonType, cPepIonTypeName, '+');
            terminiCPepIons.add(cIon);
        }
        return terminiCPepIons;
    }

    /**
     * Here all possible fragment ions derived from a linked peptide are written
     * as an arraylist of character of a single letter amino acid code on
     * Branching approach
     *
     * @param isNtermini - enables two different generations of aas containing
     * either N-termini (true) or C-termini (false)
     * @return
     */
    public ArrayList<ArrayList<Character>> getAALinkedPeptide(boolean isNtermini) {
        ArrayList<ArrayList<Character>> characters = new ArrayList<ArrayList<Character>>();
        int startIndex = linker_position_on_linkedPeptide,
                endIndex = linkedPeptide.getSequence().length(),
                last_generated_index = 1;
        if (isNtermini) {
            endIndex--;
            last_generated_index = 0;
        }
        ArrayList<Character> startCharacters = new ArrayList<Character>();
        for (int i = startIndex; i < endIndex; i++) {
            char start = linkedPeptide.getSequence().charAt(i);
            startCharacters.add(start);
            characters.add(new ArrayList(startCharacters));
            for (int start_i = startIndex - 1; start_i >= last_generated_index; start_i--) {
                char tmp_char_start_i = linkedPeptide.getSequence().charAt(start_i);
                ArrayList<Character> cpy = new ArrayList<Character>(characters.get(characters.size() - 1));
                cpy.add(0, tmp_char_start_i);
                characters.add(cpy);
            }
        }
        return characters;
    }

    /**
     * This method calculates monoisotopic masses of all linkedIons including
     * N-termini with branching approach
     *
     * AIK*NK would yield ions of K and KN (in the right side), IK and
     * AIK+(N-termini) (left side), and an ion containing last amino acid is not
     * calculated
     *
     * @param fragmentIonType : PeptideFragmentIon
     * type-PeptideFragmentIon.A_ION, PeptideFragmentIon.B_ION,
     * PeptideFragmentIon.C_ION
     */
    private ArrayList<CPeptideIon> calculateNTerminiCPeptideIons(int fragmentIonType) {
        // prepare first part of peptide..
        String lepName = "lepA";
        CPeptideIonType cPeptideIonType = CPeptideIonType.LinkedPeptideBackbone_PepA;
        if (!isLinkedPepA) {
            cPeptideIonType = CPeptideIonType.LinkedPeptideBackbone_PepB;
            lepName = "lepB";
        }
        String abbrIonType = getAbbrIonType(fragmentIonType);
        String rootName = lepName + "_" + abbrIonType + "_";
        // Now prepare linked ions...
        ArrayList<CPeptideIon> nTerminiCPepIons_Branching = new ArrayList<CPeptideIon>();
        int startInd = linker_position_on_linkedPeptide,
                pepLen = linkedPeptide.getSequence().length();
        double nTerminiMassAddition = getMassDiff(fragmentIonType);   // b yields 0, the other needs to be calculated 
        // Left branching...
        // get mass of node...then add it to a list...
        char node = linkedPeptide.getSequence().charAt(linker_position_on_linkedPeptide);
        double startMass = AminoAcid.getAminoAcid(node).getMonoisotopicMass();
        String nodeName = rootName + "_" + linker_position_on_linkedPeptide;
        CPeptideIon cPepIonNode = new CPeptideIon(intensity, startMass, cPeptideIonType, fragmentIonType, nodeName, '+');
        nTerminiCPepIons_Branching.add(cPepIonNode);
        // Since it is N-termini, we shall not go till the end...
        for (int i = startInd + 1; i < pepLen - 1; i++) {
            char start = linkedPeptide.getSequence().charAt(i);
            // here, select all aminoacids to the right side...             
            startMass += AminoAcid.getAminoAcid(start).getMonoisotopicMass();
            if (i == 0) {
                startMass += nTerminiMassAddition;
            }
            int naming_index = i + 1;
            String name = rootName + "_" + naming_index;
            CPeptideIon cPepIon = new CPeptideIon(intensity, startMass, cPeptideIonType, fragmentIonType, name, '+');
            nTerminiCPepIons_Branching.add(cPepIon);
        }
        startMass = AminoAcid.getAminoAcid(node).getMonoisotopicMass();
        // here prepare residues derived from the left of the linked one!
        for (int i = startInd - 1; i >= 0; i--) {
            char tmp_char_start_i = linkedPeptide.getSequence().charAt(i);
            double leftRes = AminoAcid.getAminoAcid(tmp_char_start_i).getMonoisotopicMass();
            startMass += leftRes;
            if (i == 0) {
                startMass += nTerminiMassAddition;
            }
            int naming_index = i + 1;
            String name = rootName + "_" + naming_index;
            CPeptideIon cPepIon = new CPeptideIon(intensity, startMass, cPeptideIonType, fragmentIonType, name, '+');
            nTerminiCPepIons_Branching.add(cPepIon);
        }

        return nTerminiCPepIons_Branching;
    }

    /**
     * This method calculates monoisotopic masses of all linkedIons including
     * C-termini with branching approach
     *
     * AIK*NK would yield ions of K, KN, KNK(+Ctermini) (in the right side), IK
     * (left side), and an ion containing first amino acid is not calculated
     *
     * @param fragmentIonType : PeptideFragmentIon
     * type-PeptideFragmentIon.X_ION, PeptideFragmentIon.Y_ION,
     * PeptideFragmentIon.Z_ION
     */
    private ArrayList<CPeptideIon> calculateCTerminiCPeptideIons(int fragmentIonType) {
        // prepare first part of peptide..
        String lepName = "lepA";
        CPeptideIonType cPeptideIonType = CPeptideIonType.LinkedPeptideBackbone_PepA;
        if (!isLinkedPepA) {
            cPeptideIonType = CPeptideIonType.LinkedPeptideBackbone_PepB;
            lepName = "lepB";
        }
        String abbrIonType = getAbbrIonType(fragmentIonType);
        String rootName = lepName + "_" + abbrIonType + "_";
        // Now prepare linked ions...
        ArrayList<CPeptideIon> cTerminiCPepIons_Branching = new ArrayList<CPeptideIon>();
        int startInd = linker_position_on_linkedPeptide,
                pepLen = linkedPeptide.getSequence().length();
        double cTerminiMassAddition = getMassDiff(fragmentIonType);
        // Left branching...
        // get mass of node...then add it to a list...
        char node = linkedPeptide.getSequence().charAt(linker_position_on_linkedPeptide);
        double startMass = AminoAcid.getAminoAcid(node).getMonoisotopicMass();
        int index_for_naming = linker_position_on_linkedPeptide + 1;
        String nodeName = rootName + "_" + index_for_naming;
        CPeptideIon cPepIonNode = new CPeptideIon(intensity, startMass, cPeptideIonType, fragmentIonType, nodeName, '+');
        cTerminiCPepIons_Branching.add(cPepIonNode);
        // Since it is N-termini, we shall not go till the end...ONLY HERE C-TERMINI CAN BE FOUND!
        for (int i = startInd + 1; i < pepLen; i++) {
            char start = linkedPeptide.getSequence().charAt(i);
            // here, select all aminoacids to the right side...             
            startMass += AminoAcid.getAminoAcid(start).getMonoisotopicMass();
            if (i == pepLen - 1) {
                startMass += cTerminiMassAddition;
            }
            int naming_index = pepLen - i;
            String name = rootName + "_" + naming_index;
            CPeptideIon cPepIon = new CPeptideIon(intensity, startMass, cPeptideIonType, fragmentIonType, name, '+');
            cTerminiCPepIons_Branching.add(cPepIon);
        }
        startMass = AminoAcid.getAminoAcid(node).getMonoisotopicMass();
        // here prepare residues derived from the left of the linked one!
        for (int i = startInd - 1; i > 0; i--) {
            char tmp_char_start_i = linkedPeptide.getSequence().charAt(i);
            double leftRes = AminoAcid.getAminoAcid(tmp_char_start_i).getMonoisotopicMass();
            startMass += leftRes;
            int naming_index = pepLen - i;
            String name = rootName + "_" + naming_index;
            CPeptideIon cPepIon = new CPeptideIon(intensity, startMass, cPeptideIonType, fragmentIonType, name, '+');
            cTerminiCPepIons_Branching.add(cPepIon);
        }
        return cTerminiCPepIons_Branching;
    }

    /**
     * This method calculate mass shift on the first ion based on either
     * containing N-termini or C-termini
     *
     * @param fragmentIonType PeptideFragmentIon int class (use
     * PeptideFragmentIon.xxx_ION!
     * @return
     */
    public static double getMassDiff(int fragmentIonType) {
        double mass = 0;
        switch (fragmentIonType) {
            case PeptideFragmentIon.A_ION:
                mass = -(Atom.C.getMonoisotopicMass() + Atom.O.getMonoisotopicMass());
                break;
            case PeptideFragmentIon.C_ION:
                mass = Atom.N.getMonoisotopicMass() + (3 * Atom.H.getMonoisotopicMass());
                break;
            case PeptideFragmentIon.X_ION:
                mass = Atom.C.getMonoisotopicMass() + (2 * Atom.O.getMonoisotopicMass());
                break;
            case PeptideFragmentIon.Y_ION:
                mass = 2 * Atom.H.getMonoisotopicMass() + (Atom.O.getMonoisotopicMass());
                break;
            case PeptideFragmentIon.Z_ION:
                mass = -Atom.N.getMonoisotopicMass() + Atom.O.getMonoisotopicMass();
                break;
        }
        return mass;
    }

    /**
     * This method returns one letter string name-lower case for given
     * fragmentIontype
     *
     * @param fragmentIonType
     * @return
     */
    public static String getAbbrIonType(int fragmentIonType) {
        String abbr = "b";
        switch (fragmentIonType) {
            case PeptideFragmentIon.A_ION:
                abbr = "a";
                break;
            case PeptideFragmentIon.C_ION:
                abbr = "c";
                break;
            case PeptideFragmentIon.X_ION:
                abbr = "x";
                break;
            case PeptideFragmentIon.Y_ION:
                abbr = "y";
                break;
            case PeptideFragmentIon.Z_ION:
                abbr = "z";
                break;
        }
        return abbr;
    }
}
