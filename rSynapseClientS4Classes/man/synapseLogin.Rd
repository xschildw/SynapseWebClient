\name{synapseLogin}
\alias{synapseLogin}
\alias{synapseLogout}
\alias{.getPassword}
\title{
	Login to Synapse
}
\description{
	Logs the user into synapse. 
}
\usage{
	synapseLogin(username, password)
	synapseLogout()
	.getPassword()
}

\arguments{
  \item{username}{
		Synapse username
	}
  \item{password}{
		API password associated with the username (see details)
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
function (username, password, curlHandle = getCurlHandle(), host = .getAuthEndpointLocation(), 
    path = .getAuthEndpointPrefix()) 
{
    kService <- "/session"
    entity <- list()
    entity$email <- username
    entity$password <- password
    response <- synapsePost(uri = kService, entity = entity, 
        host = host, path = path, curl = curlHandle)
    .checkCurlResponse(curlHandle, response)
    synapseSessionToken(response$sessionToken, check.validity = FALSE)
    .setCache("sessionTimestamp", Sys.time())
    cat(paste("Welcome ", response$displayName, "!\n", sep = ""))
  }
}
% Add one or more standard keywords, see file 'KEYWORDS' in the
% R documentation directory.
\keyword{ ~kwd1 }
\keyword{ ~kwd2 }% __ONLY ONE__ keyword per line
