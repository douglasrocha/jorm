package com.perfani.jorm.exceptions;

public class InvalidEntityException extends Exception
{
    private static final long serialVersionUID = -7613929139064850016L;

    public InvalidEntityException()
    {
    	super("Not a valid entity object");
    }
}
