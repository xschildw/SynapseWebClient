tcga.extractTissueType <-
function( tcgaIDs )
{
	parts <- strsplit( fixIds( tcgaIDs ) , "-" , fixed = TRUE )	
	return( sapply( parts , function( x ) { substring( x[4] , 1 , 2 ) } , simplify = TRUE ) )
}

