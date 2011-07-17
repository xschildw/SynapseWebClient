\name{buildLevel3CNVMatrices}
\alias{buildLevel3CNVMatrices}
\title{Reformat and summarize Level 3 TCGA CNV data}
\description{
This function reformats the Level 3 CNV data from the TCGA project to prepare
it for summarization.
}
\usage{
buildLevel3CNVMatrices( lvl3.file.path , SampleTypes = "01" )
}
\arguments{
  \item{lvl3.file.path}{
Path to Level 3 CNV data including the name of the file.
}
  \item{SampleTypes}{
Character vector selecting the Sample Types to use in the qc procedure.  If duplicates exist within sample type, they will all be included.  To find the sample types and their codes, type data(sampleTypes) to load a data.frame with the codes and their description.
}
}
\details{
This function takes arguments describing where the data is located and a
character vector describing the type of data input and creates a list of
segmentation values by chromosome.
}
\value{
A list of segmentation values grouped by chromosome, plus their positions
within an IRanges object.
}
\author{
Justin Guinney and David Henderson
}
\examples{
# To be added later.
}
\keyword{ tcga }
