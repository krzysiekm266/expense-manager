package com.myproject.manager.pojo;

import java.util.Date;

import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.ngram.NGramFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

@AnalyzerDef(name="ngrams",
	tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
	filters = {
		@TokenFilterDef(factory = StandardFilterFactory.class),
		@TokenFilterDef(factory = LowerCaseFilterFactory.class),
		@TokenFilterDef(factory = StopFilterFactory.class),
		@TokenFilterDef(factory = NGramFilterFactory.class,
			params = { 
				@Parameter(name="minGramSize",value="3"),
				@Parameter(name="maxGramSize",value="3")
			}
		)
			
	}
)

@Entity
@Indexed
public class Product {
	@Id
	@GeneratedValue
	private long idproduct;
	
	@Column
	@Field(analyzer = @Analyzer(definition = "ngrams"))
	private String name;
	
	@Column
	@Field( analyze=Analyze.NO)
	private double price;
	
	@Column
	@Field(analyzer = @Analyzer(definition="ngrams"))
	private String description;
	
	@Column(name="PurchaseDate")
	@Field(analyze=Analyze.NO)
	Date purchaseDate;
	
	@IndexedEmbedded(depth=1)
	private Shop shop;
	
	public Product() {}
	
	public Product( String name, double price,String description,Shop shop,Date purchaseDate) {
		super();
		
		this.name = name;
		this.price = price;
		this.description = description;
		this.shop = shop;
		this.purchaseDate = purchaseDate;
		
	}

	public long getId() {
		return idproduct;
	}
	public void setId(long idproduct) {
		this.idproduct = idproduct;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	@Override
	public String toString() {
		return "Product [idproduct=" + idproduct + ", name=" + name + ", price=" + price + ", description="
				+ description + ", purchaseDate=" + purchaseDate + ", shop=" + shop + "]";
	}

	

	
	
	
}
