package com.perfani.jorm.test;

import java.util.Calendar;

import com.perfani.jorm.action.QueryParser;
import com.perfani.jorm.exceptions.InvalidEntityException;

public class MainClass
{

	public static void main(String[] args)
	{		
		ExampleEntity example = new ExampleEntity();
		example.setId(1);
		example.setUsername("lala");
		example.setPassword("woot");
		example.setAge(18);
		example.setMoneyInWalet(1505.13);
		example.setBirthDate(Calendar.getInstance().getTime());
		
		try
        {
	        System.out.println(QueryParser.getSelectByPrimaryKeysQuery(example));
	        System.out.println(QueryParser.getSelectAllQuery(example));
	        System.out.println(QueryParser.getInsertIntoQuery(example));
	        System.out.println(QueryParser.getUpdateByIdQuery(example));
	        System.out.println(QueryParser.getDeleteByPrimaryKeyQuery(example));   
        }
        catch (IllegalArgumentException | IllegalAccessException | InvalidEntityException e)
        {
	        e.printStackTrace();
        }
	}

}
