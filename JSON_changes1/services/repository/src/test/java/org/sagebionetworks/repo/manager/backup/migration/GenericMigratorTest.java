package org.sagebionetworks.repo.manager.backup.migration;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

import org.sagebionetworks.repo.model.Annotations;
import org.sagebionetworks.repo.model.EntityType;
import org.sagebionetworks.repo.model.NamedAnnotations;
import org.sagebionetworks.repo.model.NodeRevisionBackup;
import org.sagebionetworks.repo.model.registry.MigrationSpec;
import org.sagebionetworks.repo.model.registry.EntityTypeMigrationSpec;
import org.sagebionetworks.repo.model.registry.FieldMigrationSpec;
import org.sagebionetworks.repo.model.registry.FieldDescription;
import org.sagebionetworks.repo.model.registry.MigrationSpecData;

public class GenericMigratorTest {
	
	GenericMigrator genericMigrator;;
	NodeRevisionBackup toMigrate;
	
	@Before
	public void before(){
//		mockType = Mockito.mock(EntityType.class);
		genericMigrator = new GenericMigrator();
		toMigrate = new NodeRevisionBackup();
		toMigrate.setNamedAnnotations(new NamedAnnotations());
	}
	
	@Test
	public void testMigratePrimaryToPrimaryString() {
		for (EntityType type: EntityType.values()) {
			MigrationSpec ms = new MigrationSpec();
			List<EntityTypeMigrationSpec> listEms = new ArrayList<EntityTypeMigrationSpec>();
			EntityTypeMigrationSpec ems = new EntityTypeMigrationSpec();
			ems.setEntityType(type.name());
			List<FieldMigrationSpec> listFms = new ArrayList<FieldMigrationSpec>();
			FieldMigrationSpec fms = new FieldMigrationSpec();
			FieldDescription source = new FieldDescription();
			FieldDescription dest = new FieldDescription();
			source.setName("old_name");
			source.setType("string");
			source.setBucket("primary");
			dest.setName("name");
			dest.setType("string");
			dest.setBucket("primary");
			fms.setSource(source);
			fms.setDestination(dest);
			listFms.add(fms);
			listEms.add(ems);
			ems.setFields(listFms);
			ms.setMigrationMetadata(listEms);
			MigrationSpecData msd = new MigrationSpecData();
			msd.setData(ms);
			genericMigrator.setMigrationSpecData(msd);
			String oldKey = "old_name";
			String newKey = "name";
			String valueToMigrate = "Value to be migrated";
			Annotations primaryAnnotations = toMigrate.getNamedAnnotations().getPrimaryAnnotations();
			primaryAnnotations.addAnnotation(oldKey, valueToMigrate);
			Map<String, Collection<String>> stringAnnos = primaryAnnotations.getStringAnnotations();
			assertNotNull(stringAnnos.get(oldKey));
			genericMigrator.migrateOneStep(toMigrate, type);
			assertNull(stringAnnos.get(oldKey));
			assertNotNull(stringAnnos.get(newKey));
			assertEquals(1, stringAnnos.get(newKey).size());
			assertEquals(valueToMigrate, stringAnnos.get(newKey).iterator().next());
		}
		return;
	}
	
	@Test
	public void testMigratePrimaryToAdditionalString() {
		for (EntityType type: EntityType.values()) {
			MigrationSpec ms = new MigrationSpec();
			List<EntityTypeMigrationSpec> listEms = new ArrayList<EntityTypeMigrationSpec>();
			EntityTypeMigrationSpec ems = new EntityTypeMigrationSpec();
			ems.setEntityType(type.name());
			List<FieldMigrationSpec> listFms = new ArrayList<FieldMigrationSpec>();
			FieldMigrationSpec fms = new FieldMigrationSpec();
			FieldDescription source = new FieldDescription();
			FieldDescription dest = new FieldDescription();
			source.setName("old_name");
			source.setType("string");
			source.setBucket("primary");
			dest.setName("new_name");
			dest.setType("string");
			dest.setBucket("additional");
			fms.setSource(source);
			fms.setDestination(dest);
			listFms.add(fms);
			listEms.add(ems);
			ems.setFields(listFms);
			ms.setMigrationMetadata(listEms);
			MigrationSpecData msd = new MigrationSpecData();
			msd.setData(ms);
			genericMigrator.setMigrationSpecData(msd);
			String oldKey = "old_name";
			String newKey = "new_name";
			String valueToMigrate = "Value to be migrated";
			Annotations primaryAnnotations = toMigrate.getNamedAnnotations().getPrimaryAnnotations();
			Annotations additionalAnnotations = toMigrate.getNamedAnnotations().getAdditionalAnnotations();
			primaryAnnotations.addAnnotation(oldKey, valueToMigrate);
			Map<String, Collection<String>> primaryStringAnnos = primaryAnnotations.getStringAnnotations();
			Map<String, Collection<String>> additionalStringAnnos = additionalAnnotations.getStringAnnotations();
			assertNotNull(primaryStringAnnos.get(oldKey));
			genericMigrator.migrateOneStep(toMigrate, type);
			assertNull(primaryStringAnnos.get(oldKey));
			assertNotNull(additionalStringAnnos.get(newKey));
			assertEquals(1, additionalStringAnnos.get(newKey).size());
			assertEquals(valueToMigrate, additionalStringAnnos.get(newKey).iterator().next());
		}
		return;
	}
	
}
