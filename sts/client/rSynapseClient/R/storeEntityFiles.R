# TODO: Add comment
# 
# Author: Matt Furia
###############################################################################


setMethod(
		f = "storeEntityFiles",
		signature = "SynapseEntity",
		definition = function(entity){
			stop("Only Layer entities can contain stored files")
		}
)

setMethod(
		f = "storeEntityFiles",
		signature = "Layer",
		definition = function(entity){
			if(length(entity$files) == 0)
				stop("Entity has no files to store")
			
			if(!all(mk <- file.exists(file.path(entity@location@cacheDir, entity@location@files))))
				stop("Not all files listed by the entity exist.")
				
			## build the outfile name
			dataFileName <- gsub("^[\\/]+", "", tempfile(fileext=".zip", tmpdir=""))
			if(!is.null(propertyValue(entity, "name")))
				dataFileName <- sprintf("%s%s",gsub('[\\W]', "_", propertyValue(entity, "name"), perl=TRUE),".zip")
			
			dataFileName <- file.path(tempdir(), dataFileName)
			
			## if zipFile exists delete it before creating
			if(file.exists(dataFileName))
				file.remove(dataFileName)
			
			## change directory to the cache directory
			oldDir <- getwd()
			setwd(entity@location@cacheDir)
			suppressWarnings(zipRetVal <- zip(zipfile=normalizePath(dataFileName, mustWork=FALSE), files=gsub("^/","",entity@location@files)))
			setwd(oldDir)
			
			## if zip failes, load uncompressed
			if(zipRetVal != 0L){
				msg <- sprintf("Unable to zip layerData Files. Error code: %i.",zipRetVal)
				if(length(entity@location@files) > 1)
					stop(msg, " Make sure that zip is installed on your computer. Without zip, only one file can be uploaded at a time")
				warning("Zip was not installed on your computer. Uploading layer data uncompressed. Directory structure will not be preserved.")
				dataFileName <- entity@location@files
			}
			
			storeFile(entity, dataFileName)
		}
)

setMethod(
		f = "storeFile",
		signature = signature("Layer", "character"),
		definition = function(entity, filePath){
			
			# Change into the cache directory, if filePath is a relative path, maybe 
			# we'll find it here, if filePath is an absolute path, changing the working 
			# directory will have no effect
			oldDir <- getwd()
			setwd(entity$cacheDir)
						
			if(!all(file.exists(filePath))) {
				stop(paste("File", filePath, "does not exist, current working directory is", getwd()))
			}
			
			## parse out the filename
			filename <- gsub(sprintf("%s%s%s", "^.+",.Platform$file.sep, "+"), "",filePath)

			## Compute the provenance checksum
			checksum <- as.character(md5sum(filePath))
			
			if(is.null(propertyValue(entity, "id"))){
				## Create the layer in Synapse
				entity <- createEntity(entity)
			} 

			## Get credentials needed to upload to S3
			s3Token <- list()
			s3Token$md5 <- checksum
			s3Token$path <- filename
			s3Token <- synapsePost(propertyValue(entity, "s3Token"), s3Token)
			
			## Upload the data file to S3
			tryCatch(
					synapseUploadFile(url = s3Token$presignedUrl,
							srcfile = filePath,
							checksum = s3Token$md5,
							contentType = s3Token$contentType
					),
					error = function(e){
						warning(sprintf("failed to upload data file, please try again: %s", e))
						setwd(oldDir)
						return(entity)
					}
			)
			
			## Store the new location in Synapse
			## Note, to future-proof this we would put in logic to merge locationData, 
			## but we don't have any entities with data in two locations yet, so let's 
			## save that for the java implementation of this
			locationData <- list()
			locationData$path <- s3Token$path
			locationData$type <- "awss3"
			propertyValue(entity, "locations") <- list(locationData)
			propertyValue(entity, "md5") <- s3Token$md5
			propertyValue(entity, "contentType") <- s3Token$contentType
			entity <- updateEntity(entity)
			
			## move the data file from where it is to the local cache directory
			parsedUrl <- .ParsedUrl(s3Token$presignedUrl)
			destdir <- file.path(synapseCacheDir(), gsub("^/", "", parsedUrl@pathPrefix))
			destdir <- path.expand(destdir)
			
			if(file.exists(destdir))
				unlink(destdir, recursive = TRUE)
			dir.create(destdir, recursive = TRUE)
			
			if(!file.copy(filePath, destdir, overwrite = TRUE)){
				warning("Failed to copy file to local cache")
				## unpack into the local cache and update the location entity
				entity@location <- CachedLocation(entity@location, .unpack(filepath))
				
			}else{
				entity@location@cacheDir <- destdir
				## unpack into the local cache and update the location entity
				entity@location <- CachedLocation(entity@location, .unpack(file.path(destdir, filename)))
			}
			
			setwd(oldDir)
			refreshEntity(entity)
		}
)
