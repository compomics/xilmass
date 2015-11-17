/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.biology.IonFactory;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import crossLinker.CrossLinker;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This abstract class holds information on cross-linker, fragmentation mode,
 * list of theoretical ions (also theoretical peak information as intensity)
 *
 * @author Sule
 */
public abstract class CrossLinking {

    protected CrossLinker linker;
    protected FragmentationMode fragmentation_mode;
    protected HashSet<CPeptideIon> theoretical_ions = new HashSet<CPeptideIon>();
    protected IonFactory fragmentFactory = IonFactory.getInstance();
    protected boolean is_monoisotopic_mass = true,
            isMassCalculated = false;
    protected double intensity = 100,
            theoretical_xlinked_mass = 0;
    protected CrossLinkingType linkingType;

    
    public CrossLinker getLinker() {
        return linker;
    }

    public FragmentationMode getFragmentation_mode() {
        return fragmentation_mode;
    }

    public HashSet<CPeptideIon> getTheoretical_ions() {
        return theoretical_ions;
    }

    public IonFactory getFragmentFactory() {
        return fragmentFactory;
    }

    public boolean isIs_monoisotopic_mass() {
        return is_monoisotopic_mass;
    }

    public boolean isIsMassCalculated() {
        return isMassCalculated;
    }

    public double getIntensity() {
        return intensity;
    }

    public double getTheoretical_xlinked_mass() {
        return theoretical_xlinked_mass;
    }

    public void setTheoretical_xlinked_mass(double theoretical_xlinked_mass) {
        this.theoretical_xlinked_mass = theoretical_xlinked_mass;
    }
    
    public CrossLinkingType getLinkingType() {
        return linkingType;
    }

    /**
     * This method retrieves product ions in a selected mode for only a peptide
     *
     * @param ion_type
     * @param linked_index
     * @param product_ions
     * @param mass_shift
     * @param pepName
     * @param cPepIonType
     * @param isA2Required true: a2 ion is introduced, false: only b and y ions
     * for HCD
     *
     * @return NI IONS!
     */
    public HashSet<CPeptideIon> prepareBackbone(HashMap<Integer, ArrayList<Ion>> product_ions,
            int ion_type, int linked_index, double mass_shift, String pepName, CPeptideIonType cPepIonType, boolean isA2Required) {
        HashSet<CPeptideIon> backbones = new HashSet<CPeptideIon>();
        String abbrIonType = LinkedPeptideFragmentIon.getAbbrIonType(ion_type);
        String rootName = pepName + "_" + abbrIonType;
        ArrayList<Ion> tmp_ions = product_ions.get(ion_type);
        if (isA2Required && ion_type == PeptideFragmentIon.A_ION) {
            Ion a2 = tmp_ions.get(1);
            tmp_ions = new ArrayList<Ion>();
            tmp_ions.add(a2);
        }
        for (int index = 0; index < tmp_ions.size(); index++) {
            Ion ion = tmp_ions.get(index);
            double ion_mass = ion.getTheoreticMass();
            if (index > linked_index && linkingType.equals(CrossLinkingType.CROSSLINK)) { // from a linker index on a peptide, shift remaining ions with a mass of a linkedPeptide       
                ion_mass += mass_shift + linker.getMassShift_Type2();
            } else if (index >= linked_index && linkingType.equals(CrossLinkingType.MONOLINK)) {
                ion_mass += mass_shift;
            } else {

            }
            int index_to_show = index + 1;
            if (isA2Required && ion_type == PeptideFragmentIon.A_ION) {
                index_to_show++;
            }
            String ionName = rootName + index_to_show;
            boolean isFound = false;
            // check if there is an ion with the same mass already...Because there are two N-terminis and C-terminis!
            for (CPeptideIon cPepTheo : theoretical_ions) {
                double tmp_cpeptheo = cPepTheo.getMass();
                if (Math.abs(tmp_cpeptheo - ion_mass) < 0.0000001) {
                    ionName = cPepTheo.getName() + "_" + ionName;
                    cPepTheo.setName(ionName);
                    isFound = true;
                }
            }
            if (!isFound) {
                CPeptideIon cIon = new CPeptideIon(intensity, ion_mass, cPepIonType, ion_type, ionName);
                backbones.add(cIon);
                theoretical_ions.add(cIon);
            }
        }
        return backbones;
    }

    public abstract String toPrint();

    /**
     * This method returns modification info derived from a given peptide. It
     * only returns variable modifications
     *
     * @param peptide
     * @return
     */
    public static String getModificationInfo(Peptide peptide) {
        ArrayList<ModificationMatch> modificationMatches = peptide.getModificationMatches();
        String info = "";
        boolean isModificationFound = false;
        for (ModificationMatch m : modificationMatches) {
            if (m.isVariable()) {
                String tmp = "[" + m.getTheoreticPtm() + "_" + m.getModificationSite() + "]";
                info += tmp + ";";
                isModificationFound = true;
            }
        }
        if (!isModificationFound) {
            info = "-";
        }
        return info;
    }

    public String getSequenceWithPtms(Peptide peptide, PTMFactory ptmFactory) {
        StringBuilder alteredPeptideSequence = new StringBuilder();
        ArrayList<ModificationMatch> modificationMatches = peptide.getModificationMatches();
        DecimalFormat df = new DecimalFormat("#.00");
        for (int i = 0; i < peptide.getSequence().length(); i++) {
            boolean modified = false;
            for (int j = 0; j < modificationMatches.size() && !modified; j++) {
                ModificationMatch m = modificationMatches.get(j);
                if (m.getModificationSite() == (i + 1)) {
                    String modName = "";
                    if (m.isVariable()) {
                        modified = true;
                        int type = ptmFactory.getPTM(m.getTheoreticPtm()).getType();
                        // type=1 is n-term type=0 is aminoacid - If type is amino-acid PTM..
                        if (type == PTM.MODAA) {
                            double mass = ptmFactory.getPTM(m.getTheoreticPtm()).getMass();
                            String format = df.format(mass);
                            modName = "[" + format + "]";
                            alteredPeptideSequence.append(peptide.getSequence().charAt(i));
                            alteredPeptideSequence.append(modName);
                        } else if (type == PTM.MODCAA || type == PTM.MODCPAA || type == PTM.MODNAA || type == PTM.MODNPAA) {
                            System.err.print("Do not know how to write this modifications!");
                        }
                    } else {
                        modified = true;
                        // type=1 is n-term type=0 is aminoacid - If type is amino-acid PTM..
                        alteredPeptideSequence.append(peptide.getSequence().charAt(i));
                    }
                }
            }
            if (!modified) {
                alteredPeptideSequence.append(peptide.getSequence().charAt(i));
            }
        }
        return alteredPeptideSequence.toString();
    }

    /**
     * To sort CrossLinking objects in a ascending order of theoretical mass
     */
    public static final Comparator<CrossLinking> Crosslinking_xlinked_mass_ASC_order
            = new Comparator<CrossLinking>() {
                @Override
                public int compare(CrossLinking o1, CrossLinking o2) {
                    return o1.getTheoretical_xlinked_mass() < o2.getTheoretical_xlinked_mass() ? -1 : o1.getTheoretical_xlinked_mass() == o2.getTheoretical_xlinked_mass() ? 0 : 1;
                }
            };

}
