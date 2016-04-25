/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.xwalk_uniprot;

/**
 * 
 * This class contains information from a cross-linked peptide to prepare a PyMol script
 * 
 * @author Sule
 */
public class PyMOLEntry {

    private String pdb,
            atomA,
            atomB,
            idDis,
            type,
            uniprotAccA,
            uniprotIndexA,
            uniprotAccB,
            uniprotIndexB,
            sas,
            betaDist,
            alphaDist,
            command = "";

    public PyMOLEntry(String pdb, String name, String atomA, String atomB, String idDis, String type, String uniprotAccA, String uniprotIndexA, String uniprotAccB, String uniprotIndexB,
            String sas, String betaDist, String alphaDist, boolean isCarbonAlpha) {
        this.pdb = pdb;
        this.atomA = atomA;
        this.atomB = atomB;
        this.idDis = idDis;
        this.type = type;
        this.uniprotAccA = uniprotAccA;
        this.uniprotIndexA = uniprotIndexA;
        this.uniprotAccB = uniprotAccB;
        this.uniprotIndexB = uniprotIndexB;
        this.sas = sas;
        this.betaDist = betaDist;
        this.alphaDist = alphaDist;

        String structure = pdb.substring(0, pdb.indexOf(".pdb"));
        String[] firsts = atomA.split("-"),
                seconds = atomB.split("-");
        String firstInfo = "/" + structure + "//" + firsts[2] + "/" + firsts[0] + "`" + firsts[1] + "/" + "CA",
                secondInfo = "/" + structure + "//" + seconds[2] + "/" + seconds[0] + "`" + seconds[1] + "/" + "CA";
        if (!isCarbonAlpha) {
            firstInfo = "/" + structure + "//" + firsts[2] + "/" + firsts[0] + "`" + firsts[1] + "/" + "CB";
            secondInfo = "/" + structure + "//" + seconds[2] + "/" + seconds[0] + "`" + seconds[1] + "/" + "CB";
        }
        // write also pymol command here...
        command = "dst=cmd.distance('" + name + "','" + firstInfo + "','" + secondInfo + "')";
    }

    public String getPdb() {
        return pdb;
    }

    public void setPdb(String pdb) {
        this.pdb = pdb;
    }

    public String getAtomA() {
        return atomA;
    }

    public void setAtomA(String atomA) {
        this.atomA = atomA;
    }

    public String getAtomB() {
        return atomB;
    }

    public void setAtomB(String atomB) {
        this.atomB = atomB;
    }

    public String getIdDis() {
        return idDis;
    }

    public void setIdDis(String idDis) {
        this.idDis = idDis;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUniprotAccA() {
        return uniprotAccA;
    }

    public void setUniprotAccA(String uniprotAccA) {
        this.uniprotAccA = uniprotAccA;
    }

    public String getUniprotIndexA() {
        return uniprotIndexA;
    }

    public void setUniprotIndexA(String uniprotIndexA) {
        this.uniprotIndexA = uniprotIndexA;
    }

    public String getUniprotAccB() {
        return uniprotAccB;
    }

    public void setUniprotAccB(String uniprotAccB) {
        this.uniprotAccB = uniprotAccB;
    }

    public String getUniprotIndexB() {
        return uniprotIndexB;
    }

    public void setUniprotIndexB(String uniprotIndexB) {
        this.uniprotIndexB = uniprotIndexB;
    }

    public String getSas() {
        return sas;
    }

    public void setSas(String sas) {
        this.sas = sas;
    }

    public String getBetaDist() {
        return betaDist;
    }

    public void setBetaDist(String betaDist) {
        this.betaDist = betaDist;
    }

    public String getAlphaDist() {
        return alphaDist;
    }

    public void setAlphaDist(String alphaDist) {
        this.alphaDist = alphaDist;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return "PyMOLEntry{" + "pdb=" + pdb + ", atomA=" + atomA + ", atomB=" + atomB + ", idDis=" + idDis + ", type=" + type + ", uniprotAccA=" + uniprotAccA + ", uniprotIndexA=" + uniprotIndexA + ", uniprotAccB=" + uniprotAccB + ", uniprotIndexB=" + uniprotIndexB + ", sas=" + sas + ", betaDist=" + betaDist + ", alphaDist=" + alphaDist + ", command=" + command + '}';
    }
    
    
}
