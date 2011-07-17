\name{plotCombat}
\alias{plotCombat}
\title{
Plot prior diagnostics from the ComBat normalization routine.
}
\description{
Plots prior diagnostic plots for priors used in the ComBat normalization routine.
}
\usage{
plotCombat(gamma.hat, gamma.bar, t2, delta.hat, a.prior, b.prior, ...)
}
\arguments{
  \item{gamma.hat}{
gamma prior estimates.
}
  \item{gamma.bar}{
Vector of mean of gamma estimates.
}
  \item{t2}{
Vector of gamma hat variances.
}
  \item{delta.hat}{
Within batch gene expression variances.
}
  \item{a.prior}{
Shape prior for gamma distribution.
}
  \item{b.prior}{
Shape prior for gamma distribution.
}
  \item{\dots}{
Other plotting parameters
}
}
\details{
Plots for prior diagnostics.
}
\value{
Plots.
}
\author{
Justin Guinney
}
\examples{
# To be added later
}
\keyword{ TCGA }
