package org.sagebionetworks.repo.model;


import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

public class StepTest {
	
	@Test
	public void testRoundTripStep() throws JSONObjectAdapterException {
		Date d = new Date();
		Step s1 = new Step();
		JSONObjectAdapter adapter1 = new JSONObjectAdapterImpl();
		JSONObjectAdapter adapter2 = new JSONObjectAdapterImpl();

		s1.setAccessControlList("/acl");
		s1.setAnnotations("/annotations");
		s1.setCommandLine("commandLine");
		s1.setCreatedBy("createdBy");
		s1.setDescription("description");
		s1.setEtag("etag");
		s1.setId("id");
		s1.setName("name");
		s1.setParentId("parentId");
		s1.setUri("uri");
		s1.setCreatedOn(d);
		s1.setEndDate(d);
		s1.setModifiedOn(d);
		s1.setStartDate(d);
		
		Set<Code> codeRefs = new HashSet<Code>();
		codeRefs.add(new Code());
		codeRefs.add(new Code());
		s1.setCode(null);
		
		Set<EnvironmentDescriptor> envDescRefs = new HashSet<EnvironmentDescriptor>();
		envDescRefs.add(new EnvironmentDescriptor());
		s1.setEnvironmentDescriptors(null);
		
		Set<Reference> inputRefs = new HashSet<Reference>();
		Set<Reference> outputRefs = new HashSet<Reference>();
		inputRefs.add(new Reference());
		outputRefs.add(new Reference());
		s1.setInput(inputRefs);
		s1.setOutput(outputRefs);
		
		adapter1 = s1.writeToJSONObject(adapter1);
		String s = adapter1.toJSONString();
		adapter2 = JSONObjectAdapterImpl.createAdapterFromJSONString(s);
		Step s2 = new Step(adapter2);
		
		assertEquals(s1, s2);
		return;
	}
}
