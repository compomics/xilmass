/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.validated;

import analyse.shared.Information;
import analyse.shared.retrieving.RetrieveKojak;
import analyse.shared.retrieving.RetrievePLink;
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
        File xilmassInput = new File("C:/Users/Sule/Documents/PhD/XLinked/XLinkData_Freiburg/competetives/xilmass/td_dss_hcdElite_Both_Scr4_MinPeak0_rDecoy_allPeaks.txt"),
                validatedPLinkInput = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\pLink/pLink_Native5FDRValidated_Elite_OnlyTarget.txt"),
                allXPSMPLink = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\pLink/pLink_AllXPSMs_Validated_Elite_OnlyTarget.txt"),
                kojak = new File("");

        boolean isTraditionalFDR = false; // true=regular FDR approach, false=pLink approach on FDR calculation during comparison 
        int analysis = 0; // 0:Xilmass, 1:pLinkValidated 2:pLinkAll 10:Xilmass-PLinkValidated 11:Xilmass-PLinkAll      
        double fdr = 0.0500000;

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
        }

        if (analysis < 10) {
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

        if (analysis > 10) {
            ArrayList<String> names = new ArrayList<String>();
            // To compare two inputs... 
            RetrieveValidatedList first = null, // the first information list..
                    second = null; // the second information list...
            if (analysis == 10) {
                names.add("Xilmass");
                names.add("ValidatedPLink");
                names.add("Shared");
                first = new RetrieveXilmass(xilmassInput);
                second = new RetrievepLinkValidateds(validatedPLinkInput);
            } else if (analysis == 11) {
                names.add("Xilmass");
                names.add("RawPLink");
                names.add("Shared");
                first = new RetrieveXilmass(xilmassInput);
                second = new RetrievePLink(allXPSMPLink); // PLink result from raw xpsm...
            } else if (analysis == 12) {
                names.add("Xilmass");
                names.add("Kojak");
                names.add("Shared");
                first = new RetrieveXilmass(xilmassInput);
                second = new RetrieveKojak(kojak); // PLink result from raw xpsm...
            }
            // now find shared XPSMs and cross linking sites...
            ArrayList<Information> firstValidateds = first.getValidateds(0.0500, isTraditionalFDR),
                    secondValidateds = second.getRetrievedInfo(); // Here there are only validated hits 
            ArrayList<Information> sharedValidatedList = getSharedInfo(firstValidateds, secondValidateds);
            printValidated(firstValidateds, secondValidateds, sharedValidatedList, names);

            // now find xlinking sites
            HashMap<CrossLinkingSite, Integer> firstXLinkingSites = RetrieveValidatedList.getXLinkingSites(firstValidateds),
                    secondXLinkingSites = RetrieveValidatedList.getXLinkingSites(secondValidateds);
            HashMap<CrossLinkingSite, Integer> sharedXLinkingSites = getSharedXLinkingSites(firstValidateds, secondValidateds);

            printXlinkings(firstXLinkingSites, secondXLinkingSites, sharedXLinkingSites, names);

            // find only first and second..
            HashMap<CrossLinkingSite, Integer> onlyFirstXLinkingSites = findOnly(firstXLinkingSites, sharedXLinkingSites),
                    onlySecondXLinkingSites = findOnly(secondXLinkingSites, sharedXLinkingSites);

            // print only the firstList/shared/secondList xlinking sites information
            printXLinkingSites(onlyFirstXLinkingSites);
            printXLinkingSites(sharedXLinkingSites);
            printXLinkingSites(onlySecondXLinkingSites);

//        System.out.println("Same MSMS but different linking...");
            //            sameMSMSdiffXLinking(xilmassValidateds, pLnkVals, isTraditionalFDR);
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
    public static HashMap<CrossLinkingSite, Integer> getSharedXLinkingSites(ArrayList<Information> first, ArrayList<Information> second) {
        HashMap<CrossLinkingSite, Integer> xlinkingxilmass = RetrieveValidatedList.getXLinkingSites(first),
                xlinkingspLink = RetrieveValidatedList.getXLinkingSites(second),
                sharedXLinkedsXPSMs = new HashMap<CrossLinkingSite, Integer>();
        // each each cross linking sites
        for (CrossLinkingSite i : xlinkingxilmass.keySet()) {
            for (CrossLinkingSite iP : xlinkingspLink.keySet()) {
                if (i.getProteinA().equals(iP.getProteinA()) && i.getProteinB().equals(iP.getProteinB()) && i.getLinkA().equals(iP.getLinkA()) && i.getLinkB().equals(iP.getLinkB())
                        || i.getProteinA().equals(iP.getProteinB()) && i.getProteinB().equals(iP.getProteinA()) && i.getLinkA().equals(iP.getLinkB()) && i.getLinkB().equals(iP.getLinkA())) {
                    sharedXLinkedsXPSMs.put(i, xlinkingspLink.get(iP));
                    sharedXLinkedsXPSMs.put(i, xlinkingxilmass.get(iP));
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
            for (Information iP : second) {
                if (i.getFileName().equals(iP.getFileName()) && i.getScanNumber().equals(iP.getScanNumber())) {
                    if (i.getProteinA().equals(iP.getProteinA()) && i.getProteinB().equals(iP.getProteinB()) && i.getLinkA().equals(iP.getLinkA()) && i.getLinkB().equals(iP.getLinkB())
                            || i.getProteinA().equals(iP.getProteinB()) && i.getProteinB().equals(iP.getProteinA()) && i.getLinkA().equals(iP.getLinkB()) && i.getLinkB().equals(iP.getLinkA())) {
                        diffs.add(i);
                    }
                }
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
        System.out.println(names.get(0) + "=" + "\t" + firstValidateds.size());
        System.out.println(names.get(1) + "=" + "\t" + secondValidateds.size());
        System.out.println(names.get(2) + "=" + "\t" + sharedValidatedList.size());
    }

    private static void printXlinkings(HashMap<CrossLinkingSite, Integer> firstXLinkingSites, HashMap<CrossLinkingSite, Integer> secondXLinkingSites,
            HashMap<CrossLinkingSite, Integer> sharedXLinkingSites, ArrayList<String> names) {
        System.out.println(names.get(0) + "=" + "\t" + firstXLinkingSites.size());
        System.out.println(names.get(1) + "=" + "\t" + secondXLinkingSites.size());
        System.out.println(names.get(2) + "=" + "\t" + sharedXLinkingSites.size());
    }

    public static HashMap<CrossLinkingSite, Integer> findOnly(HashMap<CrossLinkingSite, Integer> xLinkingSites, HashMap<CrossLinkingSite, Integer> sharedXLinkingSites) {
        HashMap<CrossLinkingSite, Integer> onlyXLinkingSites = new HashMap<CrossLinkingSite, Integer>();
        for (CrossLinkingSite c : xLinkingSites.keySet()) {
            if (!sharedXLinkingSites.containsKey(c)) {
                onlyXLinkingSites.put(c, xLinkingSites.get(c));
            }
        }
        return onlyXLinkingSites;
    }
}
