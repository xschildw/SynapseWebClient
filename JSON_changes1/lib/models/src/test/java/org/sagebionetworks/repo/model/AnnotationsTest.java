package org.sagebionetworks.repo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.junit.Test;

/**
 * Test basic opperations of annaotations.
 * @author jmhill
 *
 */
public class AnnotationsTest {
	
	@Test
	public void testAddString(){
		Annotations anno = new Annotations();
		anno.addAnnotation("key1", "value1");
		anno.addAnnotation("key1", "value2");
		anno.addAnnotation("key2", "value3");
		Map<String, Collection<String>> map = anno.getStringAnnotations();
		assertNotNull(map);
		// There should be two collections, the first with two values
		assertEquals(2, map.size());
		Collection<String> valueone = map.get("key1");
		assertNotNull(valueone);
		assertEquals(2, valueone.size());
	}
	
	@Test
	public void testAddLong(){
		Annotations anno = new Annotations();
		anno.addAnnotation("key1", new Long(1));
		anno.addAnnotation("key1", new Long(2));
		anno.addAnnotation("key2", new Long(2));
		Map<String, Collection<Long>> map = anno.getLongAnnotations();
		assertNotNull(map);
		// There should be two collections, the first with two values
		assertEquals(2, map.size());
		Collection<Long> valueone = map.get("key1");
		assertNotNull(valueone);
		assertEquals(2, valueone.size());
	}
	
	@Test
	public void testAddDouble(){
		Annotations anno = new Annotations();
		anno.addAnnotation("key1", new Double(1.1));
		anno.addAnnotation("key1", new Double(2.2));
		anno.addAnnotation("key2", new Double(2.4));
		Map<String, Collection<Double>> map = anno.getDoubleAnnotations();
		assertNotNull(map);
		// There should be two collections, the first with two values
		assertEquals(2, map.size());
		Collection<Double> valueone = map.get("key1");
		assertNotNull(valueone);
		assertEquals(2, valueone.size());
	}
	
	@Test
	public void testAddDate(){
		Annotations anno = new Annotations();
		anno.addAnnotation("key1", new Date(123123L));
		anno.addAnnotation("key1", new Date(434345L));
		anno.addAnnotation("key2", new Date(345346L));
		Map<String, Collection<Date>> map = anno.getDateAnnotations();
		assertNotNull(map);
		// There should be two collections, the first with two values
		assertEquals(2, map.size());
		Collection<Date> valueone = map.get("key1");
		assertNotNull(valueone);
		assertEquals(2, valueone.size());
	}
	
	@Test
	public void testDeleteString() {
		Annotations anno = new Annotations();
		anno.addAnnotation("key1", "value1");
		anno.addAnnotation("key2", "value2.1");
		anno.addAnnotation("key2", "value2.2");
		anno.addAnnotation("key3", "value3");
		Collection<String> deleted = (Collection<String>)anno.deleteAnnotation("key1");
		assertNotNull(deleted);
		Map<String, Collection<String>> map = anno.getStringAnnotations();
		assertNull(map.get("key1"));
		assertNull(anno.getValue("key1"));
		return;
	}
	
	@Test
	public void testDeleteLong() {
		Annotations anno = new Annotations();
		anno.addAnnotation("key1", new Long(1));
		anno.addAnnotation("key2", new Long(2));
		anno.addAnnotation("key2", new Long(3));
		anno.addAnnotation("key3", new Long(4));
		Collection<Long> deleted = (Collection<Long>)anno.deleteAnnotation("key2");
		assertNotNull(deleted);
		assertEquals(2, deleted.size());
		Map<String, Collection<Long>> map = anno.getLongAnnotations();
		assertNull(map.get("key2"));
		assertNull(anno.getValue("key2"));
		return;
	}
	
	@Test
	public void testDeleteDouble() {
		Annotations anno = new Annotations();
		anno.addAnnotation("key1", new Double(1));
		anno.addAnnotation("key2", new Double(2));
		anno.addAnnotation("key2", new Double(3));
		anno.addAnnotation("key3", new Double(4));
		Collection<Double> deleted = (Collection<Double>)anno.deleteAnnotation("key2");
		assertNotNull(deleted);
		assertEquals(2, deleted.size());
		Map<String, Collection<Double>> map = anno.getDoubleAnnotations();
		assertNull(map.get("key2"));
		assertNull(anno.getValue("key2"));
		return;
	}
	
	@Test
	public void testDeleteDate() {
		Annotations anno = new Annotations();
		anno.addAnnotation("key1", new Date());
		anno.addAnnotation("key2", new Date());
		anno.addAnnotation("key2", new Date());
		anno.addAnnotation("key3", new Date());
		Collection<Date> deleted = (Collection<Date>)anno.deleteAnnotation("key2");
		assertNotNull(deleted);
		assertEquals(2, deleted.size());
		Map<String, Collection<Date>> map = anno.getDateAnnotations();
		assertNull(map.get("key2"));
		assertNull(anno.getValue("key2"));
		return;
	}
	
	@Test
	public void testGetStringArray() {
		Annotations anno = new Annotations();
		anno.addAnnotation("key1", "value1");
		anno.addAnnotation("key1", "value2");
		anno.addAnnotation("key2", "value3");
		Collection<String> result = (Collection<String>)anno.getValue("key1");
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("value1", result.iterator().next());
		return;
	}
	
	@Test
	public void testGetLongArray() {
		Annotations anno = new Annotations();
		anno.addAnnotation("key1", new Long(1));
		anno.addAnnotation("key1", new Long(2));
		anno.addAnnotation("key2", new Long(3));
		Collection<Long> result = (Collection<Long>)anno.getValue("key1");
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(new Long(1), result.iterator().next());
		return;
	}
	
	@Test
	public void testGetDoubleArray() {
		Annotations anno = new Annotations();
		anno.addAnnotation("key1", new Double(1));
		anno.addAnnotation("key1", new Double(2));
		anno.addAnnotation("key2", new Double(3));
		Collection<Double> result = (Collection<Double>)anno.getValue("key1");
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(new Double(1), result.iterator().next());
		return;
	}
	
	@Test
	public void testGetDateArray() {
		Annotations anno = new Annotations();
		Date v = new Date();
		anno.addAnnotation("key1", v);
		anno.addAnnotation("key1", new Date());
		anno.addAnnotation("key2", new Date());
		Collection<Date> result = (Collection<Date>)anno.getValue("key1");
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(v, result.iterator().next());
		return;
	}

}
