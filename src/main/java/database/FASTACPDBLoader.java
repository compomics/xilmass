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
import java.util.HashSet;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.xmlpull.v1.XmlPullParserException;
import start.CPeptideInfo;
import static start.CPeptideInfo.getPTMName;
import start.GetPTMs;
import theoretical.CPeptides;
import theoretical.FragmentationMode;
import theoretical.MonoLinkedPeptides;

/**
 * This class is used to load constructed database with their peptide masses
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
     * @param linker
     * @param fragMode
     * @param isContrastLinkedAttachmentOn
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static HashMap<CPeptides, Double> readFiletoGetCPeptideTheoMass(File file, PTMFactory ptmFactory, CrossLinker linker,
            FragmentationMode fragMode, boolean isContrastLinkedAttachmentOn)
            throws XmlPullParserException, IOException {
        HashMap<CPeptides, Double> cPeptides_Masses = new HashMap<CPeptides, Double>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("protein")) {
                String[] split = line.split("\t");
                String proteinA = split[0],
                        proteinB = split[1],
                        peptideAseq = split[2],
                        peptideBseq = split[3],
                        fixedModA = split[6],
                        fixedModB = split[7],
                        variableModA = split[8],
                        variableModB = split[9];
                // linker positions...
                Integer linkerPosPeptideA = Integer.parseInt(split[4]),
                        linkerPosPeptideB = Integer.parseInt(split[5]);
                Double mass = Double.parseDouble(split[10]);
                ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModA, false),
                        fixedPTM_peptideB = GetPTMs.getPTM(ptmFactory, fixedModB, false);
                // Start putting them on a list which will contain also variable PTMs
                ArrayList<ModificationMatch> ptms_peptideA = new ArrayList<ModificationMatch>(fixedPTM_peptideA),
                        ptms_peptideB = new ArrayList<ModificationMatch>(fixedPTM_peptideB);
                // Add variable PTMs and also a list of several fixed PTMs
                ArrayList<ModificationMatch> variablePTM_peptideA = GetPTMs.getPTM(ptmFactory, variableModA, true),
                        variablePTM_peptideB = GetPTMs.getPTM(ptmFactory, variableModB, true);
                ptms_peptideA.addAll(variablePTM_peptideA);
                ptms_peptideB.addAll(variablePTM_peptideB);
                // First peptideA
                Peptide peptideA = new Peptide(peptideAseq, ptms_peptideA),
                        peptideB = new Peptide(peptideBseq, ptms_peptideB);
                // now generate peptide...
                CPeptides cPeptide = new CPeptides(proteinA, proteinB, peptideA, peptideB, linker, linkerPosPeptideA, linkerPosPeptideB, fragMode, isContrastLinkedAttachmentOn);
                cPeptides_Masses.put(cPeptide, mass);
            }
        }
        return cPeptides_Masses;
    }

    /**
     * This method generates a list of CPeptides with always a fixed
     * Modification and all possible variable modifications..
     *
     *
     * @param header_sequence a list of crosslinked header and their
     * corresponding sequences
     * @param ptmFactory is already instantiated PTMFactory
     * @param fixedModifications is a list of given fixed Modifications.
     * @param variableModifications is a list of given variable modifications
     * @param linker a CrossLinker object to construct CPeptides objects
     * @param fragMode fragmentation mode
     * @param isContrastLinkedAttachmentOn
     * @param max_mods_per_peptide
     * @param acc_and_length is list of accession numbers and its sequence
     * length
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static ArrayList<CPeptides> generate_peptide_mass_index(HashMap<String, String> header_sequence,
            PTMFactory ptmFactory, ArrayList<String> fixedModifications, ArrayList<String> variableModifications, int max_mods_per_peptide,
            CrossLinker linker, FragmentationMode fragMode, boolean isContrastLinkedAttachmentOn,
            HashMap<String, Integer> acc_and_length) throws XmlPullParserException, IOException {
        ArrayList<CPeptides> cPeptides = new ArrayList<CPeptides>();
        // Read each header to construct CrossLinkedPeptide object
        for (String header : header_sequence.keySet()) {
            if (!header.startsWith("contaminant") && !header.isEmpty()) {
                StringBuilder proteinA = new StringBuilder(),
                        proteinB = new StringBuilder(),
                        peptideAseq = new StringBuilder(),
                        peptideBseq = new StringBuilder();
                String[] split = header.split("_");
                int positionHeaderLinkerA = 1,
                        positionHeaderLinkerB = 3,
                        control = 0;
                // making sure spliting for reversed sequences..
                if (split[positionHeaderLinkerA].contains("REVERSE") || split[positionHeaderLinkerA].contains("SHUFFLE")) {
                    positionHeaderLinkerA++;
                    positionHeaderLinkerB++;
                }
                if (split[positionHeaderLinkerB + control].contains("REVERSE") || split[positionHeaderLinkerB + control].contains("SHUFFLE")) {
                    positionHeaderLinkerB++;
                }
                int writen_linkerPositionPeptideA = Integer.parseInt(split[positionHeaderLinkerA]),
                        writen_linkerPositionPeptideB = Integer.parseInt(split[positionHeaderLinkerB]);
                // indices for linker positions necessary for constructing a CrossLinkedPeptide object... for from upper...
                int linkerPosPeptideA = writen_linkerPositionPeptideA - 1,
                        linkerPosPeptideB = writen_linkerPositionPeptideB - 1;
                // now get protein names
                String[] headerSplit = header.substring(0).split("_");
                for (int i = 0; i < positionHeaderLinkerA; i++) {
                    proteinA.append(headerSplit[i]);
                    if (i != positionHeaderLinkerA - 1) {
                        proteinA.append("_");
                    }
                }
                for (int i = positionHeaderLinkerA + 1; i < positionHeaderLinkerB; i++) {
                    proteinB.append(headerSplit[i]);
                    if (i != positionHeaderLinkerB - 1) {
                        proteinB.append("_");
                    }
                }
                // example proteinA is P04233(165-201) or P04233_REVERSED(165-201)
                // check if tryptic peptide contains protein termini
                boolean containsPeptideAProteinNTermini = checkProteinContainsProteinTermini(proteinA.toString(), true, acc_and_length), // does peptideA contains the first amino acid of a protein (protein N-termini)
                        containsPeptideBProteinNTermini = checkProteinContainsProteinTermini(proteinB.toString(), true, acc_and_length), // does peptideB contains the first amino acid of a protein(protein N-termini)
                        containsPeptideAProteinCTermini = checkProteinContainsProteinTermini(proteinA.toString(), false, acc_and_length),// does peptideA contains the last amino acid of a protein (protein C-termini)
                        containsPeptideBProteinCTermini = checkProteinContainsProteinTermini(proteinB.toString(), false, acc_and_length);// does peptideB contains the last amino acid of a protein (protein C-termini)
                // and now peptide sequences..
                peptideAseq = new StringBuilder(header_sequence.get(header).substring(0, header_sequence.get(header).indexOf("|")).replace("*", ""));
                peptideBseq = new StringBuilder(header_sequence.get(header).substring((header_sequence.get(header).indexOf("|") + 1), header_sequence.get(header).length()).replace("*", ""));
                // First, find fixed variable modifications to construct a Peptide object!
                ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModifications, peptideAseq.toString(), false, containsPeptideAProteinNTermini, containsPeptideAProteinCTermini),
                        fixedPTM_peptideB = GetPTMs.getPTM(ptmFactory, fixedModifications, peptideBseq.toString(), false, containsPeptideBProteinNTermini, containsPeptideBProteinCTermini);
                // Then, get all variable PTMs locations for a given Peptide sequence
                ArrayList<GetPTMs.PTMNameIndex> possiblePTMsPepA = GetPTMs.getPTMwithPTMNameIndex(ptmFactory, variableModifications, peptideAseq.toString(), true, containsPeptideAProteinNTermini, containsPeptideAProteinCTermini),
                        possiblePTMsPepB = GetPTMs.getPTMwithPTMNameIndex(ptmFactory, variableModifications, peptideBseq.toString(), true, containsPeptideBProteinNTermini, containsPeptideBProteinCTermini);
                // Now generate all possible variable PTMs combinations derived from a given peptide sequence and modifications       
                ArrayList<Peptide> peptideAs = getPeptidesVarPTMs(possiblePTMsPepA, peptideAseq, fixedPTM_peptideA, max_mods_per_peptide),
                        peptideBs = getPeptidesVarPTMs(possiblePTMsPepB, peptideBseq, fixedPTM_peptideB, max_mods_per_peptide);
                // fill all possible modified peptides here...
                StringBuilder info = new StringBuilder(),
                        rInfo = new StringBuilder();
                // fill all possible modified peptides here...
                for (Peptide pA : peptideAs) {
                    for (Peptide pB : peptideBs) {
                        CPeptides cPeptide = new CPeptides(proteinA.toString(), proteinB.toString(), pA, pB, linker, linkerPosPeptideA,
                                linkerPosPeptideB, fragMode, isContrastLinkedAttachmentOn);
                        cPeptides.add(cPeptide);
                    }
                }
            }
        }
        return cPeptides;
    }

    /**
     * This method generates a list of CPeptides with always a fixed
     * Modification and all possible variable modifications.. Write each
     * CPeptide object on a given BufferedWriter
     *
     *
     * @param bw
     * @param header_sequence a list of crosslinked header and their
     * corresponding sequences
     * @param ptmFactory is already instantiated PTMFactory
     * @param fixedModifications is a list of given fixed Modifications.
     * @param variableModifications is a list of given variable modifications
     * @param linker a CrossLinker object to construct CPeptides objects
     * @param fragMode fragmentation mode
     * @param isContrastLinkedAttachmentOn
     * @param max_mods_per_peptide
     * @param acc_and_length is list of accession numbers and its sequence
     * length
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static HashSet<StringBuilder> generate_peptide_mass_index(BufferedWriter bw, HashMap<String, StringBuilder> header_sequence,
            PTMFactory ptmFactory, ArrayList<String> fixedModifications, ArrayList<String> variableModifications, int max_mods_per_peptide,
            CrossLinker linker, FragmentationMode fragMode, boolean isContrastLinkedAttachmentOn,
            HashMap<String, Integer> acc_and_length) throws XmlPullParserException, IOException {
        HashSet<StringBuilder> headers = new HashSet<StringBuilder>();
        HashSet<String> hs = new HashSet<String>();
        boolean isCPeptidesObjConstructed = false;
        CPeptides cPeptide = null;
        // Read each header to construct CrossLinkedPeptide object
        for (String header : header_sequence.keySet()) {
            if (!header.startsWith("contaminant") && !header.isEmpty()) {
                StringBuilder proteinA = new StringBuilder(),
                        proteinB = new StringBuilder(),
                        peptideAseq = new StringBuilder(),
                        peptideBseq = new StringBuilder();
                String[] split = header.split("_");
                int positionHeaderLinkerA = 1,
                        positionHeaderLinkerB = 3,
                        control = 0;
                // making sure spliting for reversed sequences..
                if (split[positionHeaderLinkerA].contains("REVERSE") || split[positionHeaderLinkerA].contains("SHUFFLE")) {
                    positionHeaderLinkerA++;
                    positionHeaderLinkerB++;
                }
                if (split[positionHeaderLinkerB + control].contains("REVERSE") || split[positionHeaderLinkerB + control].contains("SHUFFLE")) {
                    positionHeaderLinkerB++;
                }
                int writen_linkerPositionPeptideA = Integer.parseInt(split[positionHeaderLinkerA]),
                        writen_linkerPositionPeptideB = Integer.parseInt(split[positionHeaderLinkerB]);
                // indices for linker positions necessary for constructing a CrossLinkedPeptide object... for from upper...
                int linkerPosPeptideA = writen_linkerPositionPeptideA - 1,
                        linkerPosPeptideB = writen_linkerPositionPeptideB - 1;
                // now get protein names
                String[] headerSplit = header.substring(0).split("_");
                for (int i = 0; i < positionHeaderLinkerA; i++) {
                    proteinA.append(headerSplit[i]);
                    if (i != positionHeaderLinkerA - 1) {
                        proteinA.append("_");
                    }
                }
                for (int i = positionHeaderLinkerA + 1; i < positionHeaderLinkerB; i++) {
                    proteinB.append(headerSplit[i]);
                    if (i != positionHeaderLinkerB - 1) {
                        proteinB.append("_");
                    }
                }
                // example proteinA is P04233(165-201) or P04233_REVERSED(165-201)
                // check if tryptic peptide contains protein termini
                boolean containsPeptideAProteinNTermini = checkProteinContainsProteinTermini(proteinA.toString(), true, acc_and_length), // does peptideA contains the first amino acid of a protein (protein N-termini)
                        containsPeptideBProteinNTermini = checkProteinContainsProteinTermini(proteinB.toString(), true, acc_and_length), // does peptideB contains the first amino acid of a protein(protein N-termini)
                        containsPeptideAProteinCTermini = checkProteinContainsProteinTermini(proteinA.toString(), false, acc_and_length),// does peptideA contains the last amino acid of a protein (protein C-termini)
                        containsPeptideBProteinCTermini = checkProteinContainsProteinTermini(proteinB.toString(), false, acc_and_length);// does peptideB contains the last amino acid of a protein (protein C-termini)
                // and now peptide sequences..
                peptideAseq = new StringBuilder(header_sequence.get(header).substring(0, header_sequence.get(header).indexOf("|")).replace("*", ""));
                peptideBseq = new StringBuilder(header_sequence.get(header).substring((header_sequence.get(header).indexOf("|") + 1), header_sequence.get(header).length()).replace("*", ""));
                // First, find fixed variable modifications to construct a Peptide object!
                ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModifications, peptideAseq.toString(), false, containsPeptideAProteinNTermini, containsPeptideAProteinCTermini),
                        fixedPTM_peptideB = GetPTMs.getPTM(ptmFactory, fixedModifications, peptideBseq.toString(), false, containsPeptideBProteinNTermini, containsPeptideBProteinCTermini);
                // Then, get all variable PTMs locations for a given Peptide sequence
                ArrayList<GetPTMs.PTMNameIndex> possiblePTMsPepA = GetPTMs.getPTMwithPTMNameIndex(ptmFactory, variableModifications, peptideAseq.toString(), true, containsPeptideAProteinNTermini, containsPeptideAProteinCTermini),
                        possiblePTMsPepB = GetPTMs.getPTMwithPTMNameIndex(ptmFactory, variableModifications, peptideBseq.toString(), true, containsPeptideBProteinNTermini, containsPeptideBProteinCTermini);
                // Now generate all possible variable PTMs combinations derived from a given peptide sequence and modifications       
                ArrayList<Peptide> peptideAs = getPeptidesVarPTMs(possiblePTMsPepA, peptideAseq, fixedPTM_peptideA, max_mods_per_peptide),
                        peptideBs = getPeptidesVarPTMs(possiblePTMsPepB, peptideBseq, fixedPTM_peptideB, max_mods_per_peptide);
                // fill all possible modified peptides here...
                StringBuilder info = new StringBuilder(),
                        rInfo = new StringBuilder();
                for (Peptide pA : peptideAs) {
                    for (Peptide pB : peptideBs) {
                        if (!isCPeptidesObjConstructed) {
                            cPeptide = new CPeptides(proteinA.toString(), proteinB.toString(), pA, pB, linker, linkerPosPeptideA, linkerPosPeptideB,
                                    fragMode, isContrastLinkedAttachmentOn);
                            String labelInfo = "lightLabeled";
                            if (cPeptide.getLinker().isIsLabeled()) {
                                labelInfo = "heavyLabeled";
                            }
                            info = CPeptideInfo.getInfo(cPeptide, true);
                            rInfo = CPeptideInfo.getInfo(cPeptide, false);
                            if (!hs.contains(info.toString()) && !hs.contains(rInfo.toString())) {
                                hs.add(info.toString());
                                headers.add(new StringBuilder().append(info).append("\t").append(labelInfo).append("\n"));
                                bw.write(new StringBuilder().append(info).append("\t").append(labelInfo).append("\n").toString());
                            }
                        } else {
                            cPeptide.setProteinA(proteinA.toString());
                            cPeptide.setProteinB(proteinB.toString());
                            cPeptide.setPeptideA(pA);
                            cPeptide.setPeptideB(pB);
                            cPeptide.setLinker_position_on_peptideA(linkerPosPeptideA);
                            cPeptide.setLinker_position_on_peptideB(linkerPosPeptideB);
                            info = CPeptideInfo.getInfo(cPeptide, true);
                            rInfo = CPeptideInfo.getInfo(cPeptide, false);
                            String labelInfo = "lightLabeled";
                            if (cPeptide.getLinker().isIsLabeled()) {
                                labelInfo = "heavyLabeled";
                            }
                            if (!hs.contains(info.toString()) && !hs.contains(rInfo.toString())) {
                                hs.add(info.toString());
                                headers.add(new StringBuilder().append(info).append("\t").append(labelInfo).append("\n"));
                                bw.write(new StringBuilder().append(info).append("\t").append(labelInfo).append("\n").toString());
                            }
                        }
                        isCPeptidesObjConstructed = true;
                    }
                }
            }
        }
        return headers;
    }

    public static HashSet<StringBuilder> generate_peptide_mass_index_for_contaminants(BufferedWriter bw, HashMap<String, StringBuilder> header_sequence,
            PTMFactory ptmFactory, ArrayList<String> fixedModifications, ArrayList<String> variableModifications, int max_mods_per_peptide,
            FragmentationMode fragMode, boolean isContrastLinkedAttachmentOn,
            HashMap<String, Integer> acc_and_length) throws XmlPullParserException, IOException {
        // This part for Contaminant sequence
        HashSet<StringBuilder> headers = new HashSet<StringBuilder>();
        for (String header : header_sequence.keySet()) {
            if (!header.isEmpty() && header.startsWith("contaminant")) {
                String contaminant_seq = header_sequence.get(header).toString();
                // check if tryptic peptide contains protein termini
                boolean containsProteinNTermini = checkProteinContainsProteinTermini(header, true, acc_and_length), // if contains the first amino acid of a protein (protein N-termini)
                        containsProteinCTermini = checkProteinContainsProteinTermini(header, false, acc_and_length); // ifcontains the last amino acid of a protein (protein C-termini
                ArrayList<ModificationMatch> fixedPTM_contaminant = GetPTMs.getPTM(ptmFactory, fixedModifications, contaminant_seq, false, containsProteinNTermini, containsProteinCTermini);
                ArrayList<GetPTMs.PTMNameIndex> possiblePTMsContaminant = GetPTMs.getPTMwithPTMNameIndex(ptmFactory, variableModifications, contaminant_seq, true, containsProteinNTermini, containsProteinCTermini);
                ArrayList<Peptide> contaminantAs = getPeptidesVarPTMs(possiblePTMsContaminant, new StringBuilder(contaminant_seq), fixedPTM_contaminant, max_mods_per_peptide);
                for (Peptide c : contaminantAs) {
                    String fixedModPepB = getPTMName(c.getModificationMatches(), false),
                            varModPep = getPTMName(c.getModificationMatches(), true);
                    double mass = c.getMass();
                    StringBuilder sb = new StringBuilder(header).append("\t").append("-").append("\t").append(contaminant_seq).append("\t").append("-").append("\t").append("-")
                            .append("\t").append("-").append("\t").append(fixedModPepB).append("\t").append("-").append("\t").append(varModPep).append("\t").append("-").append("\t").append(mass).append("\n");
                    bw.write(sb.toString());
                    headers.add(sb);
                }
            }
        }
        return headers;
    }

    /**
     *
     * @param possibleDetailedVariablePTMs a list of possible variable PTMs
     * @param peptideSeq a peptide sequence
     * @param fixedPTM a list of max_modifications PTMs
     * @param max_mods_per_peptide - how many variable modifications are allowed
     * on top
     * @return
     */
    private static ArrayList<Peptide> getPeptidesVarPTMs(ArrayList<GetPTMs.PTMNameIndex> possibleDetailedVariablePTMs, StringBuilder peptideSeq, ArrayList<ModificationMatch> fixedPTM, int max_modifications) {
        ArrayList<Peptide> peptides = new ArrayList<Peptide>();
        ICombinatoricsVector<GetPTMs.PTMNameIndex> varMods = Factory.createVector(possibleDetailedVariablePTMs);
        Generator<GetPTMs.PTMNameIndex> gen = Factory.createSubSetGenerator(varMods);
        for (ICombinatoricsVector<GetPTMs.PTMNameIndex> tmpVarMods : gen) {
            if (tmpVarMods.getSize() <= max_modifications) {
                Peptide pep = constructPeptideVarMods(peptideSeq.toString(), fixedPTM, tmpVarMods);
                peptides.add(pep);
            }
        }
        return peptides;
    }

    private static Peptide constructPeptideVarMods(String peptideSeq, ArrayList<ModificationMatch> fixedPTMs, ICombinatoricsVector<GetPTMs.PTMNameIndex> variablePTMs) {
        ArrayList<ModificationMatch> ptms = new ArrayList<ModificationMatch>(fixedPTMs);
        for (GetPTMs.PTMNameIndex tmpInfo : variablePTMs) {
            ModificationMatch tmpMM = new ModificationMatch(tmpInfo.getPtmName(), true, tmpInfo.getPtmIndex());
            ptms.add(tmpMM);
        }
        Peptide p = new Peptide(peptideSeq, ptms);
        return p;
    }

    /**
     * This method generates a list of CPeptides with always a fixed
     * Modification and all possible variable modifications.. Write each
     * CPeptide object on a given BufferedWriter
     *
     *
     * @param bw
     * @param header_sequence a list of crosslinked header and their
     * corresponding sequences
     * @param ptmFactory is already instantiated PTMFactory
     * @param fixedModifications is a list of given fixed Modifications.
     * @param variableModifications is a list of given variable modifications
     * @param linker a CrossLinker object to construct CPeptides objects
     * @param fragMode fragmentation mode
     * @param acc_and_length is list of accession numbers and its sequence
     * length
     * @param max_mods_per_peptide
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static HashSet<StringBuilder> generate_peptide_mass_index_monoLink(BufferedWriter bw, HashMap<String, StringBuilder> header_sequence,
            PTMFactory ptmFactory, ArrayList<String> fixedModifications, ArrayList<String> variableModifications, int max_mods_per_peptide,
            CrossLinker linker, FragmentationMode fragMode, HashMap<String, Integer> acc_and_length) throws XmlPullParserException, IOException {
        HashSet<StringBuilder> headers = new HashSet<StringBuilder>();
        boolean isMonoLinkedPeptideObjConstructed = false;
        StringBuilder proteinA,
                proteinB,
                peptideAseq,
                peptideBseq;
        MonoLinkedPeptides mPeptides = null;
        // Read each header to construct CrossLinkedPeptide object
        for (String header : header_sequence.keySet()) {
            if (!header.startsWith("contaminant") && !header.isEmpty()) {
                String[] split = header.split("_");
                int pepAIndex = 1,
                        pepBIndex = 3;
                int writen_linkerPositionPeptideA = Integer.parseInt(split[pepAIndex]),
                        writen_linkerPositionPeptideB = Integer.parseInt(split[pepBIndex]);
                // indices for linker positions necessary for constructing a CrossLinkedPeptide object...
                int linkerPosPeptideA = writen_linkerPositionPeptideA - 1,
                        linkerPosPeptideB = writen_linkerPositionPeptideB - 1;
                // now get protein names
                String[] headerSplit = header.substring(0).split("_");
                String proteinBStr = headerSplit[2];

                proteinA = new StringBuilder(headerSplit[0]);
                proteinB = new StringBuilder(proteinBStr);
                // example proteinA is P04233REVERSED(165-201)
                // check if tryptic peptide contains protein termini
                boolean containsPeptideAProteinNTermini = checkProteinContainsProteinTermini(proteinA.toString(), true, acc_and_length), // peptide contains the first amino acid of a protein (protein N-termini)
                        containsPeptideBProteinNTermini = checkProteinContainsProteinTermini(proteinB.toString(), true, acc_and_length), // peptide contains the first amino acid of a protein(protein N-termini)
                        containsPeptideAProteinCTermini = checkProteinContainsProteinTermini(proteinA.toString(), false, acc_and_length),// peptide contains the last amino acid of a protein (protein C-termini)
                        containsPeptideBProteinCTermini = checkProteinContainsProteinTermini(proteinB.toString(), false, acc_and_length);// peptide contains the last amino acid of a protein (protein C-termini)

                // and now peptide sequences..
                peptideAseq = new StringBuilder(header_sequence.get(header).substring(0, header_sequence.get(header).indexOf("|")).replace("*", ""));
                peptideBseq = new StringBuilder(header_sequence.get(header).substring((header_sequence.get(header).indexOf("|") + 1), header_sequence.get(header).length()).replace("*", ""));
                // First, find fixed variable modifications to construct a Peptide object!
                ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModifications, peptideAseq.toString(), false, containsPeptideAProteinNTermini, containsPeptideAProteinCTermini),
                        fixedPTM_peptideB = GetPTMs.getPTM(ptmFactory, fixedModifications, peptideBseq.toString(), false, containsPeptideBProteinNTermini, containsPeptideBProteinCTermini);
                // Then, get all variable PTMs locations for a given Peptide sequence
                ArrayList<GetPTMs.PTMNameIndex> possiblePTMsPepA = GetPTMs.getPTMwithPTMNameIndex(ptmFactory, variableModifications, peptideAseq.toString(), true, containsPeptideAProteinNTermini, containsPeptideAProteinCTermini),
                        possiblePTMsPepB = GetPTMs.getPTMwithPTMNameIndex(ptmFactory, variableModifications, peptideBseq.toString(), true, containsPeptideBProteinNTermini, containsPeptideBProteinCTermini);
                // Now generate all possible variable PTMs combinations derived from a given peptide sequence and modifications       
                ArrayList<Peptide> peptideAs = getPeptidesVarPTMs(possiblePTMsPepA, peptideAseq, fixedPTM_peptideA, max_mods_per_peptide),
                        peptideBs = getPeptidesVarPTMs(possiblePTMsPepB, peptideBseq, fixedPTM_peptideB, max_mods_per_peptide);
                // fill all monolinked peptides here...
                for (Peptide pA : peptideAs) {
                    if (!isMonoLinkedPeptideObjConstructed) {
                        mPeptides = new MonoLinkedPeptides(pA, proteinA.toString(), linkerPosPeptideA, linker, fragMode);
                        StringBuilder info = CPeptideInfo.getInfo(mPeptides, true),
                                rInfo = CPeptideInfo.getInfo(mPeptides, true);
                        if (!headers.contains(info) && !header.contains(rInfo)) {
                            headers.add(info.append("\n"));
                            bw.write(info + "\n");
                        }
                    } else {
                        mPeptides.setPeptide(pA);
                        mPeptides.setProtein(proteinA.toString());
                        mPeptides.setLinker_position(linkerPosPeptideA);
                        StringBuilder info = CPeptideInfo.getInfo(mPeptides, true),
                                rInfo = CPeptideInfo.getInfo(mPeptides, true);
                        if (!headers.contains(info) && !header.contains(rInfo)) {
                            headers.add(info.append("\n"));
                            bw.write(info + "\n");
                        }
                    }
                }
                isMonoLinkedPeptideObjConstructed = true;
                for (Peptide pB : peptideBs) {
                    if (!isMonoLinkedPeptideObjConstructed) {
                        isMonoLinkedPeptideObjConstructed = true;
                        mPeptides = new MonoLinkedPeptides(pB, proteinB.toString(), linkerPosPeptideB, linker, fragMode);
                        StringBuilder info = CPeptideInfo.getInfo(mPeptides, true),
                                rInfo = CPeptideInfo.getInfo(mPeptides, true);
                        if (!headers.contains(info) && !header.contains(rInfo)) {
                            headers.add(info.append("\n"));
                            bw.write(info + "\n");
                        }
                    } else {
                        mPeptides.setPeptide(pB);
                        mPeptides.setProtein(proteinB.toString());
                        mPeptides.setLinker_position(linkerPosPeptideB);
                        StringBuilder info = CPeptideInfo.getInfo(mPeptides, true),
                                rInfo = CPeptideInfo.getInfo(mPeptides, true);
                        if (!headers.contains(info) && !header.contains(rInfo)) {
                            headers.add(info.append("\n"));
                            bw.write(info + "\n");
                        }
                    }
                }
            }
        }
        return headers;
    }

    /**
     * This method checks if a tryptic peptide contains a either protein
     * N-terminus or C-terminus. It checks protein accession number of a tryptic
     * peptide (for example P04233_REVERSED(165-201)). If a tryptic peptide
     * contains the first amino acid, then protein n-termini still exist. If a
     * trypyic peptide contains the last amino acid, then protein c-termini
     * still exist
     *
     * @param proteinA protein accession number for a tryptic peptide including
     * position within a protein
     * @param checkNTermini true: checking for N-termini, false: checking for
     * C-termini
     * @param acc_and_length a map of accession number with corresponding
     * protein length
     *
     * @return true means that a tryptic peptide contains that protein termini/
     * false means that a tryptic peptide does not contain this
     */
    public static boolean checkProteinContainsProteinTermini(String proteinA, boolean checkNTermini, HashMap<String, Integer> acc_and_length) {
        boolean doesContainTermini = false;
        String acc = proteinA.substring(0, proteinA.indexOf("(")).replace(" ", ""),
                cleaveageInformation = proteinA.substring(proteinA.indexOf("("));
        int length = acc_and_length.get(acc);
        String start = "(1-",
                end = "-" + length + ")";
        if (checkNTermini) {
            if (cleaveageInformation.contains(start)) {
                doesContainTermini = true;
            }
        } else {
            if (cleaveageInformation.contains(end)) {
                doesContainTermini = true;
            }
        }
        return doesContainTermini;
    }
}
