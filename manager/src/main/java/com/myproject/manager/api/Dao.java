package com.myproject.manager.api;

import java.util.Date;



public interface Dao {
	void addRow(String shopName,String productName,double price,String productDescription,Date date);
	void removeRow(long id);
	void updateRow(String entity,String fieldName,Object fieldValue,String idName,Object idValue);
	int  showRows();
	int  rowsCount();
	int  search(String search,double minPrice,double maxPrice,Date minDate,Date maxDate);
	double getExpensesSummary();

	
	


}
