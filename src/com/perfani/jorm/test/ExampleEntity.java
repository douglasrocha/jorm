package com.perfani.jorm.test;

import java.util.Date;

import com.perfani.jorm.annotations.Column;
import com.perfani.jorm.annotations.Entity;
import com.perfani.jorm.annotations.PrimaryKey;

@Entity(name = "user")
public class ExampleEntity
{
	@PrimaryKey(autoIncrement = true)
	@Column(name = "id")
	private int id;
	
	@Column(name = "user")
	private String username;
	
	@Column(name = "pass")
	private String password;

	@Column(name = "age")
	private int age;
	
	@Column(name = "amountMoney")
	private double moneyInWalet;
	
	@Column(name = "birthDate")
	private Date birthDate;
	
	/**
	 * @return the id
	 */
    public int getId()
    {
	    return id;
    }

	/**
	 * @param id the id to set
	 */
    public void setId(int id)
    {
	    this.id = id;
    }

	/**
	 * @return the username
	 */
    public String getUsername()
    {
	    return username;
    }

	/**
	 * @param username the username to set
	 */
    public void setUsername(String username)
    {
	    this.username = username;
    }

	/**
	 * @return the password
	 */
    public String getPassword()
    {
	    return password;
    }

	/**
	 * @param password the password to set
	 */
    public void setPassword(String password)
    {
	    this.password = password;
    }

	/**
	 * @return the age
	 */
    public int getAge()
    {
	    return age;
    }

	/**
	 * @param age the age to set
	 */
    public void setAge(int age)
    {
	    this.age = age;
    }

	/**
	 * @return the moneyInWalet
	 */
    public double getMoneyInWalet()
    {
	    return moneyInWalet;
    }

	/**
	 * @param moneyInWalet the moneyInWalet to set
	 */
    public void setMoneyInWalet(double moneyInWalet)
    {
	    this.moneyInWalet = moneyInWalet;
    }

	/**
	 * @return the birthDate
	 */
    public Date getBirthDate()
    {
	    return birthDate;
    }

	/**
	 * @param birthDate the birthDate to set
	 */
    public void setBirthDate(Date birthDate)
    {
	    this.birthDate = birthDate;
    }
}
