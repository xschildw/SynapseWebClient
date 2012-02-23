#------------------------------
# 1) sign up for GoogleStorage https://code.google.com/apis/console/, provide a credit card for billing, and create a bucket
myBucket <- 'YOUR_BUCKET_NAME_HERE'

#------------------------------
# 2) Get the code on https://code.google.com/p/bigr/wiki/BigStoreRClient
source('BigStore.R')
source('OAuthClient.R') # TODO The OAuthClient functionality needs to move into the repository service, its 
                        # not much of a secret if its embedded in an R package

#------------------------------
# 3) grant this application (this script) access to your bucket
authState <- new ("AuthState",
                  client_id="525677998968.apps.googleusercontent.com",
                  client_secret="MalVXph6pOQ61MUPf5dszp4v",
                  redirect_uri="urn:ietf:wg:oauth:2.0:oob",
                  endpoint_url="https://accounts.google.com/o/oauth2/",
                  auth_code="",
                  access_token="",
                  refresh_token="")
url <- GenerateAuthCodeUrl(authState)
utils::browseURL(URLdecode(url))
# in your browser click on "allow" and copy and paste the AuthCode from the browser into this statement

authState <- SetAuthCode(authState, 'YOUR_AUTH_CODE_HERE')
authState <- GetAccessTokens(authState)
# So now this application (this script) has full access to any of your GoogleStorage buckets

#------------------------------
# 4) Store and retreive an R object
input <- list(foo='bar', one=1)
PutRObject(input, myBucket, 'test1.json', authState)
output <- GetRObject(myBucket, 'test1.json', authState)

library(RUnit)
checkEquals(2, length(output))
checkEquals('bar', output$foo)
checkEquals(1, output$one)

#------------------------------
# 5) Go to the GoogleStorage web ui and you will see that test1.json is in your bucket https://sandbox.google.com/storage/
