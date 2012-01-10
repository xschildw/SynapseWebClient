package org.sagebionetworks.repo.manager.backup.migration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import org.sagebionetworks.repo.manager.NodeTranslationUtils;
import org.sagebionetworks.repo.model.Annotations;
import org.sagebionetworks.repo.model.NamedAnnotations;
import org.sagebionetworks.repo.model.NodeRevisionBackup;
import org.sagebionetworks.repo.model.EntityType;
import org.sagebionetworks.repo.model.registry.MigrationSpecData;

/**
 * The only job for this step is to take NodeRevisionBackup from v0 to v1.
 * @author John
 *
 */
public class RevisionStepV0toV1 implements RevisionMigrationStep {
	
	// Use the migration spec to get a list of primary fields that are getting deleted
	// and therefore are not in the schema anymore. This should prevent having these
	// fields migrated to additional instead of primary.
	private MigrationSpecData migrationSpecData;

	public void setMigrationSpecData(MigrationSpecData m) {
		this.migrationSpecData = m;
	}
	
	public MigrationSpecData getMigrationSpecData() {
		return this.migrationSpecData;
	}

	public RevisionStepV0toV1(MigrationSpecData msd) {
		this.migrationSpecData = msd;
	}

	@Override
	public NodeRevisionBackup migrateOneStep(NodeRevisionBackup toMigrate, EntityType type) {
		// Only migrate v0 (null) to v1.
		if(!isXmlV0(toMigrate.getXmlVersion())) return toMigrate;
		
		// The major change between v0 and v1 as annotations now have a name-space.
		// Therefore, annotations are stored in a map of annotations rather than as a single set.
		// Note: This addresses PLFM-203.
		NamedAnnotations namespaceAnnotations = new NamedAnnotations();
		Annotations primaryAnnotations = namespaceAnnotations.getPrimaryAnnotations();
		Annotations additionalAnnotations = namespaceAnnotations.getAdditionalAnnotations();
		
		List<String> primaryFieldsToDelete = this.migrationSpecData.getPrimaryFieldsToDelete(type);
		
		// Split the single set of annotations into a set for each name-spaces
		Annotations oldStyleAnnos = toMigrate.getAnnotations();
		if(oldStyleAnnos != null){
			Iterator<String> it = oldStyleAnnos.keySet().iterator();
			while(it.hasNext()){
				String key = it.next();
				Collection values = oldStyleAnnos.getAllValues(key);
				if(NodeTranslationUtils.isPrimaryFieldName(type, key)){
					// Add it to the primary.
					primaryAnnotations.addAnnotation(key, values);
				}else{
					// Add it to additional if not in in list of primary to delete.
					if (! primaryFieldsToDelete.contains(key))
						additionalAnnotations.addAnnotation(key, values);
				}
			}
		}
		// Set the new annotations.
		toMigrate.setNamedAnnotations(namespaceAnnotations);
		// Clear-out the old annotations
		toMigrate.setAnnotations(null);
		// Now that we are done, change the version to v1
		toMigrate.setXmlVersion(NodeRevisionBackup.XML_V_1);
		return toMigrate;
	}
	
	/**
	 * We are on V0 if the current step is 
	 * @param xmlVersion
	 * @return
	 */
	protected boolean isXmlV0(String xmlVersion){
		// The first version did not have a version string and will be null.
		if(xmlVersion == null) return true;
		// If the V0 string is applied.
		return NodeRevisionBackup.XML_V_0.equals(xmlVersion);
	}

}
