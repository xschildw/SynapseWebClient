\name{tcga.extractTissueType}
\alias{tcga.extractTissueType}
\title{
Extracts the tissue ID from the standard TCGA ID.
}
\description{
Takes a vector of standard TCGA IDs and extracts the Tissue Type.
}
\usage{
tcga.extractTissueType(tcgaIDs)
}
\arguments{
  \item{tcgaIDs}{
A vector of standard TCGA IDs.
}
}
\details{
Uses regular expressions to extract the tissue type from a standard TCGA ID.
}
\value{
A vector of tissue types.
}
\author{
Justin Guinney
}
\examples{
#to be added later
}
\keyword{ TCGA }
