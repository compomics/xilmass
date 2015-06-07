/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.xwalk_datamerge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class reads output from XWalk for predicted cross linkings. Based on how
 * sequences are selected on PDB, this assigns true index in Uniprot. Both
 * indexing starts from 1.
 *
 * @author Sule
 */
public class Merge {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        File input = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\structure\\predicted_cross_linking/xwalk_prediction.txt"),
                output = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\structure\\predicted_cross_linking/xwalk_prediction_uniprot.txt");

        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        BufferedReader br = new BufferedReader(new FileReader(input));

        String line = "";
        while ((line = br.readLine()) != null) {
            if (line.startsWith("File")) {
                // write title
                bw.write(line + "\t" + "uniprotAccProtein1" + "\t" + "uniprotIndexProtein1" + "\t" + "uniprotAccProtein2" + "\t" + "uniprotIndexProtein2" + "\n");

            } else {
                // read each line to find an index at uniprot accession
                String[] split = line.split("\t");
                String structureName = split[0],
                        atom1Info = split[1],
                        atom2Info = split[2];
                // parse information to decide uniprot based info
                String[] resAtom1 = getRes(structureName, atom1Info),
                        resAtom2 = getRes(structureName, atom2Info);
                String uniprotProteinAccAtom1 = resAtom1[0],
                        uniprotIndexAtom1 = resAtom1[1],
                        uniprotProteinAccAtom2 = resAtom2[0],
                        uniprotIndexAtom2 = resAtom2[1];
                bw.write(line + "\t" + uniprotProteinAccAtom1 + "\t" + uniprotIndexAtom1 + "\t" + uniprotProteinAccAtom2 + "\t" + uniprotIndexAtom2 + "\n");
            }
        }
        bw.close();
    }

    /**
     * This method returns Uniprot accession number and a residue index on
     * Uniprot from a given atomInfo by XWalk
     *
     * @param structure name of structure - 2F3Y.pdb or 4Q57.pdb
     * @param atomInfo pymol/PDB information that needs to be parsed
     *
     * @return Uniprot protein accession [0] and index on Uniprot sequence [1]
     */
    private static String[] getRes(String structure, String atomInfo) {
        String[] split = atomInfo.split("-");
        String residue = split[0],
                chain = split[2];
        Integer pdbIndex = Integer.parseInt(split[1]),
                uniprotIndex = pdbIndex;
        String proteinAcc = "P62158";
        // Calmodulin - P62158
        if (structure.equals("2F3Y.pdb")) {
            uniprotIndex++;
        }
        // Plectin - Q15149
        if (structure.equals("4Q57.pdb")) {
            // chainA: Calmodulin - P62158, chainB: Plectin - Q15149
            if (chain.equals("B")) {
                proteinAcc = "Q15149";
                uniprotIndex = uniprotIndex - 21;
            } else {
                uniprotIndex++;
            }
        }
        String uniprotIndexStr = uniprotIndex.toString();
        String[] res = {proteinAcc, uniprotIndexStr};
        return res;
    }

}
