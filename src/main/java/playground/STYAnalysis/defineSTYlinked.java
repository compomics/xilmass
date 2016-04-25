/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package playground.STYAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Sule
 */
public class defineSTYlinked {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
       // File input = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\side_reactions\\xilmass02_sty_1_10_min2\\xilmass_sty_xlsite.txt"),
         //     output = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\side_reactions\\xilmass02_sty_1_10_min2\\xilmass_SideReaction_STY_xlsite.txt");
        File input = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\side_reactions/xilmass_org_xlsite.txt"),
              output = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\side_reactions\\xilmass_SideReaction_ORG_xlsite.txt");

        BufferedReader br = new BufferedReader(new FileReader(input));
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        String line = "";
        while ((line = br.readLine()) != null) {
            if (line.startsWith("SpectrumFile")) {
                bw.write(line + "\t" + "STYXLNames" + "\t" + "IntraPeptideInfo" + "\n");
            } else {
                String[] split = line.split("\t");
                String pepA = split[9],
                        pepB = split[12];
                Integer indA = Integer.parseInt(split[16]) - 1,
                        indB = Integer.parseInt(split[17]) - 1,
                        proAind = Integer.parseInt(split[18]),
                        proBind = Integer.parseInt(split[19]);
                String nameA = "K",
                        nameB = "K";
                char charA = pepA.charAt(indA),
                        charB = pepB.charAt(indB);

                if (proAind == 1) {
                    nameA = "ProteinN";
                } else if (charA == 'S') {
                    nameA = "S";
                } else if (charA == 'T') {
                    nameA = "T";
                } else if (charA == 'Y') {
                    nameA = "Y";
                }
                if (proBind == 1) {
                    nameB = "ProteinN";
                } else if (charB == 'S') {
                    nameB = "S";
                } else if (charB == 'T') {
                    nameB = "T";
                } else if (charB == 'Y') {
                    nameB = "Y";
                }
                String t = nameA + "_" + nameB;

                if (charB == 'K' && proAind == 1) {
                    t = nameA + "_" + nameB;
                } else if (charA == 'K' && proBind == 1) {
                    t = nameB + "_" + nameA;
                } else if (charA != 'K' && charA != 'S' && charA != 'T' && charA != 'Y' && proBind == 1) {
                    t = nameB + "_" + nameA;
                } else if (charB == 'K' && charA != 'K') {
                    t = nameB + "_" + nameA;
                } else if (charA == 'S' && proBind == 1) {
                    t = nameB + "_" + nameA;
                } else if (charB == 'S' && (charA == 'T' || charA == 'Y')) {
                    t = nameB + "_" + nameA;
                } else if (charA == 'T' && proBind == 1) {
                    t = nameB + "_" + nameA;
                } else if (charB == 'T' && charA == 'Y') {
                    t = nameB + "_" + nameA;
                } else if (charA == 'Y' && proBind == 1) {
                    t = nameB + "_" + nameA;
                }
                String intraInfo = "Non-intraPeptide",
                        proA = split[10],
                        proB = split[13];
                if (proA.equals(proB) && proAind.equals(proBind)) {
                    intraInfo = "intraPeptide";
                }
                bw.write(line + "\t" + t + "\t" + intraInfo + "\n");
            }
        }
        bw.close();
    }

}
