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
 * @author deflaux
 *
 */
public class EtagResolver implements ModelAndViewResolver {

    private static final Logger log = Logger.getLogger(EtagResolver.class.getName());

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
