prepare.clinical <- function( clinicalFilePath )
{
	clinical <- read.table( clinicalFilePath , na.strings = "null" , header = TRUE ,
                          as.is = TRUE , sep = "\t" , row.names = 1 )
	cnsr <- is.na( clinical$DAYSTODEATH )
	surv.time <- clinical$DAYSTODEATH
	surv.time[cnsr] <- clinical$DAYSTOLASTFOLLOWUP[cnsr] 
	
	clinical$surv.time <- surv.time
	clinical$died <- !cnsr
	clinical$censored <- cnsr
	clinical$age <- round( abs( clinical$DAYSTOBIRTH / 365 ) )
	return( clinical )
}

