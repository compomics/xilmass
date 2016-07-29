/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import com.compomics.dbtoolkit.io.DBLoaderLoader;
import com.compomics.dbtoolkit.io.interfaces.DBLoader;
import com.compomics.util.protein.Header;
import com.compomics.util.protein.Protein;
import java.io.File;
import java.util.HashMap;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Sule
 */
public class CreateDatabaseTest {
    
    public CreateDatabaseTest() {
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
     * Test of digest_insilico method, of class CreateDatabase.
     */
    public void testDigest_insilico() throws Exception {
        System.out.println("digest_insilico");
        // Here construct...
        String inputFileName = "Data/Test/database/art_testing1Ent.fasta",
                outputFileName = "Data/Test/database/del/test_output_digest_insilico",
                enzymeFile = "C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\src\\main\\resources/enzymes.txt",
                crossLinker = "DSS",
                enzymeName = "Trypsin";
        int minLen = 4,
                maxLen_for_combined = 40;
        CreateDatabase instance = new CreateDatabase(inputFileName, outputFileName, "Data/Test/database/art_testing_testfastacp",
                crossLinker, "Inter", "Trypsin", enzymeFile, "4", "350", "4000.0", minLen, maxLen_for_combined, true, true, false, false, false);
        // Note min mass=300 gives two RR.. but not 350.. min mass=350 might be good to start for in silico digestion

        instance.digest_insilico();
        DBLoader loader = DBLoaderLoader.loadDB(new File(outputFileName));
        long countNumberOfEntries = loader.countNumberOfEntries();
        assertEquals(7, countNumberOfEntries);
        Protein start_protein = null;
        while ((start_protein = loader.nextProtein()) != null) {
            System.out.println(start_protein.getSequence().getSequence());
        }
    }

    /**
     * Test of create_crossLinkedPeptides method, of class CreateDatabase.
     */
    public void testCreate_crossLinkedPeptides() throws Exception {
        System.out.println("create_crossLinkedPeptides");

        String inputFileName = "Data/Test/database/art_testing2Ent.fasta",
                outputFileName = "Data/Test/database/art_testing2Ent_insilico.fasta",
                enzymeFile = "C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\src\\main\\resources/enzymes.txt",
                crossLinker = "DSS",
                enzymeName = "Trypsin";
        int minLen = 4,
                maxLen_for_combined = 40;
        CreateDatabase instance = new CreateDatabase(inputFileName, outputFileName, "Data/Test/database/art_testing1EntCX",
                crossLinker, "Intra", enzymeName, enzymeFile, "1", "350", "4000.0", minLen, maxLen_for_combined, true, true, false, false, false);
        HashMap<String, StringBuilder> headersAndSequences = instance.getHeadersAndSequences();
        assertEquals(10, headersAndSequences.size());

        instance = new CreateDatabase(inputFileName, outputFileName, "Data/Test/database/art_testing1EntCX",
                crossLinker, "Inter", enzymeName, enzymeFile, "1", "350", "4000.0", minLen, maxLen_for_combined, true, true, false, false, false);
        headersAndSequences = instance.getHeadersAndSequences();
        assertEquals(0, headersAndSequences.size());

        instance = new CreateDatabase(inputFileName, outputFileName, "Data/Test/database/art_testing1EntCX",
                crossLinker, "Both", enzymeName, enzymeFile, "1", "350", "4000.0", minLen, maxLen_for_combined, true, true, false, false, false);
        headersAndSequences = instance.getHeadersAndSequences();
        assertEquals(10, headersAndSequences.size());
        for (String h : headersAndSequences.keySet()) {
            System.out.println(h + "\t" + headersAndSequences.get(h));
        }
    }

    /**
     * Test of getAccession_and_length method, of class CreateDatabase.
     *
     * @throws Exception
     */
    public void testGetAccession_and_length() throws Exception {
        System.out.println("testGtAccession_and_length");
        String givenDBName = "data/Test/database/art_test_db.fasta",
                inSilicoPeptideDBName = "data/Test/database/art_test_db_insilico.fasta",
                cxDBName = "data/Test/database/art_test_db.fastacp",
                crossLinkerName = "DSS",
                crossLinkedProteinTypes = "Both",
                enzymeName = "Trypsin",
                misclevaged = "2",
                lowMass = "200",
                highMass = "5000",
                enzymeFileName = "src/main/resources/enzymes.txt";
        int minLen = 4,
                maxLen_for_combined = 40;
        // filtering of in silico peptides on peptide masses
        boolean does_link_to_itself = true, // if a peptide itself links to itself..
                isLabeled = true; //now it is heavy-labeled crosslinker
        CreateDatabase instance = new CreateDatabase(
                givenDBName,
                inSilicoPeptideDBName,
                cxDBName, // db related parameters
                crossLinkerName, // crossLinker name
                crossLinkedProteinTypes, // crossLinking type: Both/Inter/Intra
                enzymeName, enzymeFileName, misclevaged, // enzyme related parameters
                lowMass, highMass, // filtering of in silico peptides on peptide masses
                minLen, // minimum length for each in silico digested peptide
                maxLen_for_combined, // maximum lenght for a length for cross linked peptide (maxLen<len(A)+len(B)
                does_link_to_itself, // if a peptide itself links to itself..
                isLabeled,
                false, false, false);
        HashMap<String, Integer> acc_and_length = instance.getAccession_and_length(givenDBName);
        assertEquals(4, acc_and_length.size());
        assertTrue(acc_and_length.containsKey("P17152"));
        assertTrue(acc_and_length.containsKey("P17154"));
        assertTrue(acc_and_length.containsKey("P17155"));
        assertTrue(acc_and_length.containsKey("P17156"));

        assertEquals(new Integer(38), acc_and_length.get("P17152"));
        assertEquals(new Integer(34), acc_and_length.get("P17154"));
        assertEquals(new Integer(47), acc_and_length.get("P17155"));
        assertEquals(new Integer(80), acc_and_length.get("P17156"));
    }

    /**
     * Test of construct_header_and_sequence method, of class CreateDatabase.
     */
    @Test
    public void testConstruct_header_and_sequence() throws Exception {
        System.out.println("prepare_header_and_sequence");
        Protein proteinA = new Protein("Q9QXS1-3_REVERSED(53-65)", "YVKNMDILMPKHR"),
                proteinB = new Protein("Q9QXS1-3(181-215)", "HKPMLIDMNKVYRQTNLENLDQAFSVAERDLGVTR");
        Header h = Header.parseFromFASTA(">generic|Q9QXS1-3_REVERSED(53-65)| Protein A");
        proteinA.setHeader(h);

        h = Header.parseFromFASTA(">generic|Q9QXS1-3(181-215)| Protein B");
        proteinB.setHeader(h);

        int positionA = 2,
                positionB = 9;
        LinkedResidue start = new LinkedResidue(proteinA, positionA, LinkedResidueType.K, true, true),
                next = new LinkedResidue(proteinB, positionB, LinkedResidueType.K, true, true);
        StringBuilder[] prepare_header_and_sequence = CreateDatabase.construct_header_and_sequence(start, next);
        assertEquals("Q9QXS1-3(181-215)_10_Q9QXS1-3_REVERSED(53-65)_3", prepare_header_and_sequence[0].toString());
        assertEquals("HKPMLIDMNK*VYRQTNLENLDQAFSVAERDLGVTR|YVK*NMDILMPKHR", prepare_header_and_sequence[1].toString());

        // SECOND TEST-Both reversed..
        h = Header.parseFromFASTA(">generic|Q9QXS1-3_REVERSED(53-65)| Protein A");
        proteinA.setHeader(h);
        h = Header.parseFromFASTA(">generic|Q9QXS1-3_REVERSED(181-215)| Protein B");
        proteinB.setHeader(h);

        positionA = 2;
        positionB = 9;
        start = new LinkedResidue(proteinA, positionA, LinkedResidueType.K, true, true);
        next = new LinkedResidue(proteinB, positionB, LinkedResidueType.K, true, true);
        prepare_header_and_sequence = CreateDatabase.construct_header_and_sequence(start, next);
        assertEquals("Q9QXS1-3_REVERSED(181-215)_10_Q9QXS1-3_REVERSED(53-65)_3", prepare_header_and_sequence[0].toString());
        assertEquals("HKPMLIDMNK*VYRQTNLENLDQAFSVAERDLGVTR|YVK*NMDILMPKHR", prepare_header_and_sequence[1].toString());

        // THIRD TEST-Both shuffled..
        h = Header.parseFromFASTA(">generic|Q9QXS1-3_SHUFFLED(53-65)| Protein A");
        proteinA.setHeader(h);
        h = Header.parseFromFASTA(">generic|Q9QXS1-3_SHUFFLED(181-215)| Protein B");
        proteinB.setHeader(h);

        positionA = 2;
        positionB = 9;
        start = new LinkedResidue(proteinA, positionA, LinkedResidueType.K, true, true);
        next = new LinkedResidue(proteinB, positionB, LinkedResidueType.K, true, true);
        prepare_header_and_sequence = CreateDatabase.construct_header_and_sequence(start, next);
        assertEquals("Q9QXS1-3_SHUFFLED(181-215)_10_Q9QXS1-3_SHUFFLED(53-65)_3", prepare_header_and_sequence[0].toString());
        assertEquals("HKPMLIDMNK*VYRQTNLENLDQAFSVAERDLGVTR|YVK*NMDILMPKHR", prepare_header_and_sequence[1].toString());

        // FOURTH TEST-Starting with Methionine..
        proteinA = new Protein("P62158(1-9)", "MDILMPKHR");
        proteinB = new Protein("Q9QXS1-3(181-215)", "HKPMLIDMNKVYRQTNLENLDQAFSVAERDLGVTR");
        h = Header.parseFromFASTA(">generic|P62158(1-9)| Protein A");
        proteinA.setHeader(h);

        h = Header.parseFromFASTA(">generic|Q9QXS1-3(181-215)| Protein B");
        proteinB.setHeader(h);

        positionA = 0;
        positionB = 9;
        start = new LinkedResidue(proteinA, positionA, LinkedResidueType.M, true, true);
        next = new LinkedResidue(proteinB, positionB, LinkedResidueType.K, true, true);
        prepare_header_and_sequence = CreateDatabase.construct_header_and_sequence(start, next);
        assertEquals("Q9QXS1-3(181-215)_10_P62158(1-9)_1", prepare_header_and_sequence[0].toString());
        assertEquals("HKPMLIDMNK*VYRQTNLENLDQAFSVAERDLGVTR|M*DILMPKHR", prepare_header_and_sequence[1].toString());

        // Now starting with a peptide after methionine..
        positionA = 0;
        positionB = 9;
        start = new LinkedResidue(proteinA, positionA, LinkedResidueType.NTerminiIncludesM, true, true);
        next = new LinkedResidue(proteinB, positionB, LinkedResidueType.K, true, true);
        prepare_header_and_sequence = CreateDatabase.construct_header_and_sequence(start, next);
        assertEquals("Q9QXS1-3(181-215)_10_P62158(2-9)_1", prepare_header_and_sequence[0].toString());
        assertEquals("HKPMLIDMNK*VYRQTNLENLDQAFSVAERDLGVTR|D*ILMPKHR", prepare_header_and_sequence[1].toString());

        // now starting with a peptide with containing n-termini
        proteinA = new Protein("P62158(1-9)", "GDILMPKHR");
        h = Header.parseFromFASTA(">generic|P62158(1-9)| Protein A");
        proteinA.setHeader(h);

        positionA = 0;
        positionB = 9;
        start = new LinkedResidue(proteinA, positionA, LinkedResidueType.NTerminus, true, true);
        next = new LinkedResidue(proteinB, positionB, LinkedResidueType.K, true, true);
        prepare_header_and_sequence = CreateDatabase.construct_header_and_sequence(start, next);
        assertEquals("Q9QXS1-3(181-215)_10_P62158(1-9)_1", prepare_header_and_sequence[0].toString());
        assertEquals("HKPMLIDMNK*VYRQTNLENLDQAFSVAERDLGVTR|G*DILMPKHR", prepare_header_and_sequence[1].toString());

        // both of them have M on protein n-termini
        proteinA = new Protein("P62158(1-9)", "MDILMPKHR");
        proteinB = new Protein("P62158(1-9)", "MDILMPKHR");
        h = Header.parseFromFASTA(">generic|P62158(1-9)| Protein A");
        proteinA.setHeader(h);
        proteinB.setHeader(h);
        start = new LinkedResidue(proteinA, 0, LinkedResidueType.M, true, true);
        next = new LinkedResidue(proteinB, 0, LinkedResidueType.M, true, true);
        prepare_header_and_sequence = CreateDatabase.construct_header_and_sequence(start, next);
        assertEquals("P62158(1-9)_1_P62158(1-9)_1", prepare_header_and_sequence[0].toString());
        assertEquals("M*DILMPKHR|M*DILMPKHR", prepare_header_and_sequence[1].toString());

        proteinA = new Protein("P62158-3_REVERSED(1-9)", "MDILMPKHR");
        proteinB = new Protein("P62158-3_REVERSED(1-9)", "MDILMPKHR");
        h = Header.parseFromFASTA(">generic|P62158-3_REVERSED(1-9)| Protein A");
        proteinA.setHeader(h);
        proteinB.setHeader(h);
        start = new LinkedResidue(proteinA, 0, LinkedResidueType.M, true, true);
        next = new LinkedResidue(proteinB, 0, LinkedResidueType.M, true, true);
        prepare_header_and_sequence = CreateDatabase.construct_header_and_sequence(start, next);
        assertEquals("P62158-3_REVERSED(1-9)_1_P62158-3_REVERSED(1-9)_1", prepare_header_and_sequence[0].toString());
        assertEquals("M*DILMPKHR|M*DILMPKHR", prepare_header_and_sequence[1].toString());

        proteinA = new Protein("P62158-3_REVERSED(1-9)", "MDILMPKHR");
        proteinB = new Protein("P62158-3_REVERSED(1-9)", "MDILMPKHRRRR");
        h = Header.parseFromFASTA(">generic|P62158-3_REVERSED(1-9)| Protein A");
        proteinA.setHeader(h);
        proteinB.setHeader(h);
        start = new LinkedResidue(proteinA, 0, LinkedResidueType.NTerminiIncludesM, true, true);
        next = new LinkedResidue(proteinB, 0, LinkedResidueType.M, true, true);
        prepare_header_and_sequence = CreateDatabase.construct_header_and_sequence(start, next);
        assertEquals("P62158-3_REVERSED(1-9)_1_P62158-3_REVERSED(2-9)_1", prepare_header_and_sequence[0].toString());
        assertEquals("M*DILMPKHRRRR|D*ILMPKHR", prepare_header_and_sequence[1].toString());

        proteinA = new Protein("P62158-3_REVERSED(1-9)", "MDILMPKHR");
        proteinB = new Protein("P62158-4_REVERSED(1-9)", "MDWWMPKHR");
        h = Header.parseFromFASTA(">generic|P62158-3_REVERSED(1-9)| Protein A");
        Header h2 = Header.parseFromFASTA(">generic|P62158-4_REVERSED(1-9)| Protein A");
        proteinA.setHeader(h);
        proteinB.setHeader(h2);
        start = new LinkedResidue(proteinA, 0, LinkedResidueType.M, true, true);
        next = new LinkedResidue(proteinB, 0, LinkedResidueType.M, true, true);
        prepare_header_and_sequence = CreateDatabase.construct_header_and_sequence(start, next);
        assertEquals("P62158-4_REVERSED(1-9)_1_P62158-3_REVERSED(1-9)_1", prepare_header_and_sequence[0].toString());
        assertEquals("M*DWWMPKHR|M*DILMPKHR", prepare_header_and_sequence[1].toString());

        proteinA = new Protein("P62158-3_REVERSED(1-9)", "MDWWWWWHR");
        proteinB = new Protein("P62158-4_REVERSED(1-9)", "MDWWMPKHR");
        h = Header.parseFromFASTA(">generic|P62158-3_REVERSED(1-9)| Protein A");
        h2 = Header.parseFromFASTA(">generic|P62158-4_REVERSED(1-9)| Protein A");
        proteinA.setHeader(h);
        proteinB.setHeader(h2);
        start = new LinkedResidue(proteinA, 0, LinkedResidueType.M, true, true);
        next = new LinkedResidue(proteinB, 0, LinkedResidueType.M, true, true);
        prepare_header_and_sequence = CreateDatabase.construct_header_and_sequence(start, next);
        assertEquals("P62158-3_REVERSED(1-9)_1_P62158-4_REVERSED(1-9)_1", prepare_header_and_sequence[0].toString());
        assertEquals("M*DWWWWWHR|M*DWWMPKHR", prepare_header_and_sequence[1].toString());
    }

    /**
     * Test of reverse method, of class CreateDatabase.
     */
    @Test
    public void testReverse() throws Exception {
        System.out.println("reverse");
        StringBuilder[] header_and_sequence = new StringBuilder[2];
        header_and_sequence[0] = new StringBuilder("P62158-3_REVERSED(1-9)_1_P62158-3_REVERSED(2-9)_1");
        header_and_sequence[1] = new StringBuilder("M*DILMPKHRRRR|D*ILMPKHR");
        StringBuilder rev = CreateDatabase.reverse(header_and_sequence);
        System.out.println(rev.toString());
        assertEquals("P62158-3_REVERSED(2-9)_1_P62158-3_REVERSED(1-9)_1", rev.toString());

        header_and_sequence = new StringBuilder[2];
        header_and_sequence[0] = new StringBuilder("P62158-3(1-9)_1_P62158-3(2-9)_1");
        header_and_sequence[1] = new StringBuilder("M*DILMPKHRRRR|D*ILMPKHR");
        rev = CreateDatabase.reverse(header_and_sequence);
        assertEquals("P62158-3(2-9)_1_P62158-3(1-9)_1", rev.toString());

        header_and_sequence = new StringBuilder[2];
        header_and_sequence[0] = new StringBuilder("P62158-3(1-9)_1_P62158-3_REVERSED(2-9)_1");
        header_and_sequence[1] = new StringBuilder("M*DILMPKHRRRR|D*ILMPKHR");
        rev = CreateDatabase.reverse(header_and_sequence);
        assertEquals("P62158-3_REVERSED(2-9)_1_P62158-3(1-9)_1", rev.toString());
    }

    /**
     * Test of isDBFormatRight(), of class CreateDatabase
     */
    @Test
    public void testIsDBFormatRight() throws Exception {
        System.out.println("testIsDBFormatRight");
        String input1 = "Data/Test/database/format/art_input1.fasta",
                input2 = "Data/Test/database/format/art_input2.fasta",
                input3 = "Data/Test/database/format/art_input3.fasta",
                input4 = "Data/Test/database/format/art_input4.fasta";
        boolean isInput1Right = CreateDatabase.isDBFormatRight(input1),
                isInput2Right = CreateDatabase.isDBFormatRight(input2),
                isInput3Right = CreateDatabase.isDBFormatRight(input3),
                isInput4Right = CreateDatabase.isDBFormatRight(input4);

        assertTrue(isInput1Right);
        assertFalse(isInput2Right);
        assertFalse(isInput3Right);
        assertFalse(isInput4Right);
    }

    
}
