fixIds <- function( tcgaIDs )
{
	return( gsub( "\\." , "-" , as.matrix( tcgaIDs ) ) )
}

