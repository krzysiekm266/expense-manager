package com.myproject.manager.configuration;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Properties;
import javax.swing.table.DefaultTableModel;

import org.apache.lucene.document.DateTools;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.asm.Label;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import com.myproject.manager.ManagerJTableModel;

import com.myproject.manager.api.DaoApp;
import com.myproject.manager.dao.HibernateDao;

@Configuration
public class ManagerConfiguration {
	 @Bean
	 public BasicDataSource dataSource() {
	        BasicDataSource dataSource = new BasicDataSource();
	        //dataSource.setDriverClassName("com.mysql.jdbc.Driver");
	        dataSource.setUrl("jdbc:mysql://localhost/expenses?serverTimezone=UTC");
	        dataSource.setUsername("expensemanager");
	        dataSource.setPassword("12345678");
	        
	 
	        return dataSource;
	    }
	@Bean
    public LocalSessionFactoryBean sessionFactory() {
	
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setPackagesToScan( "com.myproject.manager.pojo"  );
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setHibernateProperties(hibernateProperties());
        
       
       
        return sessionFactory;
    }
	
	
	private final Properties hibernateProperties() {
	        Properties hibernateProperties = new Properties();
	        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update");
	        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
	        hibernateProperties.setProperty("hibernate.show_sql", "true");
	        hibernateProperties.setProperty("hibernate.id.new_generator_mappings", "false");
	        //for hibernate search
	        hibernateProperties.setProperty("hibernate.search.default.directory_provider", "filesystem");
	        hibernateProperties.setProperty("hibernate.search.default.indexBase", "target/luceneIndex");
	        hibernateProperties.setProperty("hibernate.search.lucene_version", "LATEST");
	 
	        return hibernateProperties;
	}
 
	
    @Bean
    public PlatformTransactionManager hibernateTransactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setDataSource(dataSource());
        transactionManager.setSessionFactory(sessionFactory().getObject());
       
        
        return transactionManager;
    }
  
  
 

    
    @Bean
    public DefaultTableModel dataBaseTableModel() {
    	ManagerJTableModel dataBaseTableModel = new ManagerJTableModel();
    	dataBaseTableModel.addColumn("id");
    	dataBaseTableModel.addColumn("Nazwa produktu");
    	dataBaseTableModel.addColumn("Cena");
    	dataBaseTableModel.addColumn("Opis produktu");
    	dataBaseTableModel.addColumn("Data zakupu");
    	dataBaseTableModel.addColumn("Nazwa sklepu");
    	return dataBaseTableModel;
    }
    
    @Bean 
    public DefaultTableModel infoTableModel() {
    	ManagerJTableModel infoTableModel = new ManagerJTableModel();
    	
    	String[] months = {"","styczeń","luty","marzec","kwiecień","maj","czerwiec","lipiec","sierpień","wrzesień","pazdziernik","listopad","grudzień","Podsumowanie roczne"};
    	infoTableModel.setColumnIdentifiers(months);
    	
    	
    	return infoTableModel;
    }
	
   @Bean
   public DaoApp hibernateDao() {
	   return new HibernateDao();
   }
    
}
