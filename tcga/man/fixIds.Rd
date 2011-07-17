\name{fixIds}
\alias{fixIds}
\title{
Replace field separators with "-"
}
\description{
Function that replaces TCGA field separators in IDs with "-"
}
\usage{
fixIds( tcgaIDs )
}
\arguments{
  \item{tcgaIDs}{
Vector of TCGA IDs
}
}
\details{
Quickly takes a vector of TCGA IDs and returns a vector with the field
separators replaced with "-".
}
\value{
Vector of transformed TCGA IDs.
}
\author{
Justin Guinney and David Henderson
}
\examples{
#newIDs <- fixIds( oldIDs )
}
\keyword{ tcga }
