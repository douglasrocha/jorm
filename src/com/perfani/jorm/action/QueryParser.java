package com.perfani.jorm.action;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.perfani.jorm.annotations.Column;
import com.perfani.jorm.annotations.Entity;
import com.perfani.jorm.annotations.PrimaryKey;
import com.perfani.jorm.exceptions.InvalidEntityException;

public class QueryParser
{
	private static boolean isEntity(Class<? extends Object> obj)
	{
		return obj.isAnnotationPresent(Entity.class);
	}
	
	private static String parseValueToCompatibleQueryString(Object fieldValue)
	{
		if (fieldValue instanceof String)
		{
			return String.format("\"%s\"", fieldValue);				
		}
		else if (fieldValue instanceof Date)
		{
			Date date = (Date) fieldValue;
			DateFormat df = DateFormat.getDateInstance();
			return String.format("\"%s\"", df.format(date));
		}
		else
		{
			return fieldValue.toString();
		}
	}
	
	private static String getEntityName(Object obj) 
			throws InvalidEntityException
	{
		Class<? extends Object> objClass = obj.getClass();
		
		if (!isEntity(objClass))
		{
			throw new InvalidEntityException();		
		}
		
		Annotation annotation = objClass.getAnnotation(Entity.class);
		Entity entity = (Entity) annotation;
		
		return entity.name();
	}
	
	private static String getColumnsSeparatedByCommas(Object obj) 
			throws InvalidEntityException
	{
		ArrayList<String> columnNames = new ArrayList<String>();
		Class<? extends Object> objClass = obj.getClass();
		
		// If entity annotation is not present, returns null
		if (!isEntity(objClass))
		{
			throw new InvalidEntityException();
		}
		
		// Get all attributes of the class
		Field[] fields = objClass.getDeclaredFields();
		
		for (Field currentField : fields)
		{
            if (currentField.isAnnotationPresent(Column.class))
            {
            	Annotation annotation = currentField.getAnnotation(Column.class);
            	Column column = (Column) annotation;
            	String colName = column.name();
            	
            	columnNames.add(colName);
            }
		}
		
		return String.join(", ", columnNames);
	}

	private static String getPrimaryKeyConditions(Object obj) 
			throws IllegalArgumentException, IllegalAccessException, InvalidEntityException
	{
		ArrayList<Field> listPrimaryKeys = new ArrayList<>();
		ArrayList<String> listPrimaryKeysString = new ArrayList<>();
		Class<? extends Object> objClass = obj.getClass();
		
		// If entity annotation is not present, returns null
		if (!isEntity(objClass))
		{
			throw new InvalidEntityException();
		}
		
		// Get all attributes of the class
		Field[] fields = objClass.getDeclaredFields();
		
		// Get all fields that are columns and primary keys
		for (Field currentField : fields)
		{
            if (currentField.isAnnotationPresent(Column.class) 
            	&& currentField.isAnnotationPresent(PrimaryKey.class))
            {
            	listPrimaryKeys.add(currentField);
            }
		}
		
		for (Field currentKey : listPrimaryKeys)
		{
			Annotation colAnnotation = currentKey.getAnnotation(Column.class);
			Column column = (Column) colAnnotation;
			String colName = column.name();
			
			currentKey.setAccessible(true);
			String fieldValue = currentKey.get(obj).toString();
			
			listPrimaryKeysString.add(String.format("%s = %s", colName, fieldValue));
		}
		
		return String.join(" AND ", listPrimaryKeysString);
	}
	
	private static String getColumnsValuesSeparatedByCommas(Object obj, boolean isInsert)
			throws IllegalArgumentException, IllegalAccessException, InvalidEntityException
	{
		ArrayList<String> columnValues = new ArrayList<String>();
		Class<? extends Object> objClass = obj.getClass();
		
		// If entity annotation is not present, returns null
		if (!isEntity(objClass))
		{
			throw new InvalidEntityException();
		}
		
		// Get all attributes of the class
		Field[] fields = objClass.getDeclaredFields();
		
		for (Field currentField : fields)
		{
			PrimaryKey pk = null;
			
			if (!currentField.isAnnotationPresent(Column.class))
			{
				continue;
			}
			
			currentField.setAccessible(true);
			Object fieldValue = currentField.get(obj);
			
			if (currentField.isAnnotationPresent(PrimaryKey.class))
			{
				pk = (PrimaryKey) currentField.getAnnotation(PrimaryKey.class);
				
				columnValues.add(pk.autoIncrement() && isInsert ? " " 
							: parseValueToCompatibleQueryString(fieldValue));
				
				continue;
			}			
			
			columnValues.add(parseValueToCompatibleQueryString(fieldValue));
		}
		
		return String.join(", ", columnValues);
	}
	
	private static String getColumnsEqualToValuesSeparatedByCommas(Object obj) 
			throws IllegalArgumentException, IllegalAccessException, InvalidEntityException
	{
		ArrayList<String> listKVP = new ArrayList<>();
		Class<? extends Object> objClass = obj.getClass();
		
		// If entity annotation is not present, returns null
		if (!isEntity(objClass))
		{
			throw new InvalidEntityException();
		}
		
		// Get all attributes of the class
		Field[] fields = objClass.getDeclaredFields();
		
		// Get all fields that are columns and primary keys
		for (Field currentField : fields)
		{
            if (currentField.isAnnotationPresent(Column.class))
            {
            	Annotation colAnnotation = currentField.getAnnotation(Column.class);
    			Column column = (Column) colAnnotation;
    			String colName = column.name();
    			
    			currentField.setAccessible(true);
    			Object fieldValue = currentField.get(obj);
    			String stringValue = parseValueToCompatibleQueryString(fieldValue);
    			
    			listKVP.add(String.format("%s = %s", colName, stringValue));
            }
		}
		
		return String.join(", ", listKVP);
	}
	
	public static String getSelectByPrimaryKeysQuery(Object obj) 
			throws IllegalArgumentException, IllegalAccessException, InvalidEntityException
	{
		return String.format
			(
				"SELECT %s FROM %s WHERE %s", 
				getColumnsSeparatedByCommas(obj),
				getEntityName(obj),
				getPrimaryKeyConditions(obj)
			);
	}
	
	public static String getSelectAllQuery(Object obj) 
			throws InvalidEntityException
	{
		return String.format("SELECT %s FROM %s", 
							 getColumnsSeparatedByCommas(obj),
							 getEntityName(obj));
	}
	
	public static String getInsertIntoQuery(Object obj) 
			throws IllegalArgumentException, IllegalAccessException, InvalidEntityException
	{
		return String.format("INSERT INTO %s (%s) VALUES (%s)",
							 getEntityName(obj),
							 getColumnsSeparatedByCommas(obj),
							 getColumnsValuesSeparatedByCommas(obj, true));
	}
	
	public static String getUpdateByIdQuery(Object obj) 
			throws IllegalArgumentException, IllegalAccessException, InvalidEntityException
	{
		return String.format("UPDATE %s SET %s WHERE %s", 
							 getEntityName(obj),
							 getColumnsEqualToValuesSeparatedByCommas(obj),
							 getPrimaryKeyConditions(obj));
	}
	
	public static String getDeleteByPrimaryKeyQuery(Object obj) 
			throws IllegalArgumentException, IllegalAccessException, InvalidEntityException
	{
		return String.format("DELETE FROM %s WHERE %s", 
							 getEntityName(obj),
							 getPrimaryKeyConditions(obj)); 
	}
}
