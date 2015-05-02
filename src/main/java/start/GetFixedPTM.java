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
public class GetFixedPTM {

    public static ArrayList<ModificationMatch> getPTM(PTMFactory ptmFactory, ArrayList<String> ptmNames, String peptideSequence) throws XmlPullParserException, IOException {
        ArrayList<ModificationMatch> modifications = new ArrayList<ModificationMatch>();
        // Getting one fixed PTMs
        for (String ptmName : ptmNames) {            
            PTM testPTM = ptmFactory.getPTM(ptmName);
            String theoreticPTM = testPTM.getName();
            int target = testPTM.getPattern().getTarget();
            ArrayList<Character> targetAAs = testPTM.getPattern().getTargetedAA(target);
            for (Character targetAA : targetAAs) {
                for (int i = 0; i < peptideSequence.length(); i++) {
                    char aa = peptideSequence.charAt(i);
                    if (aa == targetAA) {
                        int index = i + 1;
                        ModificationMatch m = new ModificationMatch(theoreticPTM, false, index);
                        modifications.add(m);
                    }
                }
            }
        }
        return modifications;
    }
}
