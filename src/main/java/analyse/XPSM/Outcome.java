/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.XPSM;


/**
 *
 * @author Sule
 */
public abstract class Outcome {

    protected String[] target_proteins;
    protected String accessProteinA,
            accessProteinB;
    protected boolean hasTraditionalDecoy = false;
    protected int crossLinkedSitePro1,
            crossLinkedSitePro2;

    public String[] getTarget_proteins() {
        return target_proteins;
    }

    public void setTarget_proteins(String[] target_proteins) {
        this.target_proteins = target_proteins;
    }

    public String getAccessProteinA() {
        return accessProteinA;
    }

    public void setAccessProteinA(String accessProteinA) {
        this.accessProteinA = accessProteinA;
    }

    public String getAccessProteinB() {
        return accessProteinB;
    }

    public void setAccessProteinB(String accessProteinB) {
        this.accessProteinB = accessProteinB;
    }

    public boolean isHasTraditionalDecoy() {
        return hasTraditionalDecoy;
    }

    public void setHasTraditionalDecoy(boolean hasTraditionalDecoy) {
        this.hasTraditionalDecoy = hasTraditionalDecoy;
    }

    public int getCrossLinkedSitePro1() {
        return crossLinkedSitePro1;
    }

    public void setCrossLinkedSitePro1(int crossLinkedSitePro1) {
        this.crossLinkedSitePro1 = crossLinkedSitePro1;
    }

    public int getCrossLinkedSitePro2() {
        return crossLinkedSitePro2;
    }

    public void setCrossLinkedSitePro2(int crossLinkedSitePro2) {
        this.crossLinkedSitePro2 = crossLinkedSitePro2;
    }

//       
//    public String assetTrueLinking(HashSet<TrueLinking> trueLinkings) {
//        String res = "Not-predicted" + "\t" + "-";
//        for (TrueLinking tl : trueLinkings) {
//            if (tl.getProteinA().equals(accessProteinA)
//                    && tl.getProteinB().equals(accessProteinB)
//                    && tl.getIndexA() == crossLinkedSitePro1
//                    && tl.getIndexB() == crossLinkedSitePro2) {
//                res = tl.getClassification() + "\t" + tl.getEuclidean_distance_alpha() + "\t" + tl.getEuclidean_distance_beta();
//            }
//        }
//        return res;
//    }

    public String getTargetDecoy() {
        String first_protein_name = target_proteins[0],
                second_protein_name = target_proteins[1];
        String type = "";
        if (hasTraditionalDecoy) {
            System.err.println("NOT WORKING8");
        } else {
            type = "half-decoy";
            if ((accessProteinA.equals(first_protein_name) || accessProteinA.equals(second_protein_name))
                    && (accessProteinB.equals(first_protein_name) || accessProteinB.equals(second_protein_name))) {
                type = "target";
            }
            if ((!accessProteinA.equals(first_protein_name) && !accessProteinA.equals(second_protein_name))
                    && (!accessProteinB.equals(first_protein_name) && !accessProteinB.equals(second_protein_name))) {
                type = "decoy";
            }
        }
        return type;
    }

}
