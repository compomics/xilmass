/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.db.robustness;

import analyse.db.robustness.CheckRobustnessDecoyApproach;
import com.compomics.util.protein.Protein;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sule
 */
public class CheckRobustnessDecoyApproachTest {

    // generate X number of database.
    File targetFasta = new File("C:/Users/Sule/Documents/PhD/XLinked/databases/cam_plectin.fasta"), // fasta file with targets
            decoyFasta = new File("C:/Users/Sule/Documents/PhD/XLinked/databases/uniprot_2261_Pfuriosus.fasta"), // decoy fasta file 
            infoFile = new File("C:/Users/Sule/Documents/PhD/XLinked/databases/td_target_2Pfus_info.txt");
    String targetDecoyFileStartsWith = "C:/Users/Sule/Documents/PhD/XLinked/databases/td_target_2Pfus_",
            dbFolder = "C:/Users/Sule/Documents/PhD/XLinked/databases/";

    int[] proteinLengths = {149, 242};
    int dbs_num = 10, // how many target-decoy dbs are going to be generated
            first_protein_index = 10,
            second_protein_index = 11,
            spectrum_name_index = 2,
            score_index = 9;
    String[] target_protein_names = {"Q15149", "P62158"};

    public CheckRobustnessDecoyApproachTest() {
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

//    /**
//     * Test of main method, of class CheckRobustnessDecoyApproach.
//     */
//    @Test
//    public void testMain() throws Exception {
//        System.out.println("main");
//        String[] args = null;
//        CheckRobustnessDecoyApproach.main(args);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of mergeFile method, of class CheckRobustnessDecoyApproach.
     */
    @Test
    public void testMergeFile() throws Exception {
        System.out.println("mergeFile");
        String tmpResultFileLabeled = "";
        String tmpResultFileNoLabeled = "";
        String outputFile = "";
        int first_protein_index = 0;
        int second_protein_index = 0;
        String[] target_protein_names = null;
        CheckRobustnessDecoyApproach.mergeFile(tmpResultFileLabeled, tmpResultFileNoLabeled, outputFile, first_protein_index, second_protein_index, target_protein_names);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTargetType method, of class CheckRobustnessDecoyApproach.
     */
    @Test
    public void testGetTargetType() {
        System.out.println("getTargetType");
        String proteinAName = "";
        String proteinBName = "";
        String[] target_protein_names = null;
        String expResult = "";
        String result = CheckRobustnessDecoyApproach.getTargetType(proteinAName, proteinBName, target_protein_names);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of generateDB method, of class CheckRobustnessDecoyApproach.
     */
    @Test
    public void testGenerateDB() throws Exception {
        System.out.println("generateDB");
        File targetFasta = null;
        File decoyFasta = null;
        String targetDecoyFileStartsWith = "";
        int dbs_num = 0;
        int[] proteinLengths = null;
        String[] target_protein_names = null;
        File infoFile = null;
        ArrayList<File> expResult = null;
        ArrayList<File> result = CheckRobustnessDecoyApproach.generateDB(targetFasta, decoyFasta, targetDecoyFileStartsWith, dbs_num, proteinLengths, target_protein_names, infoFile);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cutDecoys method, of class CheckRobustnessDecoyApproach.
     */
    @Test
    public void testCutDecoys() {
        System.out.println("cutDecoys");
        Protein selectedDecoy = null;
        int targetLength = 0;
        CheckRobustnessDecoyApproach.cutDecoys(selectedDecoy, targetLength);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cutDecoysInside method, of class CheckRobustnessDecoyApproach.
     */
    @Test
    public void testCutDecoysInside() {
        System.out.println("cutDecoysInside");

        int cuttingIndex = 20,// from where to cut
                expectedDecoyLen = 30; // how long a decoy is expected to be 
        String selectedDecoySeq = "YGRWNKAESLYDPYTNTFPVGLLPRVKKFLNSKGYRVRIKDERKIEGEKLNSTWNEKYKL",
                expResult = "GLLPRVKKFLNSKGYRVRIKDERKIEGEKL",
                result = CheckRobustnessDecoyApproach.cutDecoysInside(cuttingIndex, expectedDecoyLen, selectedDecoySeq);
        assertEquals(expResult, result);

        cuttingIndex = 20;// from where to cut
        expectedDecoyLen = 20; // how long a decoy is expected to be 
        selectedDecoySeq = "YGRWNKAESLYDPYTNTFPVGLLPRVKKFLNSKGYRVRIKDERKIEGEKLNSTWNEKYKL";
        expResult = "GLLPRVKKFLNSKGYRVRIK";
        result = CheckRobustnessDecoyApproach.cutDecoysInside(cuttingIndex, expectedDecoyLen, selectedDecoySeq);
        assertEquals(expResult, result);
    }

    /**
     * Test of cutDecoysBothSides method, of class CheckRobustnessDecoyApproach.
     */
    @Test
    public void testCutDecoysBothSides() {
        System.out.println("cutDecoysBothSides");

        int selectedTermini = 0; // N termini is selected
        int aaSNumToRemoveFromSelectedTermini = 50; // how many aas need to be removed
        int aaSNumToRemove = 80; // how many ass need to be removed overall
        String selectedDecoySeq = "YGRWNKAESLYDPYTNTFPVGLLPRVKKFLNSKGYRVRIKDERKIEGEKLNSTWNEKYKL\n"
                + "RKYQMKAVKKALKEKMGVLALPVGSGKTIVGLRIIHELDLSALIIVHTKELLYQWAQKIK\n"
                + "EVLGIDPGIIGDNKWYEGPITVAMIQTLLSRGTDKFERKYAVVLFDECHRTSAAEKFYEV\n"
                + "GVNLPQVYRFGLSATPWRRLRGEEMKIEGVVGPIIYEVKAEDLIREGFLAKPKFEIIEYQ\n"
                + "SH";
        String expResult = "NSTWNEKYKL\n"
                + "RKYQMKAVKKALKEKMGVLALPVGSGKTIVGLRIIHELDLSALIIVHTKELLYQWAQKIK\n"
                + "EVLGIDPGIIGDNKWYEGPITVAMIQTLLSRGTDKFERKYAVVLFDECHRTSAAEKFYEV\n"
                + "GVNLPQVYRFGLSATPWRRLRGEEMKIEGVVG";
        String result = CheckRobustnessDecoyApproach.cutDecoysBothSides(selectedTermini, aaSNumToRemoveFromSelectedTermini, aaSNumToRemove, selectedDecoySeq);
        assertEquals(expResult, result);

        selectedTermini = 1;
        expResult = "NSKGYRVRIKDERKIEGEKLNSTWNEKYKL\n"
                + "RKYQMKAVKKALKEKMGVLALPVGSGKTIVGLRIIHELDLSALIIVHTKELLYQWAQKIK\n"
                + "EVLGIDPGIIGDNKWYEGPITVAMIQTLLSRGTDKFERKYAVVLFDECHRTSAAEKFYEV\n"
                + "GVNLPQVYRFGL";
        result = CheckRobustnessDecoyApproach.cutDecoysBothSides(selectedTermini, aaSNumToRemoveFromSelectedTermini, aaSNumToRemove, selectedDecoySeq);
        assertEquals(expResult, result);
    }

    /**
     * Test of pasteDecoys method, of class CheckRobustnessDecoyApproach.
     */
    @Test
    public void testPasteDecoys() {
        System.out.println("pasteDecoys");
        Protein selectedDecoy = null;
        HashSet<Integer> selectedDecoysIndices = null;
        ArrayList<Protein> proteins = null;
        int seqNumToAdd = 0;
        CheckRobustnessDecoyApproach.pasteDecoys(selectedDecoy, selectedDecoysIndices, proteins, seqNumToAdd);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of performPastingToDecoy method, of class
     * CheckRobustnessDecoyApproach.
     */
    @Test
    public void testPerformPastingToDecoy() {
        System.out.println("performPastingToDecoy");
        String selectedDecoyStr = "GLLPRVKKFLNSKGYRVRIKDERKIEGEKL";
        int indexOnSelectedDecoy = 2;
        String subToAddStr = "AAAAA";
        String performPastingToDecoy = CheckRobustnessDecoyApproach.performPastingToDecoy(selectedDecoyStr, indexOnSelectedDecoy, subToAddStr);
        assertEquals("GLAAAAALPRVKKFLNSKGYRVRIKDERKIEGEKL", performPastingToDecoy);
    }

}
