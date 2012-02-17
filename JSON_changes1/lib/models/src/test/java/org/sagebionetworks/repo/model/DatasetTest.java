package org.sagebionetworks.repo.model;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

public class DatasetTest {
	
	@Test
	public void testRoundTripDataset() throws JSONObjectAdapterException {
		Dataset ds1 = new Dataset();
		JSONObjectAdapter adapter1 = new JSONObjectAdapterImpl();
		JSONObjectAdapter adapter2 = new JSONObjectAdapterImpl();
		Date d = new Date();
		
		ds1.setAccessControlList("/acl");
		ds1.setAnnotations("annotations");
		ds1.setCreatedBy("createdBy");
		ds1.setCreatedOn(d);
		ds1.setDescription("description");
		ds1.setEtag("etag");
		ds1.setId("id");
		ds1.setLayers("/layers");
		ds1.setModifiedBy("modifiedBy");
		ds1.setModifiedOn(d);
		ds1.setName("name");
		ds1.setUri("uri");

//	TODO: Remove Locationable from .json and following code
		ds1.setVersionComment("versionComment");
		ds1.setVersionLabel("versionLabel");
		ds1.setVersionNumber(1L);
		ds1.setVersionUrl("versionUrl");
		ds1.setVersions("versions");
		ds1.setContentType("txt");
		ds1.setMd5("abcdef");
		List<LocationData> ldl = new ArrayList<LocationData>();
		LocationData ld = new LocationData();
		ld.setPath("path");
		ld.setType(LocationTypeNames.sage);
		ldl.add(ld);
		ds1.setLocations(ldl);


		ds1.setEulaId("0");
		ds1.setHasClinicalData(Boolean.TRUE);
		ds1.setHasGeneticData(Boolean.TRUE);
		ds1.setHasExpressionData(Boolean.TRUE);
		ds1.setStatus("status");

//		String disease1, disease2;
//		disease1 = "disease1";
//		disease2 = "disease2";
//		List<String> diseases = new ArrayList<String>();
//		diseases.add(disease1);
//		diseases.add(disease2);
//		ds1.setDiseases(diseases);

//		String tissue1 = "tissue1";
//		List<String> tissues = new ArrayList<String>();
//		tissues.add(tissue1);
//		ds1.setSampleSource(tissues);
//		
//		String sampleType1, sampleType2;
//		sampleType1 = "normal tissue";
//		sampleType2 = "tumor_tissue";
//		List<String> sampleTypes = new ArrayList<String>();
//		sampleTypes.add(sampleType1);
//		sampleTypes.add(sampleType2);
//		ds1.setSampleType(sampleTypes);
		
		adapter1 = ds1.writeToJSONObject(adapter1);
		String s = adapter1.toJSONString();
		adapter2 = JSONObjectAdapterImpl.createAdapterFromJSONString(s);
		Dataset ds2 = new Dataset(adapter2);
		
		assertEquals(ds1, ds2);
	}
}
