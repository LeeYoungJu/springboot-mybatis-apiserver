package kr.co.meatmatch.config.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "kr.co.meatmatch.mapper.nicedata"
            , sqlSessionFactoryRef = "thirdSqlSessionFactory"
            , sqlSessionTemplateRef = "thirdSqlSessionTemplate")
public class NiceDataDBConfig {
    @Bean
    @Qualifier("thirdHikariConfig")
    @ConfigurationProperties(prefix = "spring.datasource.hikari.third")
    public HikariConfig thirdHikariConfig() {
        return new HikariConfig();
    }
    @Bean
    @Qualifier("thirdDataSource")
    public DataSource thirdDataSource() throws Exception {
        return new HikariDataSource(thirdHikariConfig());
    }

    @Bean(name = "thirdSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier("thirdDataSource") DataSource dataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis/mybatis-config.xml"));
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mapper/nicedata/*.xml"));

        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "thirdSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("thirdSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
