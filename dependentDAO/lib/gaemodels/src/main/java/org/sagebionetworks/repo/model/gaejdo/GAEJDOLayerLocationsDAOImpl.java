package org.sagebionetworks.repo.model.gaejdo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.sagebionetworks.repo.model.DatastoreException;
import org.sagebionetworks.repo.model.InvalidModelException;
import org.sagebionetworks.repo.model.LayerLocation;
import org.sagebionetworks.repo.model.LayerLocations;
import org.sagebionetworks.repo.model.LayerLocationsDAO;

import com.google.appengine.api.datastore.KeyFactory;

/**
 * @author deflaux
 * 
 */
public class GAEJDOLayerLocationsDAOImpl extends
		GAEJDOBaseDAOImpl<LayerLocations, GAEJDOInputDataLayer> implements
		LayerLocationsDAO {

	@Override
	protected LayerLocations newDTO() {
		return new LayerLocations();
	}

	@Override
	protected void copyToDto(GAEJDOInputDataLayer jdo, LayerLocations dto)
			throws DatastoreException {
		dto.setId(KeyFactory.keyToString(jdo.getId()));
		Collection<LayerLocation> dtoLocations = new HashSet<LayerLocation>();
		if (null != jdo.getLocations()) {
			for (GAEJDOLayerLocation location : jdo.getLocations()) {
				dtoLocations.add(location.toLayerLocation());
			}
		}
		dto.setLocations(dtoLocations);
	}

	@Override
	protected void copyFromDto(LayerLocations dto, GAEJDOInputDataLayer jdo)
			throws InvalidModelException {
		//
		// Confirm that the DTO is valid by checking that all required fields
		// are set
		//
		// Question: is this where we want this sort of logic?
		// Dev Note: right now the only required field is name and type but I
		// can imagine
		// that the
		// validation logic will become more complex over time
		if (null == dto.getLocations()) {
			throw new InvalidModelException(
					"'locations' is a required property for LayerLocations");
		}
		Collection<GAEJDOLayerLocation> jdoLocations = new HashSet<GAEJDOLayerLocation>();
		if (null != dto.getLocations()) {
			for (LayerLocation location : dto.getLocations()) {
				jdoLocations.add(new GAEJDOLayerLocation(location));
			}
		}
		jdo.setLocations(jdoLocations);
	}

	@Override
	protected Class<GAEJDOInputDataLayer> getJdoClass() {
		return GAEJDOInputDataLayer.class;
	}

	@Override
	public Collection<String> getPrimaryFields() {
		Collection<String> fields = new ArrayList<String>();
		fields.add("locations");
		return fields;
	}

	@Override
	protected GAEJDOInputDataLayer newJDO() {
		GAEJDOInputDataLayer jdo = new GAEJDOInputDataLayer();
		return jdo;
	}

}
