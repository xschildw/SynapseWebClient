# log into my local synapse stack
goLocal()

# this is just to show what's happening under the covers, you don't need to call this
showStep() 

# load some clinical data
clinicalData <- getEntity(3032)
showStep()

# load some expression data
expressionData <- getEntity(3029)
showStep()

####
# Do some science here
# . . .
# and get some interesting results worth saving in Synapse
####


# Create a project for results
myName <- "Nicole A. Deflaux"
project <- Project(list(
	name=paste("Machine Learning Results - ", myName)
	))
project <- createEntity(project)
# Create a dataset for results
dataset <- Dataset(list(
	name="Analysis Plots",
	parentId=propertyValue(project, "id")
	))
dataset <- createEntity(dataset)

# Create a Graph
attach(mtcars)
plot(wt, mpg) 
abline(lm(mpg~wt))
title("Regression of MPG on Weight")
outputFileElasticNet <- "/Users/deflaux/mygraph.jpg"
jpeg(outputFileElasticNet)

# Store the resulting graph in Synapse
elasticNetLayer <- Layer(list(
	name="ElasticNet Results for PLX4720",
	type="M", 
	parentId=propertyValue(dataset, "id")))
elasticNetLayer <- addFile(elasticNetLayer, outputFileElasticNet)
elasticNetLayer <- storeEntity(elasticNetLayer)

showStep()

# I'm going to share what I did with my colleagues
analysis <- Analysis(list(description="glmnet algorithm applied to Cell Line Data and Sanger Drug Response data", 
													name="myFirstAnalysis",
													parentId=propertyValue(project, "id")))
#onWeb()  !!
showStep()

# q() will also do this
stoppedStep <- stopStep()
showStep(stoppedStep)