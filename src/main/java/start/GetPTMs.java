/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author Sule
 */
public class GetPTMs {

    /**
     * To construct a list of ModificationMatches from a given list of string
     * with PTMs
     *
     * @param ptmFactory
     * @param ptmNames
     * @param peptideSequence
     * @param isVariable true:variable PTM; false:fixedPTM
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static ArrayList<ModificationMatch> getPTM(PTMFactory ptmFactory, ArrayList<String> ptmNames, String peptideSequence, boolean isVariable) throws XmlPullParserException, IOException {
        ArrayList<ModificationMatch> modifications = new ArrayList<ModificationMatch>();
        // Getting each PTM one by one..
        for (String ptmName : ptmNames) {
            ArrayList<ModificationMatch> ptm = getPTM(ptmFactory, ptmName, peptideSequence, isVariable);
            modifications.addAll(ptm);
        }
        return modifications;
    }

    /**
     * To construct a list of ModificationMatches from a given String of PTM
     *
     * @param ptmFactory
     * @param ptmName
     * @param peptideSequence
     * @param isVariable
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static ArrayList<ModificationMatch> getPTM(PTMFactory ptmFactory, String ptmName, String peptideSequence, boolean isVariable) throws XmlPullParserException, IOException {
        // Create an instance of the subset generator as combinatorially for variables..
        ArrayList<ModificationMatch> modifications = new ArrayList<ModificationMatch>();
        if (!ptmName.isEmpty()) {
            ArrayList<String> ptms = getPTMStoRead(ptmName);
            for (String ptm : ptms) {
                PTM tmpPTM = ptmFactory.getPTM(ptm);
                String theoreticPTM = tmpPTM.getName();
                int ptmType = tmpPTM.getType();
                if (ptmType == PTM.MODAA
                        || ptmType == PTM.MODNAA
                        || ptmType == PTM.MODCAA
                        || ptmType == PTM.MODCPAA
                        || ptmType == PTM.MODNPAA) {
                    int target = tmpPTM.getPattern().getTarget();
                    ArrayList<Character> targetAAs = tmpPTM.getPattern().getTargetedAA(target);
                    for (Character targetAA : targetAAs) {
                        for (int i = 0; i < peptideSequence.length(); i++) {
                            char aa = peptideSequence.charAt(i);
                            if (aa == targetAA) {
                                int index = i + 1;
                                ModificationMatch modification = new ModificationMatch(theoreticPTM, isVariable, index);
                                modifications.add(modification);
                            }
                        }
                    }
                } else if (ptmType == PTM.MODN
                        || ptmType == PTM.MODNP) {
                    ModificationMatch modification = new ModificationMatch(theoreticPTM, isVariable, 1);
                    modifications.add(modification);
                } else if (ptmType == PTM.MODC
                        || ptmType == PTM.MODCP) {
                    ModificationMatch modification = new ModificationMatch(theoreticPTM, isVariable, peptideSequence.length());
                    modifications.add(modification);
                }
            }
        }
        return modifications;
    }

    /**
     * To construct a list of ModificationMatches from a given String of PTM
     *
     * @param ptmFactory
     * @param ptmName
     * @param isVariable
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static ArrayList<ModificationMatch> getPTM(PTMFactory ptmFactory, String ptmName, boolean isVariable) throws XmlPullParserException, IOException {
        // Create an instance of the subset generator as combinatorially for variables..
        ArrayList<ModificationMatch> modifications = new ArrayList<ModificationMatch>();
        if (!ptmName.isEmpty()) {
            ArrayList<String> ptms = getPTMStoRead(ptmName);
            for (String ptm : ptms) {
                String tmpPTM = ptm.substring(0, ptm.lastIndexOf("_"));
                int index = Integer.parseInt(ptm.substring(ptm.lastIndexOf("_") + 1));
                PTM actualPTM = ptmFactory.getPTM(tmpPTM);
                String theoreticPTM = actualPTM.getName();
                ModificationMatch modification = new ModificationMatch(theoreticPTM, isVariable, index);
                modifications.add(modification);
            }
        }
        return modifications;
    }

    public static ArrayList<PTMNameIndex> getPTMwithPTMNameIndex(PTMFactory ptmFactory, ArrayList<String> ptmNames, String peptideSequence, boolean isVariable) throws XmlPullParserException, IOException {
        ArrayList<PTMNameIndex> modifications = new ArrayList<PTMNameIndex>();
        // Getting each PTM one by one..
        for (String ptmName : ptmNames) {
            ArrayList<PTMNameIndex> ptm = getPossiblePTMs(ptmFactory, ptmName, peptideSequence, isVariable);
            modifications.addAll(ptm);
        }
        return modifications;
    }

    public static ArrayList<PTMNameIndex> getPossiblePTMs(PTMFactory ptmFactory, String ptmName, String peptideSequence, boolean isVariable) throws XmlPullParserException, IOException {
        ArrayList<PTMNameIndex> ptmNameAndIndices = new ArrayList<PTMNameIndex>();
        PTM tmpPTM = ptmFactory.getPTM(ptmName);
        int ptmType = tmpPTM.getType();
        if (ptmType == PTM.MODAA
                || ptmType == PTM.MODNAA
                || ptmType == PTM.MODCAA
                || ptmType == PTM.MODCPAA
                || ptmType == PTM.MODNPAA) {
            int target = tmpPTM.getPattern().getTarget();
            ArrayList<Character> targetAAs = tmpPTM.getPattern().getTargetedAA(target);
            for (Character targetAA : targetAAs) {
                for (int i = 0; i < peptideSequence.length(); i++) {
                    char aa = peptideSequence.charAt(i);
                    if (aa == targetAA) {
                        int index = i + 1;
                        PTMNameIndex ptmAndindex = new PTMNameIndex(ptmName, index);
                        ptmNameAndIndices.add(ptmAndindex);
                    }
                }
            }
        } else if (ptmType == PTM.MODN
                || ptmType == PTM.MODNP) {
            PTMNameIndex ptmAndindex = new PTMNameIndex(ptmName, 1);
            ptmNameAndIndices.add(ptmAndindex);
        } else if (ptmType == PTM.MODC
                || ptmType == PTM.MODCP) {
            PTMNameIndex ptmAndindex = new PTMNameIndex(ptmName, peptideSequence.length());
            ptmNameAndIndices.add(ptmAndindex);
        }

        return ptmNameAndIndices;
    }

    private static ArrayList<String> getPTMStoRead(String ptmName) {
        ArrayList<String> ptms = new ArrayList<String>();
        String[] split = ptmName.split("\\[");
        for (String tmp : split) {
            String ptm = tmp.split("]")[0];
            if (!ptm.isEmpty()) {
                ptms.add(ptm);
            }
        }
        return ptms;

    }

    public static class PTMNameIndex {

        String ptmName;
        int ptmIndex;

        public PTMNameIndex(String ptmName, int ptmIndex) {
            this.ptmName = ptmName;
            this.ptmIndex = ptmIndex;
        }

        public String getPtmName() {
            return ptmName;
        }

        public void setPtmName(String ptmName) {
            this.ptmName = ptmName;
        }

        public int getPtmIndex() {
            return ptmIndex;
        }

        public void setPtmIndex(int ptmIndex) {
            this.ptmIndex = ptmIndex;
        }

    }
}
