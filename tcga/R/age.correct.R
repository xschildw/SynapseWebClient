age.correct <- function( eset )
{
	pheno <- pData( eset )
	age.eset <- eset[,!is.na( pheno$age )]
	age <- age.eset$age
  if ( any( is.na( exprs( age.eset ) ) ) )
  {
    exprs.age <- exprs( age.eset )
    nColumns <- ncol( exprs.age )
    for ( i in nrow( exprs.age ) )
    {
      notNAs <- c(1:nColumns)[-which( is.na( exprs.age[i,] ) )]
      exprs.age[i,notNAs] <- qr.resid( qr( model.matrix( ~ age[notNAs] ) ) , exprs.age[i,notNAs] ) + mean( exprs.age[i,notNAs] )
    }
    exprs( age.eset ) <- exprs.age
  }
  else
  {
	  x.qr <- qr( model.matrix( ~age ) )
	  fit.res <- qr.resid( x.qr , t( exprs( age.eset ) ) )
	  exprs( age.eset ) <- t( fit.res ) + rowMeans( exprs( age.eset ) )
  }
	return( age.eset )
}
