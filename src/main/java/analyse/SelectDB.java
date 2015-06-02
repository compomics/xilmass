/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse;

import com.compomics.dbtoolkit.io.DBLoaderLoader;
import com.compomics.dbtoolkit.io.interfaces.DBLoader;
import com.compomics.util.protein.Protein;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * TODO: Commit changes on Protein class to write a FASTA sequence with 60 aa
 * per a line
 *
 * This class is used to randomly select a decoy protein with the same length of
 * given target protein. If there is none, it select any random decoy protein
 * with similar length (+/-5aa)
 *
 *
 * @author Sule
 */
public class SelectDB {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        HashSet<Protein> decoys = new HashSet<Protein>();
        File decoyFasta = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\databases/uniprot_2261_Pfuriosus.fasta"),
                selectedDecoysFasta = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\databases/selected_uniprot_2261_Pfuriosus_freiburg2.fasta");

        int[] lens = {149, 242};
        for (int len : lens) {
            DBLoader loader = DBLoaderLoader.loadDB(decoyFasta);
            Protein protein = null;
            ArrayList<Protein> selectedProteins = new ArrayList<Protein>();
            // get a crossLinkerName object        
            while ((protein = loader.nextProtein()) != null) {
                String sequence = protein.getSequence().getSequence();
                if (sequence.length() == len) {
                    selectedProteins.add(protein);
                }
            }
            System.out.println("Decoys with " + len + " of length=" + selectedProteins.size());
            if (!selectedProteins.isEmpty()) {
                boolean isSelected = false;
                while (!isSelected) {
                    // randomly select protein
                    Random r = new Random();
                    int nextInt = r.nextInt(selectedProteins.size());
                    // select a randomly selected the same length sequence.. 
                    Protein selectedDecoy = selectedProteins.get(nextInt);
                    if (!decoys.contains(selectedDecoy)) {
                        decoys.add(selectedDecoy);
                        isSelected = true;
                    }
                }
                System.out.println("a decoy protein with the same length as " + len + " is found.");
            } else {
                // if selectedproteins list is empty, it means that there is no decoy with THE SAME LENGTH
                // so select the proteins with similar length +/-5 aa

                loader = DBLoaderLoader.loadDB(decoyFasta);
                protein = null;
                // get a crossLinkerName object        
                while ((protein = loader.nextProtein()) != null) {
                    String sequence = protein.getSequence().getSequence();
                    if (sequence.length() <= len + 5 && sequence.length() >= len - 5) {
                        selectedProteins.add(protein);
                    }
                }
                System.out.println("Decoys with " + "+/-" + len + " of length=" + selectedProteins.size());

                if (!selectedProteins.isEmpty()) {

                    boolean isSelected = false;
                    while (!isSelected) {
                        // randomly select protein
                        Random r = new Random();
                        int nextInt = r.nextInt(selectedProteins.size());
                        // select a randomly selected the same length sequence.. 
                        Protein selectedDecoy = selectedProteins.get(nextInt);
                        if (!decoys.contains(selectedDecoy)) {
                            int diff = len - (int) selectedProteins.get(nextInt).getLength();
                            System.out.println("a decoy protein with the length as " + diff + " is found.");
                            decoys.add(selectedDecoy);
                            isSelected = true;
                        }
                    }
                }
            }
        }
        // now writing a decoy database...
        System.out.println("Total selected decoys=" + decoys.size());
        System.out.println("Now writing up decoys...");
        PrintWriter pw = new PrintWriter(selectedDecoysFasta);
        // Make sure that when this method writes, it only select 58aa not 60aa...
        for (Protein p : decoys) {
            p.writeToFASTAFile(pw);
        }
        pw.close();
    }
}
