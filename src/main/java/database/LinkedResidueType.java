/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

/**
 * Linkeable residue regarding type information
 *
 * amine reactive groups: K and n-termini (such as DSS and BS3)+ side reactions with S,T,Y
 * 
 * carboxyl reactive groups: D, E and c-termini (such as EDC)
 *
 * @author Sule
 */
public enum LinkedResidueType {

    K, // a linked resiude is a lysine
    M,// a linked resiude is a methionine. 
    NTerminiIncludesM, // the second residue after Methionine on protein n-terminus, because M is mostly cleaved on protein N-terminus and in that case the second residue can be linked
    NTerminus, // only the first residue on protein N-terminus is linked, not the second one.
    E, // a linked residue is glutamate
    D, // a linked residue is aspartate
    CTerminus, // only the last residue on a protein at protein c-terminus can be linked
    S, // a linked residue is serine
    T, // a linked residue is threonine
    Y // a linked residue is tyrosine
}
