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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.xmlpull.v1.XmlPullParserException;
import start.GetPTMs;
import theoretical.CPeptides;
import theoretical.FragmentationMode;

/**
 *
 * @author Sule
 */
public class FASTACPDBLoader {

    /**
     * This method reads a given index file and returns CPeptide_Mass hashmap
     * object
     *
     * @param file
     * @param ptmFactory
     * @param fixedModifications
     * @param fixedModNames
     * @param variableModifications
     * @param variableModNames
     * @param linker
     * @param fragMode
     * @param isBranching
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static HashMap<CPeptides, Double> get_CPeptide_theoreticalMass(File file,
            PTMFactory ptmFactory, ArrayList<String> fixedModifications, String fixedModNames,
            ArrayList<String> variableModifications, String variableModNames,
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
                        nextSeq = split[3],
                        modA = split[6],
                        modB = split[7];
                // linker positions...
                Integer linker_position_start = Integer.parseInt(split[4]),
                        linker_position_next = Integer.parseInt(split[5]);
                Double theoreticalMass = Double.parseDouble(split[8]);

                // First find fixed variable modifications...
                ArrayList<ModificationMatch> ptms_peptideA = GetPTMs.getPTM(ptmFactory, modA, fixedModifications, startSeq),
                        ptms_peptideB = GetPTMs.getPTM(ptmFactory, modB, fixedModifications, nextSeq);

                // First peptideA
                Peptide peptideA = new Peptide(startSeq, ptms_peptideA),
                        peptideB = new Peptide(nextSeq, ptms_peptideB);

                // now generate peptide...
                CPeptides cPeptide = new CPeptides(proteinA, proteinB, peptideA, peptideB, linker, linker_position_start, linker_position_next, fragMode, isBranching);
                cPeptide_theoreticalMass.put(cPeptide, theoreticalMass);
            }
        }
        return cPeptide_theoreticalMass;
    }

    /**
     * This method generates a peptide and mass file
     *
     * @param bw
     * @param header_sequence
     * @param ptmFactory
     * @param fixedModifications
     * @param fixedModNames
     * @param tmpVarMods
     * @param variableModifications
     * @param variableModNames
     * @param tmpTargetAAs
     * @param linker
     * @param fragMode
     * @param isBranching
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static HashMap<CPeptides, Double> generate_peptide_mass_index(BufferedWriter bw,
            HashMap<String, String> header_sequence,
            PTMFactory ptmFactory, ArrayList<String> fixedModifications, String fixedModNames,
            ICombinatoricsVector<String> tmpVarMods,
            CrossLinker linker, FragmentationMode fragMode, boolean isBranching) throws XmlPullParserException, IOException {

        HashMap<CPeptides, Double> cPeptide_theoreticalMass = new HashMap<CPeptides, Double>();
        StringBuilder proteinA,
                proteinB,
                startSeq,
                nextSeq,
                writingInfo;
        CPeptides cPeptide;
        Peptide peptideA = null,
                peptideB = null;

        for (String header : header_sequence.keySet()) {
            writingInfo = new StringBuilder();
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
            // linker positions for constructing a XLinked peptide...
            int linker_position_start_for_program = linker_position_start - 1,
                    linker_position_next_for_program = linker_position_next - 1;

            String[] headerSplit = header.substring(0).split("_");

            proteinA = new StringBuilder(headerSplit[0]);
            proteinB = new StringBuilder(headerSplit[2]);

            writingInfo.append(proteinA).append("\t").append(proteinB).append("\t");

            startSeq = new StringBuilder(header_sequence.get(header).substring(0, header_sequence.get(header).indexOf("|")).replace("*", ""));
            nextSeq = new StringBuilder(header_sequence.get(header).substring((header_sequence.get(header).indexOf("|") + 1), header_sequence.get(header).length()).replace("*", ""));

            writingInfo.append(startSeq).append("\t").append(nextSeq).append("\t");

            // First find fixed variable modifications...
            ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModifications, startSeq.toString(), false),
                    fixedPTM_peptideB = GetPTMs.getPTM(ptmFactory, fixedModifications, nextSeq.toString(), false);

            ArrayList<ModificationMatch> ptms_peptideA = new ArrayList<ModificationMatch>(fixedPTM_peptideA),
                    ptms_peptideB = new ArrayList<ModificationMatch>(fixedPTM_peptideB);
            for (String tmpVarMod : tmpVarMods) {
                // Add variable PTMs and also a list of several fixed PTMs
                ArrayList<ModificationMatch> variablePTM_peptideA = GetPTMs.getPTM(ptmFactory, tmpVarMod, startSeq.toString(), true),
                        variablePTM_peptideB = GetPTMs.getPTM(ptmFactory, tmpVarMod, nextSeq.toString(), true);
                ptms_peptideA.addAll(variablePTM_peptideA);
                ptms_peptideB.addAll(variablePTM_peptideB);
            }

            String modPepA = getPTMName(ptms_peptideA),
                    modPepB = getPTMName(ptms_peptideB);

            peptideA = new Peptide(startSeq.toString(), ptms_peptideA);
            peptideB = new Peptide(nextSeq.toString(), ptms_peptideB);

            // now generate peptide...
            cPeptide = new CPeptides(proteinA.toString(), proteinB.toString(), peptideA, peptideB, linker, linker_position_start_for_program, linker_position_next_for_program, fragMode, isBranching);
            writingInfo.append(linker_position_start_for_program).append("\t").append(linker_position_next_for_program).append("\t");
            double theoreticalMass = cPeptide.getTheoreticalXLinkedMass();
            writingInfo.append(modPepA).append("\t").append(modPepB).append("\t");
            cPeptide_theoreticalMass.put(cPeptide, theoreticalMass);
            writingInfo.append(theoreticalMass).append("\n");
            bw.write(writingInfo.toString());
            cPeptide_theoreticalMass.put(cPeptide, theoreticalMass);
        }
        return cPeptide_theoreticalMass;
    }

    private static String getPTMName(ArrayList<ModificationMatch> ptms_peptide) {
        String tmp_ptm = "";
        for (ModificationMatch m : ptms_peptide) {
            String tmp = "[" + m.getTheoreticPtm() + "_" + m.getModificationSite() + "]";
            tmp_ptm += tmp;
        }
        return tmp_ptm;
    }

}
