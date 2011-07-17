tcga.extractBatch <-
function( tcgaIDs )
{
	parts <- strsplit( fixIds( tcgaIDs ) , "-" , fixed = TRUE )	
	return( as.factor( sapply( parts , function( x ) { x[6] } , simplify = TRUE ) ) )
}

