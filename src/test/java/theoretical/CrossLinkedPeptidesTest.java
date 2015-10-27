/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.biology.IonFactory;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import crossLinker.CrossLinker;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xmlpull.v1.XmlPullParserException;
import start.GetPTMs;

/**
 *
 * @author Sule
 */
public class CrossLinkedPeptidesTest {

    public CrossLinkedPeptidesTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    /**
     * Test of getSequenceWithPtms method, of class CrossLinking.
     */
    @Test
    public void testGetSequenceWithPtms() throws XmlPullParserException, IOException {
        System.out.println("getSequenceWithPtms");
        String peptideSequence = "MLCSDAIK";
        // Importing PTMs
        File modsFile = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\compomics-utilities\\src/test/resources/experiment/mods.xml");
        PTMFactory ptmFactory = PTMFactory.getInstance();
        ptmFactory.importModifications(modsFile, false);
        // Getting one fixed PTMs
        ArrayList<String> theoreticPTMs = new ArrayList<String>();
        theoreticPTMs.add("oxidation of m");
//        theoreticPTMs.add("carbamidomethyl c");
//        theoreticPTMs.add("propionamide c");
//        theoreticPTMs.add("pyro-cmc");
//        theoreticPTMs.add("oxidation of m");
        ArrayList<ModificationMatch> result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, true);
        ModificationMatch carbamidomethyl = new ModificationMatch("carbamidomethyl c", false, 3);
        result.add(carbamidomethyl);
        Peptide p = new Peptide(peptideSequence, result);
        CrossLinking instance = new CrossLinkedPeptidesImpl();
        String expResult = "M[15.99]LCSDAIK";
        String r = instance.getSequenceWithPtms(p, ptmFactory);
        assertEquals(expResult, r);

        theoreticPTMs = new ArrayList<String>();
        theoreticPTMs.add("oxidation of m");
        theoreticPTMs.add("acetylation of protein n-term");
        result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, true);
        result.add(carbamidomethyl);
        p = new Peptide(peptideSequence, result);
        instance = new CrossLinkedPeptidesImpl();
        expResult = "M[15.99]LCSDAIK";
        r = instance.getSequenceWithPtms(p, ptmFactory);
        assertEquals(expResult, r);
    }

    public class CrossLinkedPeptidesImpl extends CrossLinking {

        public String toPrint() {
            return "";
        }
    }

}
