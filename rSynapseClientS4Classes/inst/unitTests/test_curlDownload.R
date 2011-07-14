# Unit tests for file download functions
# 
# Author: Matt Furia
###############################################################################

.setUp <- function(){
	.setCache("sourceFile", "")
	.setCache("destFile", "")
}

.tearDown <- function(){
	.deleteCache("sourceFile")
	.deleteCache("destFile")
}

unitTestBigDownload <- function(){
	
}


