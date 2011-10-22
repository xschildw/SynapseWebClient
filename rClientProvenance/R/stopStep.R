setGeneric(
	name = "stopStep",
	def = function(parentEntity){
		standardGeneric("stopStep")
	}
	)

setMethod(
	f = "stopStep",
	signature = "SynapseEntity",
	definition = function(parentEntity){
		stopStep(propertyValue(parentEntity, id))
	}
	)

setMethod(
	f = "stopStep",
	signature = "numeric",
	definition = function(parentEntity) {
		stopStep(as.character(parentEntity))
	}
	)

setMethod(
	f = "stopStep",
	signature = "missing",
	definition = function(parentEntity) {
		stopStep(NA_character_)
	}
	)

setMethod(
	f = "stopStep",
	signature = "character",
	definition = function(parentEntity) {
		step <-	.getCache("currentStep")
		step <- getEntity(step)
		# TODO is there a better way to make ISO-8601 dates in R?
		propertyValue(step, "endDate") <- format(Sys.time(), "%FT%H:%M:%S")
		step <- updateEntity(step)
		.deleteCache("currentStep")
		step
	}
	)

