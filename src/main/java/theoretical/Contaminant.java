/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class holds information for Contaminants to calculate scores
 *
 * @author Sule
 */
public class Contaminant extends CrossLinking {

    private Peptide peptide;
    private String protein;
    private int linker_position;
    private HashMap<Integer, ArrayList<Ion>> product_ions = new HashMap<Integer, ArrayList<Ion>>();

    /**
     * This constructs a Contaminant object
     *
     * @param peptide a Contaminant peptide object
     * @param protein a Contaminant protein string
     * @param fragmentation_mode
     * @param is_Branching_Approach
     */
    public Contaminant(Peptide peptide, String protein, FragmentationMode fragmentation_mode) {
        super.linkingType = CrossLinkingType.CONTAMINANT;
        this.protein = protein;
        this.peptide = peptide;
        this.fragmentation_mode = fragmentation_mode;
        product_ions = fragmentFactory.getFragmentIons(peptide).get(0); // only peptide fragment ions
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public String getProtein() {
        return protein;
    }

    public int getLinker_position() {
        return linker_position;
    }

    public void setPeptide(Peptide peptide) {
        this.peptide = peptide;
        product_ions = fragmentFactory.getFragmentIons(peptide).get(0);
        isMassCalculated = false;
    }

    public void setProtein(String protein) {
        isMassCalculated = false;
        this.protein = protein;
    }

    public void setLinker_position(int linker_position) {
        isMassCalculated = false;
        this.linker_position = linker_position;
    }

    @Override
    public double getTheoretical_xlinked_mass() {
        if (!isMassCalculated) {
            theoretical_xlinked_mass = peptide.getMass();
            isMassCalculated = true;
        }
        return theoretical_xlinked_mass;
    }

    /**
     * This method returns a theoretical spectrum.
     *
     * If a theoretical spectrum is empty, then prepare_theoretical_spectrum
     * method generates it.
     *
     *
     * @return contains all fragment ions in ASC mass order.
     */
    @Override
    public HashSet<CPeptideIon> getTheoretical_ions() {
        if (theoretical_ions.isEmpty()) {
            product_ions = fragmentFactory.getFragmentIons(peptide).get(0); // only peptide fragment ions
            prepare_theoretical_spectrum();
        }
        return theoretical_ions;
    }

    private void prepare_theoretical_spectrum() {
        theoretical_ions.addAll(getBackbone(product_ions));
        ArrayList<CPeptideIon> theoretical_ions_al = new ArrayList<CPeptideIon>(theoretical_ions);
        Collections.sort(theoretical_ions_al, CPeptideIon.Ion_ASC_mass_order);
        theoretical_ions = new HashSet<CPeptideIon>(theoretical_ions_al);
    }

    private HashSet<CPeptideIon> getBackbone(HashMap<Integer, ArrayList<Ion>> product_ions) {
        HashSet<CPeptideIon> backbones = new HashSet<CPeptideIon>();
        // prepare for naming
        String pepName = "pep";
        int pep_length = peptide.getSequence().length();
        int linked_index = linker_position;
        CPeptideIonType cPepIonType = CPeptideIonType.Backbone;
        ArrayList<Integer> ion_types = new ArrayList<Integer>();
        // get only peptide fragment ions
        // 0= a, 1=b, 2=c, 3=x, 4=y, 5=z
        if (fragmentation_mode.equals(FragmentationMode.CID)) {
            // mostly b and y ions
            ion_types.add(PeptideFragmentIon.B_ION);
            ion_types.add(PeptideFragmentIon.Y_ION);
        } else if (fragmentation_mode.equals(FragmentationMode.ETD)) {
            // mostly c and z ions
            ion_types.add(PeptideFragmentIon.C_ION);
            ion_types.add(PeptideFragmentIon.Z_ION);
        } else if (fragmentation_mode.equals(FragmentationMode.HCD)) {
            // mostly y ions and then b and a ions
            ion_types.add(PeptideFragmentIon.A_ION);
            ion_types.add(PeptideFragmentIon.B_ION);
            ion_types.add(PeptideFragmentIon.Y_ION);
        } else if (fragmentation_mode.equals(FragmentationMode.HCD_all)) {
            // mostly y ions and then b and a ions
            ion_types.add(PeptideFragmentIon.A_ION);
            ion_types.add(PeptideFragmentIon.B_ION);
            ion_types.add(PeptideFragmentIon.X_ION);
            ion_types.add(PeptideFragmentIon.Y_ION);
        }
        for (Integer tmp_ion_type : ion_types) {
            int index = linked_index;
            if (tmp_ion_type == PeptideFragmentIon.X_ION || tmp_ion_type == PeptideFragmentIon.Y_ION || tmp_ion_type == PeptideFragmentIon.Z_ION) {
                index = pep_length - linked_index - 1;
            }
            backbones.addAll(prepareBackbone(product_ions, tmp_ion_type, index, 0, pepName, cPepIonType, true));
        }
        return backbones;
    }

    @Override
    public String toPrint() {
        return peptide.getSequence() + "\t" + protein + "\t" + getModificationInfo(peptide) + "\t"
                + "-" + "\t" + "-" + "\t" + "-" + "\t"
                + "-" + "\t" + "-" + "\t"
                + "-" + "\t" + "-" + "\t"
                + "contaminant";
    }

}
