assert <- function( logicalTest , msg = "Assert failed" )
{
	if ( any( is.na( logicalTest ) ) || !all( logicalTest ) )
  { 
		stop( paste = c( "Assert failed: " , msg ) )
	}
}
