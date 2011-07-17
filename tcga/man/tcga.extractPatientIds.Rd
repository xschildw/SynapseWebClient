\name{tcga.extractPatientIds}
\alias{tcga.extractPatientIds}
\title{
Extracts the patient ID from the standard TCGA ID.
}
\description{
Takes a standard TCGA ID and extracts the patient ID.
}
\usage{
tcga.extractPatientIds(tcgaIDs)
}
\arguments{
  \item{tcgaIDs}{
A vector of standard TCGA IDs.
}
}
\details{
Uses regular expressions to extract the patient ID from a standard TCGA ID.
}
\value{
A vector of patient IDs.
}
\author{
Justin Guinney
}
\examples{
#To be added later.
}
\keyword{ TCGA }
