.curl_writer_open <-
		function(filename)
{
	if (!is.character(filename) || 1L != length(filename))
		stop("'filename' must be character(1)")
	dir <- dirname(filename)
	if (!file.exists(dir) || !file.info(dir)$isdir)
		stop("'dirname(filename)' does not exist or is not a directory")
	filename <- file.path(normalizePath(dir), basename(filename))
	if (file.exists(filename))
		stop("'filename' must not already exist")
	
	.Call("writer_open", filename)
}

.curl_writer_close <-
		function(ext)
{
	.Call("writer_close", ext)
}

.curl_writer_download <-
		function(url, filename=tempfile(), writeFunction=.getCache('curlWriter'))
{
	
	ext <- .curl_writer_open(filename)
	on.exit(.curl_writer_close(ext))
	status <- curlPerform(URL=url, writefunction=writeFunction,
			writedata=ext)
	if (0L != status)
		stop("'curlPerform' failed; status: ", status)
	filename
}
