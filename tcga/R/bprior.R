bprior <-
function( gamma.hat )
{
  m <- mean( gamma.hat )
  s2 <- var( gamma.hat )
  return( ( m * s2 + m^3 ) / s2 )
}

