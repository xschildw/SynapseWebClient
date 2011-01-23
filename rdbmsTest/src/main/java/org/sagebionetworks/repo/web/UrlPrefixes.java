package org.sagebionetworks.repo.web;

import java.util.HashMap;
import java.util.Map;

import org.sagebionetworks.repo.model.Comment;
import org.sagebionetworks.repo.model.Message;

/**
 * This is a work-in progress.  Let's see if I can factor this info here.<p>
 * 
 * TODO comment me!
 * 
 * @author deflaux
 *
 */
@SuppressWarnings("unchecked")
public class UrlPrefixes {

    /**
     * TODO comment me!
     * 
     */
    public static final String COMMENT = "/comment";
    /**
     * TODO comment me!
     * 
     */
    public static final String ENTITY = "/entity";
    /**
     * TODO comment me!
     * 
     */
    public static final String MESSAGE = "/message";
    
    /**
     * Mapping of type to url prefix
     */
    public static final Map<Class, String> MODEL2URL;
    
    static {
        MODEL2URL = new HashMap<Class, String>();
        MODEL2URL.put(Comment.class, COMMENT);
        MODEL2URL.put(Object.class, ENTITY);
        MODEL2URL.put(Message.class, MESSAGE);
    }
    
}
