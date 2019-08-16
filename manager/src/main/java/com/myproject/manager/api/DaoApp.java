package com.myproject.manager.api;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;



public interface DaoApp {
	List<?> search(String search,double minPrice,double maxPrice,Date minDate,Date maxDate);
	List<?>  getAllRows();
	List<?> addRow(String shopName,String productName,double price,String productDescription,Date date);
	List<?> updateRow(String entity,String fieldName,Object fieldValue,String idName,Object idValue);
	List<?> removeRow(long id);
	
}
