/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.db.robustness;

import com.compomics.dbtoolkit.io.DBLoaderLoader;
import com.compomics.dbtoolkit.io.interfaces.DBLoader;
import com.compomics.util.protein.Protein;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import start.Start;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public class CheckRobustnessDecoyApproach {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException, Exception {
        // generate X number of database.

        File targetFasta = new File("C:/Users/Sule/Documents/PhD/XLinked/databases/cam_plectin.fasta"), // fasta file with targets
                decoyFasta = new File("C:/Users/Sule/Documents/PhD/XLinked/databases/uniprot_2261_Pfuriosus.fasta"), // decoy fasta file 
                infoFile = new File("C:/Users/Sule/Documents/PhD/XLinked/databases/td_target_2Pfus_info.txt");
        String targetDecoyFileStartsWith = "C:/Users/Sule/Documents/PhD/XLinked/databases/td_target_2Pfus_",
                dbFolder = "C:/Users/Sule/Documents/PhD/XLinked/databases/";
        int[] proteinLengths = {149, 242};
        int dbs_num = 20, // how many target-decoy dbs are going to be generated
                first_protein_index = 10,
                second_protein_index = 11,
                spectrum_name_index = 2,
                score_index = 9;
        String[] target_protein_names = {"Q15149", "P62158"};
        String resultFile = "result";

        if (args.length != 0) {
            targetFasta = new File(args[0]); // fasta file with targets
            decoyFasta = new File(args[1]); // decoy fasta file 
            infoFile = new File(args[2]);
            targetDecoyFileStartsWith = args[3];
            dbFolder = args[4];
            dbs_num = Integer.parseInt(args[5]); // how many target-decoy dbs are going to be generated
            resultFile = args[6] + "result";
        }

        // now generate dsb_num number of target_decoy databases with different decoys and write information for selection on infoFile 
        ArrayList<File> tds = generateDB_Improved(targetFasta, decoyFasta, targetDecoyFileStartsWith, dbs_num, proteinLengths, target_protein_names, infoFile);

//        ArrayList<File> tds = generateDB_Improved(targetFasta, decoyFasta, targetDecoyFileStartsWith, dbs_num, proteinLengths, target_protein_names, infoFile);
        // run each database with searh algorithm
        for (File tmp_target_decoy_db : tds) {
            String tmpResultFileLabeled = resultFile + "_labeled_" + tmp_target_decoy_db.getName() + ".txt",
                    tmpResultFileNoLabeled = resultFile + "_Nolabeled_" + tmp_target_decoy_db.getName() + ".txt",
                    outputFile = resultFile + "_" + tmp_target_decoy_db.getName() + ".txt";

            // run with labeling approach
            String[] givens = {"labeled", tmp_target_decoy_db.getPath(), dbFolder, tmpResultFileLabeled};
            Start.main(givens);

            // run with nonlabeling approach
            String[] givens2 = {"nolabeled", tmp_target_decoy_db.getPath(), dbFolder, tmpResultFileNoLabeled};
            Start.main(givens2);

            // merge light/heavy labeled results together
            mergeFile(tmpResultFileLabeled, tmpResultFileNoLabeled, outputFile,
                    first_protein_index, second_protein_index,
                    target_protein_names);
        }
    }

    /**
     * This method start with reading a file called tmpResultFileLabeled (Make
     * sure that this is indeed light labeled crosslinked ones). For each XPSM,
     * it checks if an assigned protein is target/decoy/half-decoy. Then, it
     * reads heavy labeled crosslinked results and again assign their
     * targetness. It writes all on a given outputfile.
     *
     *
     * @param tmpResultFileLabeled is light labeled result
     * @param tmpResultFileNoLabeled is heavy labeled result
     * @param outputFile a mergerd output file
     * @param first_protein_index index of proteinA on a given result file
     * @param second_protein_index index of proteinB on a given result file
     * @param target_protein_names a String array of target proteins
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void mergeFile(String tmpResultFileLabeled, String tmpResultFileNoLabeled, String outputFile,
            int first_protein_index, int second_protein_index, String[] target_protein_names) throws FileNotFoundException, IOException {
        // write out results together
        // starts with the first input file - heavy labeled one...
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
        String labeled = "heavy";
        writeOutput(tmpResultFileLabeled, false, bw, first_protein_index, second_protein_index, target_protein_names, labeled);

        // now write the second input file - ligth labeled cross linker
        labeled = "light";
        writeOutput(tmpResultFileNoLabeled, true, bw, first_protein_index, second_protein_index, target_protein_names, labeled);
        // now input file is ready and close it
        bw.close();
    }

    private static void writeOutput(String tmpResultFileLabeled, boolean isTitleWriten, BufferedWriter bw,
            int first_protein_index, int second_protein_index, String[] target_protein_names, String labeled) throws FileNotFoundException, IOException {
        // read each file for labeled/nonLabeled result
        String line = "";
        BufferedReader br = new BufferedReader(new FileReader(new File(tmpResultFileLabeled)));
        while ((line = br.readLine()) != null) {
            if (line.startsWith("Spectrum") && !isTitleWriten) {
                bw.write(line + "\t" + "indexProteinA" + "\t" + "indexProteinB" + "\t" + "Target_Decoy" + "\t" + "Labeled" + "\n");
                isTitleWriten = true;
            } else if (!line.startsWith("Spectrum")) {
                String[] split = line.split("\t");
                String proteinAInfo = split[first_protein_index],
                        proteinBInfo = split[second_protein_index],
                        proteinAName = proteinAInfo.substring(0, proteinAInfo.indexOf("(")),
                        proteinBName = proteinBInfo.substring(0, proteinBInfo.indexOf("(")),
                        indexProteinA = proteinAInfo.substring(proteinAInfo.indexOf("(") + 1, proteinAInfo.indexOf(")")),
                        indexProteinB = proteinBInfo.substring(proteinBInfo.indexOf("(") + 1, proteinBInfo.indexOf(")")),
                        targetType = getTargetType(proteinAName, proteinBName, target_protein_names);
                bw.write(line + "\t" + indexProteinA + "\t" + indexProteinB + "\t" + targetType + "\t" + labeled + "\n");
            }
        }
    }

    /**
     * This method returns a name of this crosslinked peptide. target decoy
     * half-decoy
     *
     * @param proteinAName
     * @param proteinBName
     * @param target_protein_names
     * @return
     */
    public static String getTargetType(String proteinAName, String proteinBName, String[] target_protein_names) {
        String type = "half-decoy",
                first_protein_name = target_protein_names[0],
                second_protein_name = target_protein_names[1];
        if ((proteinAName.equals(first_protein_name) || proteinAName.equals(second_protein_name))
                && (proteinBName.equals(first_protein_name) || proteinBName.equals(second_protein_name))) {
            type = "target";
        }
        if ((!proteinAName.equals(first_protein_name) && !proteinAName.equals(second_protein_name))
                && (!proteinBName.equals(first_protein_name) && !proteinBName.equals(second_protein_name))) {
            type = "decoy";
        }
        return type;
    }

    public static ArrayList<File> generateDB(File targetFasta, File decoyFasta, String targetDecoyFileStartsWith, int number_dbs, int[] expectedDecoyLengths, String[] target_protein_names, File infoFile) throws IOException {
        ArrayList<File> tds = new ArrayList<File>();
        BufferedWriter bw = new BufferedWriter(new FileWriter(infoFile));
        HashSet<Integer> indicesOfSelectedDecoys = new HashSet<Integer>();

        String title = "FileName" + "\t" + "FirstProteinLength" + "\t" + "FirstProteinAcc" + "\t" + "SecondProteinLength" + "\t" + "SecondProteinAcc" + "\t"
                + "Decoy1Info" + "\t" + "Decoy1Length" + "\t" + "Decoy1Info" + "\t" + "Decoy1Length" + "\n";
        bw.write(title);
        for (int db_num = 0; db_num < number_dbs; db_num++) {
            String fileName = targetDecoyFileStartsWith + db_num + ".fasta",
                    info = fileName + "\t" + expectedDecoyLengths[0] + "\t" + target_protein_names[0] + "\t" + expectedDecoyLengths[1] + "\t" + target_protein_names[1] + "\t"; // write info about target_decoy database...
            System.out.println(fileName);

            HashSet<Protein> decoys = new HashSet<Protein>();
            for (int expectedDecoyLength : expectedDecoyLengths) {
                DBLoader loader = DBLoaderLoader.loadDB(decoyFasta);
                Protein protein = null;
                ArrayList<Protein> proteins = new ArrayList<Protein>();
                // put all proteins in a list to randomly select one...  
                while ((protein = loader.nextProtein()) != null) {
                    proteins.add(protein);
                }
                // now select randomly one decoy
                Random r = new Random();
                int nextInt = r.nextInt(proteins.size());
                // making sure that selected decoys were not selected before...
                if (indicesOfSelectedDecoys.contains(nextInt)) {
                    while (!indicesOfSelectedDecoys.contains(nextInt)) {
                        nextInt = r.nextInt(proteins.size());
                    }
                }
                indicesOfSelectedDecoys.add(nextInt);
                Protein selectedDecoy = proteins.get(nextInt);
                String tmpAcc = selectedDecoy.getHeader().getAccession();
                int decoyLen = (int) selectedDecoy.getLength();
                // select a randomly selected the same length sequence.. 
                if (decoyLen == expectedDecoyLength) {
                    System.out.println("I am the same length...");
                    decoys.add(selectedDecoy);
                    info += "SameLength" + "_" + tmpAcc + "\t" + selectedDecoy.getLength() + "\t";
                }
                // if a randomly selected decoy has shorter or bigger than  5 aas
                if (decoyLen >= expectedDecoyLength - 5 && selectedDecoy.getLength() <= expectedDecoyLength + 5) {
                    System.out.println("I am a bit larger...");
                    decoys.add(selectedDecoy);
                    info += "CloseLength" + "_" + tmpAcc + "\t" + selectedDecoy.getLength() + "\t";
                }
                // if a randomly selected decoy has bigger than  5 aas
                if (decoyLen > expectedDecoyLength + 5) {
                    System.out.println("I am super bigggg");
                    cutDecoys(selectedDecoy, expectedDecoyLength);
                    decoys.add(selectedDecoy);
                    info += "LargerLength" + "_" + tmpAcc + "\t" + selectedDecoy.getLength() + "\t";
                }
                // if a randomly selected decoy has smaller than  5 aas
                if (decoyLen < expectedDecoyLength - 5) {
                    System.out.println("I am super small...");
                    int seqsToAdd = expectedDecoyLength - decoyLen;
                    // randomly select one decoy and paste all together to have enough size
                    String tmp = pasteDecoys(selectedDecoy, indicesOfSelectedDecoys, proteins, seqsToAdd);
                    selectedDecoy.getSequence().setSequence(tmp);
                    decoys.add(selectedDecoy);
                    info += "ShorterLength" + "_" + tmpAcc + "\t" + selectedDecoy.getLength() + "\t";
                }
            }
            // and now write them up on a fasta file
            File td = write(targetFasta, decoys, fileName);
            tds.add(td);
            info += "\n";
            bw.write(info);
        }
        bw.close();
        return tds;
    }

    /**
     * This is the one target/decoy strategy has been tested
     *
     *
     * @param targetFasta
     * @param decoyFasta
     * @param targetDecoyFileStartsWith
     * @param number_dbs
     * @param expectedDecoyLengths
     * @param target_protein_names
     * @param infoFile
     * @return
     * @throws IOException
     */
    public static ArrayList<File> generateDB_Improved(File targetFasta, File decoyFasta, String targetDecoyFileStartsWith, int number_dbs, int[] expectedDecoyLengths, String[] target_protein_names, File infoFile) throws IOException {
        // first fill a list with proteins
        DBLoader loader = DBLoaderLoader.loadDB(decoyFasta);
        Protein protein = null;
        ArrayList<Protein> proteins = new ArrayList<Protein>();
        // put all proteins in a list to randomly select one...  
        while ((protein = loader.nextProtein()) != null) {
            proteins.add(protein);
        }
        //list of tds files...
        ArrayList<File> tds = new ArrayList<File>();
        BufferedWriter bw = new BufferedWriter(new FileWriter(infoFile));
        // write title for info file
        String title = "FileName" + "\t" + "FirstProteinLength" + "\t" + "FirstProteinAcc" + "\t" + "SecondProteinLength" + "\t" + "SecondProteinAcc" + "\t"
                + "Decoy1Info" + "\t" + "Decoy1Length" + "\t" + "Decoy1Info" + "\t" + "Decoy1Length" + "\n";
        bw.write(title);
        HashMap<Integer, HashSet<Protein>> dbNum_proteinIndices = selectProteinsForDBs(number_dbs, targetDecoyFileStartsWith, expectedDecoyLengths, target_protein_names, proteins);
        for (int db_num = 0; db_num < number_dbs; db_num++) {
            String fileName = targetDecoyFileStartsWith + db_num + ".fasta",
                    info = fileName + "\t" + expectedDecoyLengths[0] + "\t" + target_protein_names[0] + "\t" + expectedDecoyLengths[1] + "\t" + target_protein_names[1] + "\t"; // write info about target_decoy database...
            System.out.println(fileName);
            // now select proteins from proteinIndices
            HashSet<Protein> tmp_selected_decoys = dbNum_proteinIndices.get(db_num),
                    decoysToWrite = new HashSet<Protein>();
            HashSet<Protein> decoys = new HashSet<Protein>();
            int i = 0;
            for (Protein selectedDecoy : tmp_selected_decoys) {
                decoys.add(selectedDecoy);
//                    Protein selectedDecoy = proteins.get(int_selectedDecoy);
                int expectedDecoyLength = expectedDecoyLengths[i];
                String tmpAcc = selectedDecoy.getHeader().getAccession();
                int decoyLen = (int) selectedDecoy.getLength();
                // select a randomly selected the same length sequence.. 
                if (decoyLen == expectedDecoyLength) {
                    System.out.println("I am the same length...");
                    decoysToWrite.add(selectedDecoy);
                    info += "SameLength" + "_" + tmpAcc + "\t" + selectedDecoy.getLength() + "\t";
                } // if a randomly selected decoy has shorter or bigger than  5 aas
                else if (decoyLen >= expectedDecoyLength - 5 && selectedDecoy.getLength() <= expectedDecoyLength + 5) {
                    System.out.println("I am a bit larger...");
                    decoysToWrite.add(selectedDecoy);
                    info += "CloseLength" + "_" + tmpAcc + "\t" + selectedDecoy.getLength() + "\t";
                } // if a randomly selected decoy has bigger than  5 aas
                else if (decoyLen > expectedDecoyLength + 5) {
                    System.out.println("I am super bigggg");
                    cutDecoys(selectedDecoy, expectedDecoyLength);
                    decoysToWrite.add(selectedDecoy);
                    info += "LargerLength" + "_" + tmpAcc + "\t" + selectedDecoy.getLength() + "\t";
                } // if a randomly selected decoy has smaller than  5 aas
                else if (decoyLen < expectedDecoyLength - 5) {
                    System.out.println("I am super small...");
                    int seqsToAdd = expectedDecoyLength - decoyLen;
                    // randomly select one decoy and paste all together to have enough size
                    String tmp = pasteDecoys2(selectedDecoy, decoys, proteins, seqsToAdd);
                    selectedDecoy.getSequence().setSequence(tmp);
                    decoysToWrite.add(selectedDecoy);
                    info += "ShorterLength" + "_" + tmpAcc + "\t" + selectedDecoy.getLength() + "\t";
                }
                i++;
            }
            info += "\n";
            File td = write(targetFasta, decoysToWrite, fileName);
            tds.add(td);
            // and now write them up on a fasta file
            bw.write(info);
        }
        bw.close();
        return tds;
    }

    public static ArrayList<File> generateDB_PossibleRedundancy(File targetFasta, File decoyFasta, String targetDecoyFileStartsWith, int number_dbs, int[] expectedDecoyLengths, String[] target_protein_names, File infoFile) throws IOException {
        // first fill a list with proteins
        DBLoader loader = DBLoaderLoader.loadDB(decoyFasta);
        Protein protein = null;
        ArrayList<Protein> proteins = new ArrayList<Protein>();
        // put all proteins in a list to randomly select one...  
        while ((protein = loader.nextProtein()) != null) {
            proteins.add(protein);
        }
        //list of tds files...
        ArrayList<File> tds = new ArrayList<File>();
        BufferedWriter bw = new BufferedWriter(new FileWriter(infoFile));
        // write title for info file
        String title = "FileName" + "\t" + "FirstProteinLength" + "\t" + "FirstProteinAcc" + "\t" + "SecondProteinLength" + "\t" + "SecondProteinAcc" + "\t"
                + "Decoy1Info" + "\t" + "Decoy1Length" + "\t" + "Decoy1Info" + "\t" + "Decoy1Length" + "\n";
        bw.write(title);
        HashMap<Integer, HashSet<Protein>> dbNum_proteinIndices = selectProteinsForDBs(number_dbs, targetDecoyFileStartsWith, expectedDecoyLengths, target_protein_names, proteins);
        for (int db_num = 0; db_num < number_dbs; db_num++) {
            String fileName = targetDecoyFileStartsWith + db_num + ".fasta",
                    info = fileName + "\t" + expectedDecoyLengths[0] + "\t" + target_protein_names[0] + "\t" + expectedDecoyLengths[1] + "\t" + target_protein_names[1] + "\t"; // write info about target_decoy database...
            System.out.println(fileName);
            // now select proteins from proteinIndices
            HashSet<Protein> tmp_selected_decoys = dbNum_proteinIndices.get(db_num),
                    decoysToWrite = new HashSet<Protein>();
            HashSet<Protein> decoys = new HashSet<Protein>();
            int i = 0;
            for (Protein selectedDecoy : tmp_selected_decoys) {
                decoys.add(selectedDecoy);
//                    Protein selectedDecoy = proteins.get(int_selectedDecoy);
                int expectedDecoyLength = expectedDecoyLengths[i];
                String tmpAcc = selectedDecoy.getHeader().getAccession();
                int decoyLen = (int) selectedDecoy.getLength();
                // select a randomly selected the same length sequence.. 
                if (decoyLen == expectedDecoyLength) {
                    System.out.println("I am the same length...");
                    decoysToWrite.add(selectedDecoy);
                    info += "SameLength" + "_" + tmpAcc + "\t" + selectedDecoy.getLength() + "\t";
                }
                // if a randomly selected decoy has shorter or bigger than  5 aas
                if (decoyLen >= expectedDecoyLength - 5 && selectedDecoy.getLength() <= expectedDecoyLength + 5) {
                    System.out.println("I am a bit larger...");
                    decoysToWrite.add(selectedDecoy);
                    info += "CloseLength" + "_" + tmpAcc + "\t" + selectedDecoy.getLength() + "\t";
                }
                // if a randomly selected decoy has bigger than  5 aas
                if (decoyLen > expectedDecoyLength + 5) {
                    System.out.println("I am super bigggg");
                    cutDecoys(selectedDecoy, expectedDecoyLength);
                    decoysToWrite.add(selectedDecoy);
                    info += "LargerLength" + "_" + tmpAcc + "\t" + selectedDecoy.getLength() + "\t";
                }
                // if a randomly selected decoy has smaller than  5 aas
                if (decoyLen < expectedDecoyLength - 5) {
                    System.out.println("I am super small...");
                    int seqsToAdd = expectedDecoyLength - decoyLen;
                    // randomly select one decoy and paste all together to have enough size
                    String tmp = pasteDecoys2(selectedDecoy, decoys, proteins, seqsToAdd);
                    selectedDecoy.getSequence().setSequence(tmp);
                    decoysToWrite.add(selectedDecoy);
                    info += "ShorterLength" + "_" + tmpAcc + "\t" + selectedDecoy.getLength() + "\t";
                }
                i++;
            }
            info += "\n";
            File td = write(targetFasta, decoysToWrite, fileName);
            tds.add(td);
            // and now write them up on a fasta file
            bw.write(info);
        }
        bw.close();
        return tds;
    }

    private static HashMap<Integer, HashSet<Protein>> selectProteinsForDBs(int number_dbs, String targetDecoyFileStartsWith, int[] expectedDecoyLengths, String[] target_protein_names, ArrayList<Protein> proteins) {
        // select proteins

        HashMap<Integer, HashSet<Protein>> dbNum_proteinIndices = new HashMap<Integer, HashSet<Protein>>();
        for (int db_num = 0; db_num < number_dbs; db_num++) {
            HashSet<Protein> listOfSelectedPros = new HashSet< Protein>();
            String fileName = targetDecoyFileStartsWith + db_num + ".fasta",
                    info = fileName + "\t" + expectedDecoyLengths[0] + "\t" + target_protein_names[0] + "\t" + expectedDecoyLengths[1] + "\t" + target_protein_names[1] + "\t"; // write info about target_decoy database...
            System.out.println(fileName);
            // now select randomly one decoy
            Random r = new Random();
            ArrayList<Protein> selectedPros = new ArrayList<Protein>();
            for (int exp : expectedDecoyLengths) {
                int nextInt = r.nextInt(proteins.size());
                Protein tmpPro = proteins.get(nextInt);
                selectedPros.add(tmpPro);
            }
            if (listOfSelectedPros.containsAll(selectedPros)) {
                // the set is already found before, so re-select them
                boolean isSame = true;
                while (isSame) {
                    selectedPros = new ArrayList<Protein>();
                    for (int exp : expectedDecoyLengths) {
                        int nextInt = r.nextInt(proteins.size());
                        Protein tmpPro = proteins.get(nextInt);
                        selectedPros.add(tmpPro);
                    }
                    if (!listOfSelectedPros.containsAll(selectedPros)) {
                        isSame = false;
                    }
                }
            } else {
                listOfSelectedPros.addAll(selectedPros);
            }
            dbNum_proteinIndices.put(db_num, listOfSelectedPros);
        }
        return dbNum_proteinIndices;
    }

    /**
     * This method cuts a decoy if it is longer than a target peptide with 5aas
     * It randomly select to cut either from N-termini or C-termini or from both
     * sides or directly inside..
     *
     * @param selectedDecoy
     * @param expectedDecoyLength
     */
    public static void cutDecoys(Protein selectedDecoy, int expectedDecoyLength) {
        Random r = new Random();
        String tmpSeq = "",
                selectedDecoySeq = selectedDecoy.getSequence().getSequence();
        int cutting_procedure = r.nextInt(4),
                //0-cut from N-termini
                //1-cut from C-termini
                //2-cut from both with randomly decided number of aa on the N-termini and then remaining on the C-termini
                //3-removing from any location inside..
                selecedDecoyLength = selectedDecoy.getSequence().getSequence().length(),
                aaSNumToCut = Math.abs(selecedDecoyLength - expectedDecoyLength - 1);
        // remove from N-termini 
        if (cutting_procedure == 0) {
            tmpSeq = selectedDecoySeq.substring(aaSNumToCut + 1);
            // remove from C-termini 
        } else if (cutting_procedure == 1) {
            tmpSeq = selectedDecoySeq.substring(0, expectedDecoyLength);
            // remove from both sides..
        } else if (cutting_procedure == 2) {
            int selectedTermini = r.nextInt(2), // N: 0 or C-termini:1 
                    aaSNumToRemoveFromSelectedTermini = r.nextInt(aaSNumToCut); // how many aas need to be removed from selected termini
            tmpSeq = cutDecoysBothSides(selectedTermini, aaSNumToRemoveFromSelectedTermini, aaSNumToCut, selectedDecoySeq);
            // remove decoy from inside
        } else if (cutting_procedure == 3) {
            // first select where to cut a decoy
            int cuttingIndex = r.nextInt(selecedDecoyLength);
            boolean isRightIndex = false;
            while (!isRightIndex) {
                // making sure that the selected part is inside of decoy
                if ((cuttingIndex + expectedDecoyLength) < selecedDecoyLength) {
                    isRightIndex = true;
                } else {
                    cuttingIndex = r.nextInt(selecedDecoyLength);
                }
            }
            tmpSeq = cutDecoysInside(cuttingIndex, expectedDecoyLength, selectedDecoySeq);
        }
        selectedDecoy.getSequence().setSequence(tmpSeq);
    }

    /**
     * This method removes a selected part at inside of a decoy. If a
     * cuttingIndex is very close to ends, it finds another index.. (Otherwise
     * it looks like cutfrom both side)
     *
     * @param cuttingIndex
     * @param expectedDecoyLength
     * @param selectedDecoySeq
     * @return
     */
    public static String cutDecoysInside(int cuttingIndex, int expectedDecoyLength, String selectedDecoySeq) {
        String tmpSeq = "";
        int selecedDecoyLength = selectedDecoySeq.length();
        if ((cuttingIndex + expectedDecoyLength) < selecedDecoyLength) {
            tmpSeq += selectedDecoySeq.substring(cuttingIndex, (cuttingIndex + expectedDecoyLength));
        } else {
            System.err.println("Error; it behaves like cut from both sides, change index! ");
            // meaning that decoy sequence is smaller, so cuts the end of C-termini and get first parts from N-termini to make one decoy
//            tmpSeq = selectedDecoySeq.substring(expectedDecoyLength - selecedDecoyLength + cuttingIndex, cuttingIndex);
        }
        return tmpSeq;
    }

    /**
     * This method removes aasNumToRemoveFromSelectedTermini from
     * selectedTermini and then remove the remaining ones from other termini
     *
     * @param selectedTermini:0-Ntermini 1-Ctermini
     * @param aaSNumToRemoveFromSelectedTermini how many aas need to be removed
     * from selected termini
     * @param aaSNumToRemove how many aas need to be removed overall (so bigger
     * than aaSNumToRemoveFromSelectedTermini)
     * @param selectedDecoySeq selected decoy sequence
     * @return
     */
    public static String cutDecoysBothSides(int selectedTermini, int aaSNumToRemoveFromSelectedTermini, int aaSNumToRemove, String selectedDecoySeq) {
        String tmpSeq = "";
        int selecedDecoyLength = (int) selectedDecoySeq.length();
        if (selectedTermini == 0) { // remove from n-termini with removeDiff
            tmpSeq = selectedDecoySeq.substring(aaSNumToRemoveFromSelectedTermini, selecedDecoyLength - aaSNumToRemove + aaSNumToRemoveFromSelectedTermini - 1);
        } else if (selectedTermini == 1) { // remove from c-termini with removeDiff
            tmpSeq = selectedDecoySeq.substring(aaSNumToRemove - aaSNumToRemoveFromSelectedTermini, selecedDecoyLength - aaSNumToRemoveFromSelectedTermini - 1);
        }
        // remove from inside
        return tmpSeq;
    }

    /**
     * This method pastes some part of another randomly chosen decoy to paste
     * into selectedDecoy.
     *
     * @param proteins a list of proteins on decoy database
     * @param selectedDecoy a decoy in selection
     * @param expLength a length of target peptide
     * @param indicesOfSelectedDecoys a list of indices for selected decoys
     */
    public static String pasteDecoys2(Protein selectedDecoy, HashSet<Protein> indicesOfSelectedDecoys, ArrayList<Protein> proteins, int seqNumToAdd) {
        // first randomly select a decoy and make sure that it is not already selected on
        String selectedDecoyStr = selectedDecoy.getSequence().getSequence();
        int selectedDecoyLength = (int) selectedDecoyStr.length(),
                expectedLen = selectedDecoyLength + seqNumToAdd;
        // make sure that after pasting a new entry, a selected decoy now has expected len..
        while (selectedDecoyLength < expectedLen) {
            Random r = new Random();
            int nextInt = r.nextInt(proteins.size());// randomly pick one protein 
            Protein tmpProtein = proteins.get(nextInt);
            // making sure that selected decoys were not selected before...
            if (indicesOfSelectedDecoys.contains(tmpProtein)) {
                while (!indicesOfSelectedDecoys.contains(tmpProtein)) {
                    nextInt = r.nextInt(proteins.size());
                }
            }
            tmpProtein = proteins.get(nextInt);
            indicesOfSelectedDecoys.add(tmpProtein);
            String toPasteDecoy = tmpProtein.getSequence().getSequence();

            // randomly find an index to cut toAdd
            int toPasteDecoyLength = (int) toPasteDecoy.length(),
                    indexOnToPasteDecoy = r.nextInt(toPasteDecoyLength);
            String subToAddStr = "";
            if (indexOnToPasteDecoy + seqNumToAdd < toPasteDecoyLength) {
                subToAddStr = toPasteDecoy.substring(indexOnToPasteDecoy, indexOnToPasteDecoy + seqNumToAdd);
            } else {
                subToAddStr = toPasteDecoy.substring(indexOnToPasteDecoy);
            }
            int indexOnSelectedDecoy = r.nextInt(selectedDecoyLength); // decide where you want to insert
            selectedDecoyStr = performPastingToDecoy(selectedDecoyStr, indexOnSelectedDecoy, subToAddStr);
            selectedDecoyLength = selectedDecoyStr.length();
            seqNumToAdd = expectedLen - selectedDecoyLength;
        }
        return selectedDecoyStr;
    }

    /**
     * This method pastes some part of another randomly chosen decoy to paste
     * into selectedDecoy.
     *
     * @param proteins a list of proteins on decoy database
     * @param selectedDecoy a decoy in selection
     * @param expLength a length of target peptide
     * @param indicesOfSelectedDecoys a list of indices for selected decoys
     */
    public static String pasteDecoys(Protein selectedDecoy, HashSet<Integer> indicesOfSelectedDecoys, ArrayList<Protein> proteins, int seqNumToAdd) {
        // first randomly select a decoy and make sure that it is not already selected on
        String selectedDecoyStr = selectedDecoy.getSequence().getSequence();
        int selectedDecoyLength = (int) selectedDecoyStr.length(),
                expectedLen = selectedDecoyLength + seqNumToAdd;
        // make sure that after pasting a new entry, a selected decoy now has expected len..
        while (selectedDecoyLength < expectedLen) {
            Random r = new Random();
            int nextInt = r.nextInt(proteins.size());// randomly pick one protein 
            // making sure that selected decoys were not selected before...
            if (indicesOfSelectedDecoys.contains(nextInt)) {
                while (!indicesOfSelectedDecoys.contains(nextInt)) {
                    nextInt = r.nextInt(proteins.size());
                }
            }
            indicesOfSelectedDecoys.add(nextInt);
            String toPasteDecoy = proteins.get(nextInt).getSequence().getSequence();

            // randomly find an index to cut toAdd
            int toPasteDecoyLength = (int) toPasteDecoy.length(),
                    indexOnToPasteDecoy = r.nextInt(toPasteDecoyLength);
            String subToAddStr = "";
            if (indexOnToPasteDecoy + seqNumToAdd < toPasteDecoyLength) {
                subToAddStr = toPasteDecoy.substring(indexOnToPasteDecoy, indexOnToPasteDecoy + seqNumToAdd);
            } else {
                subToAddStr = toPasteDecoy.substring(indexOnToPasteDecoy);
            }
            int indexOnSelectedDecoy = r.nextInt(selectedDecoyLength); // decide where you want to insert
            selectedDecoyStr = performPastingToDecoy(selectedDecoyStr, indexOnSelectedDecoy, subToAddStr);
            selectedDecoyLength = selectedDecoyStr.length();
            seqNumToAdd = expectedLen - selectedDecoyLength;
        }
        return selectedDecoyStr;
    }

    public static String performPastingToDecoy(String selectedDecoyStr, int indexOnSelectedDecoy, String subToAddStr) {
        String tmp = "";
        if (indexOnSelectedDecoy != selectedDecoyStr.length()) {
            tmp = selectedDecoyStr.substring(0, indexOnSelectedDecoy) + subToAddStr + selectedDecoyStr.substring(indexOnSelectedDecoy);
        } else {
            tmp = selectedDecoyStr.substring(0, indexOnSelectedDecoy) + subToAddStr;
        }
        return tmp;
    }

    /**
     * This method writes a new fasta file with fileName for selected list of
     * decoys while merging them with targetFasta
     *
     * @param targetFasta a fasta file containing targets
     * @param decoys - selected (and might be also modified) decoys
     * @param fileName - target_decoy database name
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static File write(File targetFasta, HashSet<Protein> decoys, String fileName) throws FileNotFoundException, IOException {
        File td = new File(fileName);
        PrintWriter pw = new PrintWriter(td);
        DBLoader loader = DBLoaderLoader.loadDB(targetFasta);
        Protein protein = null;
        int totalSize = 0;
        // put all proteins in a list to randomly select one...  
        while ((protein = loader.nextProtein()) != null) {
            protein.writeToFASTAFile(pw);
            totalSize++;
        }

        for (Protein p : decoys) {
            p.writeToFASTAFile(pw);
            totalSize++;
        }
        System.out.println(targetFasta.getName() + "\t" + "totalSize" + totalSize);
        pw.close();
        return td;
    }

}
