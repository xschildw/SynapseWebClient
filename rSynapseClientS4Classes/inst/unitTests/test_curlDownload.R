# Unit tests for file download functions
# 
# Author: Matt Furia
###############################################################################

.setUp <- function(){
	.setCache("localSourceFile",tempfile())
}

.tearDown <- function(){
	if(file.exists(.getCache('localSourceFile')))
		file.remove(.getCache('localSourceFile'))
	.deleteCache("localSourceFile")
}

#unitTestBigDownload <- function(){
#	d <- matrix(nrow=1000, ncol=1000, data=1)
#	for(i in 1:100){
#		write(d,file = .getCache("localSourceFile"),ncolumns=1000, sep="\t", append=TRUE)
#	}
#	sourceChecksum <- md5sum(.getCache("localSourceFile"))
#	.setCache("destFile", synapseDownloadFile(url= paste("file://", .getCache("localSourceFile"), sep="")))
#	destChecksum <- md5sum(.getCache("destFile"))
#	checkEquals(as.character(sourceChecksum), as.character(destChecksum))
#}

unitTestLocalFileDownload <- function(){
	d <- matrix(nrow=100, ncol=100, data=1)
	write(d,file = .getCache("localSourceFile"),ncolumns=1000, sep="\t")
	sourceChecksum <- md5sum(.getCache("localSourceFile"))
	.setCache("destFile", synapseDownloadFile(url= paste("file://", .getCache("localSourceFile"), sep="")))
	destChecksum <- md5sum(.getCache("destFile"))
	if(file.exists(.getCache('destFile')))
		file.remove(.getCache('destFile'))
	checkEquals(as.character(sourceChecksum), as.character(destChecksum))
}

unitTestRemoteFileDownload <- function(){
	
}

unitTestMd5Sum <- function(){
	
}

unitTestInvalidSourceFile <- function(){
	
}

unitTestInvalidDestDir <- function(){
	
}


