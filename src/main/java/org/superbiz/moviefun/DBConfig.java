package org.superbiz.moviefun;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.superbiz.moviefun.datasources.DataSourceConfig;

import javax.sql.DataSource;

@Configuration
public class DBConfig {

    @Bean("album-config")
    @ConfigurationProperties("moviefun.datasources.albums")
    public DataSourceConfig getAlbumDataSourceConfig() {
        return new DataSourceConfig();
    }

    @Bean("albums-datasource")
    public DataSource albumsDataSource(
            @Qualifier("album-config") DataSourceConfig dataSourceConfig
    ) {
        return getHikariDataSource(dataSourceConfig);
    }

    @Bean("albums-entity-manager-factory-bean")
    public LocalContainerEntityManagerFactoryBean albumsLocalContainerEntityManagerFactoryBean(
            @Qualifier("albums-datasource") DataSource dataSource,
            JpaVendorAdapter jpaVendorAdapter
    ) {
        return setupEntityMangerFactoryBean(dataSource, jpaVendorAdapter, "albums-persistence");
    }

    @Bean("albums-transaction-manager")
    public PlatformTransactionManager albumsPlatformTransactionManager(
            @Qualifier("albums-entity-manager-factory-bean") LocalContainerEntityManagerFactoryBean entityManagerFactoryBean
    ) {
        return new JpaTransactionManager(entityManagerFactoryBean.getObject());
    }

    @Bean("movie-config")
    @ConfigurationProperties("moviefun.datasources.movies")
    public DataSourceConfig getMovieDataSourceConfig() {
        return new DataSourceConfig();
    }

    @Bean("movies-datasource")
    public DataSource moviesDataSource(
            @Qualifier("movie-config") DataSourceConfig dataSourceConfig
    ) {
        return getHikariDataSource(dataSourceConfig);
    }
    
    @Bean("movies-entity-manager-factory-bean")
    public LocalContainerEntityManagerFactoryBean moviesLocalContainerEntityManagerFactoryBean(
            @Qualifier("movies-datasource") DataSource dataSource,
            JpaVendorAdapter jpaVendorAdapter
    ) {
        return setupEntityMangerFactoryBean(dataSource, jpaVendorAdapter, "movies-persistence");
    }

    @Bean("movies-transaction-manager")
    public PlatformTransactionManager moviesPlatformTransactionManager(
            @Qualifier("movies-entity-manager-factory-bean") LocalContainerEntityManagerFactoryBean entityManagerFactoryBean
    ) {
        return new JpaTransactionManager(entityManagerFactoryBean.getObject());
    }

    @Bean
    public JpaVendorAdapter hibernateJpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        return hibernateJpaVendorAdapter;
    }

    private LocalContainerEntityManagerFactoryBean setupEntityMangerFactoryBean(
            DataSource dataSource,
            JpaVendorAdapter jpaVendorAdapter,
            String persistenceUnitName
    ) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun");
        localContainerEntityManagerFactoryBean.setPersistenceUnitName(persistenceUnitName);
        return localContainerEntityManagerFactoryBean;
    }

    private DataSource getHikariDataSource(@Qualifier("album-config") DataSourceConfig dataSourceConfig) {
        HikariDataSource hikariDataSource = new HikariDataSource();

        DataSource innerDatasource = DataSourceBuilder
                .create()
                .password(dataSourceConfig.getPassword())
                .username(dataSourceConfig.getUsername())
                .url(dataSourceConfig.getUrl())
                .build();

        hikariDataSource.setDataSource(innerDatasource);
        return hikariDataSource;
    }

}
