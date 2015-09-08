/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.XPSM.outcome;


/**
 * This class hold information from each Xlinking algorithms
 *
 * @author Sule
 */
public abstract class Outcome {

    protected String[] target_proteins;
    protected String accessProteinA,
            accessProteinB,
            label;
    protected int crossLinkedSitePro1,
            crossLinkedSitePro2;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

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
}
