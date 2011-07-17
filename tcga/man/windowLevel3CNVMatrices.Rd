\name{windowLevel3CNVMatrices}
\alias{windowLevel3CNVMatrices}
\title{Summarizes segmentation data from Level 3 TCGA CNV data files}
\description{
This function takes the reformatted segmentation data from a Level 3 TCGA CNV
data file and a window size and summarizes the data.  The summary is the mean
segmentation value within the defined window size.
}
\usage{
windowLevel3CNVMatrices( chrMatList , windowSize = 60000 , incSize = NULL )
}
\arguments{
  \item{chrMatList}{
Output from buildLevel3CNVMatrices.
}
  \item{windowSize}{
Window size used in calculating the mean segmentation value.
}
  \item{incSize}{
Shift in window along sequence.
}
}
\details{
This function takes the output from buildLevel3CNVMatrices and calculates the
mean segmentation value within a user defined window.
}
\value{
A matrix of mean segmentation values.  The columns are samples and the rows are
the segments.  The labels of the rows are of the format chromosome_position.
}
\author{
Justin Guinney and David Henderson
}
\examples{
# To be added later
}
\keyword{ tcga }
