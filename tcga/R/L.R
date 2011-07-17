L <-
function( x , g.hat , d.hat )
{
  return( prod( dnorm( x , g.hat , sqrt( d.hat ) ) ) )
}

