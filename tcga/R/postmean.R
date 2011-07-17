postmean <-
function( g.hat , g.bar , n,d.star , t2 )
{
  return( ( t2 * n * g.hat + d.star * g.bar ) / ( t2 * n + d.star ) )
}

