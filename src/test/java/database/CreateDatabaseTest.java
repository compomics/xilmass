///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package database;
//
//import com.compomics.dbtoolkit.io.DBLoaderLoader;
//import com.compomics.dbtoolkit.io.interfaces.DBLoader;
//import com.compomics.util.protein.Protein;
//import crossLinker.CrossLinkerName;
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.util.HashMap;
//import junit.framework.TestCase;
//
///**
// *
// * @author Sule
// */
//public class CreateDatabaseTest extends TestCase {
//
//    public CreateDatabaseTest(String testName) {
//        super(testName);
//    }
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//    }
//
//    @Override
//    protected void tearDown() throws Exception {
//        super.tearDown();
//    }
//
//    /**
//     * Test of digest_insilico method, of class CreateDatabase.
//     */
//    public void testDigest_insilico() throws Exception {
//        System.out.println("digest_insilico");
//        // Here construct...
//        String inputFileName = "Data/Test/database/art_testing1Ent.fasta",
//                inputFile2Name = "Data/Test/database/art_testing1Ent_K.fasta",
//                outputFileName = "Data/Test/database/del/test_output_digest_insilico",
//                crossLinker = "DSS",
//                enzymeName = "Trypsin";
//        int minLen = 4,
//                maxLen_for_combined = 40;
//        CreateDatabase instance = new CreateDatabase(inputFileName, outputFileName, crossLinker, "Inter", minLen, maxLen_for_combined),
//                instance2 = new CreateDatabase(inputFile2Name, outputFileName, crossLinker, "Inter", minLen, maxLen_for_combined);
//
//        instance.digest_insilico();
//        DBLoader loader = DBLoaderLoader.loadDB(new File(outputFileName));
//        Protein start_protein = null;
//        long countNumberOfEntries = loader.countNumberOfEntries();
//        assertEquals(6, countNumberOfEntries);
//        while ((start_protein = loader.nextProtein()) != null) {
//            System.out.println(start_protein.getSequence().getSequence());
//        }
//
//        instance2.digest_insilico();
//        DBLoader loader2 = DBLoaderLoader.loadDB(new File(outputFileName));
//        start_protein = null;
//        countNumberOfEntries = loader2.countNumberOfEntries();
//        assertEquals(10, countNumberOfEntries);
//        while ((start_protein = loader2.nextProtein()) != null) {
//            System.out.println(start_protein.getSequence().getSequence());
//        }
//    }
//
//    /**
//     * Test of create_crossLinkedPeptides method, of class CreateDatabase.
//     */
//    public void testCreate_crossLinkedPeptides() throws Exception {
//        System.out.println("create_crossLinkedPeptides");
//
//        // Here construct...
//        String inputFileName = "Data/Test/database/art_testing1Ent.fasta",
//                inputFile2Name = "Data/Test/database/art_testing1Ent_K.fasta",
//                outputFileName = "Data/Test/database/del/test_output_Create_crossLinkedPeptides",
//                crossLinker = "DSS",
//                enzymeName = "Trypsin";
//        int minLen = 4,
//                maxLen_for_combined = 40;
//        CreateDatabase instance = new CreateDatabase(inputFileName, outputFileName, crossLinker, "Inter", minLen, maxLen_for_combined),
//                instance2 = new CreateDatabase(inputFile2Name, outputFileName, crossLinker, "Inter", minLen, maxLen_for_combined),
//                instance3 = new CreateDatabase(inputFile2Name, outputFileName, crossLinker, "Intra", minLen, maxLen_for_combined);
//
//        instance.digest_insilico();
//        instance.create_crossLinkedPeptides();
//        String crossLinkedDBName = instance.getCrossLinkedDB().getName();
//
//        BufferedReader br = new BufferedReader(new FileReader(crossLinkedDBName));
//        int entries = 0;
//        String line = "";
//        while ((line = br.readLine()) != null) {
//            System.out.println(line);
//            if (line.startsWith(">")) {
//                entries++;
//            }
//        }
//        assertEquals(0, entries);
//
//        instance2.digest_insilico();
//        instance2.create_crossLinkedPeptides();
//        crossLinkedDBName = instance2.getCrossLinkedDB().getName();
//        br = new BufferedReader(new FileReader(crossLinkedDBName));
//        entries = 0;
//        line = "";
//        while ((line = br.readLine()) != null) {
//            if (line.startsWith(">")) {
//                entries++;
//            } else {
//                System.out.println(line);
//
//            }
//        }
//        assertEquals(60, entries);
//
//        instance3.digest_insilico();
//        instance3.create_crossLinkedPeptides();
//        crossLinkedDBName = instance3.getCrossLinkedDB().getName();
//        br = new BufferedReader(new FileReader(crossLinkedDBName));
//        entries = 0;
//        line = "";
//        while ((line = br.readLine()) != null) {
//            if (line.startsWith(">")) {
//                entries++;
//            } else {
//                System.out.println(line);
//
//            }
//        }
//        assertEquals(0, entries);
//    }
//
//    /**
//     * Test of generate_peptide_combinations method, of class CreateDatabase.
//     */
//    public void testGenerate_peptide_combinations() throws Exception {
//        System.out.println("generate_peptide_combinations");
//        Protein next_protein = new Protein("P2_(10-18)", "RLGKPGKMN");
//
//        CrossLinkerName linker = CrossLinkerName.DSS;
//        String startCoreHeader = "P1_(4-9)";
//        String mod_startSeq = "AWGRK*R";
//        BufferedWriter bw = new BufferedWriter(new FileWriter("test.txt"));
//        String possible_linked_aa = "RLGKPGKMN";
//        String inputFileName = "Data/Test/database/art_testing1Ent.fasta",
//                inputFile2Name = "Data/Test/database/art_testing1Ent_K.fasta",
//                outputFileName = "Data/Test/database/del/test_output_generatePeptideCombinations",
//                crossLinker = "DSS";
//        int minLen = 4,
//                maxLen_for_combined = 40;
//        CreateDatabase instance = new CreateDatabase(inputFileName, outputFileName, crossLinker, "Inter", minLen, maxLen_for_combined);
//        instance.digest_insilico();
//
//        HashMap<String, String> pep_combinations = instance.generate_peptide_combinations(next_protein, linker, "Protein1", startCoreHeader, mod_startSeq, bw, possible_linked_aa, 4);
//        String header = "P1_(4-9)_5_P2_(10-18)_7",
//                sequence = "AWGRK*R|RLGKPGK*MN";
//        int counting = 0;
//        for (String key : pep_combinations.keySet()) {
//            if (pep_combinations.get(key).equals(sequence)) {
//                counting++;
//            }
//        }
//        assertEquals(1, counting);
//
//        counting = 0;
//        for (String key : pep_combinations.keySet()) {
//            if (key.startsWith(header)) {
//                counting++;
//            }
//        }
//        assertEquals(1, counting);
//
//        counting = 0;
//        sequence = "AWGRK*R|RLGK*PGKMN";
//        header = "P1_(4-9)_5_P2_(10-18)_4";
//        for (String key : pep_combinations.keySet()) {
//            if (pep_combinations.get(key).equals(sequence)) {
//                counting++;
//            }
//        }
//        assertEquals(1, counting);
//
//        counting = 0;
//        for (String key : pep_combinations.keySet()) {
//            if (key.startsWith(header)) {
//                counting++;
//            }
//        }
//        assertEquals(1, counting);
//
//        CreateDatabase instance2 = new CreateDatabase(inputFileName, outputFileName, crossLinker, "Inter", minLen, maxLen_for_combined, true);
//        instance.digest_insilico();
//        HashMap<String, String> pep_combinations2 = instance2.generate_peptide_combinations(next_protein, linker, "Protein2", startCoreHeader, mod_startSeq, bw, possible_linked_aa, 4);
//
//        counting = 0;
//        header = "P1_(4-9)_5_P2_(10-18)_3_inverted";
//        sequence = "AWGRK*R|NMK*GPKGLR";
//        for (String key : pep_combinations2.keySet()) {
//            if (pep_combinations2.get(key).equals(sequence)) {
//                counting++;
//            }
//        }
//        assertEquals(1, counting);
//
//        counting = 0;
//        for (String key : pep_combinations2.keySet()) {
//            if (key.startsWith(header)) {
//                counting++;
//            }
//        }
//        assertEquals(1, counting);
//
//        counting = 0;
//        header = "P1_(4-9)_5_P2_(10-18)_6_inverted";
//        sequence = "AWGRK*R|NMKGPK*GLR";
//        for (String key : pep_combinations2.keySet()) {
//            if (pep_combinations2.get(key).equals(sequence)) {
//                counting++;
//            }
//        }
//        assertEquals(1, counting);
//
//        counting = 0;
//        for (String key : pep_combinations2.keySet()) {
//            if (key.startsWith(header)) {
//                counting++;
//            }
//        }
//        assertEquals(1, counting);
//    }
//
//    /**
//     * Test of construct method, of class CreateDatabase.
//     */
//    public void testConstruct() throws Exception {
//        System.out.println("construct");
//        System.out.println("construct");
//        String inputFileName = "Data/Test/database/art_testing1Ent.fasta",
//                inputFile2Name = "Data/Test/database/art_testing1Ent_K.fasta",
//                outputFileName = "Data/Test/database/del/test_output_construct",
//                crossLinker = "DSS";
//        int minLen = 4,
//                maxLen_for_combined = 40;
//        CreateDatabase instance = new CreateDatabase(inputFileName, outputFileName, crossLinker, "Inter", minLen, maxLen_for_combined, true);
//        CreateDatabase instance2 = new CreateDatabase(inputFile2Name, "Data/Test/database/del/test_output_construct2", crossLinker, "Intra", minLen, maxLen_for_combined);
//
//        instance.construct();
//        instance2.construct();
//        // TODO review the generated test code and remove the default call to fail.
////        fail("The test case is a prototype.");
//    }
//
//}
