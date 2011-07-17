\name{writeLevel3CNVFile}
\alias{writeLevel3CNVFile}
\title{Outputs results of reformatting and summarizing CNV data from the TCGA project.}
\description{
This function takes a root path, file root name, output from windowing of CNV data, and a logical whether RData or text output is desired and writes data to a file.
}
\usage{
writeLevel3CNVFile( filePath = "/tmp" , fileRoot = "output" , cnvObject , RData = TRUE , textFile = FALSE )
}
\arguments{
  \item{filePath}{
Root path to where output is desired.  Do not include the file name and the directory must already exist.
}
  \item{fileRoot}{
Root name of the file with the extension missing.
}
  \item{cnvObject}{
Matrix of CNV window summary output from windowLevel3CNVMatrices.
}
  \item{RData}{
Logical whether a binary RData file should be written.
}
  \item{textFile}{
Logical for whether a tab delimited text file should be output.
}
}
\details{
This function takes a root file path, a root file name, a matrix output from
windowLevel3CNVMatrices and writes the data to disk in either a RData object or
a tab delimited text file.
}
\value{
A binary or text file on disk containing the mean segmentation values within
a user defined window.
}
\author{
David Henderson
}
\examples{
# To be added later
}
\keyword{ tcga }
