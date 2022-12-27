package br.com.hsneves.certi.test.config;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import br.com.hsneves.certi.test.utils.Constants;
import br.com.hsneves.certi.test.utils.DatabaseConstants;

/**
 * 
 * @author Henrique Neves
 *
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = DatabaseConstants.REPOSITORY_PACKAGE, entityManagerFactoryRef = DatabaseConstants.ENTITY_MANAGER_FACTORY, transactionManagerRef = DatabaseConstants.TRANSACTION_MANAGER)
@Profile(Constants.PROFILE_PRODUCTION)
public class CertiTestJpaConfig {
	
	@Value("${db.certi.test.hibernate.dialect:org.sqlite.hibernate.dialect.SQLiteDialect}")
	private String hibernateDialect;

	@Value("${db.certi.test.jdbc.driver.class:org.sqlite.JDBC}")
	private String jdbcDriverClass;

	@Value("${db.certi.test.jdbc.url:jdbc:sqlite:certi-test-pokemon.db}")
	private String jdbcUrl;

	@Value("${db.certi.test.username:}")
	private String username;

	@Value("${db.certi.test.password:}")
	private String password;
	
	@Value("${db.certi.test.jpa.show-sql:false}")
	private boolean showSql;

	@Value("${db.certi.test.jpa.format-sql:false}")
	private boolean formatSql;

	@Value("${db.certi.test.jpa.hibernate.ddl-auto:update}")
	private String hibernateDdlAuto;
	
	@Bean(name = DatabaseConstants.DATA_SOURCE)
	@Primary
	public DataSource dataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(this.jdbcUrl);
		dataSource.setDriverClassName(this.jdbcDriverClass);
		dataSource.setUsername(this.username);
		dataSource.setPassword(this.password);
		return dataSource;
	}

	@Bean(name = DatabaseConstants.ENTITY_MANAGER_FACTORY)
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier(DatabaseConstants.DATA_SOURCE) DataSource dataSource) {
		HashMap<String, Object> hibernateProperties = new HashMap<>();
		hibernateProperties.put(DatabaseConstants.HIBERNATE_HBM2DDL_AUTO, this.hibernateDdlAuto);
		hibernateProperties.put(DatabaseConstants.HIBERNATE_DIALECT, this.hibernateDialect);
		hibernateProperties.put(DatabaseConstants.HIBERNATE_SHOW_SQL, this.showSql);
		hibernateProperties.put(DatabaseConstants.HIBERNATE_FORMAT_SQL, this.formatSql);
		return builder.dataSource(dataSource).packages(DatabaseConstants.PACKAGES_TO_SCAN).properties(hibernateProperties).persistenceUnit(DatabaseConstants.PERSISTENCE_UNIT).build();
	}

	@Bean
	@Primary
	public PlatformTransactionManager transactionManager(@Qualifier(DatabaseConstants.ENTITY_MANAGER_FACTORY) EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
}
