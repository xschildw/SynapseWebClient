/**
 * 
 */
package org.sagebionetworks.repo.web;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.ModelAndViewResolver;

/**
 * This interceptor adds an ETag header to any response returning a model object as the body of the response.
 * <p>
 * Dev Note: Although the class below implements a view resolver interface, it is really more like a postHandle
 * interceptor.  I tried to instead implement org.springframework.web.servlet.HandlerInterceptor but it only receives 
 * the ModelAndView, not the result object for the response body as facilitated by Spring 3.0's REST support.
 * 
 * @author deflaux
 *
 */
public class EtagResolver implements ModelAndViewResolver {

    private static final Logger log = Logger.getLogger(EtagResolver.class.getName());

    @SuppressWarnings("unchecked")
    @Override
    public ModelAndView resolveModelAndView(Method handlerMethod,
            Class handlerType, Object returnValue,
            ExtendedModelMap implicitModel, NativeWebRequest webRequest) {

        if(null != returnValue) {
            Integer etag = returnValue.hashCode();
            log.fine("adding Etag: " + etag);
            HttpServletResponse response = (HttpServletResponse) webRequest.getNativeResponse();
            response.setIntHeader(RequestParameters.ETAG_HEADER, etag);
        }
        return UNRESOLVED;
    }
}
