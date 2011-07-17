\name{tcga.extractBatch}
\alias{tcga.extractBatch}
\title{
Extracts batch information from TCGA IDs.
}
\description{
Extracts the batch information from a standard TCGA ID.
}
\usage{
tcga.extractBatch(tcgaIDs)
}
\arguments{
  \item{tcgaIDs}{
A vector of standard TCGA IDs.
}
}
\details{
Uses regular expressions to extract the batch information from a standard TCGA ID.
}
\value{
A vector of batch IDs.
}
\author{
Justin Guinney
}
\examples{
#To be added later
}
\keyword{ TCGA }
