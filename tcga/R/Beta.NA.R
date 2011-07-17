Beta.NA <-
function( y , X )
{
  des <- X[!is.na( y ),]
  y1 <- y[!is.na( y )]
  return( solve( t( des ) %*% des ) %*% t( des ) %*% y1 )
}

