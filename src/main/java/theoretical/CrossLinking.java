/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.biology.IonFactory;
import com.compomics.util.experiment.biology.NeutralLoss;
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
import org.apache.log4j.Logger;

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
    public static double intensity = 100,
            intensity_neutralLosses = 100,
            theoretical_xlinked_mass = 0;
    protected int neutralLossesCase; // 0: No neutral losses 1: Special cases (a water loss for D/E/S/T and an ammonia loss for K/N/Q/R with the presence of a parent ion) 2: All water and ammonia losses 
    protected CrossLinkingType linkingType;
    protected PTMFactory ptmFactory = PTMFactory.getInstance();
    private static final Logger LOGGER = Logger.getLogger(CrossLinking.class);

    /* getter and setter methods */
    public CrossLinker getLinker() {
        return linker;
    }

    public FragmentationMode getFragmentation_mode() {
        return fragmentation_mode;
    }

    public HashSet<CPeptideIon> getTheoretical_ions() {
        return theoretical_ions;
    }

    public void setTheoretical_ions(HashSet<CPeptideIon> theoretical_ions) {
        this.theoretical_ions = theoretical_ions;
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
        CrossLinking.theoretical_xlinked_mass = theoretical_xlinked_mass;
    }

    public CrossLinkingType getLinkingType() {
        return linkingType;
    }

    public int neutralLossesCase() {
        return neutralLossesCase;
    }

    public void setNeutralLossesCase(int neutralLossesCase) {
        this.neutralLossesCase = neutralLossesCase;
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
            int ion_type, int linked_index, double mass_shift, String pepName, CPeptideIonType cPepIonType, boolean isA2Required, Peptide peptide) {
        // get the right CPeptideIonType for neutral losses
        CPeptideIonType cPepIonTypeWaterLoss = getCPeptideIonTypeForNeutralLoss(cPepIonType),
                cPepIonTypeAmmoniaLoss = getCPeptideIonTypeForNeutralLoss(cPepIonType);
        // now prepare ions for backbones
        HashSet<CPeptideIon> backbones = new HashSet<CPeptideIon>();
        String abbrIonType = LinkedPeptideFragmentIon.getAbbrIonType(ion_type);
        String rootName = pepName + "" + abbrIonType;
        ArrayList<Ion> tmp_ions = product_ions.get(ion_type);
        if (isA2Required && ion_type == PeptideFragmentIon.A_ION) {
            Ion a2 = tmp_ions.get(1);
            tmp_ions = new ArrayList<Ion>();
            tmp_ions.add(a2);
        }
        int seqIndex = 0;
        for (int index = 0; index < tmp_ions.size(); index++) {
            Ion ion = tmp_ions.get(index);
            String name = ion.getName();
            // there are some fragment ions with specific loss, this control allows to keep only standard ions (b/y/a etc..)
            if (!name.contains("-")) {
                char aaCode = peptide.getSequence().charAt(seqIndex);
                if (ion_type == PeptideFragmentIon.X_ION || ion_type == PeptideFragmentIon.Y_ION || ion_type == PeptideFragmentIon.Z_ION) {
                    aaCode = peptide.getSequence().charAt(peptide.getSequence().length() - seqIndex - 1);
                }
                seqIndex++;
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
                // check if there is an ion  with the same mass already...Because there are two N-terminis and C-terminis!
                String ionName = rootName + index_to_show;
                boolean isFound = isCPeptideIonWithSameMassPresent(ion_mass, ionName);
                if (!isFound) {
                    CPeptideIon cIon = new CPeptideIon(intensity, ion_mass, cPepIonType, ion_type, ionName, aaCode);
                    // add all water and ammonia losses
                    if (neutralLossesCase == 2) {
                        CPeptideIon cIon_waterloss = new CPeptideIon(intensity_neutralLosses, (ion_mass - NeutralLoss.H2O.getMass()), cPepIonTypeWaterLoss, 6, (ionName + "°"), aaCode),
                                cIon_ammoniaLoss = new CPeptideIon(intensity_neutralLosses, (ion_mass - NeutralLoss.NH3.getMass()), cPepIonTypeAmmoniaLoss, 7, (ionName + "*"), aaCode);
                        backbones.add(cIon_waterloss);
                        theoretical_ions.add(cIon_ammoniaLoss);
                        backbones.add(cIon_waterloss);
                        theoretical_ions.add(cIon_ammoniaLoss);
                    }
                    backbones.add(cIon);
                    theoretical_ions.add(cIon);
                }
            } else {
                // the selected ion has already been updated via isCPeptideIonWithSameMassPresent method.. 
            }
        }
        return backbones;
    }

    /**
     * This method checks if there is an ion (from Ion or CPeptideIon class)
     * with the same mass already... This might happen because there are two
     * N-terminis and C-terminis!
     *
     * @param ion_mass is mass of ion (neutral, without charge)
     * @param ionName
     * @return
     */
    public boolean isCPeptideIonWithSameMassPresent(double ion_mass, String ionName) {
        boolean isFound = false,
                isSameName = false;
        for (CPeptideIon cPepTheo : theoretical_ions) {
            double tmp_cpeptheo = cPepTheo.getMass();
            if (Math.abs(tmp_cpeptheo - ion_mass) < 0.0000001) {
                // check also names.. because it might be an ion with Bb3Ab3, with the same mass of an ion of Ab3Bb3..      
                if (ionName.contains("_")) {
                    String[] parts = ionName.split("_");
                    for (String part : parts) {
                        int split_index = part.indexOf('A');
                        if (split_index == 0) {
                            // do nothing..because theoretical ions are calculated after getting ions from PeptideA, then comes PeptideB
                        } else {
                            String reversed = ionName.substring(split_index) + ionName.substring(0, split_index);
                            if (reversed.equals(part)) {
                                isSameName = true;
                                isFound = true;
                            }
                        }
                    }
                } else if (ionName.contains("A") && ionName.contains("B")) {
                    String reversed = ionName.substring(ionName.indexOf('A')) + ionName.substring(0, ionName.indexOf('A'));
                    if (reversed.equals(ionName)) {
                        isSameName = true;
                        isFound = true;
                    }
                }
                if (!isSameName) {
                    ionName = cPepTheo.getName() + "_" + ionName;
                    cPepTheo.setName(ionName);
                    isFound = true;
                }
            }
        }
        return isFound;
    }

    public abstract String toPrint();

    /**
     * This method returns modification info derived from a given peptide. It
     * only returns variable modifications
     *
     * @param peptide
     * @return
     */
    public String getModificationInfo(Peptide peptide) {
        ArrayList<ModificationMatch> modificationMatches = peptide.getModificationMatches();
        String info = "";
        boolean isModificationFound = false;
        for (ModificationMatch m : modificationMatches) {
            String ptm = m.getTheoreticPtm();

            PTM tmpPTM = ptmFactory.getPTM(ptm);
            int ptmType = tmpPTM.getType();
            if ((ptmType == PTM.MODAA // particular amino acid at any location
                    || ptmType == PTM.MODCAA // particular amino acid at the the C-terminus of a protein
                    || ptmType == PTM.MODCPAA // particular amino acid must exist c-terminus of a peptide
                    || ptmType == PTM.MODNAA // particular amino acid at the the N-terminus of a protein
                    || ptmType == PTM.MODNPAA) && m.isVariable()) { // particular amino
                String tmp = "[" + ptm + "_" + m.getModificationSite() + "]";
                info += tmp + ";";
                isModificationFound = true;
            } else if (ptmType == PTM.MODN
                    || ptmType == PTM.MODNP
                    || ptmType == PTM.MODC
                    || ptmType == PTM.MODCP) {
                String tmp = "[" + ptm + "]";
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
            for (int j = 0; j < modificationMatches.size(); j++) {
                ModificationMatch m = modificationMatches.get(j);
                if (m.getModificationSite() == (i + 1)) {
                    String modName = "";
                    if (m.isVariable()) {
                        modified = true;
                        int type = ptmFactory.getPTM(m.getTheoreticPtm()).getType();
                        // type=1 is n-term type=0 is aminoacid - If type is amino-acid PTM..
                        if (type == PTM.MODAA || type == PTM.MODCAA || type == PTM.MODCPAA || type == PTM.MODNAA || type == PTM.MODNPAA) {
                            double mass = ptmFactory.getPTM(m.getTheoreticPtm()).getMass();
                            String format = df.format(mass);
                            modName = "[" + format + "]";
                            alteredPeptideSequence.append(peptide.getSequence().charAt(i));
                            alteredPeptideSequence.append(modName);
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

    /**
     * This method finds the right ion-type of given cPepIonType. for a selected
     * At the moment, there is no separation between water, or ammonia or the
     * other losses.
     *
     * @param cPepIonType
     * @return
     */
    private CPeptideIonType getCPeptideIonTypeForNeutralLoss(CPeptideIonType cPepIonType) {
        CPeptideIonType neutralLossCPepIonType = CPeptideIonType.NeutralLoss;
        if (cPepIonType.equals(CPeptideIonType.Backbone_PepA)) {
            neutralLossCPepIonType = CPeptideIonType.NeutralLoss_Backbone_PepA;
        } else if (cPepIonType.equals(CPeptideIonType.Backbone_PepB)) {
            neutralLossCPepIonType = CPeptideIonType.NeutralLoss_Backbone_PepB;
        }
        return neutralLossCPepIonType;
    }

}
