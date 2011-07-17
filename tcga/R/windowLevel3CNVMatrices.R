windowLevel3CNVMatrices <- function( chrMatList , windowSize = 60000 , incSize = NULL )
{
  cat("Windowing CNV\n")

  if ( is.null( incSize ) )
  {
    incSize <- round( windowSize / 2 )
  }

  allChrMat2 <- sapply( 1:length( chrMatList ) , function( i )
      {
        cat( "chr" , i , "\n" )
        chr <- chrMatList[[i]]
        minPos <- min( start( chr$ir ) )
        maxPos <- max( end( chr$ir ) )
        allPos <- seq( from = ( incSize + minPos ) , to = (maxPos - incSize ) , by = incSize )

        allPosIR <- IRanges( allPos - incSize , allPos + incSize )
        matchMat <- matchMatrix( findOverlaps( allPosIR , chr$ir ) )

        winMat <- t( sapply( 1:length( allPosIR ) , function( idx )
                    {
                      matchIndexes <- matchMat[matchMat[,1] == idx,2]
                      if ( length( matchIndexes ) > 0 )
                      {
                        matchIR <- chr$ir[matchIndexes]
                        matchData <- rbind( chr$data[matchIndexes,] )
                        widthIR <- width( matchIR )

                        # weighted average based on length of segment
                        weightedAvgPerPos <- colSums( widthIR * matchData ) / sum( widthIR )
                      }
                      else
                      {
                        weightedAvgPerPos <- rep( 0 , ncol( chr$data ) )
                      }
                      weightedAvgPerPos
                    } ) )

        #winMat <- matrix(0, nrow=length(pos),ncol=ncol(chr$data),dimnames=list(pos, colnames(chr$data)))
        colnames( winMat ) <- colnames( chr$data )
        rownames( winMat ) <- paste( i , allPos , sep = "_" )
        return( winMat )
      })

  do.call( "rbind" , allChrMat2 )
}
