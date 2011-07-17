tcga.extractPatientIds <- function( tcgaIDs )
{
	parts <- strsplit( fixIds( tcgaIDs ) , "-" , fixed = TRUE )
	return( sapply( parts , function( x ) { paste( x[1] , x[2] , x[3] ,
                  sep = "-" ) } , simplify = TRUE ) )
}

