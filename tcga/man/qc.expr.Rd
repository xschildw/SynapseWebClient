\name{qc.expr}
\alias{qc.expr}
\title{Performs QC on TCGA expression data and outputs either a RData or text object.}
\description{
This function takes the directory where the TCGA data has been extracted and
the directory where you want output dumped as arguments and performs a QC
of the expression data.  Either RData or text file can be requested as the
output format.
}
\usage{
qc.expr(platform.name, exprFilePath, clinical, out.dir = NULL, RData = TRUE, textFile = FALSE, cancerType = NULL, tcgaLevel = NULL, log2 = FALSE, Impute = FALSE, SampleTypes = "01")
}
\arguments{
  \item{platform.name}{
This is either u133a, agilent, or exon according to the array used to create the expression values.
}
  \item{exprFilePath}{
Full path to where the expression data have been extracted.
}
  \item{clinical}{
Full path to where the clinical data have been extracted.
}
  \item{out.dir}{
Full path to where the output is to be placed.
}
  \item{RData}{
Logical for whether a RData binary object should be output.
}
  \item{textFile}{
Logical for whether a text file should be output.
}
  \item{cancerType}{
Short name for the cancer type of the data.
}
  \item{tcgaLevel}{
Numeric describing the level of data downloaded from TCGA.  Currently, only level 2 and level 3 data are supported.
}
  \item{log2}{
Logical for whether the data should be log2 transformed.
}
  \item{Impute}{
Logical for whether the data should be imputed when missing values are detected.
}
  \item{SampleTypes}{
Character vector selecting the Sample Types to use in the qc procedure.  If duplicates exist within sample type, they will all be included.  To find the sample types and their codes, type data(sampleTypes) to load a data.frame with the codes and their description.
}
}
\details{
This function takes arguments describing where the data is located and where
the QC'ed data is to be output, along with other parameters describing the
type of data input and outputs either RData or text format QC'ed data.
}
\value{
Function returns a list containing the ComBat normalized, batch adjusted data
and the ComBat normalized and batch and age adjusted data.
}
\author{
Justin Guinney and David Henderson
}
\examples{

## Not run
\dontrun{
out.dir <- "~/Projects/DAT_002__TCGA_ovarian/11_2010/processed/data/"
expr.prefix <- "~/DAT_002__TCGA_Ovarian/10_2010/raw/"
tcga.clinical.file <- paste( expr.prefix , "Clinical/BCR/clinical_patient_public_OV.txt" , sep = "" )
cancerType <- "OV"

data.list <- list()
data.list[[1]] <- list( platform = "u133a" , tcgaLevel = 2 , log2 = FALSE , cancerType = cancerType ,
    expr.path = paste( expr.prefix , "Expression-Genes/BI__HT_HG-U133A/Level_2/broad.mit.edu__HT_HG-U133A__probeset_rma.txt" , sep = "" ) )
data.list[[2]] <- list( platform = "u133a" , tcgaLevel = 3 ,log2 = FALSE ,  cancerType = cancerType ,
    expr.path = paste( expr.prefix , "Expression-Genes/BI__HT_HG-U133A/Level_3/broad.mit.edu__HT_HG-U133A__gene_expression_analysis.txt" , sep = "" ) )
data.list[[3]] <- list( platform = "exon" , tcgaLevel = 3 , log2 = TRUE , cancerType = cancerType ,
    expr.path = paste( expr.prefix , "Expression-Exon/LBL__HuEx-1_0-st-v2/Level_3/lbl.gov__HuEx-1_0-st-v2__exon_expression_analysis.gene.txt" , sep = "" ) )
data.list[[4]] <- list( platform = "agilent" , tcgaLevel = 3 , log2 = FALSE , cancerType = cancerType ,
    expr.path = paste( expr.prefix , "Expression-Genes/UNC__AgilentG4502A_07_3/Level_3/unc.edu__AgilentG4502A_07_3__gene_expression_analysis.txt" , sep = "" ) )
    
clinical <- prepare.clinical( tcga.clinical.file )

for ( ds in data.list )
{
  eset.ret <- qc.expr( ds$platform , ds$expr.path , clinical , out.dir = out.dir ,
                       cancerType = ds$cancerType , tcgaLevel = ds$tcgaLevel ,
                       tumor.only = TRUE , log2 = ds$log2 , RData = TRUE , textFile = TRUE )
}
}
}
\keyword{ tcga }
