tcga.extractCenter <-
function( tcgaIDs )
{
	parts <- strsplit( fixIds( tcgaIDs ) , "-" , fixed = TRUE )	
	return( as.factor( sapply( parts , function( x ) { x[2] } , simplify = TRUE ) ) )
}

