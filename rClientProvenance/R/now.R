.now <- function() {
	as.POSIXlt(Sys.time(), 'UTC')
}

.nowAsString <- function() {
	format(.now(), "%FT%H:%M:%SZ")
}