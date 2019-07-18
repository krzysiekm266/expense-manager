package com.myproject.manager.dao;


import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.exception.EmptyQueryException;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import com.myproject.manager.api.Dao;
import com.myproject.manager.pojo.Product;
import com.myproject.manager.pojo.Shop;


/*
 * klasa uzywa metod interfejsu Dao do pobierania i przetwarzanie danych
 */
public class HibernateDao implements Dao{
	
	@Autowired
	private DefaultTableModel jTableModel;
	@Autowired
	private HibernateTransactionManager transactionManager;
	private Session session=null;
	private FullTextSession fullTextSession=null;
	private double  expensesSummary=0;
	
	public HibernateDao() {}
	
	private void  setJTableModelDataFromQueryResult(ListIterator<?> listIterator) {
		//wyczysc model
		while(jTableModel.getRowCount()!=0) {
			jTableModel.removeRow(jTableModel.getRowCount()-1);
		}
		
		this.expensesSummary=0;
		//wypelnij model danymi
		while(listIterator.hasNext()) {
			Product product = (Product)listIterator.next();
			jTableModel.addRow(new Object[] {
				product.getId(),
				product.getName(),
				product.getPrice(),
				product.getDescription(),
				product.getPurchaseDate(),
				product.getShop().getName()
			});
			this.expensesSummary += product.getPrice();
			
		}
	}
	public void addRow(String shopName,String productName,double price,String productDescription,Date purchaseDate) {
		try {
			session = transactionManager.getSessionFactory().openSession();
			session.beginTransaction();
			Shop newShop = new Shop(shopName);
			Product newProduct = new Product(productName, price,productDescription,newShop,purchaseDate);
			
			session.persist(newProduct);
	
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
		
	}
	
	public void removeRow(long idProduct) {
		try {
			session = transactionManager.getSessionFactory().openSession();
			session.beginTransaction();
				String hql = "DELETE FROM Product WHERE idproduct = :id";
				Query<?> hibernateQuery =  session.createQuery(hql);
				hibernateQuery.setParameter("id", idProduct);
				hibernateQuery.executeUpdate();
			
				for(int i=0;i<jTableModel.getRowCount();i++) {
					if( (long)jTableModel.getValueAt(i, 0)==idProduct ) {
						jTableModel.removeRow(i);
						break;
					}					
				}
		
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
		
	}

	public void updateRow(String entity,String fieldName,Object fieldValue,String idName,Object idValue) {
		try {
			session = transactionManager.getSessionFactory().openSession();
			session.beginTransaction();
				String hql = "UPDATE "+entity+
						 	" set "+fieldName+"= :fieldValue WHERE "+idName+" = :idValue";	
				Query<?> hibernateQuery = session.createQuery(hql);	
				hibernateQuery.setParameter("fieldValue", fieldValue);
				hibernateQuery.setParameter("idValue", idValue);
				int result = hibernateQuery.executeUpdate();
				System.out.println("Rows affected: " + result);	
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
		
	}


	public int showRows() {
		int resultCount=0;
		try {
			session = transactionManager.getSessionFactory().openSession();
			session.beginTransaction();
				Query<?> hibernateQuery =  session.createQuery("FROM Product");
				List<?> list = hibernateQuery.list();
				resultCount = list.size();
				ListIterator<?> iter = list.listIterator();
				setJTableModelDataFromQueryResult(iter);	
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
		return resultCount;
	}


	@Override
	public int search(String search,double minPrice,double maxPrice,Date minDate,Date maxDate) {
		int resultCount=0;
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
			
				org.hibernate.query.Query<?> hibernateQuery = fullTextSession.createFullTextQuery(luceneQuery, Product.class);
				List<?> list = hibernateQuery.list();
				resultCount = list.size();
				ListIterator<?> iter = list.listIterator();
				setJTableModelDataFromQueryResult(iter);
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
			
			while(jTableModel.getRowCount()!=0) {
				jTableModel.removeRow(jTableModel.getRowCount()-1);
			}
			JOptionPane.showMessageDialog(null,"Wyszukiwana fraza powinna mieć min 3 znaki","Błąd wyszukiwania",JOptionPane.PLAIN_MESSAGE);
		}
		finally {
			if(fullTextSession!=null) {
				fullTextSession.close();
			}
			
			
		}
		return resultCount;
	}

	@Override
	public int rowsCount() {
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
