# Xilmass
 * [Tool Description](#tool-description)
 * [Project Description](#project-description)
 * [Downloads](#downloads)
 * [Usage](#usage)
  
---
##Tool description

Xilmass is a novel algorithm to identify cross-linked peptides. Xilmass uses a new way to compose the search database and a new scoring function that scores each experimental spectrum against complete theoretical spectra. Xilmass can be used for both labeled and unlabeled cross-linkers.

[Go to top of page](#xilmass)

----
## Project description

Chemical cross-linking coupled with mass spectrometry (CX-MS/MS) plays an important role in unravelling protein interactions and supports the determination of protein structure. Cross-linking enables the study of proteins that are non-crystallizable or have a protein size above 50 kDa. Identification of the resulting CX-MS/MS spectra is a computationally challenging task. First of all, the search space is tremendously increased because all peptide-to-peptide combinations have to be taken into account. Secondly, CX-MS/MS spectra are more complex than a spectrum derived from a single peptide  because these spectra contain fragment ions from two peptides. Thirdly, the peptide mixture contains only few cross-linked peptides compared to single peptides in the mixture. In addition, there is a high dynamic range of cross-linked products due to the fact that cross-linking can occur between residues of the same protein (intra-protein cross-linked peptides), between residues of different proteins (inter-protein cross-linked peptides), or even between two cross-linked peptides (higher order cross-liked peptides). Although several computational approaches are available for the identification of spectra from cross-linked peptides, there still remains a room for improvement. Some of the available approaches are based on the linearization of cross-linked peptide-pairs and the usage of peptide labeling to facilitate cross-linked peptide identification. Here, we introduce Xilmass, a novel algorithm to identify cross-linked peptides that uses a new way to construct the search database and a new scoring function that relies on complete theoretical spectra. 


[Go to top of page](#xilmass)

----
## Downloads

Download the Xilmass algorithm <a href="http://genesis.ugent.be/maven2/com/compomics/xilmass/0.2.1/xilmass-0.2.1.zip" onclick="trackOutboundLink('usage','download','xilmass','http://genesis.ugent.be/maven2/com/compomics/xilmass/0.2.1/xilmass-0.2.1.zip'); return false;">here</a>.  

Download [a simple Xilmass-visualization tool] (http://genesis.ugent.be/maven2/com/compomics/xilmass-visualize/0.1/xilmass-visualize-0.1.zip).

----

## Usage
See the [wiki](https://github.com/compomics/xilmass/wiki).

----

| Java | Maven | Netbeans | Lucene |
|:--:|:--:|:--:|:--:|
|[![java](http://genesis.ugent.be/public_data/image/java.png)](http://java.com/en/) | [![maven](http://genesis.ugent.be/public_data/image/maven.png)](http://maven.apache.org/) | [![netbeans](https://netbeans.org/images_www/visual-guidelines/NB-logo-single.jpg)](https://netbeans.org/) | [![lucene](https://lucene.apache.org/images/lucene_logo_green_300.png)](https://lucene.apache.org/) |


[Go to top of page](#xilmass)
