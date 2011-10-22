setGeneric(
	name = "stopStep",
	def = function(step){
		standardGeneric("stopStep")
	}
	)

setMethod(
	f = "stopStep",
	signature = "SynapseEntity",
	definition = function(step){
		stopStep(propertyValue(step, id))
	}
	)

setMethod(
	f = "stopStep",
	signature = "numeric",
	definition = function(step) {
		stopStep(as.character(step))
	}
	)

setMethod(
	f = "stopStep",
	signature = "missing",
	definition = function(step) {
		stopStep(NA_character_)
	}
	)

setMethod(
	f = "stopStep",
	signature = "character",
	definition = function(step) {
		# If we were not passed a step, stop the current step
		if(missing(step) || is.na(step)) {
			step <-	.getCache("currentStep")
			if(is.null(step)) {
				stop("There is no step to stop")
			}
		}
		
		step <- getEntity(step)
		# TODO is there a better way to make ISO-8601 dates in R?
		propertyValue(step, "endDate") <- format(Sys.time(), "%FT%H:%M:%S")
		step <- updateEntity(step)
		.setCache("previousStep", step)
		.deleteCache("currentStep")
		step
	}
	)

