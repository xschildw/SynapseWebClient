# Unit tests for in-memory cache for global variables
# 
# Author: Matt Furia
###############################################################################

.setUp <- 
		function()
{
	.setCache("testKey", "testValue")
}

.tearDown <- 
		function()
{
	.deleteCache("testKey")
}

unitTestGetCacheValue <- 
		function()
{
	checkEquals(.getCache("testKey"), "testValue")
}

unitTestSetCacheValue <- 
		function()
{
	.setCache("testKey", "testValueNew")
	checkEquals(.getCache("testKey"), "testValueNew")
}

unitTestDeleteCacheValue <- 
		function()
{
	.deleteCache("testKey")
	checkTrue(is.null(.getCache("testKey")))
}