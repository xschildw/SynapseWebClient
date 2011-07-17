\name{tcga.extractCenter}
\alias{tcga.extractCenter}
\title{
Extracts the center the patient attended from the standard TCGA ID.
}
\description{
Extracts the center where the patient attended from a standard TCGA ID
}
\usage{
tcga.extractCenter(tcgaIDs)
}
\arguments{
  \item{tcgaIDs}{
A vector of standard TCGA IDs.
}
}
\details{
Uses regular expressions to extract the center where a patient attended from a standard TCGA ID.
}
\value{
A vector of center IDs.
}
\author{
Justin Guinney
}
\examples{
#To be added later.
}
\keyword{ TCGA }
