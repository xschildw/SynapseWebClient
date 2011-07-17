qc.expr <- function( platform.name , exprFilePath , clinical , out.dir = NULL ,
                      RData = TRUE , textFile = FALSE , cancerType = NULL ,
                      tcgaLevel = NULL , log2 = FALSE , Impute = FALSE ,
                      SampleTypes = "01" )
{	
	cat( "QC platform: " , platform.name , "\n" )
	if ( isTRUE( identical( tcgaLevel , 2 ) ) )
  {
		cat( "Loading level 2 data\n" )
		expr <- as.matrix( read.table( exprFilePath , sep = "\t" , header = FALSE ,
                       row.names = 1 , as.is = TRUE , skip = 2 ) )
		colnames( expr ) <- read.table( exprFilePath , sep = "\t" , header = FALSE ,
                                    row.names = 1 , as.is = TRUE , nrows = 1 )
	}
  else if ( isTRUE( identical( tcgaLevel , 3 ) ) )
  {
		# 3-column format for level 3 data
		cat( "Loading level 3 data\n" )
		expr <- tcga.file.to.matrix( exprFilePath )
	}
  else
  {
    cat( "tcgaLevel must be specified\n" )
  }
  
  cat( "Selecting Sample Types\n" )
  match.idxs <- tcga.extractTissueType( colnames( expr ) ) %in% SampleTypes
  expr <- expr[,match.idxs]
  
	if ( log2 )
  {
		cat( "Applying log2\n" )
		expr <- log2( expr )
	}
	cat( "Correcting batch effect\n" )
	expr.qc <- ComBat( expr , matrix( tcga.extractBatch( colnames( expr ) ) , ncol = 1 ,
                     dimnames = list( NULL , "Batch" ) ) )

	if ( Impute )
  {
    # impute NAs
	  if ( any( is.na( expr.qc ) ) )
    {
		  cat( "NAs found.  Imputing using knn.\n" )
		  expr.qc <- impute.knn( expr.qc )$data
	  }
  }

	cat( "Matching clinical data\n" )	
  matchedIDs <- tcga.extractPatientIds( colnames( expr.qc ) )[tcga.extractPatientIds( colnames( expr.qc ) ) %in%
                                                               rownames( clinical )]
  matchedIDs <- rownames( clinical )[rownames( clinical ) %in% matchedIDs]

	expr.qc.matched <- expr.qc[,tcga.extractPatientIds( colnames( expr.qc ) ) %in% matchedIDs]

	clinical.matched <- clinical[rownames( clinical ) %in% matchedIDs,]
  
  clinical.matched <- clinical.matched[tcga.extractPatientIds( colnames( expr.qc.matched ) ),]

	assert( all( tcga.extractPatientIds( colnames( expr.qc.matched ) ) %in%
          rownames( clinical.matched ) ) )

  clinical.matched$tcgaID <- colnames( expr.qc.matched )
  colnames( expr.qc.matched ) <- rownames( clinical.matched )
	
	experimentData <- new( "MIAME", name = "Sage formatted TCGA",
			lab = "TCGA" , 
			contact = "ncicb@pop.nci.nih.gov" ,
			url = "http://cancergenome.nih.gov/" ,
      other = list( correction = "batch" , formatting = list( lab = "Sage Bionetworks" ,
              codeAuthor = "Justin Guinney" , contact = "Justin.Guinney@SageBase.Org" ,
              url = "http://www.sagebase.org" ) ) )
	cat( "Building expression set\n" )
  esetName <- paste( "tcga" , cancerType , platform.name , sep = "." )
	assign( esetName , new( "ExpressionSet" , 
			exprs = expr.qc.matched , 
			experimentData = experimentData,
			phenoData = new( "AnnotatedDataFrame" , data = clinical.matched ) ) )
	
  esetNameAge <- paste( esetName , "Age" , sep = "." )
  assign( esetNameAge , age.correct( get( esetName ) ) )
  experimentData <- new( "MIAME", name = "Sage formatted TCGA",
      lab = "TCGA" , 
      contact = "ncicb@pop.nci.nih.gov" ,
      url = "http://cancergenome.nih.gov/" ,
      other = list( correction = "batch and age" , formatting = list( lab = "Sage Bionetworks" ,
              codeAuthor = "Sage Repository Group" , contact = "RepData@SageBase.Org" ,
              url = "http://www.sagebase.org" ) ) )
  assign( esetNameAge , new( "ExpressionSet" , 
          exprs = exprs( get( esetNameAge ) ) , 
          experimentData = experimentData ,
          phenoData = new( "AnnotatedDataFrame" , data = pData( get( esetNameAge ) ) ) ) )
	
	# write batch only as R data object and tab-delimited file
	cat( "Writing data to " , out.dir , "\n" )
	batchFilePrefix <- paste( out.dir , "tcga_" , paste( cancerType , "_" , sep = "" ) ,
                              platform.name , paste( "_lvl" , tcgaLevel , sep = "" ) ,
                              "_batch_only" , sep = "" )
  if ( RData )
  {
	  save( list = paste( "tcga" , cancerType , platform.name , sep = "." ) ,
          file = paste( batchFilePrefix , ".RData" , sep = "" ) )
  }
  if ( textFile )
  {
    write.exprs( get( esetName ) , file = paste( batchFilePrefix , ".txt" , sep = "" ) ,
                 sep = "\t" , quote = FALSE )
  }

	# write batch * age corrected as R data object and tab-delimited file
	batchAgeFilePrefix <- paste( out.dir , "tcga_" , paste( cancerType , "_" , sep = "" ) ,
                                  platform.name , paste( "_lvl" , tcgaLevel , sep = "" ) ,
			                            "_batch_age" , sep = "" )
  if ( RData )
  {
    save( list = paste( esetName , "Age" , sep = "." ) , file = paste( batchAgeFilePrefix , ".RData" , sep = "" ) )
  }
  if ( textFile )
  {
    write.exprs( get( esetNameAge ) , file = paste( batchAgeFilePrefix , ".txt" , sep = "" ) ,
                 sep = "\t" , quote = FALSE )
  }
	return( list( assign( esetName , get( esetName ) ) , assign( esetNameAge , get( esetNameAge ) ) ) )
}

