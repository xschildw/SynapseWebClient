# This test is only run during R CMD check
# It should invoke all unit tests but no integration tests
require("synapseClient") || stop("unable to load synapseClient package")
synapseAuthServiceEndpoint("https://staging-auth.elasticbeanstalk.com/auth/v1")
synapseRepoServiceEndpoint("https://staging-reposervice.elasticbeanstalk.com/repo/v1")
synapseClient:::.test()
