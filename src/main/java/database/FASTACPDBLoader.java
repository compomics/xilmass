/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import com.compomics.dbtoolkit.io.implementations.DefaultDBLoader;
import com.compomics.dbtoolkit.io.interfaces.DBLoader;
import com.compomics.dbtoolkit.io.interfaces.Filter;
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
public class FASTACPDBLoader  {   
    
    
     public static HashMap<CPeptides, Double> getCPeptide_TheoreticalMass(File mass_pept_cache, HashMap<String, String> header_sequence, PTMFactory ptmFactory, String fixed_modification, CrossLinker linker, FragmentationMode fragMode) throws XmlPullParserException, IOException {
        HashMap<CPeptides, Double> cPeptides_theoreticalMasses = new HashMap<CPeptides, Double>();
        Peptide peptideAlpha = null,
                peptideBeta = null;
        CPeptides cPeptide = null;
        String seqs = "",
                startSeq = "",
                nextSeq = "";
        BufferedWriter bw = new BufferedWriter(new FileWriter(mass_pept_cache));
        bw.write("start_sequence \t next_sequence \t linker_position_start \t linker_position_next \t theoretical_mass \n");
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
            int linker_position_start = Integer.parseInt(split[startIndex]) - 1,
                    linker_position_next = Integer.parseInt(split[endIndex]) - 1;
            seqs = header_sequence.get(header);
            startSeq = seqs.substring(0, seqs.indexOf("|")).replace("*", "");
            nextSeq = seqs.substring((seqs.indexOf("|") + 1), seqs.length()).replace("*", "");
            bw.write(startSeq + "\t" + nextSeq + "\t");

            ArrayList<ModificationMatch> ptmAlpha = GetFixedPTM.getPTM(ptmFactory, fixed_modification, startSeq),
                    ptmBeta = GetFixedPTM.getPTM(ptmFactory, fixed_modification, nextSeq);
            peptideAlpha = new Peptide(startSeq, ptmAlpha);
            peptideBeta = new Peptide(nextSeq, ptmBeta);
            cPeptide = new CPeptides(peptideAlpha, peptideBeta, linker, linker_position_start, linker_position_next, fragMode);
            bw.write(linker_position_start + "\t" + linker_position_next + "\t");

            double theoretical_mass = cPeptide.getTheoretical_mass();
            cPeptides_theoreticalMasses.put(cPeptide, theoretical_mass);
            bw.write(theoretical_mass + "\n");
        }
        bw.close();
        return cPeptides_theoreticalMasses;
    }
     
     public static HashMap<CPeptides, Double> getCPeptide_TheoreticalMass(File file, PTMFactory ptmFactory, String fixed_modification, CrossLinker linker, FragmentationMode fragMode) throws XmlPullParserException, IOException {
        HashMap<CPeptides, Double> cPeptide_theoreticalMass = new HashMap<CPeptides, Double>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("start")) {
                String[] split = line.split("\t");
                String startSeq = split[0],
                        nextSeq = split[1];
                Integer linker_position_start = Integer.parseInt(split[2]),
                        linker_position_next = Integer.parseInt(split[3]);
                Double theoreticalMass = Double.parseDouble(split[4]);

                ArrayList<ModificationMatch> ptmAlpha = GetFixedPTM.getPTM(ptmFactory, fixed_modification, startSeq),
                        ptmBeta = GetFixedPTM.getPTM(ptmFactory, fixed_modification, nextSeq);

                Peptide peptideAlpha = new Peptide(startSeq, ptmAlpha),
                        peptideBeta = new Peptide(nextSeq, ptmBeta);
                CPeptides cPeptide = new CPeptides(peptideAlpha, peptideBeta, linker, linker_position_start, linker_position_next, fragMode);
                cPeptide_theoreticalMass.put(cPeptide, theoreticalMass);
            }
        }
        return cPeptide_theoreticalMass;
    }

   
}
