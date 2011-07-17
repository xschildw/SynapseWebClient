synapseLogin <- 
		function(username, password)
{
	## constants
	kService <- "/session"
	## end constants
	
	## get auth service endpoint and prefix from memory cache
	host = .getAuthEndpointLocation()
	path = .getAuthEndpointPrefix()
	
	if(missing(password)){
		password <- .getPassword()
	}
	
	entity <- list()
	entity$email <- username
	entity$password <- password
	
	## Login and check for success
	response <- synapsePost(uri = kService, 
					entity = entity, 
					host = host, 
					path = path,
				)
	
	## Cache the sessionToken. No need to check validity since it was just created
	sessionToken(response$sessionToken, checkValidity=FALSE)
	.setCache("sessionTimestamp", Sys.time())
	message(paste("Welcome ", response$displayName, "!\n", sep=""))
}

synapseLogout <-
		function(localOnly=FALSE)
{
	## constants
	kService <- "/session"
	## end constants
	
	## get auth service endpoint and prefix from memory cache
	host = .getAuthEndpointLocation()
	path = .getAuthEndpointPrefix()
	
	entity <- list(sessionToken = sessionToken())
	
	if(!localOnly){
		response <- synapseDelete(uri = kService,
				entity = entity,
				host = host,
				path = path
		)
	}
	
	sessionToken(NULL)
}

.getPassword <- function(){
	cat("Password: ")
	## this only suppresses output in unix-like terminals
	## TODO: add support for suppressing output in DOS terminals and GUI interfaces
	system("stty -echo")
	password <- readline()
	system("stty echo")
	cat("\n")
	return(password)
}