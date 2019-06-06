package com.myproject.manager.pojo;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;

@Embeddable
public class Shop {
	
	@Column
	@Field(analyzer = @Analyzer(definition="ngrams"))
	private String nameShop;
	
	public Shop() {}
	public Shop(String name) {
		super();
		this.nameShop = name;
	}
	
	public String getName() {
		return nameShop;
	}
	public void setName(String name) {
		this.nameShop = name;
	}
	@Override
	public String toString() {
		return "Shop [nameShop=" + nameShop + "]";
	}
	
	
}
