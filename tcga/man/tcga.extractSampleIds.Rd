\name{tcga.extractSampleIds}
\alias{tcga.extractSampleIds}
\title{
Extracts the sample ID from the standard TCGA ID.
}
\description{
Takes a vector of standard TCGA IDs and extracts the Sample ID.
}
\usage{
tcga.extractSampleIds(tcgaIDs)
}
\arguments{
  \item{tcgaIDs}{
A vector of standard TCGA IDs.
}
}
\details{
Uses regular expressions to extract the sample ID from a vector of standard TCGA IDs.
}
\value{
A vector of sample IDs.
}
\author{
Justin Guinney
}
\examples{
#To be added later.
}
\keyword{ TCGA }
