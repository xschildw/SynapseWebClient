\name{synapseWorkflow}
\alias{finishWorkflowTask}
\alias{skipWorkflowTask}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{
	Alter the state of Workflow Tasks
}
\description{
	
}
\usage{
	finishWorkflowTask(outputLayerId)
	skipWorkflowTask(reason = "this script does not want to work on this task")
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{outputLayerId}{
	%%     ~~Describe \code{outputLayerId} here~~
	}
  \item{reason}{
  	%% Descript reason here
  	}
}
\details{
%%  ~~ If necessary, more details than the description above ~~
}
\value{
%%  ~Describe the value returned
%%  If it is a LIST, use
%%  \item{comp1 }{Description of 'comp1'}
%%  \item{comp2 }{Description of 'comp2'}
%% ...
}
\references{
%% ~put references to the literature/web site here ~
}
\author{
%%  ~~who you are~~
}
\note{
%%  ~~further notes~~
}

%% ~Make other sections like Warning with \section{Warning }{....} ~

\seealso{
%% ~~objects to See Also as \code{\link{help}}, ~~~
}
\examples{
##---- Should be DIRECTLY executable !! ----
##-- ==>  Define data, use random,
##--	or do  help(data=index)  for the standard data sets.

## The function is currently defined as
function (outputLayerId) 
{
    constants <- new("SynapseWorkflowConstants")
    result <- list()
    result[constants@kOutputLayerIdKey] <- outputLayerId
    write(constants@kOutputStartDelimiterPattern, stdout())
    write(.toJSON(result), stdout())
    write(constants@kOutputEndDelimiterPattern, stdout())
  }
}
% Add one or more standard keywords, see file 'KEYWORDS' in the
% R documentation directory.
\keyword{ ~kwd1 }
\keyword{ ~kwd2 }% __ONLY ONE__ keyword per line
