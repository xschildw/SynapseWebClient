# Copyright 2011 Google Inc. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Author: yolken@google.com (Benjamin Yolken)
#
# R interface to Google Storage for Developers service;
# see http://code.google.com/apis/storage/ for
# more details.

library("RCurl")
library("XML")

# Some sample buckets and objects for testing purposes
sample_project_id <- "748770561672"
sample_bucket_name <- "dive"
sample_object_name <- "tutorial/countries.csv"


BigstoreBucketUrl <- function(
  bucket_name, bigstore_url_suffix=".commondatastorage.googleapis.com") {
  # Create URL associated with a Google Storage bucket.
  return(paste("https://", bucket_name, bigstore_url_suffix, sep=""))
}


GetService <- function(
  auth_state, project_id) {
  # Get a list of all the buckets associated with a particular project.
  #
  # Args:
  #   auth_state: An AuthState instance
  #   project_id: String project ID
  #
  # Returns:
  #   A list of (string) names, one for each bucket in the project
  curl <- getCurlHandle()
  header <- basicHeaderGatherer()
  
  result <- getURLContent(
    "https://commondatastorage.googleapis.com/",
    verbose=TRUE,
    ssl.verifypeer=FALSE,
    timeout=30,
    httpheader=c(
      "Authorization"=paste("OAuth ", auth_state@access_token, sep=""),
      "Content-Length"="0",
      "Date"=format(Sys.time(), "%a, %d %b %Y %X %Z"),
      "x-goog-project-id"=project_id,
      "x-goog-api-version"="2"),
    curl=curl)
  
  result_xml_model <- xmlTreeParse(result, asText=TRUE, getDTD=FALSE)
  contents <- xmlElementsByTagName(
    xmlRoot(result_xml_model), "Bucket", recursive=TRUE)
  
  contents_list <- list()
  
  for (i in c(1:length(contents))) {
    contents_list[[i]] <- contents[[i]][[1]][[1]]
  }
  
  return(contents_list)
}


GetBucket <- function(bucket_name, auth_state) {
  # Get a list of all the objects in a bucket.
  #
  # Args:
  #   bucket_name: String name of bucket
  #   auth_state: An AuthState instance
  #
  # Returns:
  #   A list of (string) names, one for each object in the bucket
  curl <- getCurlHandle()
  header <- basicHeaderGatherer()
  
  modified_url <- paste(BigstoreBucketUrl(bucket_name), "/", sep="")
  
  result <- getURLContent(
    modified_url,
    verbose=TRUE,
    ssl.verifypeer=FALSE,
    timeout=30,
    httpheader=c(
      "Authorization"=paste("OAuth ", auth_state@access_token, sep=""),
      "Content-Length"="0",
      "Date"=format(Sys.time(), "%a, %d %b %Y %X %Z"),
      "x-goog-api-version"="2"),
    curl=curl)
  
  # Parse XML returned by API
  result_xml_model <- xmlTreeParse(result, asText=TRUE, getDTD=FALSE)
  contents <- xmlElementsByTagName(xmlRoot(result_xml_model), "Contents")
  
  contents_list <- list()
  
  for (i in c(1:length(contents))) {
    contents_list[[i]] <- contents[[i]][[1]][[1]]
  }
  
  return(contents_list)
}


PutBucket <- function(bucket_name, auth_state, project_id) {
  # Create a bucket associated with a specific Google Storage project.
  #
  # Args:
  #   bucket_name: String name of bucket
  #   auth_state: An AuthState instance
  #   project_id: String project ID
  curl <- getCurlHandle()
  header <- basicHeaderGatherer()
  
  modified_url <- paste(BigstoreBucketUrl(bucket_name), "/", sep="")
  
  result <- curlPerform(
    url=modified_url,
    verbose=TRUE,
    headerfunction=header$update,
    customrequest="PUT",
    httpheader=c(
      "Authorization"=paste("OAuth ", auth_state@access_token, sep=""),
      "Content-Length"="0",
      "Date"=format(Sys.time(), "%a, %d %b %Y %X %Z"),
      "x-goog-project-id"=project_id,
      "x-goog-api-version"="2"))  
}


DeleteBucket <- function(
  bucket_name, auth_state) {
  # Delete a bucket in Google Storage.
  #
  # Args:
  #   bucket_name: String name of bucket
  #   auth_state: An AuthState instance
  #   project_id: String project ID
  curl <- getCurlHandle()
  header <- basicHeaderGatherer()
  
  modified_url <- paste(BigstoreBucketUrl(bucket_name), "/", sep="")
  
  result <- curlPerform(
    url=modified_url,
    verbose=TRUE,
    headerfunction=header$update,
    customrequest="DELETE",
    httpheader=c(
      "Authorization"=paste("OAuth ", auth_state@access_token, sep=""),
      "Content-Length"="0",
      "Date"=format(Sys.time(), "%a, %d %b %Y %X %Z"),
      "x-goog-api-version"="2"))  
}


GetObject <- function(bucket_name, object_name, auth_state) {
  # Get an object from Google Storage. 
  #
  # Type casting is automatically handled by RCurl using the content-type
  # field in the response header. Generally, the result will be
  # interpreted as either "character" or "raw".
  #
  # Args:
  #   bucket_name: String name of bucket
  #   object_name: String name of an object in the previous
  #   auth_state: An AuthState instance
  #
  # Returns:
  #   The fetched object
  curl <- getCurlHandle()
  header <- basicHeaderGatherer()
  
  modified_url <- paste(BigstoreBucketUrl(bucket_name), "/",
                        object_name, sep="")
  
  result <- getURLContent(
    modified_url,
    verbose=TRUE,
    ssl.verifypeer=FALSE,
    timeout=30,
    httpheader=c(
      "Authorization"=paste("OAuth ", auth_state@access_token, sep=""),
      "Content-Length"="0",
      "Date"=format(Sys.time(), "%a, %d %b %Y %X %Z"),
      "x-goog-api-version"="2"),
    curl=curl)
  
  return(result[[1]])
}


GetRObject <- function(bucket_name, object_name, auth_state) {
  # Get a JSON-encoded object from Google Storage and convert it to an R object.
  #
  # Args:
  #   bucket_name: String name of bucket
  #   object_name: String name of an object in the previous
  #   auth_state: An AuthState instance
  #
  # Returns:
  #   The fetched object in R format
  json_obj <- GetObject(
    bucket_name, object_name, auth_state)
  
  return(fromJSON(json_obj))
}


PutFile <- function(
  file_connection, bucket_name, object_name, auth_state,
  binary_mode=FALSE) {
  # Write a file into Google Storage.
  #
  # Args:
  #   file_connection: An opened connection to an R file-like object
  #   bucket_name: String name of bucket
  #   object_name: String name of an object
  #   auth_state: An AuthState instance
  #   binary_mode: Use binary mode to write file bytes; if FALSE, file contents
  #                are assumed to be UTF-8 encoded text
  curl <- getCurlHandle()
  
  modified_url <- paste(BigstoreBucketUrl(bucket_name), "/",
                        object_name, sep="")
  
  header <- basicHeaderGatherer()
  result_text <- basicTextGatherer()
  
  # Set content writer function based on file type
  if (binary_mode) {
    read_function <- function(num_bytes){
      return(readBin(file_connection, "raw", n=num_bytes)) }
    content_type <- 'application/octet-stream'
  } else {
    read_function <- function(num_chars){
      return(readChar(file_connection, num_chars)) }
    content_type <- 'text/plain'
  }
  
  result <- curlPerform(
    url=modified_url,
    verbose=TRUE,
    headerfunction=header$update,
    writefunction=result_text$update,
    readfunction=read_function,
    upload=TRUE,
    httpheader=c(
      "Authorization"=paste("OAuth ", auth_state@access_token, sep=""),
      "Content-Type"=content_type,
      "Date"=format(Sys.time(), "%a, %d %b %Y %X %Z"),
      "x-goog-api-version"="2"))
}


PutRObject <- function(r_object, bucket_name, object_name, auth_state) {
  # Encode an R object into JSON format and store it in Google Storage.
  #
  # Args:
  #   r_object: R object to be stored
  #   bucket_name: String name of bucket
  #   object_name: String name of an object
  #   auth_state: An AuthState instance
  json_repr <- toJSON(r_object)
  temp_file <- file()
  writeChar(json_repr, temp_file)
  
  PutFile(temp_file, bucket_name, object_name, auth_state)
  
  close(temp_file)
}


DeleteObject <- function(bucket_name, object_name, auth_state) {
  # Delete an object in Google Storage.
  #
  # Args:
  #   bucket_name: String name of bucket
  #   object_name: String name of an object in the previous
  #   auth_state: An AuthState instance
  curl <- getCurlHandle()
  
  modified_url <- paste(BigstoreBucketUrl(bucket_name), "/",
                        object_name, sep="")
  
  header <- basicHeaderGatherer()
  result_text <- basicTextGatherer()
  
  result <- curlPerform(
    url=modified_url,
    verbose=TRUE,
    headerfunction=header$update,
    writefunction=result_text$update,
    customrequest="DELETE",
    httpheader=c(
      "Authorization"=paste("OAuth ", auth_state@access_token, sep=""),
      "Content-Length"="0",
      "Date"=format(Sys.time(), "%a, %d %b %Y %X %Z"),
      "x-goog-api-version"="2"))
}


CsvToDataFrame <- function(csv_string) {
  # Convert a string in CSV format to an R data frame.
  #
  # Useful for handling CSVs that are fetched using the get_object() function.
  csv_connection <- textConnection(csv_string, "r")
  
  data_frame <- read.csv(csv_connection)
  
  close(csv_connection)
  
  return(data_frame)
}
