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
import start.GetPTMs;
import theoretical.CPeptides;
import theoretical.FragmentationMode;

/**
 *
 * @author Sule
 */
public class FASTACPDBLoader {

    public static HashMap<CPeptides, Double> getCPeptide_TheoreticalMass(File mass_pept_cache, HashMap<String, String> header_sequence,
            PTMFactory ptmFactory, ArrayList<String> fixedModifications, ArrayList<String> variableModifications,
            CrossLinker linker, FragmentationMode fragMode, boolean isBranching) throws XmlPullParserException, IOException {
        HashMap<CPeptides, Double> cPeptide_theoreticalMass = new HashMap<CPeptides, Double>();

        String proteinA,
                proteinB;
        CPeptides cPeptide;
        String seqs,
                startSeq,
                nextSeq;
        BufferedWriter bw = new BufferedWriter(new FileWriter(mass_pept_cache));
        bw.write("proteinA" + "\t" + "proteinB" + "\t" + "peptideA" + "\t" + "peptideB" + "\t" + "linker_position_PepA" + "\t" + "linker_position_PepB" + "\t" + "theoretical_mass" + "\n");
        for (String header : header_sequence.keySet()) {
            String writingInfo = "";
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
               // linker positions...
            linker_position_next = linker_position_next - 1;
            linker_position_start = linker_position_start - 1;

            seqs = header_sequence.get(header);

            String[] headerSplit = header.substring(0).split("_");

            proteinA = headerSplit[0];
            proteinB = headerSplit[2];
            writingInfo = (proteinA + "\t" + proteinB + "\t");

            startSeq = seqs.substring(0, seqs.indexOf("|")).replace("*", "");
            nextSeq = seqs.substring((seqs.indexOf("|") + 1), seqs.length()).replace("*", "");
            writingInfo += (startSeq + "\t" + nextSeq + "\t");
         
            // First find fixed variable modifications...
            ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModifications, startSeq, false),
                    fixedPTM_peptideB = GetPTMs.getPTM(ptmFactory, fixedModifications, nextSeq, false);

            // Add variable PTMs and also a list of several fixed PTMs
            ArrayList<ModificationMatch> variablePTM_peptideA = GetPTMs.getPTM(ptmFactory, variableModifications, startSeq, true),
                    variablePTM_peptideB = GetPTMs.getPTM(ptmFactory, variableModifications, nextSeq, true);

            // First start with PeptideA
            ArrayList<ModificationMatch> ptms_peptideA = new ArrayList<ModificationMatch>(fixedPTM_peptideA);

            for (int iA = -1; iA < variablePTM_peptideA.size(); iA++) {
                String toWrite = writingInfo;
                if (iA > -1) {
                    ModificationMatch varModificationMatchPepA = variablePTM_peptideA.get(iA);
                    ptms_peptideA.add(varModificationMatchPepA);
                }
                Peptide peptideA = new Peptide(startSeq, ptms_peptideA);
                // Then peptideB
                ArrayList<ModificationMatch> ptms_peptideB = new ArrayList<ModificationMatch>(fixedPTM_peptideB);
                for (int iB = -1; iB < variablePTM_peptideB.size(); iB++) {
                    if (iB > -1) {
                        ModificationMatch varModificationMatchPepB = variablePTM_peptideB.get(iB);
                        ptms_peptideB.add(varModificationMatchPepB);
                    }
                    Peptide peptideB = new Peptide(nextSeq, ptms_peptideB);
                    // now generate peptide...
                    cPeptide = new CPeptides(proteinA, proteinB, peptideA, peptideB, linker, linker_position_start, linker_position_next, fragMode, isBranching);
                    toWrite += (linker_position_start + "\t" + linker_position_next + "\t");

                    double theoreticalMass = cPeptide.getTheoreticalXLinkedMass();
                    cPeptide_theoreticalMass.put(cPeptide, theoreticalMass);
                    toWrite += (theoreticalMass + "\n");
                    bw.write(toWrite);
                    cPeptide_theoreticalMass.put(cPeptide, theoreticalMass);
                }
            }
        }
        bw.close();
        return cPeptide_theoreticalMass;
    }

    public static HashMap<CPeptides, Double> getCPeptide_TheoreticalMass(File file,
            PTMFactory ptmFactory, ArrayList<String> fixedModifications, ArrayList<String> variableModifications,
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
                // linker positions...
                Integer linker_position_start = Integer.parseInt(split[4]),
                        linker_position_next = Integer.parseInt(split[5]);
                Double theoreticalMass = Double.parseDouble(split[6]);

                // First find fixed variable modifications...
                ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModifications, startSeq, false),
                        fixedPTM_peptideB = GetPTMs.getPTM(ptmFactory, fixedModifications, nextSeq, false);
                // Then, select variable PTMs 
                ArrayList<ModificationMatch> variablePTM_peptideA = GetPTMs.getPTM(ptmFactory, variableModifications, startSeq, true),
                        variablePTM_peptideB = GetPTMs.getPTM(ptmFactory, variableModifications, nextSeq, true);

                // First peptideA
                ArrayList<ModificationMatch> ptms_peptideA = new ArrayList<ModificationMatch>(fixedPTM_peptideA);
                for (int iA = -1; iA < variablePTM_peptideA.size(); iA++) {
                    if (iA > -1) {
                        ModificationMatch varModificationMatchPepA = variablePTM_peptideA.get(iA);
                        ptms_peptideA.add(varModificationMatchPepA);
                    }
                    Peptide peptideA = new Peptide(startSeq, ptms_peptideA);
                    // Now select peptideB with each modification..
                    ArrayList<ModificationMatch> ptms_peptideB = new ArrayList<ModificationMatch>(fixedPTM_peptideB);
                    for (int iB = -1; iB < variablePTM_peptideB.size(); iB++) {
                        if (iB > -1) {
                            ModificationMatch varModificationMatchPepB = variablePTM_peptideB.get(iB);
                            ptms_peptideB.add(varModificationMatchPepB);
                        }
                        Peptide peptideB = new Peptide(nextSeq, ptms_peptideB);
                        // now generate peptide...
                        CPeptides cPeptide = new CPeptides(proteinA, proteinB, peptideA, peptideB, linker, linker_position_start, linker_position_next, fragMode, isBranching);
                        cPeptide_theoreticalMass.put(cPeptide, theoreticalMass);
                    }
                }
            }
        }
        return cPeptide_theoreticalMass;
    }

}
