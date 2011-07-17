#buildCNVMatrices <- function( file.path , SampleTypes = "01" )
#{
#  cnv.tbl <- read.table( file.path , header = TRUE , as.is = TRUE , sep = "\t" )
#  barcodes <- unique( cnv.tbl$barcode )
#  barcodes <- barcodes[tcga.extractTissueType( barcodes ) %in% SampleTypes]
#  cnv.tbl <- cnv.tbl[cnv.tbl$barcode %in% barcodes,]
#  for( pat.id in barcodes )
#  {
#    chr.pat <- chr[chr$barcode == pat.id,]
#    pat.r <- IRanges( chr.pat$start , chr.pat$stop )
#    mm <- matchMatrix( findOverlaps( dis.ir , pat.r ) )
#    chr.mat[mm[,1],pat.id] <- chr.pat$seg.mean[mm[,2]]
#  }
#  tmp <- new("CNSet",alleleA=matrix(0,nrow=nrow(cnv.tbl),ncol=2),alleleB=matrix(0,nrow=nrow(cnv.tbl),ncol=2),CA=matrix(0,nrow=nrow(cnv.tbl),ncol=2),CB=matrix(0,nrow=nrow(cnv.tbl),ncol=2),call=matrix(0,nrow=nrow(cnv.tbl),ncol=2),callProbability=matrix(0,nrow=nrow(cnv.tbl),ncol=2))
#}

buildLevel3CNVMatrices <- function( lvl3.file.path , SampleTypes = "01" )
{
  cat("Building level 3 CNV matrices\n")
  cnv.tbl <- read.table( lvl3.file.path , header = TRUE , as.is = TRUE , sep = "\t" )
  barcodes <- unique( cnv.tbl$barcode )
  barcodes <- barcodes[tcga.extractTissueType( barcodes ) %in% SampleTypes]
  cnv.tbl <- cnv.tbl[cnv.tbl$barcode %in% barcodes,]
  chrMatList <- list()
  for ( i in 1:24 )
  {
    cat( "chr " , i , "\n" )
    chr <- cnv.tbl[cnv.tbl$chromosome == i,]
    ir <- IRanges( chr$start , chr$stop )
    # disjoin finds all possible breakpoints across all samples
    dis.ir <- disjoin( ir )
    chr.mat <- matrix( 0 , nrow = length( dis.ir ) , ncol = length( barcodes ) ,
        dimnames = list( NULL , barcodes ) ) 
    
    for( pat.id in barcodes )
    {
      chr.pat <- chr[chr$barcode == pat.id,]
      pat.r <- IRanges( chr.pat$start , chr.pat$stop )
      mm <- matchMatrix( findOverlaps( dis.ir , pat.r ) )
      chr.mat[mm[,1],pat.id] <- chr.pat$seg.mean[mm[,2]]
    }
    
    chrMatList[[i]] <- list( ir = dis.ir , data = chr.mat )
  }
  return (chrMatList)
}
