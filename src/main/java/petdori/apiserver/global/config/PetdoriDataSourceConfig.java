package petdori.apiserver.global.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PetdoriDataSourceConfig {
    private final PetdoriDataSourceProperties petdoriDataSourceProperties;

    @Bean
    public DataSource routingDataSource() {
        ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();
        PetdoriDataSourceProperties.Write write = petdoriDataSourceProperties.getWrite();
        DataSource writeDataSource = createDataSource(write.getUrl());

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(write.getName(), writeDataSource);

        List<PetdoriDataSourceProperties.Read> reads = petdoriDataSourceProperties.getReads();
        for (PetdoriDataSourceProperties.Read read : reads) {
            dataSourceMap.put(read.getName(), createDataSource(read.getUrl()));
        }

        routingDataSource.setDefaultTargetDataSource(writeDataSource);
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.afterPropertiesSet();

        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    private DataSource createDataSource(String url) {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(petdoriDataSourceProperties.getDriverClassName());
        hikariDataSource.setUsername(petdoriDataSourceProperties.getUsername());
        hikariDataSource.setPassword(petdoriDataSourceProperties.getPassword());
        hikariDataSource.setJdbcUrl(url);

        return hikariDataSource;
    }

}
