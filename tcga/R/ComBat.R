ComBat <- function( dat , sampleInfo , par.prior = TRUE ,
                     filter = NULL , skip = 0 , prior.plots = FALSE )
{	
  if ( !inherits( sampleInfo , "matrix" ) )
  {
    sampleInfo <- as.matrix( sampleInfo )
  }
  if ( !any( "Batch" %in% colnames( sampleInfo ) ) )
  {
    stop( "ERROR: Sample Information File does not have a Batch column!" )
  }
  geneinfo <- NULL

  if ( !is.null( filter ) )
  {
    ngenes <- nrow( dat )
    col <- ncol( dat ) / 2
    present <- apply( dat , 1 , filter.absent , filter )
    dat <- dat[present,-( 2 * ( 1:col ) )]
    if ( skip > 0 )
    {
      geneinfo <- geneinfo[present,]
    }
    cat( "Filtered genes absent in more than" , filter , "of samples. Genes remaining:" ,
        nrow( dat ) , "; Genes filtered:" , ngenes - nrow( dat ) , "\n")
  }
  
  if ( any( apply( dat , 2 , mode ) != "numeric" ) )
  {
    stop( "ERROR: Array expression columns contain non-numeric values! (Check your .xls file for non-numeric values and if this is not the problem, make a .csv file and use the type=csv option)" )
  }

  design <- design.mat( sampleInfo )

  batches <- list.batch( sampleInfo )
  n.batch <- length( batches )
  n.batches <- sapply( batches , length )
  n.array <- sum( n.batches )

  ## Check for missing values
  NAs <- any( is.na( dat ) )
  if ( NAs )
  {
    cat( c( "Found" , sum( is.na( dat ) ) , "Missing Data Values\n" ) , sep = " " )
  }

  ##Standardize Data across genes
  cat( "Standardizing Data across genes\n" )
  if ( !NAs )
  {
    B.hat <- solve( t( design ) %*% design ) %*% t( design ) %*% t( as.matrix( dat ) )
  }
  else
  {
    B.hat <- apply( dat , 1 , Beta.NA , design )
  } #Standarization Model
  grand.mean <- t( n.batches / n.array ) %*% B.hat[1:n.batch,]
  if ( !NAs )
  {
    var.pooled <- ( ( dat - t( design %*% B.hat ) )^2 ) %*% rep( 1 / n.array , n.array )
  }
  else
  {
    var.pooled <- apply( dat - t( design %*% B.hat ) , 1 , var , na.rm = TRUE )
  }

  stand.mean <- t( grand.mean ) %*% t( rep( 1 , n.array ) )
  if ( !is.null( design ) )
  {
    tmp <- design
    tmp[,c( 1:n.batch )] <- 0
    stand.mean <- stand.mean + t( tmp %*% B.hat )
  }	
  s.data <- ( dat - stand.mean ) / ( sqrt( var.pooled ) %*% t( rep( 1 , n.array ) ) )

  ##Get regression batch effect parameters
  cat( "Fitting L/S model and finding priors\n" )
  batch.design <- design[,1:n.batch]
  if ( !NAs )
  {
    gamma.hat <- solve( t( batch.design ) %*% batch.design ) %*% t( batch.design ) %*% t( as.matrix( s.data ) )
  }
  else
  {
    gamma.hat <- apply( s.data , 1 , Beta.NA , batch.design )
  }
  delta.hat <- NULL
  for ( i in batches )
  {
    delta.hat <- rbind( delta.hat , apply( s.data[,i] , 1 , var , na.rm = TRUE ) )
  }
  
  ##Find Priors
  gamma.bar <- apply( gamma.hat , 1 , mean )
  t2 <- apply( gamma.hat , 1 , var )
  a.prior <- apply( delta.hat , 1 , aprior )
  b.prior <- apply( delta.hat , 1 , bprior )
  
  ##Plot empirical and parametric priors
  
  if ( prior.plots & par.prior )
  {
    plotCombat( gamma.hat , gamma.bar , t2 , delta.hat , a.prior , b.prior , ... )
  }
  
  ##Find EB batch adjustments
  
  gamma.star <- delta.star <- NULL
  if ( par.prior )
  {
    cat( "Finding parametric adjustments\n" )
    for ( i in 1:n.batch )
    {
      temp <- it.sol( s.data[,batches[[i]]] , gamma.hat[i,] , delta.hat[i,] ,
                       gamma.bar[i] , t2[i] , a.prior[i] , b.prior[i] )
      gamma.star <- rbind( gamma.star , temp[1,] )
      delta.star <- rbind( delta.star , temp[2,] )
    }
  }
  else
  {
    cat( "Finding nonparametric adjustments\n" )
    for ( i in 1:n.batch )
    {
      temp <- int.eprior( as.matrix( s.data[,batches[[i]]] ) , gamma.hat[i,] ,
                           delta.hat[i,] )
      gamma.star <- rbind( gamma.star , temp[1,] )
      delta.star <- rbind( delta.star , temp[2,] )
    }
  }

  ### Normalize the Data ###
  cat( "Adjusting the Data\n" )

  j <- 1
  for ( i in batches )
  {
    s.data[,i] <- ( s.data[,i] - t( batch.design[i,] %*% gamma.star ) ) /
                    ( sqrt( delta.star[j,] ) %*% t( rep( 1 , n.batches[j] ) ) )
    j <- j + 1
  }

  s.data <- ( s.data * ( sqrt( var.pooled ) %*% t( rep( 1 , n.array ) ) ) ) + stand.mean

  return( cbind( geneinfo , s.data ) )
}

