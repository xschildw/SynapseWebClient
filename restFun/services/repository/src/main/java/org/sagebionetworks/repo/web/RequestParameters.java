/*
 * RequestParameters.java
 *
 * Sage Bionetworks http://www.sagebase.org
 *
 * Original author: Nicole Deflaux (nicole.deflaux@sagebase.org)
 *
 * @file   $Id: $
 * @author $Author: $
 * @date   $DateTime: $
 *
 */

package org.sagebionetworks.repo.web;

/**
 * Constants for query parameter keys
 * <p>
 * All query parameter keys should be in this file as opposed to being
 * defined in individual controllers.  The reason for this to is help
 * ensure consistency accross controllers.
 *
 * @author deflaux
 */
public class RequestParameters {
    /**
     * Request parameter used to indicate the 1-based index of the first result
     * to be returned in a set of paginated results<p>
     * 
     * See also: <a href="http://developers.facebook.com/docs/api/">Facebook API section on paging</a>
     */
    public static final String PAGINATION_OFFSET = "offset";
    
    /**
     * Request parameter used to indicate the maximum number of results to be
     * returned in a set of paginated results<p>
     * 
     * See also: <a href="http://developers.facebook.com/docs/api/">Facebook API section on paging</a>
     */
    public static final String PAGINATION_LIMIT = "limit";
    
    /**
     * Request header used to indicate the version of the resource.<p>
     * 
     * This is commonly used for optimistic concurrency control so that 
     * conflicting updates can be detected and also conditional retrieval 
     * to improve efficiency.  See also:
     * <ul>
     * <li><a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.19">HTTP spec</a>
     * <li><a href="http://code.google.com/apis/gdata/docs/2.0/reference.html#ResourceVersioning">Google Data API</a>
     * <li><a href="http://www.odata.org/developers/protocols/operations#ConcurrencycontrolandETags">OData Protocol</a>
     * </ul>
     */
    public static final String ETAG_HEADER = "ETag";
}
