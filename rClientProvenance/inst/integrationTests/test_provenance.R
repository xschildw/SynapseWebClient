# Integration tests for provenance
# 
# Author: deflaux
###############################################################################
.setUp <- function(){
	synapseClient:::.setCache("testProjectName", paste('Provenance Integration Test Project', gsub(':', '_', date())))
}

.tearDown <- function(){
	if(!is.null(synapseClient:::.getCache("testProject"))) {
		deleteEntity(synapseClient:::.getCache("testProject"))	
		synapseClient:::.deleteCache("testProject")
	}
}

integrationTestProvenance <- function() {

	## Create Project
	project <- new(Class="Project")
	propertyValue(project,"name") <- synapseClient:::.getCache("testProjectName")
	createdProject <- createEntity(project)
	synapseClient:::.setCache("testProject", createdProject)
	checkEquals(propertyValue(createdProject,"name"), synapseClient:::.getCache("testProjectName"))
	project <- createdProject
	
	## Create Dataset
	dataset <- new(Class="Dataset")
	propertyValue(dataset, "name") <- "testDatasetName"
	propertyValue(dataset,"parentId") <- propertyValue(project, "id")
	createdDataset <- createEntity(dataset)
	checkEquals(propertyValue(createdDataset,"name"), propertyValue(dataset, "name"))
	checkEquals(propertyValue(createdDataset,"parentId"), propertyValue(project, "id"))
	dataset <- createdDataset
	
	## Create Layer
	layer <- new(Class = "PhenotypeLayer")
	propertyValue(layer, "name") <- "testPhenoLayerName"
	propertyValue(layer, "parentId") <- propertyValue(dataset,"id")
	checkEquals(propertyValue(layer,"type"), "C")
	createdLayer <- createEntity(layer)
	checkEquals(propertyValue(createdLayer,"name"), propertyValue(layer, "name"))
	checkEquals(propertyValue(createdLayer,"parentId"), propertyValue(dataset, "id"))
	inputLayer <- createdLayer

	## Start a new step
	startStep()

	## The command line used to invoke this should be stored in the commandLine field
	checkEquals(paste(commandArgs(), collapse=" "), propertyValue(step, 'commandLine'))
	
	## Get a layer, it will be added as input
	layer <- getEntity(inputLayer)
	step <- getStep()
	checkEquals(propertyValue(inputLayer, "id"), propertyValue(step, "input")[[1]]$targetId)
 	
	## Create a layer, it will be added as output
	layer <- new(Class = "ExpressionLayer")
	propertyValue(layer, "name") <- "testExprLayerName"
	propertyValue(layer, "parentId") <- propertyValue(dataset,"id")
	checkEquals(propertyValue(layer,"type"), "E")
	createdLayer <- createEntity(layer)
	checkEquals(propertyValue(createdLayer,"name"), propertyValue(layer, "name"))
	checkEquals(propertyValue(createdLayer,"parentId"), propertyValue(dataset, "id"))
	outputLayer <- createdLayer
	step <- getStep()
	checkEquals(propertyValue(outputLayer, "id"), propertyValue(step, "output")[[1]]$targetId)
	
	## Create an Analysis, it will become the parent of the step
	analysis <- createEntity(Analysis(list(parentId=propertyValue(project, "id"),
																				 name='test analysis name',
																				 description='test analysis description')))
	step <- getStep()
	checkEquals(propertyValue(analysis, "id"), propertyValue(step, "parentId"))
	
	step <- stopStep()
	# check environment descriptor
	
	
}
