/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package playground.MasaMHCProteins;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Sule
 */
public class MergeResults {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        String info = "setA";
        File xilmass = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\Masa\\MHC_proteins\\algorithms\\xilmass\\result/td_masa_" + info + ".txt"),
                pLink = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\Masa\\MHC_proteins\\algorithms\\pLink/hcd_Masa_" + info + "_inter_combine.spectra.xls"),
                xwalk = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\Masa\\MHC_proteins\\algorithms\\Xwalk/3PDO_vxl.txt"),
                output = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\Masa\\MHC_proteins\\algorithms\\" + info + "-merged.txt"),
                contaminants = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\Masa\\MHC_proteins\\cleaning\\" + info + "\\" + info + "_cont_5psmfdr.txt");
        // get list of Xwalk identifications
        HashSet<XLsites> xwalksites = getXwalkSites(xwalk);
        HashMap<String, XLsites> xilmasssites = getXilmassSites(xilmass);
        HashMap<String, XLsites> plinksites = getpLinkSites(pLink);
        ArrayList<String> contaminated_spectra = getContaminatedSpectra(contaminants);
        write(output, xilmasssites, plinksites, xwalksites, contaminated_spectra);
    }

    /**
     * Select XL sites of interest from XWalk predictions
     *
     * @param xwalk
     * @return
     * @throws IOException
     */
    private static HashSet<XLsites> getXwalkSites(File xwalk) throws IOException {
        HashSet<XLsites> xwalks = new HashSet<XLsites>();
        BufferedReader br = new BufferedReader(new FileReader(xwalk));
        String line = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("Index")) {
                String[] sp = line.split("\t");
                String atomAinfo = sp[2],
                        atomBinfo = sp[3],
                        proA = "P01903mod",
                        proB = "P01903mod";
                int corrA = 25,
                        corrB = 25;
                // Chain B is not protein of interest
                if (!atomAinfo.contains("-B-CB") && !atomBinfo.contains("-B-CB")) {
                    if (atomAinfo.contains("-C-CB")) {
                        proA = "P04233moda";
                        corrA = -17;
                    }
                    if (atomAinfo.contains("-C-CB")) {
                        proB = "P04233moda";
                        corrA = -17;
                    }
                    int proAind = Integer.parseInt(atomAinfo.split("-")[1]),
                            proBind = Integer.parseInt(atomBinfo.split("-")[1]);
                    proAind = proAind + corrA;
                    proBind = proBind + corrB;
                    XLsites o = new XLsites(proA, proB, proAind, proBind, sp[6], sp[5]);
                    System.out.println(o.toString());
                    xwalks.add(o);
                }
            }
        }
        return xwalks;
    }

    /**
     * Select Xilmass identified sites
     *
     * @param xilmass
     * @return
     */
    private static HashMap<String, XLsites> getXilmassSites(File xilmass) throws IOException {
        HashMap<String, XLsites> xilmasssites = new HashMap<String, XLsites>();
        BufferedReader br = new BufferedReader(new FileReader(xilmass));
        String line = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("SpectrumFile")) {
                String[] sp = line.split("\t");
                String key = sp[1],
                        proA = sp[10],
                        proB = sp[13];
                int proAind = Integer.parseInt(sp[18]),
                        proBind = Integer.parseInt(sp[19]);
                XLsites site = new XLsites(proA, proB, proAind, proBind);
                xilmasssites.put(key, site);
            }
        }
        return xilmasssites;
    }

    /**
     * Select pLink identified sites
     *
     * @param pLink
     * @return
     * @throws IOException
     */
    private static HashMap<String, XLsites> getpLinkSites(File pLink) throws IOException {
        HashMap<String, XLsites> pLinksites = new HashMap<String, XLsites>();
        BufferedReader br = new BufferedReader(new FileReader(pLink));
        String line = "",
                key = "",
                proA = "",
                proB = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("*") && !line.startsWith("#")) {
                //get spectrum info
                key = line.split("\t")[1];
            } else if (!line.startsWith("#") && !(line.contains("#,Spec"))) {
                String proteinpair = line.split("\t")[9];
                proA = proteinpair.split("-")[0].split("\\|")[1];
                proB = proteinpair.split("-")[1].split("\\|")[1];
                String extraInfoA = proteinpair.split("-")[0].split("\\|")[2],
                        extraInfoB = proteinpair.split("-")[1].split("\\|")[2];
                String t1 = extraInfoA.substring(extraInfoA.indexOf("(") + 1, extraInfoA.indexOf(")")),
                        t2 = extraInfoB.substring(extraInfoB.indexOf("(") + 1, extraInfoB.indexOf(")"));
                int proAind = Integer.parseInt(t1),
                        proBind = Integer.parseInt(t2);
                XLsites site = new XLsites(proA, proB, proAind, proBind);
                pLinksites.put(key, site);
            }
        }
        return pLinksites;
    }

    /**
     * Merge pLink, xilmass and xwalk results together and write down on output
     * file
     *
     * @param output is merged output
     * @param xilmasssites a list of Xilmass identified sites
     * @param plinksites a list of pLink identified sites
     * @param xwalksites a list of Xwalk predicted sites
     * @param contaminated_spectra a validated PSM list derived from
     * contaminanted spectra
     */
    private static void write(File output, HashMap<String, XLsites> xilmasssites, HashMap<String, XLsites> plinksites, HashSet<XLsites> xwalksites, ArrayList<String> contaminated_spectra) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        bw.write("XL site" + "\t" + "XWalk" + "\t" + "SASD(A)" + "\t" + "CarbonAlpha(A)" + "\t" + "Xilmass" + "\t" + "pLink" + "\n");
        // all identified spectra
        ArrayList<String> all_specs = new ArrayList<String>(xilmasssites.keySet());
        all_specs.addAll(plinksites.keySet());
        // all identified XL sites
        HashSet<XLsites> all_xl_sites = new HashSet<XLsites>(xilmasssites.values());
        all_xl_sites.addAll(plinksites.values());
        // for every spectrum 

        ArrayList<XLsites> xwalksitesal = new ArrayList<XLsites>(xwalksites);
        for (XLsites xl : all_xl_sites) {
            // if Xwalk has the value..
            bw.write(xl.toString() + "\t");
            boolean idXWalk = false;
            for (XLsites tmp : xwalksitesal) {
                if (tmp.equals(xl)) {
                    bw.write("X" + "\t" + tmp.getSasDist() + "\t" + tmp.getAlphaDist() + "\t");
                    idXWalk = true;
                    break;
                }
            }
            if (!idXWalk) {
                bw.write("-" + "\t" + "-" + "\t" + "-" + "\t");
            }
            // if Xilmass gave identifications, only select if not from a contaminantated spectra
            int xilmass_psm = 0;
            for (String spec_title : xilmasssites.keySet()) {
//                System.out.println("Xilmass-title" + "\t" + spec_title);
                if (xilmasssites.get(spec_title).equals(xl) && !contaminated_spectra.contains(spec_title)) {
                    xilmass_psm++;
                }
            }
            if (xilmass_psm == 0) {
                bw.write("-" + "\t");
            } else {
                bw.write(xilmass_psm + "\t");
            }
            // if pLink gave identifications if not from a contaminantated spectra
            int pLink_psm = 0;
            for (String spec_title : plinksites.keySet()) {
//                System.out.println("pLink-title" + "\t" + spec_title);
                if (plinksites.get(spec_title).equals(xl) && !contaminated_spectra.contains(spec_title)) {
                    pLink_psm++;
                }
            }
            if (pLink_psm == 0) {
                bw.write("-" + "\n");
            } else {
                bw.write(pLink_psm + "\n");
            }
        }
        bw.close();
    }

    /**
     * To select a contaminant derived spectra
     *
     * @param contaminants
     * @return
     * @throws IOException
     */
    private static ArrayList<String> getContaminatedSpectra(File contaminants) throws IOException {
        ArrayList<String> contaminatedSpectra = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(contaminants));
        String line = "";
        System.out.println("Contaminated spectra");
        while ((line = br.readLine()) != null) {
            String title = line.split("\t")[8];
            contaminatedSpectra.add(title);
            System.out.println(title);
        }
        System.out.println("\n");
        return contaminatedSpectra;
    }

    // a class to hold information for cross-linking sites..
    static class XLsites {

        // attributes
        private String proA,
                proB,
                sasDist = "-",
                alphaDist = "-";
        private int proAind,
                proBind;

        // constructors
        public XLsites(String proA, String proB, int proAind, int proBind) {
            this.proA = proA;
            this.proB = proB;
            this.proAind = proAind;
            this.proBind = proBind;
            // correct indexing.. 
            // if two are the same protein, then proBind is bigger value
            if (proA.equals(proB)) {
                if (proAind > proBind) {
                    this.proAind = proBind;
                    this.proBind = proAind;
                }
            } else {
                // if two are not the same. 
                // the order of proA is P01903mod,P01911mod,P04233moda
                if (proAind > proBind) {
                    this.proA = proB;
                    this.proAind = proBind;
                    this.proB = proA;
                    this.proBind = proAind;
                }
            }
        }

        public XLsites(String proA, String proB, int proAind, int proBind, String sasDist, String alphaDist) {
            this.proA = proA;
            this.proB = proB;
            this.proAind = proAind;
            this.proBind = proBind;
            if (proA.equals(proB)) {
                if (proAind > proBind) {
                    this.proAind = proBind;
                    this.proBind = proAind;
                }
            } else {
                // if two are not the same. 
                // the order of proA is P01903mod,P01911mod,P04233moda
                if (proAind > proBind) {
                    this.proA = proB;
                    this.proAind = proBind;
                    this.proB = proA;
                    this.proBind = proAind;
                }
            }
            this.sasDist = sasDist;
            this.alphaDist = alphaDist;
        }

        // getter and setter methods
        public String getProA() {
            return proA;
        }

        public void setProA(String proA) {
            this.proA = proA;
        }

        public String getProB() {
            return proB;
        }

        public void setProB(String proB) {
            this.proB = proB;
        }

        public int getProAind() {
            return proAind;
        }

        public void setProAind(int proAind) {
            this.proAind = proAind;
        }

        public int getProBind() {
            return proBind;
        }

        public void setProBind(int proBind) {
            this.proBind = proBind;
        }

        public String getSasDist() {
            return sasDist;
        }

        public void setSasDist(String sasDist) {
            this.sasDist = sasDist;
        }

        public String getAlphaDist() {
            return alphaDist;
        }

        public void setAlphaDist(String alphaDist) {
            this.alphaDist = alphaDist;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + (this.proA != null ? this.proA.hashCode() : 0);
            hash = 29 * hash + (this.proB != null ? this.proB.hashCode() : 0);
            hash = 29 * hash + this.proAind;
            hash = 29 * hash + this.proBind;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final XLsites other = (XLsites) obj;
            if ((this.proA == null) ? (other.proA != null) : !this.proA.equals(other.proA)) {
                return false;
            }
            if ((this.proB == null) ? (other.proB != null) : !this.proB.equals(other.proB)) {
                return false;
            }
            if (this.proAind != other.proAind) {
                return false;
            }
            if (this.proBind != other.proBind) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            if (!sasDist.equals("-")) {
                return proA + "_" + proAind + "_" + proB + "_" + proBind + "\t" + sasDist + "\t" + alphaDist;
            }
            return proA + "_" + proAind + "_" + proB + "_" + proBind;
        }
    }
}
