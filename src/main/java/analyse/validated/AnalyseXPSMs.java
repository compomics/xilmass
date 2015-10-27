/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.validated;

import analyse.shared.Information;
import analyse.shared.retrieving.RetrieveKojak;
import analyse.shared.retrieving.RetrievePLink;
import analyse.shared.retrieving.RetrievePercolatorKojak;
import analyse.shared.retrieving.RetrieveValidatedList;
import analyse.shared.retrieving.RetrieveXilmass;
import analyse.shared.retrieving.RetrievepLinkValidateds;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * This class is used to compare two results files against
 *
 * TODO: Test Kojak and Write/Test XQuest
 *
 * @author Sule
 */
public class AnalyseXPSMs {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        File xilmassInput = new File("C:/Users/Sule/Documents/PhD/XLinked/XLinkData_Freiburg/competetives/xilmass/td_dss_hcdElite_Both_Scr4_MP0_MC2_rDecoy_allPeaks.txt"),
                validatedPLinkInput = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\pLink/pLink_Native5FDRValidated_Elite_OnlyTarget.txt"),
                allXPSMPLink = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\pLink/pLink_AllXPSMs_Validated_Elite_OnlyTarget.txt"),
                kojak = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\kojak\\merged/kojakP_HCDDSS_Elite.txt"),
                percolator = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\kojak\\merged/kojakP_HCDDSS_Elite.txt");

        boolean isTraditionalFDR = false; // true=regular FDR approach, false=pLink approach on FDR calculation during comparison 
        int analysis = 0; // 0:Xilmass, 1:pLinkValidated 2:pLinkAll 10:Xilmass-PLinkValidated 11:Xilmass-PLinkAll      
        double fdr = 0.0500000,
                qvalue = 0.01000;

        RetrieveValidatedList o = null;
        if (analysis == 0) {
            // Xilmass results       
            o = new RetrieveXilmass(xilmassInput);
        } else if (analysis == 1) {
            o = new RetrievepLinkValidateds(validatedPLinkInput);
        } else if (analysis == 2) {
            o = new RetrievePLink(allXPSMPLink);
        } else if (analysis == 3) {
            o = new RetrieveKojak(kojak);
        } else if (analysis == 4) {// percolator result ones
            o = new RetrievePercolatorKojak(percolator);
        }

        if (analysis < 10 && analysis != 4) {
            // first you need to prepare validated list via
            o.getValidateds(fdr, isTraditionalFDR);
            // now select rank list for corresponding fdr...
            HashMap<Integer, Double> rankAnDfdr = o.getRankAnDfdr(fdr);
            // print rank of found decoy and their FDR..
            printFDRs(rankAnDfdr);
            // find cross linking sites info...
            HashMap<CrossLinkingSite, Integer> idXLinkingSites = RetrieveValidatedList.getXLinkingSites(o.getValidateds(fdr, isTraditionalFDR));
            // print all found cross linking site info..
            printXLinkingSites(idXLinkingSites);
        }

        // Kojak Percolator! !
        if (analysis == 4) {
            RetrievePercolatorKojak second2 = new RetrievePercolatorKojak(kojak);
            ArrayList<Information> secondValidateds = second2.getQValidateds(qvalue);
            HashMap<CrossLinkingSite, Integer> idXLinkingSites = RetrieveValidatedList.getXLinkingSites(secondValidateds);
            // print all found cross linking site info..
            printXLinkingSites(idXLinkingSites);
        }

        if (analysis >= 10) {
            ArrayList<Information> firstValidateds = new ArrayList<Information>(),
                    secondValidateds = new ArrayList<Information>(); // Here there are only validated hits 

            ArrayList<String> names = new ArrayList<String>();
            // To compare two inputs... 
            RetrieveValidatedList first = null, // the first information list..
                    second = null; // the second information list...
            if (analysis == 10) {
                names.add("Xilmass");
                names.add("ValidatedPLink");
                names.add("Shared");
                first = new RetrieveXilmass(xilmassInput);
                firstValidateds = first.getValidateds(fdr, isTraditionalFDR);
                second = new RetrievepLinkValidateds(validatedPLinkInput);
                secondValidateds = second.getRetrievedInfo();
            } else if (analysis == 11) {
                names.add("Xilmass");
                names.add("RawPLink");
                names.add("Shared");
                first = new RetrieveXilmass(xilmassInput);
                second = new RetrievePLink(allXPSMPLink); // PLink result from raw xpsm...
                firstValidateds = first.getValidateds(fdr, isTraditionalFDR);
                System.err.println("Check PLink all results");
            } else if (analysis == 12) {
                names.add("Xilmass");
                names.add("Kojak");
                names.add("Shared");
                first = new RetrieveXilmass(xilmassInput);
                second = new RetrieveKojak(kojak); // PLink result from raw xpsm...
                firstValidateds = first.getValidateds(fdr, isTraditionalFDR);
                secondValidateds = second.getValidateds(fdr, isTraditionalFDR);
            } else if (analysis == 13) {
                names.add("Xilmass");
                names.add("PercolatorKojak");
                names.add("Shared");
                first = new RetrieveXilmass(xilmassInput);
                RetrievePercolatorKojak second2 = new RetrievePercolatorKojak(kojak); // PLink result from raw xpsm...
                firstValidateds = first.getValidateds(fdr, isTraditionalFDR);
                secondValidateds = second2.getQValidateds(qvalue);
            }
            // now find shared XPSMs and cross linking sites...
            ArrayList<Information> sharedValidatedList = getSharedInfo(firstValidateds, secondValidateds);
            printValidated(firstValidateds, secondValidateds, sharedValidatedList, names);

            // now find xlinking sites
            HashMap<CrossLinkingSite, Integer> firstXLinkingSites = RetrieveValidatedList.getXLinkingSites(firstValidateds),
                    secondXLinkingSites = RetrieveValidatedList.getXLinkingSites(secondValidateds);
            HashMap<CrossLinkingSite, String> sharedXLinkingSites = getSharedXLinkingSites(firstValidateds, secondValidateds);

            printXlinkings(firstXLinkingSites, secondXLinkingSites, sharedXLinkingSites, names);

            // find only first and second..
            HashMap<CrossLinkingSite, Integer> onlyFirstXLinkingSites = findOnly(firstXLinkingSites, sharedXLinkingSites),
                    onlySecondXLinkingSites = findOnly(secondXLinkingSites, sharedXLinkingSites);

            // print only the firstList/shared/secondList xlinking sites information
            System.out.print("\n");
            System.out.println("Only found XLinkingSites by " + names.get(0));
            printXLinkingSites(onlyFirstXLinkingSites);
            System.out.print("\n");
            System.out.println("Found shared XLinkingSites ");
            print(sharedXLinkingSites);
            System.out.print("\n");
            System.out.println("Only found XLinkingSites by " + names.get(1));
            printXLinkingSites(onlySecondXLinkingSites);

            System.out.print("\n");
            System.out.println("Same MSMS but different linking...");
            sameMSMSdiffXLinking(firstValidateds, secondValidateds);
        }
    }

    /**
     * This method compares two given arraylist of information to find
     * intersection ones
     *
     * @param first
     * @param second
     * @return
     */
    public static ArrayList<Information> getSharedInfo(ArrayList<Information> first, ArrayList<Information> second) {
        // find shared ones..
        ArrayList<Information> sharedInfo = new ArrayList<Information>();
        // check each Information objects on Xilmass results
        for (Information i : first) {
            for (Information iP : second) {
                if (i.equals(iP)) {
                    sharedInfo.add(i);
                } else if (i.getFileName().equals(iP.getFileName())
                        && i.getScanNumber().equals(iP.getScanNumber())
                        && i.getLinkA().equals(iP.getLinkB())
                        && i.getLinkB().equals(iP.getLinkA())
                        && i.getProteinA().equals(iP.getProteinB())
                        && i.getProteinB().equals(iP.getProteinA())) {
                    sharedInfo.add(i);
                }
            }
        }
        return sharedInfo;
    }

    /**
     * This method finds shared xlinking sites between two given information
     * lists
     *
     * @param first
     * @param second
     * @return
     */
    public static HashMap<CrossLinkingSite, String> getSharedXLinkingSites(ArrayList<Information> first, ArrayList<Information> second) {
        HashMap<CrossLinkingSite, Integer> firstXLinkingsXPSMs = RetrieveValidatedList.getXLinkingSites(first),
                secondXLinkingsXPSMs = RetrieveValidatedList.getXLinkingSites(second);
        HashMap<CrossLinkingSite, String> sharedXLinkedsXPSMs = new HashMap<CrossLinkingSite, String>();
        // each each cross linking sites
        for (CrossLinkingSite i : firstXLinkingsXPSMs.keySet()) {
            for (CrossLinkingSite iP : secondXLinkingsXPSMs.keySet()) {
                if (i.getProteinA().equals(iP.getProteinA()) && i.getProteinB().equals(iP.getProteinB()) && i.getLinkA().equals(iP.getLinkA()) && i.getLinkB().equals(iP.getLinkB())
                        || i.getProteinA().equals(iP.getProteinB()) && i.getProteinB().equals(iP.getProteinA()) && i.getLinkA().equals(iP.getLinkB()) && i.getLinkB().equals(iP.getLinkA())) {
                    sharedXLinkedsXPSMs.put(i, firstXLinkingsXPSMs.get(i) + "_" + secondXLinkingsXPSMs.get(iP));
                }
            }
        }
        return sharedXLinkedsXPSMs;
    }

    /**
     * This method checks if there are the same MS/MS spectra with different
     * cross linking sites..
     *
     * @param first
     * @param second
     * @throws IOException
     */
    public static void sameMSMSdiffXLinking(ArrayList<Information> first, ArrayList<Information> second) throws IOException {
        ArrayList<Information> diffs = new ArrayList<Information>();
        for (Information i : first) {
            boolean isFound = false;
            boolean isSameSpectrum = false;
            for (Information iP : second) {
                if (i.getFileName().equals(iP.getFileName()) && i.getScanNumber().equals(iP.getScanNumber())) {
                    isSameSpectrum = true;
                    if (i.getProteinA().equals(iP.getProteinA()) && i.getProteinB().equals(iP.getProteinB()) && i.getLinkA().equals(iP.getLinkA()) && i.getLinkB().equals(iP.getLinkB())) {
                        isFound = true;
                    }
                }
            }
            if (!isFound) {
                // check now other way around to be sure...
                for (Information iP : second) {
                    if (i.getFileName().equals(iP.getFileName()) && i.getScanNumber().equals(iP.getScanNumber())) {
                        isSameSpectrum = true;
                        if ((i.getProteinA().equals(iP.getProteinB()) && i.getProteinB().equals(iP.getProteinA()) && i.getLinkA().equals(iP.getLinkB()) && i.getLinkB().equals(iP.getLinkA()))) {
                            isFound = true;
                        }
                    }
                }
            }
            if (!isFound && isSameSpectrum) {
                diffs.add(i);
                System.out.println(i.getFileName() + "\t" + i.getScanNumber());

            }
        }
        System.out.println("XPSMs by Xilmass=" + first.size());
        System.out.println("XPSMs by PLink=" + second.size());
        System.out.println("SAME SPECTRUM DIFFERENT CROSS LINKING SITES=" + diffs.size());
    }

    public static void printXLinkingSites(HashMap<CrossLinkingSite, Integer> xLinkingSitesAndXPSMs) {
        System.out.println("ProteinA" + "\t" + "ProteinB" + "\t" + "LinkA" + "\t" + "LinkB" + "\t" + "XPSMs" + "\t" + "EuclideanAlpha" + "\t" + "EuclideanBeta" + "\t" + "Prediction");
        for (CrossLinkingSite i : xLinkingSitesAndXPSMs.keySet()) {
            System.out.println(i.getProteinA() + "\t" + i.getProteinB() + "\t"
                    + i.getLinkA() + "\t" + i.getLinkB() + "\t" + xLinkingSitesAndXPSMs.get(i) + "\t"
                    + i.getEuclideanAlpha() + "\t" + i.getEuclideanBeta() + "\t" + i.getPrediction());
        }
    }

    public static void print(HashMap<CrossLinkingSite, String> xLinkingSitesAndXPSMs) {
        System.out.println("ProteinA" + "\t" + "ProteinB" + "\t" + "LinkA" + "\t" + "LinkB" + "\t" + "XPSMs(First-Second)" + "\t" + "EuclideanAlpha" + "\t" + "EuclideanBeta" + "\t" + "Prediction");
        for (CrossLinkingSite i : xLinkingSitesAndXPSMs.keySet()) {
            System.out.println(i.getProteinA() + "\t" + i.getProteinB() + "\t"
                    + i.getLinkA() + "\t" + i.getLinkB() + "\t" + xLinkingSitesAndXPSMs.get(i) + "\t"
                    + i.getEuclideanAlpha() + "\t" + i.getEuclideanBeta() + "\t" + i.getPrediction());
        }
    }

    public static void printFDRs(HashMap<Integer, Double> rankAnDfdr) throws IOException {
        System.out.println("Rank \t FDR");
        ArrayList<Integer> keys = new ArrayList<Integer>(rankAnDfdr.keySet());
        Collections.sort(keys);
        for (Integer i : keys) {
            System.out.println(i + "\t" + rankAnDfdr.get(i));
        }
    }

    private static void printValidated(ArrayList<Information> firstValidateds, ArrayList<Information> secondValidateds,
            ArrayList<Information> sharedValidatedList, ArrayList<String> names) {
        System.out.println(names.get(0) + " #XPSMs=" + "\t" + firstValidateds.size());
        System.out.println(names.get(1) + " #XPSMs=" + "\t" + secondValidateds.size());
        System.out.println(names.get(2) + " #XPSMs=" + "\t" + sharedValidatedList.size());
    }

    private static void printXlinkings(HashMap<CrossLinkingSite, Integer> firstXLinkingSites, HashMap<CrossLinkingSite, Integer> secondXLinkingSites,
            HashMap<CrossLinkingSite, String> sharedXLinkingSites, ArrayList<String> names) {
        System.out.println(names.get(0) + " #XLinkingSites=" + "\t" + firstXLinkingSites.size());
        System.out.println(names.get(1) + " #XLinkingSites=" + "\t" + secondXLinkingSites.size());
        System.out.println(names.get(2) + " #XLinkingSites=" + "\t" + sharedXLinkingSites.size());
    }

    public static HashMap<CrossLinkingSite, Integer> findOnly(HashMap<CrossLinkingSite, Integer> xLinkingSites, HashMap<CrossLinkingSite, String> sharedXLinkingSites) {
        HashMap<CrossLinkingSite, Integer> onlyXLinkingSites = new HashMap<CrossLinkingSite, Integer>();
        for (CrossLinkingSite i : xLinkingSites.keySet()) {
            boolean isShared = false;
            for (CrossLinkingSite iP : sharedXLinkingSites.keySet()) {
                if (i.getProteinA().equals(iP.getProteinA()) && i.getProteinB().equals(iP.getProteinB()) && i.getLinkA().equals(iP.getLinkA()) && i.getLinkB().equals(iP.getLinkB())
                        || i.getProteinA().equals(iP.getProteinB()) && i.getProteinB().equals(iP.getProteinA()) && i.getLinkA().equals(iP.getLinkB()) && i.getLinkB().equals(iP.getLinkA())) {
                    isShared = true;
                }
            }
            if (!isShared) {
                onlyXLinkingSites.put(i, xLinkingSites.get(i));
            }
        }
        return onlyXLinkingSites;
    }
}
