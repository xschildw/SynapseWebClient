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
# R functions helpful for getting OAuth2 access tokens for Google Storage for
# Developers. May be generalized to other Google Cloud services in the future.

library("RCurl")
library("rjson")


# S4 class for storing authentication-related information
setClass(
  "AuthState",
  representation(client_id="character",
                 client_secret="character",
                 redirect_uri="character",
                 endpoint_url="character",
                 auth_code = "character",
                 access_token = "character",
                 refresh_token = "character"),
  prototype(client_id="718836969065.apps.googleusercontent.com",
            client_secret="1qeGNZYww45WJM9tHQGbG-pU",
            redirect_uri="urn:ietf:wg:oauth:2.0:oob",
            endpoint_url="https://accounts.google.com/o/oauth2/",
            auth_code=NULL,
            access_token=NULL,
            refresh_token=NULL))


GenerateAuthCodeUrl <- function(
  auth_state,
  scope="https://www.googleapis.com/auth/devstorage.full_control") {
  # Generate an authentication URL for an AuthState object.
  #
  # Args:
  #   auth_state: Instance of an AuthState object
  #   scope: Scope being requested by App
  #
  # Returns:
  #   A URL string to be pasted by the user in their browser
  result <- paste(auth_state@endpoint_url, "auth", "?", "client_id=",
                  auth_state@client_id,
                  "&redirect_uri=", auth_state@redirect_uri, "&scope=",
                  URLencode(scope, reserved=TRUE),
                  "&response_type=code", sep='')
  
  return(result)
}


SetAuthCode <- function(auth_state, auth_code) {
  # Set the authorization code (copied from browser) on an AuthState object.
  auth_state@auth_code <- auth_code
  
  return(auth_state)
}


GetAccessTokens <- function(auth_state) {
  # Get access and refresh tokens using an authorization code.
  #
  # Args:
  #   auth_state: Instance of an AuthState object with non-NULL value in its
  #               auth_code slot
  #
  # Returns:
  #   Updated auth_state with access_token and refresh_token slots populated
  #   appropriately
  curl <- getCurlHandle()
  
  header <- basicHeaderGatherer()
  results <- basicTextGatherer()
  
  string_result <- postForm(
    paste(auth_state@endpoint_url, "token", sep=''),
    .params=c("client_id"=auth_state@client_id,
              "client_secret"=auth_state@client_secret,
              "code"=auth_state@auth_code,
              "redirect_uri"=auth_state@redirect_uri,
              "grant_type"="authorization_code"),
    .opts=curlOptions(
      verbose=TRUE, ssl.verifypeer=FALSE,
      timeout=30, headerfunction=header$update,
      writefunction=results$update),
    curl=curl)
  
  token_response <- fromJSON(results$value(), method="R")
  
  auth_state@access_token <- token_response$access_token
  auth_state@refresh_token <- token_response$refresh_token
  
  return(auth_state)
}


RefreshAccessToken <- function(auth_state) {
  # Refresh the access token associated with an AuthState object
  #
  # Args:
  #   auth_state: Instance of an AuthState object with non-NULL value in its
  #               refresh_token slot
  #
  # Returns:
  #   Input auth_state containg refreshed access_token
  curl <- getCurlHandle()
  
  header <- basicHeaderGatherer()
  results <- basicTextGatherer()
  
  string_result <- postForm(
    paste(auth_state@endpoint_url, "token", sep=''),
    .params=c("client_id"=auth_state@client_id,
              "client_secret"=auth_state@client_secret,
              "refresh_token"=auth_state@refresh_token,
              "grant_type"="refresh_token"),
    .opts=curlOptions(
      verbose=TRUE, ssl.verifypeer=FALSE,
      timeout=30, headerfunction=header$update,
      writefunction=results$update),
    curl=curl)
  
  token_response <- fromJSON(results$value(), method="R")
  
  auth_state@access_token <- token_response$access_token
  
  return(auth_state)
}
