/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

import com.compomics.dbtoolkit.io.UnknownDBFormatException;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import crossLinker.CrossLinker;
import crossLinker.GetCrossLinker;
import database.CreateDatabase;
import database.FASTACPDBLoader;
import database.WriteCXDB;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import jcuda.*;
import jcuda.jcublas.*;

import static jcuda.jcublas.JCublas2.*;
import static jcuda.jcublas.cublasPointerMode.*;
import jcuda.runtime.JCuda;
import static jcuda.runtime.JCuda.*;
import static jcuda.runtime.cudaMemcpyKind.*;
import matching.FindMatch;
import org.xmlpull.v1.XmlPullParserException;
import theoretical.CPeptideIon;
import theoretical.CPeptides;
import theoretical.FragmentationMode;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public class Start {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownDBFormatException, IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException, Exception {

        System.out.println("Program starts! " + "\t" + new Date().toString());

        // STEP 1: DATABASE GENERATIONS! 
        String givenDBName = "C:\\Users\\Sule\\Documents\\PhD\\cross_linked\\xquest_examples\\data\\online_test/online_bsa_db/bsa.fasta",
                inSilicoPeptideDBName = "C:\\Users\\Sule\\Documents\\PhD\\cross_linked/xquest_examples\\data\\online_test/online_bsa_db/bsa_test_insilico",
                cxDBName = "C:\\Users\\Sule\\Documents\\PhD\\cross_linked\\xquest_examples\\data\\online_test/online_bsa_db/bsa_test_cxms",
                cxDBNameCache = cxDBName + "header_seq_mass_cxms.cache",
                folder = "C:\\Users\\Sule\\Documents\\PhD\\cross_linked\\xquest_examples\\data\\online_test/online_bsa_db",
                crossLinkerName = "BS3",
                crossLinkedProteinTypes = "Both",
                enzymeName = "Trypsin",
                enzymeFileName = "C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\src\\resources\\enzymes.txt",
                modsFileName = "C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\src\\resources\\mods.xml",
                misclevaged = "2",
                lowMass = "200",
                highMass = "5000",
                //mgfs = "C:\\Users\\Sule\\Documents\\PhD\\cross_linked\\stavroX_test",
                mgfs = "C:\\Users\\Sule\\Documents\\PhD\\cross_linked\\xquest_examples\\data\\online_test\\online_bsa_db",
                fixed_modification = ""; //fixed_modification = "carbamidomethyl c";  fixed_modification = "oxidation of M";
        FragmentationMode fragMode = FragmentationMode.CID;
        // Importing PTMs, so getting a PTMFactory object 
        PTMFactory ptmFactory = PTMFactory.getInstance();
        ptmFactory.importModifications(new File(modsFileName), false, false);
        int minLen = 0,
                maxLen_for_combined = 45,
                scoring = 0; // 0-MSAmanda, 1-Andromeda
        boolean does_link_to_itself = false,
                isLabeled = false,
                does_write_cxdb = false;
        // Parameters for searching against experimental spectrum 
        double ms2Err = 0.2, //Fragment tolerance - mz diff
                ms1Err = 10; // Precursor tolerance - ppm error 
        boolean isPPM = true; // Relative or absolute precursor tolerance 
        CrossLinker linker = GetCrossLinker.getCrossLinker(crossLinkerName, isLabeled); // Required for constructing theoretical spectra
        System.out.println("CX database is checking! " + "\t" + new Date().toString());

        // TODO: test this part! 
        // This part of the code makes sure that an already generated CXDB is not constructed again..
        int control = 0;
        HashMap<String, String> header_sequence = new HashMap<String, String>();
        HashMap<CPeptides, Double> cPeptide_TheoreticalMass = null;
        for (File f : new File(folder).listFiles()) {
            if (f.getName().endsWith(".fastacp")) {
                control++;
                File cxDBFile = new File(cxDBName + ".fastacp");
                System.out.println("An already constrcuted fastacp file is found!" + new File(cxDBName + ".fastacp").getName());
                // Read a file 
                header_sequence = getHeaderSequence(cxDBFile);
                cPeptide_TheoreticalMass = FASTACPDBLoader.getCPeptide_TheoreticalMass(new File(cxDBNameCache), ptmFactory, fixed_modification, linker, fragMode);
            }
        }
        if (control == 0) {
            // So, file is empty..             
            System.out.println("No errors, and file empty");
            CreateDatabase instance = new CreateDatabase(givenDBName, inSilicoPeptideDBName, cxDBName, // db related parameters
                    crossLinkerName, crossLinkedProteinTypes, // crossLinker related parameters
                    enzymeName, enzymeFileName, misclevaged, // enzyme related parameters
                    lowMass, highMass, // filtering of in silico peptides on peptide masses
                    minLen, maxLen_for_combined, does_link_to_itself, isLabeled);
            header_sequence = instance.getHeader_sequence();
            cPeptide_TheoreticalMass = FASTACPDBLoader.getCPeptide_TheoreticalMass(new File(cxDBNameCache), header_sequence, ptmFactory, fixed_modification, linker, fragMode);
        }
        // If necessary, write the output
        if (does_write_cxdb) {
            WriteCXDB.writeCXDB(header_sequence, cxDBName);
        }
        System.out.println("CX database is ready! " + "\t" + new Date().toString());
        System.out.println("Header and sequence object is ready" + header_sequence.size() + "\t" + new Date().toString());

        // STEP 2: CONSTRUCT CPEPTIDE OBJECTS
        ArrayList<Double> theoretical_masses = new ArrayList<Double>();
        ArrayList<CPeptides> cpeptides = new ArrayList<CPeptides>();
        for (CPeptides cPeptide : cPeptide_TheoreticalMass.keySet()) {
            double theoreticalMass = cPeptide_TheoreticalMass.get(cPeptide);
            theoretical_masses.add(theoreticalMass);
            cpeptides.add(cPeptide);
        }

        // STEP 3: GENERATE EACH THEORETICAL SPECTRUM
        // Get all MSnSpectrum! MS2 spectra
        File ms2spectra = new File(mgfs);
        int num = 0;
        // Maybe MSnSpectrum with PMs
        HashMap<Double, MSnSpectrum> precursorMass_MSnSpectrum = new HashMap<Double, MSnSpectrum>();
        // Or maybe just arraylist sorted by precursor mass values        
        for (File mgf : ms2spectra.listFiles()) {
            if (mgf.getName().endsWith("mgf")) {
                SpectrumFactory fct = SpectrumFactory.getInstance();
                fct.addSpectra(mgf);
                for (String title : fct.getSpectrumTitles(mgf.getName())) {
                    num++;
                    MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
                    precursorMass_MSnSpectrum.put(ms.getPrecursor().getMass(1), ms);
                    // PREPARE MSnSPECTRUM object no<w 
                    ArrayList<Charge> possibleCharges = ms.getPrecursor().getPossibleCharges();
                    Charge charge = possibleCharges.get(possibleCharges.size() - 1);
                    System.out.print(num + "\t" + "MSnSpectrum=" + ms.getSpectrumTitle() + "\t" + "PrecursorMZ=" + ms.getPrecursor().getMz() + "\t" + "Charge=" + ms.getPrecursor().getPossibleChargesAsString() + "\n");
                    int charge_value = charge.value;
                    double precursor_mass = ms.getPrecursor().getMass(charge_value);
                    String peptideAlphaMSAmanda = "",
                            peptideBetaMSAmanda = "",
                            peptideAlphaAndromeda = "",
                            peptideBetaAndromeda = "";
                    double maxPMSMSAmanda = Double.MIN_VALUE,
                            maxPSMAndromeda = Double.MIN_VALUE,
                            bestPPMMSAmanda = Double.MAX_VALUE,
                            bestPPMAndromeda = Double.MAX_VALUE;

                    for (int i = 0; i < theoretical_masses.size(); i++) {
                        Double theoretical_mass = theoretical_masses.get(i);
                        double tmpMS1Err = CalculateMS1Err.getMS1Err(isPPM, theoretical_mass, precursor_mass);
                        if (tmpMS1Err <= ms1Err) {
                            // Make sure that different charge states are added to the same theoretical spectra! 
                            CPeptides tmpCpeptides = cpeptides.get(i);
                            FindMatch f = new FindMatch(ms, 0, tmpCpeptides, ms2Err, charge_value); // 0 - for MSAmanda
                            double psmscore = f.getPSMscore();

                            if (maxPMSMSAmanda < psmscore) {
                                maxPMSMSAmanda = psmscore;
                                peptideAlphaMSAmanda = (tmpCpeptides.getPeptide_alpha().getSequence());
                                peptideBetaMSAmanda = (tmpCpeptides.getPeptide_beta().getSequence());
                                int linkerPositionOnAlpha = tmpCpeptides.getLinker_position_on_alpha(),
                                        linkerPositionOnBeta = tmpCpeptides.getLinker_position_on_beta();
                                bestPPMMSAmanda = tmpMS1Err;
                                System.out.print("\t" + precursor_mass + "\t" + theoretical_mass + "\t" + "MSAmanda" + "\t" + "PPM error=" + "\t" + bestPPMMSAmanda + "\t" + "MSAmanda derived score=" + maxPMSMSAmanda + "\t" + "AlphaSequence=" + peptideAlphaMSAmanda + "\t" + "BetaSequence=" + peptideBetaMSAmanda + "\t" + linkerPositionOnAlpha + "\t" + linkerPositionOnBeta + "\n");
                                // Andromeda
//                                System.out.print("\t" + "Andromeda" + "\t " + "PPM error=" + "\t" + bestPPMAndromeda + "\t" + "Andromeda derived score=" + maxPSMAndromeda + "\t" + "AlphaSequence=" + peptideAlphaAndromeda + "\t" + "BetaSequence=" + peptideBetaAndromeda + "\t" + linkerPositionOnAlpha + "\t" + linkerPositionOnBeta + "\n");
                            }
                            f.setScoring(1);// 1 - for Andromeda
                            double psmscore2 = f.getPSMscore();
                            if (maxPSMAndromeda < psmscore2) {
                                maxPSMAndromeda = psmscore2;
                                peptideAlphaAndromeda = (tmpCpeptides.getPeptide_alpha().getSequence());
                                peptideBetaAndromeda = (tmpCpeptides.getPeptide_beta().getSequence());
                                int linkerPositionOnAlpha = tmpCpeptides.getLinker_position_on_alpha(),
                                        linkerPositionOnBeta = tmpCpeptides.getLinker_position_on_beta();
                                bestPPMAndromeda = tmpMS1Err;
//                                System.out.print("\t" + "MSAmanda" + "\t" + "PPM error=" + "\t" + bestPPMMSAmanda + "\t" + "MSAmanda derived score=" + maxPMSMSAmanda + "\t" + "AlphaSequence=" + peptideAlphaMSAmanda + "\t" + "BetaSequence=" + peptideBetaMSAmanda + "\n");
                                // Andromeda
                                System.out.print("\t" + precursor_mass + "\t" + theoretical_mass + "\t" + "Andromeda" + "\t " + "PPM error=" + "\t" + bestPPMAndromeda + "\t" + "Andromeda derived score=" + maxPSMAndromeda + "\t" + "AlphaSequence=" + peptideAlphaAndromeda + "\t" + "BetaSequence=" + peptideBetaAndromeda + "\t" + linkerPositionOnAlpha + "\t" + linkerPositionOnBeta + "\n");

                            }
                        }
                    }
                }
            }
        }
    }
    

    private static HashMap<String, String> getHeaderSequence(File cxDBFile) throws IOException {
        HashMap<String, String> headerAndSequence = new HashMap<String, String>();
        BufferedReader br = new BufferedReader(new FileReader(cxDBFile));
        String line = "",
                header = "",
                sequence = "";
        while ((line = br.readLine()) != null) {

            if (line.startsWith(">")) {
                // so this is a header
                header = line.substring(1);
                sequence = "";
            } else {
                sequence = line;
                headerAndSequence.put(header, sequence);
            }
        }
        return headerAndSequence;
    }

    // ?? Do multithreading here! 
//    private static HashMap<CPeptides, Double> getCPeptide_TheoreticalMass(File mass_pept_cache, HashMap<String, String> header_sequence, PTMFactory ptmFactory, String fixed_modification, CrossLinker linker, FragmentationMode fragMode) throws XmlPullParserException, IOException {
//        HashMap<CPeptides, Double> cPeptides_theoreticalMasses = new HashMap<CPeptides, Double>();
//        Peptide peptideAlpha = null,
//                peptideBeta = null;
//        CPeptides cPeptide = null;
//        String seqs = "",
//                startSeq = "",
//                nextSeq = "";
//        BufferedWriter bw = new BufferedWriter(new FileWriter(mass_pept_cache));
//        bw.write("start_sequence \t next_sequence \t linker_position_start \t linker_position_next \t theoretical_mass \n");
//        for (String header : header_sequence.keySet()) {
//            // indices must be known to generate a cross linked theoretical spectrum
//            String[] split = header.split("_");
//            int startIndex = 1,
//                    endIndex = 3;
//            if (split[startIndex].equals("inverted")) {
//                endIndex++;
//                startIndex++;
//            }
//            if (split[endIndex].equals("inverted")) {
//                endIndex++;
//            }
//            int linker_position_start = Integer.parseInt(split[startIndex]) - 1,
//                    linker_position_next = Integer.parseInt(split[endIndex]) - 1;
//            seqs = header_sequence.get(header);
//            startSeq = seqs.substring(0, seqs.indexOf("|")).replace("*", "");
//            nextSeq = seqs.substring((seqs.indexOf("|") + 1), seqs.length()).replace("*", "");
//            bw.write(startSeq + "\t" + nextSeq + "\t");
//
//            ArrayList<ModificationMatch> ptmAlpha = GetFixedPTM.getPTM(ptmFactory, fixed_modification, startSeq),
//                    ptmBeta = GetFixedPTM.getPTM(ptmFactory, fixed_modification, nextSeq);
//            peptideAlpha = new Peptide(startSeq, ptmAlpha);
//            peptideBeta = new Peptide(nextSeq, ptmBeta);
//            cPeptide = new CPeptides(peptideAlpha, peptideBeta, linker, linker_position_start, linker_position_next, fragMode);
//            bw.write(linker_position_start + "\t" + linker_position_next + "\t");
//
//            double theoretical_mass = cPeptide.getTheoretical_mass();
//            cPeptides_theoreticalMasses.put(cPeptide, theoretical_mass);
//            bw.write(theoretical_mass + "\n");
//        }
//        bw.close();
//        return cPeptides_theoreticalMasses;
//    }
//
//    private static HashMap<CPeptides, Double> getCPeptide_TheoreticalMass(File file, PTMFactory ptmFactory, String fixed_modification, CrossLinker linker, FragmentationMode fragMode) throws XmlPullParserException, IOException {
//        HashMap<CPeptides, Double> cPeptide_theoreticalMass = new HashMap<CPeptides, Double>();
//        BufferedReader br = new BufferedReader(new FileReader(file));
//        String line = "";
//        while ((line = br.readLine()) != null) {
//            if (!line.startsWith("start")) {
//                String[] split = line.split("\t");
//                String startSeq = split[0],
//                        nextSeq = split[1];
//                Integer linker_position_start = Integer.parseInt(split[2]),
//                        linker_position_next = Integer.parseInt(split[3]);
//                Double theoreticalMass = Double.parseDouble(split[4]);
//
//                ArrayList<ModificationMatch> ptmAlpha = GetFixedPTM.getPTM(ptmFactory, fixed_modification, startSeq),
//                        ptmBeta = GetFixedPTM.getPTM(ptmFactory, fixed_modification, nextSeq);
//
//                Peptide peptideAlpha = new Peptide(startSeq, ptmAlpha),
//                        peptideBeta = new Peptide(nextSeq, ptmBeta);
//                CPeptides cPeptide = new CPeptides(peptideAlpha, peptideBeta, linker, linker_position_start, linker_position_next, fragMode);
//                cPeptide_theoreticalMass.put(cPeptide, theoreticalMass);
//            }
//        }
//        return cPeptide_theoreticalMass;
//    }
    /**
     * Simple wrapper class to allow synchronisation on the hasNext() and next()
     * methods of the iterator.
     */
//    public static class InnerIteratorSync<T> {
//
//        private Iterator<T> iter = null;
//
//        public InnerIteratorSync(Iterator<T> aIterator) {
//            iter = aIterator;
//        }
//
//        public synchronized T next() {
//            T result = null;
//            if (iter.hasNext()) {
//                result = iter.next();
//            }
//            return result;
//        }
//    }
//    public static HashMap<CPeptides, Double> get_score_via_multiThreading(MSnSpectrum msms, ArrayList<CPeptides> cPeptides, int scoring, double fragTol, int charge,
//            ExecutorService excService)
//            throws InterruptedException, ExecutionException {
//        // Now do multi-threading and calculate for all selected goat spectra for the selected pig-spectrum
//        // submit job
//        HashMap<CPeptides, Double> result = new HashMap<CPeptides, Double>();
//        List<Future<HashMap<CPeptides, Double>>> futureList = new ArrayList<Future<HashMap<CPeptides, Double>>>();
//        InnerIteratorSync<CPeptides> specs_got = new InnerIteratorSync(cPeptides.iterator());
//        while (specs_got.iter.hasNext()) {
//            CPeptides tmp_cPeptide = (CPeptides) specs_got.iter.next();
//
//            synchronized (msms) {
//                FindMatch f = new FindMatch(msms, scoring, tmp_cPeptide, fragTol, charge);
//                IteratorCalleableMultiThreadedScoring callable = new IteratorCalleableMultiThreadedScoring(f);
//                Future<HashMap<CPeptides, Double>> submit = excService.submit(callable);
//                futureList.add(submit);
//            }
//        }
//        // run over the list of Futures and
//        for (Future<HashMap<CPeptides, Double>> future : futureList) {
//            try {
//                HashMap<CPeptides, Double> spec_and_score = future.get();
//                result.putAll(spec_and_score);
//            } catch (InterruptedException e) {
//            } catch (ExecutionException e) {
//            }
//        }
//        return result;
//    }
}
