package com.myproject.manager.dao;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.apache.lucene.document.DateTools;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.jdbc.Expectations;
import org.hibernate.query.Query;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.exception.EmptyQueryException;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate5.HibernateTransactionManager;

import com.myproject.manager.api.DaoApp;
import com.myproject.manager.pojo.Product;
import com.myproject.manager.pojo.Shop;


/*
 * klasa uzywa metod interfejsu DaoApp do pobierania i przetwarzanie danych
 */
public class HibernateDao implements DaoApp{
	
	@Autowired
	private DefaultTableModel dataBaseTableModel;
	@Autowired
	//@Qualifier("infoTableModel")
	private DefaultTableModel infoTableModel;
	@Autowired
	private HibernateTransactionManager transactionManager;
	private Session session=null;
	private FullTextSession fullTextSession=null;
	private double  expensesSummary=0;
	
	public HibernateDao() {}
	
	
	
	public List<?> addRow(String shopName,String productName,double price,String productDescription,Date purchaseDate) {
		List<?> list = null;
		try {
			session = transactionManager.getSessionFactory().openSession();
			session.beginTransaction();
				Shop newShop = new Shop(shopName);
				Product newProduct = new Product(productName, price,productDescription,newShop,purchaseDate);
			
				session.persist(newProduct);
			
				Query<?> hibernateQuery = session.createQuery("FROM Product");
				list = hibernateQuery.list();
			
			session.getTransaction().commit();
			
		}catch(HibernateException e) {
			if(session.getTransaction()!=null) {
				session.getTransaction().rollback();
			}
			JOptionPane.showMessageDialog(null,e.getMessage()+"\n"+e.getCause(),"Błąd ",JOptionPane.ERROR_MESSAGE);
		}
		finally {
			if(session!=null) {
				session.close();
			}
			
		}
		return list;
	}
	
	public List<?> removeRow(long idProduct) {
		List<?> list =null;
		try {
			session = transactionManager.getSessionFactory().openSession();
			session.beginTransaction();
				String hql = "DELETE FROM Product WHERE idproduct = :id";
				Query<?> hibernateQuery =  session.createQuery(hql);
				hibernateQuery.setParameter("id", idProduct);
				hibernateQuery.executeUpdate();
				
				Query<?> hibernateQueryAll = session.createQuery("FROM Product");
				list = hibernateQueryAll.list();
				
				/*for(int i=0;i<dataBaseTableModel.getRowCount();i++) {
					if( (long)dataBaseTableModel.getValueAt(i, 0)==idProduct ) {
						dataBaseTableModel.removeRow(i);
						break;
					}					
				}*/
		
			session.getTransaction().commit();
		}catch(HibernateException e) {
			if(session.getTransaction()!=null) {
				session.getTransaction().rollback();
				
			}
			JOptionPane.showMessageDialog(null,e.getMessage()+"\n"+e.getCause(),"Błąd ",JOptionPane.ERROR_MESSAGE);
		}
		finally {
	
			if(session!=null) {
				session.close();
			}
			
		}
		return list;
	}

	public List<?> updateRow(String entity,String fieldName,Object fieldValue,String idName,Object idValue) {
		List<?> updatedList = null;
		try {
			session = transactionManager.getSessionFactory().openSession();
			session.beginTransaction();
				String hql = "UPDATE "+entity+
						 	" set "+fieldName+"= :fieldValue WHERE "+idName+" = :idValue";	
				Query<?> hibernateQuery = session.createQuery(hql);	
				hibernateQuery.setParameter("fieldValue", fieldValue);
				hibernateQuery.setParameter("idValue", idValue);
				hibernateQuery.executeUpdate();
				
				Query<?> hibernateQueryAll = session.createQuery("FROM Product");
				updatedList = hibernateQueryAll.list();
				
			session.getTransaction().commit();
		}catch(HibernateException e) {
			if(session.getTransaction()!=null) {
				session.getTransaction().rollback();
	
			}
			JOptionPane.showMessageDialog(null,e.getMessage()+"\n"+e.getCause(),"Błąd ",JOptionPane.ERROR_MESSAGE);
		}
		finally {
			if(session!=null) {
				session.close();
			}
			
		}
		return updatedList;
	}


	public List<?> getAllRows() {
		List<?> allRows = null;
		try {
			session = transactionManager.getSessionFactory().openSession();
			session.beginTransaction();
				Query<?> hibernateQuery =  session.createQuery("FROM Product");
				
				allRows = hibernateQuery.list();
				
			session.getTransaction().commit();
		}catch(HibernateException e) {
			if(session.getTransaction()!=null) {
				session.getTransaction().rollback();
			}
			JOptionPane.showMessageDialog(null,e.getMessage()+"\n"+e.getCause(),"Błąd ",JOptionPane.ERROR_MESSAGE);
		}
		finally {
			if(session!=null) {
				session.close();
			}
			
			
		}
		return allRows;
	}


	@Override
	public List<?> search(String search,double minPrice,double maxPrice,Date minDate,Date maxDate) {
		List<?> searchList=null;
		try {
			session = transactionManager.getSessionFactory().openSession();	
			fullTextSession = Search.getFullTextSession(session);
			fullTextSession.createIndexer().startAndWait();
			fullTextSession.beginTransaction();		
				QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Product.class).get();
				org.apache.lucene.search.Query luceneQuery;
				if(search.isEmpty()) {
					luceneQuery = queryBuilder
							.bool()
								.must(queryBuilder
										.range()
										.onField("price").from(minPrice).to(maxPrice)
										.createQuery()
										)
								.must(queryBuilder
										.range()
										.onField("purchaseDate").from(minDate).to(maxDate)
										.createQuery()
										)
								.createQuery();  		
				}
				else {
					luceneQuery = queryBuilder
						.bool()
							.must(queryBuilder
									.range()
									.onField("price").from(minPrice).to(maxPrice)
									.createQuery()
									)
							.must(queryBuilder
									.range()
									.onField("purchaseDate").from(minDate).to(maxDate)
									.createQuery()
									)
							.must(queryBuilder
									.keyword()
									.onFields("name","description","shop.nameShop")
									.matching(search)
									.createQuery()
									)	
							.createQuery();
				}
			
				Query<?> hibernateQuery = fullTextSession.createFullTextQuery(luceneQuery, Product.class);
				
				searchList = hibernateQuery.list();
							
			fullTextSession.getTransaction().commit();
			
		}catch(HibernateException e) {
			if(fullTextSession.getTransaction()!=null) {
				fullTextSession.getTransaction().rollback();
			}
			JOptionPane.showMessageDialog(null,e.getMessage()+"\n"+e.getCause(),"Błąd ",JOptionPane.ERROR_MESSAGE);
			
			
		}catch(InterruptedException e) {
			if(fullTextSession.getTransaction()!=null) {
				fullTextSession.getTransaction().rollback();
			}
			JOptionPane.showMessageDialog(null,e.getMessage()+"\n"+e.getCause(),"Błąd ",JOptionPane.ERROR_MESSAGE);
			
		}catch(EmptyQueryException e) {
			if(fullTextSession.getTransaction()!=null) {
				fullTextSession.getTransaction().rollback();
			}
			
			while(dataBaseTableModel.getRowCount()!=0) {
				dataBaseTableModel.removeRow(dataBaseTableModel.getRowCount()-1);
			}
			JOptionPane.showMessageDialog(null,"Wyszukiwana fraza powinna mieć min 3 znaki","Błąd wyszukiwania",JOptionPane.PLAIN_MESSAGE);
		}
		finally {
			if(fullTextSession!=null) {
				fullTextSession.close();
			}
			
			
		}
		return searchList;
	}

	@Override
	public int countRows() {
		int rowCount=0;
		try {
			session = transactionManager.getSessionFactory().openSession();
			session.beginTransaction();
				Query<?> hibernateQuery = session.createQuery("FROM Product");
				rowCount = hibernateQuery.list().size();
			session.getTransaction().commit();		
		}catch(HibernateException e) {
			if(session.getTransaction()!=null) {
				session.getTransaction().rollback();
				//System.out.print("\nblad Massage: "+e.getMessage()+"\nCause: "+e.getCause());
			}
			JOptionPane.showMessageDialog(null,e.getMessage()+"\n"+e.getCause(),"Błąd ",JOptionPane.ERROR_MESSAGE);
		}
		finally {
			if(session!=null) {
				session.close();
			}
		}
		return rowCount;
	}

	@Override
	public double getExpensesSummary() {
		return expensesSummary;
	}

	
	

	
	
}
