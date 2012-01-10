package org.sagebionetworks.repo.manager.backup.migration;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.sagebionetworks.repo.model.Annotations;
import org.sagebionetworks.repo.model.registry.MigrationSpecData.FieldMigrationSpecData;
import org.sagebionetworks.schema.ENCODING;
import org.sagebionetworks.schema.FORMAT;
import org.sagebionetworks.schema.TYPE;

public class MigrationHelper {
	public static void migrateToPrimary(Annotations srcAnnots, Annotations dstAnnots, FieldMigrationSpecData fmsd) {
		TYPE fieldType = fmsd.getDestSchema().getType();
		// First we must check the encoding
		ENCODING encoding = fmsd.getDestSchema().getContentEncoding();
		if(encoding != null){
			if(ENCODING.BINARY == encoding){
				Map<String, Collection<byte[]>> src = srcAnnots.getBlobAnnotations();
				if	(src != null){
					Collection<byte[]> valueToMigrate = src.remove(fmsd.getSrcFieldName());
					if(valueToMigrate != null){
						Map<String, Collection<byte[]>> dst = dstAnnots.getBlobAnnotations();
						dst.put(fmsd.getDestFieldName(), valueToMigrate);
					}
				}
			}else{
				throw new IllegalArgumentException("Support has not been added to migrate field TYPE="+fieldType.name()+" for ENCODING: "+encoding+", toRename: "+ fmsd.toString());
			}
		}else if(TYPE.STRING == fieldType){
			if(fmsd.getDestSchema().getFormat() != null){
				// This might be a date
				if(FORMAT.DATE_TIME == fmsd.getDestSchema().getFormat()){
					// Migrate the Date annotations.
					Map<String, Collection<Date>> src = srcAnnots.getDateAnnotations();
					if(src != null){
						Collection<Date> valueToMigrate = src.remove(fmsd.getSrcFieldName());
						if(valueToMigrate != null){
							Map<String, Collection<Date>> dst = dstAnnots.getDateAnnotations();
							dst.put(fmsd.getDestFieldName(), valueToMigrate);
						}
					}
				}else{
					throw new IllegalArgumentException("Support has not been added to migrate field TYPE="+fieldType.name()+" for FORMAT: "+fmsd.getDestSchema().getFormat()+", toRename: "+fmsd.toString());
				}
			}else{
				// Migrate the string annotations.
				Map<String, Collection<String>> src = srcAnnots.getStringAnnotations();
				if(src != null){
					Collection<String> valueToMigrate = src.remove(fmsd.getSrcFieldName());
					if(valueToMigrate != null){
						Map<String, Collection<String>> dst = dstAnnots.getStringAnnotations();
						dst.put(fmsd.getDestFieldName(), valueToMigrate);
					}
				}
			}
		}else if(TYPE.INTEGER == fieldType){
			// Migrate the string annotations.
			Map<String, Collection<Long>> src = srcAnnots.getLongAnnotations();
			if(src != null){
				Collection<Long> valueToMigrate = src.remove(fmsd.getSrcFieldName());
				if(valueToMigrate != null){
					Map<String, Collection<Long>> dst = dstAnnots.getLongAnnotations();
					dst.put(fmsd.getDestFieldName(), valueToMigrate);
				}
			}
		}else if(TYPE.NUMBER == fieldType){
			// Migrate the string annotations.
			Map<String, Collection<Double>> src = srcAnnots.getDoubleAnnotations();
			if(src != null){
				Collection<Double> valueToMigrate = src.remove(fmsd.getSrcFieldName());
				if(valueToMigrate != null){
					Map<String, Collection<Double>> dst = dstAnnots.getDoubleAnnotations();
					dst.put(fmsd.getDestFieldName(), valueToMigrate);
				}
			}
		}else{
			throw new IllegalArgumentException("Support has not been added to migrate field TYPE="+fieldType.name()+" for : "+fmsd.toString());
		}
		return;
	}
	
	public static void migrateToAdditionals(Annotations srcAnnots, Annotations dstAnnots, FieldMigrationSpecData fmsd) {
		if (fmsd.getDestType().equals("blob")) {
			Map<String, Collection<byte[]>> src = srcAnnots.getBlobAnnotations();
			if	(src != null){
				Collection<byte[]> valueToMigrate = src.remove(fmsd.getSrcFieldName());
				if(valueToMigrate != null){
					Map<String, Collection<byte[]>> dst = dstAnnots.getBlobAnnotations();
					dst.put(fmsd.getDestFieldName(), valueToMigrate);
				}
			}
		}
		if (fmsd.getDestType().equals("date")) {
			Map<String, Collection<Date>> src = srcAnnots.getDateAnnotations();
			if(src != null){
				Collection<Date> valueToMigrate = src.remove(fmsd.getSrcFieldName());
				if(valueToMigrate != null){
					Map<String, Collection<Date>> dst = dstAnnots.getDateAnnotations();
					dst.put(fmsd.getDestFieldName(), valueToMigrate);
				}
			}
		}
		if (fmsd.getDestType().equals("double")) {
			Map<String, Collection<Double>> src = srcAnnots.getDoubleAnnotations();
			if(src != null){
				Collection<Double> valueToMigrate = src.remove(fmsd.getSrcFieldName());
				if(valueToMigrate != null){
					Map<String, Collection<Double>> dst = dstAnnots.getDoubleAnnotations();
					dst.put(fmsd.getDestFieldName(), valueToMigrate);
				}
			}
		}
		if (fmsd.getDestType().equals("long")) {
			Map<String, Collection<Long>> src = srcAnnots.getLongAnnotations();
			if(src != null){
				Collection<Long> valueToMigrate = src.remove(fmsd.getSrcFieldName());
				if(valueToMigrate != null){
					Map<String, Collection<Long>> dst = dstAnnots.getLongAnnotations();
					dst.put(fmsd.getDestFieldName(), valueToMigrate);
				}
			}
		}
		if (fmsd.getDestType().equals("string")) {
			Map<String, Collection<String>> src = srcAnnots.getStringAnnotations();
			if(src != null){
				Collection<String> valueToMigrate = src.remove(fmsd.getSrcFieldName());
				if(valueToMigrate != null){
					Map<String, Collection<String>> dst = dstAnnots.getStringAnnotations();
					dst.put(fmsd.getDestFieldName(), valueToMigrate);
				}
			}
		}
		return;
	}
	
	public static void deleteFromAnnotations(Annotations srcAnnots, FieldMigrationSpecData fmsd) {
		if (fmsd.getSrcType().equals("blob")) {
			Map<String, Collection<byte[]>> src = srcAnnots.getBlobAnnotations();
			if	(src != null){
				Collection<byte[]> valueToMigrate = src.remove(fmsd.getSrcFieldName());
			}
		}
		if (fmsd.getSrcType().equals("date")) {
			Map<String, Collection<Date>> src = srcAnnots.getDateAnnotations();
			if(src != null){
				Collection<Date> valueToMigrate = src.remove(fmsd.getSrcFieldName());
			}
		}
		if (fmsd.getSrcType().equals("double")) {
			Map<String, Collection<Double>> src = srcAnnots.getDoubleAnnotations();
			if(src != null){
				Collection<Double> valueToMigrate = src.remove(fmsd.getSrcFieldName());
			}
		}
		if (fmsd.getSrcType().equals("long")) {
			Map<String, Collection<Long>> src = srcAnnots.getLongAnnotations();
			if(src != null){
				Collection<Long> valueToMigrate = src.remove(fmsd.getSrcFieldName());
			}
		}
		if (fmsd.getSrcType().equals("string")) {
			Map<String, Collection<String>> src = srcAnnots.getStringAnnotations();
			if(src != null){
				Collection<String> valueToMigrate = src.remove(fmsd.getSrcFieldName());
			}
		}
		return;
	}
	
}
