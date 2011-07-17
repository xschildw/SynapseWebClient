writeLevel3CNVFile <- function( filePath = "/tmp" , fileRoot = "output" , cnvObject , RData = TRUE , textFile = FALSE )
{
  if ( RData )
  {
    fileName <- paste( filePath , paste( fileRoot , "RData" , sep = "." ) , sep = "/" )
    save( cnvObject , file = fileName )
  }
  if ( textFile )
  {
    fileName <- paste( filePath , paste( fileRoot , "txt" , sep = "." ) , sep = "/" )
    write.table( cnvObject , file = fileName , sep = "\t" , col.names = TRUE , row.names = TRUE , quote = FALSE )
  }
}
