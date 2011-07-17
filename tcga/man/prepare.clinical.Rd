\name{prepare.clinical}
\alias{prepare.clinical}
\title{Prepare Clinical Data}
\description{
Prepare clinical data and create data.frame for input to phenoData.
}
\usage{
prepare.clinical(clinicalFilePath)
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{clinicalFilePath}{
Path to where the clinical data is located.
}
}
\details{
Takes the data and renames many of the variables to more commonly used names.
}
\value{
A data.frame that can be used to create a phenoData object.
}
\author{
Justin Guinney
}
\examples{
#To be added later.
}
% Add one or more standard keywords, see file 'KEYWORDS' in the
% R documentation directory.
\keyword{ TCGA }
