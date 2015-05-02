/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import crossLinker.CrossLinker;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParserException;
import start.GetFixedPTM;
import theoretical.CPeptides;
import theoretical.FragmentationMode;

/**
 *
 * @author Sule
 */
public class FASTACPDBLoader {

    public static HashMap<CPeptides, Double> getCPeptide_TheoreticalMass(File mass_pept_cache, HashMap<String, String> header_sequence, 
            PTMFactory ptmFactory, ArrayList<String> fixedModifications, CrossLinker linker, FragmentationMode fragMode, boolean isBranching) throws XmlPullParserException, IOException {
        HashMap<CPeptides, Double> cPeptides_theoreticalMasses = new HashMap<CPeptides, Double>();
        Peptide peptideA,
                peptideB;
        String proteinA,
                proteinB;
        CPeptides cPeptide;
        String seqs,
                startSeq,
                nextSeq;
        BufferedWriter bw = new BufferedWriter(new FileWriter(mass_pept_cache));
        bw.write("proteinA" + "\t" + "proteinB" + "\t" + "peptideA" + "\t" + "peptideB" + "\t" + "linker_position_PepA" + "\t" + "linker_position_PepB" + "\t" + "theoretical_mass" + "\n");
        for (String header : header_sequence.keySet()) {
            // indices must be known to generate a cross linked theoretical spectrum
            String[] split = header.split("_");
            int startIndex = 1,
                    endIndex = 3;
            if (split[startIndex].equals("inverted")) {
                endIndex++;
                startIndex++;
            }
            if (split[endIndex].equals("inverted")) {
                endIndex++;
            }
            int linker_position_start = Integer.parseInt(split[startIndex]),
                    linker_position_next = Integer.parseInt(split[endIndex]);
            linker_position_start = linker_position_start - 1;
            linker_position_next = linker_position_next - 1;

            seqs = header_sequence.get(header);

            String[] headerSplit = header.substring(0).split("_");

            proteinA = headerSplit[0];
            proteinB = headerSplit[2];
            bw.write(proteinA + "\t" + proteinB + "\t");

            startSeq = seqs.substring(0, seqs.indexOf("|")).replace("*", "");
            nextSeq = seqs.substring((seqs.indexOf("|") + 1), seqs.length()).replace("*", "");
            bw.write(startSeq + "\t" + nextSeq + "\t");

            // TODO: improve PTMs - add variable PTMs and also a list of several fixed PTMs

            ArrayList<ModificationMatch> ptm_pepA = GetFixedPTM.getPTM(ptmFactory, fixedModifications, startSeq),
                    ptm_pepB = GetFixedPTM.getPTM(ptmFactory, fixedModifications, nextSeq);
           
            peptideA = new Peptide(startSeq, ptm_pepA);
            peptideB = new Peptide(nextSeq, ptm_pepB);

            cPeptide = new CPeptides(proteinA, proteinB, peptideA, peptideB, linker, linker_position_start, linker_position_next, fragMode, isBranching);
            bw.write(linker_position_start + "\t" + linker_position_next + "\t");

            double theoretical_mass = cPeptide.getTheoreticalXLinkedMass();
            cPeptides_theoreticalMasses.put(cPeptide, theoretical_mass);
            bw.write(theoretical_mass + "\n");
        }
        bw.close();
        return cPeptides_theoreticalMasses;
    }

    public static HashMap<CPeptides, Double> getCPeptide_TheoreticalMass(File file, PTMFactory ptmFactory, ArrayList<String> fixedModifications,
            CrossLinker linker, FragmentationMode fragMode, boolean isBranching) throws XmlPullParserException, IOException {
        HashMap<CPeptides, Double> cPeptide_theoreticalMass = new HashMap<CPeptides, Double>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("protein")) {
                String[] split = line.split("\t");
                String proteinA = split[0],
                        proteinB = split[1],
                        startSeq = split[2],
                        nextSeq = split[3];
                Integer linker_position_start = Integer.parseInt(split[4]),
                        linker_position_next = Integer.parseInt(split[5]);
                Double theoreticalMass = Double.parseDouble(split[6]);

                // TODO: improve PTMs - add variable PTMs and also a list of several fixed PTMs
                ArrayList<ModificationMatch> ptm_peptideA = GetFixedPTM.getPTM(ptmFactory, fixedModifications, startSeq),
                        ptm_peptideB = GetFixedPTM.getPTM(ptmFactory, fixedModifications, nextSeq);
                linker_position_next = linker_position_next - 1;
                linker_position_start = linker_position_start - 1;

                Peptide peptideA = new Peptide(startSeq, ptm_peptideA),
                        peptideB = new Peptide(nextSeq, ptm_peptideB);
                CPeptides cPeptide = new CPeptides(proteinA, proteinB, peptideA, peptideB, linker, linker_position_start, linker_position_next, fragMode, isBranching);
                cPeptide_theoreticalMass.put(cPeptide, theoreticalMass);
            }
        }
        return cPeptide_theoreticalMass;
    }

//    public static HashMap<CPeptides, Double> getCPeptide_TheoreticalMass(File file, PTMFactory ptmFactory, ArrayList<String>fixedModifications, ArrayList<String> variable_modifications, CrossLinker linker, FragmentationMode fragMode, boolean isBranching) throws XmlPullParserException, IOException {
//        HashMap<CPeptides, Double> cPeptide_theoreticalMass = new HashMap<CPeptides, Double>();
//        BufferedReader br = new BufferedReader(new FileReader(file));
//        String line = "";
//        while ((line = br.readLine()) != null) {
//            if (!line.startsWith("start")) {
//                String[] split = line.split("\t");
//                String proteinA = split[0],
//                        proteinB = split[1],
//                        startSeq = split[2],
//                        nextSeq = split[3];
//                Integer linker_position_start = Integer.parseInt(split[4]),
//                        linker_position_next = Integer.parseInt(split[5]);
//                Double theoreticalMass = Double.parseDouble(split[6]);
//
//                // TODO: improve PTMs - add variable PTMs and also a list of several fixed PTMs
//                ArrayList<ModificationMatch> ptm_peptideA = GetFixedPTM.getPTM(ptmFactory, fixedModifications, startSeq),
//                        ptm_peptideB = GetFixedPTM.getPTM(ptmFactory, fixedModifications, nextSeq);
//
//                Peptide peptideA = new Peptide(startSeq, ptm_peptideA),
//                        peptideB = new Peptide(nextSeq, ptm_peptideB);
//                CPeptides cPeptide = new CPeptides(proteinA, proteinB, peptideA, peptideB, linker, linker_position_start, linker_position_next, fragMode, isBranching);
//                cPeptide_theoreticalMass.put(cPeptide, theoreticalMass);
//
//            }
//        }
//        return cPeptide_theoreticalMass;
//    }
}
