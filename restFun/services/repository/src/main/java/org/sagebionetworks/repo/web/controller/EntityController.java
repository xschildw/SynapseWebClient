package org.sagebionetworks.repo.web.controller;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.sagebionetworks.repo.server.RepositoryException;
import org.sagebionetworks.repo.server.gae.EntityGAERepository;
import org.sagebionetworks.repo.view.PaginatedResults;
import org.sagebionetworks.repo.web.ConflictingUpdateException;
import org.sagebionetworks.repo.web.NotFoundException;
import org.sagebionetworks.repo.web.ServiceConstants;
import org.sagebionetworks.repo.web.UrlPrefixes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * REST controller for CRUD operations on Entity objects
 * 
 * @author deflaux
 */
@Controller
@RequestMapping(UrlPrefixes.ENTITY)
public class EntityController extends BaseController implements AbstractEntityController<String> {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(EntityController.class.getName());
        
    private EntityGAERepository entityRepository = new EntityGAERepository();
    
    /* (non-Javadoc)
     * @see org.sagebionetworks.repo.web.controller.AbstractEntityController#getEntities(java.lang.Integer, java.lang.Integer, javax.servlet.http.HttpServletRequest)
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody PaginatedResults<String> getEntities(
            @RequestParam(value=ServiceConstants.PAGINATION_OFFSET_PARAM, 
                    required=false, 
                    defaultValue=ServiceConstants.DEFAULT_PAGINATION_OFFSET_PARAM) Integer offset,
            @RequestParam(value=ServiceConstants.PAGINATION_LIMIT_PARAM, 
                    required=false, 
                    defaultValue=ServiceConstants.DEFAULT_PAGINATION_LIMIT_PARAM) Integer limit,
                    HttpServletRequest request) {
        ServiceConstants.validatePaginationParams(offset, limit);
        List<String> entities = null; //entityRepository.get(offset, limit);
        Integer totalNumberOfEntities = 24; //(Integer) entityRepository.count();
        return new PaginatedResults<String>(request.getServletPath() + UrlPrefixes.COMMENT,
                entities, totalNumberOfEntities, offset, limit);
    }

    /* (non-Javadoc)
     * @see org.sagebionetworks.repo.web.controller.AbstractEntityController#getEntity(java.lang.Long)
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
        public @ResponseBody String getEntity(@PathVariable Long id) throws NotFoundException {
        JSONObject entity = null;
        try {
            entity = entityRepository.get(id.toString());
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(null == entity) {
            throw new NotFoundException("no entity with id " + id + " exists");
        }
        return entity.toString();
    }

    /* (non-Javadoc)
     * @see org.sagebionetworks.repo.web.controller.AbstractEntityController#createEntity(T)
     */
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody String createEntity(@RequestBody String serializedNewEntity) {
        JSONObject newEntity = null;
        try {
            newEntity = new JSONObject(serializedNewEntity);
        } catch (JSONException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        try {
            newEntity = new JSONObject(serializedNewEntity);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // TODO check newEntity.isValid()
        // newEntity.getValidationErrorEntity()
        try {
            entityRepository.add(newEntity);
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return newEntity.toString();
    }

    /* (non-Javadoc)
     * @see org.sagebionetworks.repo.web.controller.AbstractEntityController#updateEntity(java.lang.Long, java.lang.Integer, T)
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody String updateEntity(@PathVariable Long id, 
            @RequestHeader(ServiceConstants.ETAG_HEADER) Integer etag, 
            @RequestBody String serializedUpdatedEntity) throws NotFoundException, ConflictingUpdateException {
        JSONObject updatedEntity = null;
        try {
            updatedEntity = new JSONObject(serializedUpdatedEntity);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        JSONObject entity = null;
        try {
            entity = entityRepository.get(id.toString());
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(null == entity) {
            throw new NotFoundException("no entity with id " + id + " exists");
        }
        if(etag != entity.hashCode()) {
            throw new ConflictingUpdateException("entity with id " + id 
                    + "was updated since you last fetched it, retrieve it again and reapply the update");
        }
        try {
            entityRepository.add(updatedEntity);
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return updatedEntity.toString();
    }
    
    /* (non-Javadoc)
     * @see org.sagebionetworks.repo.web.controller.AbstractEntityController#deleteEntity(java.lang.Long)
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteEntity(@PathVariable Long id) throws NotFoundException {
//        if(!entityRepository.remove(id.toString())) {
//            throw new NotFoundException("no entity with id " + id + " exists");   
//        }
        try {
            entityRepository.remove(id.toString());
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        return; 
    }

    /**
     * Simple sanity check test request, using the default view<p> 
     * 
     * @param modelMap the parameter into which output data is to be stored
     * @return a dummy hard-coded response
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "test", method = RequestMethod.GET)
        public String sanityCheck(ModelMap modelMap) {
        modelMap.put("hello","REST rocks");
        return ""; // use the default view
    }
        
}
