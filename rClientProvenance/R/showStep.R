showStep <- function(step) {
	if(missing(step)) {
		step <- synapseClient:::.getCache("currentStep")
	}
	synapseClient:::.setCache("debug", TRUE)
	step <- getEntity(step)
	synapseClient:::.setCache("debug", FALSE)
	# step <- synapseClient:::.extractEntityFromSlots(synapseClient:::.getCache("currentStep"))
	# print(step)
	# message("commandLine ", step["commandLine"])
}