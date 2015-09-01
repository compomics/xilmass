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
 * This class is used to load
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
     * @param isBranching
     * @param isContrastLinkedAttachmentOn
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static HashMap<CPeptides, Double> readFiletoGetCPeptideTheoMass(File file, PTMFactory ptmFactory, CrossLinker linker,
            FragmentationMode fragMode, boolean isBranching, boolean isContrastLinkedAttachmentOn)
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
                CPeptides cPeptide = new CPeptides(proteinA, proteinB, peptideA, peptideB, linker, linkerPosPeptideA, linkerPosPeptideB, fragMode, isBranching, isContrastLinkedAttachmentOn);
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
     * @param isBranching true:is branching/false:attaching
     * @param isContrastLinkedAttachmentOn
     * @param max_mods_per_peptide
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static ArrayList<CPeptides> generate_peptide_mass_index(
            HashMap<String, String> header_sequence,
            PTMFactory ptmFactory,
            ArrayList<String> fixedModifications,
            ArrayList<String> variableModifications,
            CrossLinker linker, FragmentationMode fragMode,
            boolean isBranching,
            boolean isContrastLinkedAttachmentOn,
            int max_mods_per_peptide) throws XmlPullParserException, IOException {

        ArrayList<CPeptides> cPeptides = new ArrayList<CPeptides>();
        StringBuilder proteinA,
                proteinB,
                peptideAseq,
                peptideBseq;
        // Read each header to construct CrossLinkedPeptide object
        for (String header : header_sequence.keySet()) {
            String[] split = header.split("_");
            int pepAIndex = 1,
                    pepBIndex = 3;
            if (split[pepAIndex].equals("inverted")) {
                pepBIndex++;
                pepAIndex++;
            }
            if (split[pepBIndex].equals("inverted")) {
                pepBIndex++;
            }
            int writen_linkerPositionPeptideA = Integer.parseInt(split[pepAIndex]),
                    writen_linkerPositionPeptideB = Integer.parseInt(split[pepBIndex]);
            // indices for linker positions necessary for constructing a CrossLinkedPeptide object...
            int linkerPosPeptideA = writen_linkerPositionPeptideA - 1,
                    linkerPosPeptideB = writen_linkerPositionPeptideB - 1;
            // now get protein names
            String[] headerSplit = header.substring(0).split("_");
            proteinA = new StringBuilder(headerSplit[0]);
            proteinB = new StringBuilder(headerSplit[2]);
            // and now peptide sequences..
            peptideAseq = new StringBuilder(header_sequence.get(header).substring(0, header_sequence.get(header).indexOf("|")).replace("*", ""));
            peptideBseq = new StringBuilder(header_sequence.get(header).substring((header_sequence.get(header).indexOf("|") + 1), header_sequence.get(header).length()).replace("*", ""));
            // First, find fixed variable modifications to construct a Peptide object!
            ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModifications, peptideAseq.toString(), false),
                    fixedPTM_peptideB = GetPTMs.getPTM(ptmFactory, fixedModifications, peptideBseq.toString(), false);
            // Then, get all variable PTMs locations for a given Peptide sequence
            ArrayList<GetPTMs.PTMNameIndex> possiblePTMsPepA = GetPTMs.getPTMwithPTMNameIndex(ptmFactory, variableModifications, peptideAseq.toString(), true),
                    possiblePTMsPepB = GetPTMs.getPTMwithPTMNameIndex(ptmFactory, variableModifications, peptideBseq.toString(), true);
            // Now generate all possible variable PTMs combinations derived from a given peptide sequence and modifications       
            ArrayList<Peptide> peptideAs = getPeptidesVarPTMs(possiblePTMsPepA, peptideAseq, fixedPTM_peptideA, max_mods_per_peptide),
                    peptideBs = getPeptidesVarPTMs(possiblePTMsPepB, peptideBseq, fixedPTM_peptideB, max_mods_per_peptide);
            // fill all possible modified peptides here...
            for (Peptide pA : peptideAs) {
                for (Peptide pB : peptideBs) {
                    CPeptides cPeptide = new CPeptides(proteinA.toString(), proteinB.toString(), pA, pB, linker, linkerPosPeptideA,
                            linkerPosPeptideB, fragMode, isBranching, isContrastLinkedAttachmentOn);
                    cPeptides.add(cPeptide);
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
     * @param isBranching true:is branching/false:attaching
     * @param isContrastLinkedAttachmentOn
     * @param max_mods_per_peptide
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static HashSet<StringBuilder> generate_peptide_mass_index(
            BufferedWriter bw,
            HashMap<String, String> header_sequence,
            PTMFactory ptmFactory,
            ArrayList<String> fixedModifications,
            ArrayList<String> variableModifications,
            CrossLinker linker, FragmentationMode fragMode,
            boolean isBranching, boolean isContrastLinkedAttachmentOn,
            int max_mods_per_peptide) throws XmlPullParserException, IOException {
        HashSet<StringBuilder> headers = new HashSet<StringBuilder>();
        boolean isCPeptidesObjConstructed = false;
        StringBuilder proteinA,
                proteinB,
                peptideAseq,
                peptideBseq;
        CPeptides cPeptide = null;
        // Read each header to construct CrossLinkedPeptide object
        for (String header : header_sequence.keySet()) {
            if (!header.startsWith("contaminant") && !header.isEmpty()) {
                String[] split = header.split("_");
                int pepAIndex = 1,
                        pepBIndex = 3;
                if (split[pepAIndex].equals("inverted")) {
                    pepBIndex++;
                    pepAIndex++;
                }
                if (split[pepBIndex].equals("inverted")) {
                    pepBIndex++;
                }
                int writen_linkerPositionPeptideA = Integer.parseInt(split[pepAIndex]),
                        writen_linkerPositionPeptideB = Integer.parseInt(split[pepBIndex]);
                // indices for linker positions necessary for constructing a CrossLinkedPeptide object...
                int linkerPosPeptideA = writen_linkerPositionPeptideA - 1,
                        linkerPosPeptideB = writen_linkerPositionPeptideB - 1;
                // now get protein names
                String[] headerSplit = header.substring(0).split("_");
                String proteinBStr = headerSplit[2];
                if (pepBIndex != 3) {
                    proteinBStr += "_" + "inverted";
                }
                proteinA = new StringBuilder(headerSplit[0]);
                proteinB = new StringBuilder(proteinBStr);
                // and now peptide sequences..
                peptideAseq = new StringBuilder(header_sequence.get(header).substring(0, header_sequence.get(header).indexOf("|")).replace("*", ""));
                peptideBseq = new StringBuilder(header_sequence.get(header).substring((header_sequence.get(header).indexOf("|") + 1), header_sequence.get(header).length()).replace("*", ""));
                // First, find fixed variable modifications to construct a Peptide object!
                ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModifications, peptideAseq.toString(), false),
                        fixedPTM_peptideB = GetPTMs.getPTM(ptmFactory, fixedModifications, peptideBseq.toString(), false);
                // Then, get all variable PTMs locations for a given Peptide sequence
                ArrayList<GetPTMs.PTMNameIndex> possiblePTMsPepA = GetPTMs.getPTMwithPTMNameIndex(ptmFactory, variableModifications, peptideAseq.toString(), true),
                        possiblePTMsPepB = GetPTMs.getPTMwithPTMNameIndex(ptmFactory, variableModifications, peptideBseq.toString(), true);
                // Now generate all possible variable PTMs combinations derived from a given peptide sequence and modifications       
                ArrayList<Peptide> peptideAs = getPeptidesVarPTMs(possiblePTMsPepA, peptideAseq, fixedPTM_peptideA, max_mods_per_peptide),
                        peptideBs = getPeptidesVarPTMs(possiblePTMsPepB, peptideBseq, fixedPTM_peptideB, max_mods_per_peptide);
                // fill all possible modified peptides here...
                for (Peptide pA : peptideAs) {
                    for (Peptide pB : peptideBs) {
                        if (!isCPeptidesObjConstructed) {
                            cPeptide = new CPeptides(proteinA.toString(), proteinB.toString(), pA, pB, linker, linkerPosPeptideA, linkerPosPeptideB,
                                    fragMode, isBranching, isContrastLinkedAttachmentOn);
                            StringBuilder info = CPeptideInfo.getInfo(cPeptide, true),
                                    rInfo = CPeptideInfo.getInfo(cPeptide, false);
                            if (!headers.contains(info) && !header.contains(rInfo)) {
                                headers.add(new StringBuilder(info + "\t" + cPeptide.getLinker().isIsLabeled() + "\n"));
                                bw.write(info + "\t" + cPeptide.getLinker().isIsLabeled() + "\n");
                            }
                        } else {
                            cPeptide.setProteinA(proteinA.toString());
                            cPeptide.setProteinB(proteinB.toString());
                            cPeptide.setPeptideA(pA);
                            cPeptide.setPeptideB(pB);
                            cPeptide.setLinker_position_on_peptideA(linkerPosPeptideA);
                            cPeptide.setLinker_position_on_peptideB(linkerPosPeptideB);
                             StringBuilder info = CPeptideInfo.getInfo(cPeptide, true),
                                    rInfo = CPeptideInfo.getInfo(cPeptide, false);
                            if (!headers.contains(info) && !header.contains(rInfo)) {
                                headers.add(new StringBuilder(info + "\t" + cPeptide.getLinker().isIsLabeled() + "\n"));
                                bw.write(info + "\t" + cPeptide.getLinker().isIsLabeled() + "\n");
                            }
                        }
                        isCPeptidesObjConstructed = true;
                    }
                }
            }
        }
        return headers;
    }

    public static HashSet<StringBuilder> generate_peptide_mass_index_for_contaminants(
            BufferedWriter bw,
            HashMap<String, String> header_sequence,
            PTMFactory ptmFactory,
            ArrayList<String> fixedModifications,
            ArrayList<String> variableModifications,
            FragmentationMode fragMode,
            boolean isBranching, boolean isContrastLinkedAttachmentOn,
            int max_mods_per_peptide) throws XmlPullParserException, IOException {
        // This part for Contaminant sequence
        HashSet<StringBuilder> headers = new HashSet<StringBuilder>();

        for (String header : header_sequence.keySet()) {
            if (!header.isEmpty() && header.startsWith("contaminant")) {
                String contaminant_seq = header_sequence.get(header);
                ArrayList<ModificationMatch> fixedPTM_contaminant = GetPTMs.getPTM(ptmFactory, fixedModifications, contaminant_seq, false);
                ArrayList<GetPTMs.PTMNameIndex> possiblePTMsContaminant = GetPTMs.getPTMwithPTMNameIndex(ptmFactory, variableModifications, contaminant_seq, true);
                ArrayList<Peptide> contaminantAs = getPeptidesVarPTMs(possiblePTMsContaminant, new StringBuilder(contaminant_seq), fixedPTM_contaminant, max_mods_per_peptide);
                for (Peptide c : contaminantAs) {
                    String fixedModPepB = getPTMName(c.getModificationMatches(), false),
                            varModPep = getPTMName(c.getModificationMatches(), true);
                    double mass = c.getMass();
                    StringBuilder sb = new StringBuilder(header + "\t" + "-" + "\t" + contaminant_seq + "\t" + "-" + "\t" + "-" + "\t" + "-" + "\t" + fixedModPepB + "\t" + "-" + "\t" + varModPep + "\t" + "-" + "\t" + mass);
                    bw.write(sb + "\n");
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
     * @param isBranching true:is branching/false:attaching
     * @param max_mods_per_peptide
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static HashSet<String> generate_peptide_mass_index_monoLink(
            BufferedWriter bw,
            HashMap<String, String> header_sequence,
            PTMFactory ptmFactory,
            ArrayList<String> fixedModifications,
            ArrayList<String> variableModifications,
            CrossLinker linker, FragmentationMode fragMode, boolean isBranching,
            int max_mods_per_peptide) throws XmlPullParserException, IOException {
        HashSet<String> headers = new HashSet<String>();
        boolean isMonoLinkedPeptideObjConstructed = false;
        StringBuilder proteinA,
                proteinB,
                peptideAseq,
                peptideBseq;
        MonoLinkedPeptides mPeptides = null;
        double mass = 0;
        // Read each header to construct CrossLinkedPeptide object
        for (String header : header_sequence.keySet()) {
            if (!header.startsWith("contaminant") && !header.isEmpty()) {
                String[] split = header.split("_");
                int pepAIndex = 1,
                        pepBIndex = 3;
                if (split[pepAIndex].equals("inverted")) {
                    pepBIndex++;
                    pepAIndex++;
                }
                if (split[pepBIndex].equals("inverted")) {
                    pepBIndex++;
                }
                int writen_linkerPositionPeptideA = Integer.parseInt(split[pepAIndex]),
                        writen_linkerPositionPeptideB = Integer.parseInt(split[pepBIndex]);
                // indices for linker positions necessary for constructing a CrossLinkedPeptide object...
                int linkerPosPeptideA = writen_linkerPositionPeptideA - 1,
                        linkerPosPeptideB = writen_linkerPositionPeptideB - 1;
                // now get protein names
                String[] headerSplit = header.substring(0).split("_");
                String proteinBStr = headerSplit[2];
                if (pepBIndex != 3) {
                    proteinBStr += "_" + "inverted";
                }
                proteinA = new StringBuilder(headerSplit[0]);
                proteinB = new StringBuilder(proteinBStr);
                // and now peptide sequences..
                peptideAseq = new StringBuilder(header_sequence.get(header).substring(0, header_sequence.get(header).indexOf("|")).replace("*", ""));
                peptideBseq = new StringBuilder(header_sequence.get(header).substring((header_sequence.get(header).indexOf("|") + 1), header_sequence.get(header).length()).replace("*", ""));
                // First, find fixed variable modifications to construct a Peptide object!
                ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModifications, peptideAseq.toString(), false),
                        fixedPTM_peptideB = GetPTMs.getPTM(ptmFactory, fixedModifications, peptideBseq.toString(), false);
                // Then, get all variable PTMs locations for a given Peptide sequence
                ArrayList<GetPTMs.PTMNameIndex> possiblePTMsPepA = GetPTMs.getPTMwithPTMNameIndex(ptmFactory, variableModifications, peptideAseq.toString(), true),
                        possiblePTMsPepB = GetPTMs.getPTMwithPTMNameIndex(ptmFactory, variableModifications, peptideBseq.toString(), true);
                // Now generate all possible variable PTMs combinations derived from a given peptide sequence and modifications       
                ArrayList<Peptide> peptideAs = getPeptidesVarPTMs(possiblePTMsPepA, peptideAseq, fixedPTM_peptideA, max_mods_per_peptide),
                        peptideBs = getPeptidesVarPTMs(possiblePTMsPepB, peptideBseq, fixedPTM_peptideB, max_mods_per_peptide);
                // fill all monolinked peptides here...
                for (Peptide pA : peptideAs) {
                    if (!isMonoLinkedPeptideObjConstructed) {
                        mPeptides = new MonoLinkedPeptides(pA, proteinA.toString(), linkerPosPeptideA, linker, fragMode, isBranching);
                        mass = mPeptides.getTheoretical_xlinked_mass();
                        StringBuilder info = CPeptideInfo.getInfo(mPeptides, true),
                                rInfo = CPeptideInfo.getInfo(mPeptides, true);
                        if(!headers.contains(info) && !header.contains(rInfo)){
                        headers.add(info + "\n");
                        bw.write(info + "\n");
                        }
                    } else {
                        mPeptides.setPeptide(pA);
                        mPeptides.setProtein(proteinA.toString());
                        mPeptides.setLinker_position(linkerPosPeptideA);
                        mass = mPeptides.getTheoretical_xlinked_mass();
                       StringBuilder info = CPeptideInfo.getInfo(mPeptides, true),
                                rInfo = CPeptideInfo.getInfo(mPeptides, true);
                        if(!headers.contains(info) && !header.contains(rInfo)){
                        headers.add(info + "\n");
                        bw.write(info + "\n");
                        }
                    }
                }
                isMonoLinkedPeptideObjConstructed = true;
                for (Peptide pB : peptideBs) {
                    if (!isMonoLinkedPeptideObjConstructed) {
                        isMonoLinkedPeptideObjConstructed = true;
                        mPeptides = new MonoLinkedPeptides(pB, proteinB.toString(), linkerPosPeptideB, linker, fragMode, isBranching);
                        mass = mPeptides.getTheoretical_xlinked_mass();
                       StringBuilder info = CPeptideInfo.getInfo(mPeptides, true),
                                rInfo = CPeptideInfo.getInfo(mPeptides, true);
                        if(!headers.contains(info) && !header.contains(rInfo)){
                        headers.add(info + "\n");
                        bw.write(info + "\n");
                        }
                    } else {
                        mPeptides.setPeptide(pB);
                        mPeptides.setProtein(proteinB.toString());
                        mPeptides.setLinker_position(linkerPosPeptideB);
                        mass = mPeptides.getTheoretical_xlinked_mass();
                        StringBuilder info = CPeptideInfo.getInfo(mPeptides, true),
                                rInfo = CPeptideInfo.getInfo(mPeptides, true);
                        if(!headers.contains(info) && !header.contains(rInfo)){
                        headers.add(info + "\n");
                        bw.write(info + "\n");
                        }
                    }
                }
            }
        }
        return headers;
    }
}
