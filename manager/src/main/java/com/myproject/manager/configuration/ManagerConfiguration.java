package com.myproject.manager.configuration;

import java.util.Properties;
import javax.swing.table.DefaultTableModel;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.myproject.manager.ManagerJTableModel;
import com.myproject.manager.dao.HibernateDao;

@Configuration
@EnableTransactionManagement
public class ManagerConfiguration {
	
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
        transactionManager.setSessionFactory(sessionFactory().getObject());
        
        return transactionManager;
    }
  
    @Bean
    public BasicDataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        //dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/expenses?serverTimezone=UTC");
        dataSource.setUsername("krzysiek");
        dataSource.setPassword("12345678");
        
 
        return dataSource;
    }
 
    @Bean
    public HibernateDao hibernateDao() { 
    	
    	return new HibernateDao();
    }
    
    @Bean
    public DefaultTableModel jTableModel() {
    	ManagerJTableModel tableModel = new ManagerJTableModel();
    	tableModel.addColumn("id");
    	tableModel.addColumn("Nazwa produktu");
    	tableModel.addColumn("Cena");
    	tableModel.addColumn("Opis produktu");
    	tableModel.addColumn("Data zakupu");
    	tableModel.addColumn("Nazwa sklepu");
    	return tableModel;
    }
    
   
    
}
