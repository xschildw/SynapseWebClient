\name{age.correct}
\alias{age.correct}
\title{
Function to adjust expression data for age differences.
}
\description{
Takes an expression set as it's argument and adjusts the expression data for
differences in patient age.
}
\usage{
age.correct(eset)
}
\arguments{
  \item{eset}{
An ExpressionSet.  PhenoData must include a variable called age.
}
}
\details{
Fits a linear model to estimate an age effect in the expression data and
replace the expression values with the residuals from that model.
}
\value{
A new ExpressionSet with the residuals from the age covariate linear model.
}
\author{
Justin Guinney and David Henderson
}
\examples{
#newEset <- age.correct( eset )
}
\keyword{ tcga }
