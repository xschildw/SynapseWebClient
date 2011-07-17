tcga.file.to.matrix <-
function( data.file.in , out.file = NULL )
{
	data <- scan( data.file.in , skip = 1 , what = list( x = "" , y = "" , z = 0 ) ,
                na.strings = c( "NULL" , "null" ) )
	
	expr.ngenes <- length( unique( data$y ) )
	expr.nsamples <- length( unique( data$x ) )
	expr.names <- data$x[( ( 1:expr.nsamples ) - 1 ) * expr.ngenes + 1]
	expr <- matrix( data$z , nrow = expr.ngenes )
	colnames( expr ) <- expr.names
	rownames( expr ) <- data$y[1:expr.ngenes]
	if ( !is.null( out.file ) )
  {
		write.table( expr , out.file , sep = "\t" , quote = FALSE )
	}
	return( expr )
}

