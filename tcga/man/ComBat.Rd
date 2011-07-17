\name{ComBat}
\alias{ComBat}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{
Performs normalization using the ComBat procedure.
}
\description{
Function that normalizes expression data from the TCGA project using the ComBat procedure.
}
\usage{
ComBat(dat, sampleInfo, par.prior = TRUE, filter = NULL, skip= 0, prior.plots = FALSE)
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{dat}{
Matrix of gene expression data.
}
  \item{sampleInfo}{
Matrix of batch information.  Must have a column named Batch and that is the only column used to define normalization at present.
}
  \item{par.prior}{
Logical whether parametric priors should be sought.
}
  \item{filter}{
Percent absent threshold for removal from data set.  Set to NULL be default which results in no filtering.
}
  \item{skip}{
Unknown purpose
}
  \item{prior.plots}{
Plot prior diagnostic plots.
}
}
\details{
Performs ComBat normalization on gene expression data.
}
\value{
Normalized gene expression data as a matrix.
}
\author{
Justin Guinney and David Henderson
}
\examples{
#To be added later.
}
\keyword{ TCGA }
