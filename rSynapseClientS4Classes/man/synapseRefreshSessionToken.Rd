\name{synapseRefreshSessionToken}
\alias{synapseRefreshSessionToken}

\title{
	Refresh the Auth Service Session Token
}
\description{
	Resets the counter in the Synapse auth service that determines when the token expires. 
}
\usage{
	synapseRefreshSessionToken(sessionToken, host = .getAuthEndpointLocation())
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{sessionToken}{
		Session token returned by the Synapse auth service
	}
  \item{host}{
		The auth service host URL. Defaults to the value returned by \code{\link{.getAuthEndpointLocation}}.
	}
}
\details{
%%  ~~ If necessary, more details than the description above ~~
}
\value{
	This function has no return value, but is called for it's side-effects. The first side effect is that the expiration counter maintained by 
	the Synapse auth service is reset. The second is that the local timestamp cache value is reset.
}
\author{
	Matt Furia
}
\note{
	This function will throw an exception if the session token is invalid or is already expired.
}


\seealso{
	\code{\link{synapseLogin}}
	\code{\link{synapseSessionToken}}
}
\examples{

	## Not run:
	\dontrun{
		synapseLogin('demouser@sagebase.org','demouser-pw')
		synapseRefreshSessionToken(sessionToken())
	}
	## End (Not run)
}
