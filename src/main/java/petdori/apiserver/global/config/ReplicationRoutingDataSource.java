package petdori.apiserver.global.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.util.List;
import java.util.Map;

public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {
    private static final String READ = "read";
    private static final String WRITE = "write";
    private ReadOnlyDataSourceLoadBalancer<String> loadBalancer = new ReadOnlyDataSourceLoadBalancer<>();

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        super.setTargetDataSources(targetDataSources);
        List<String> readOnlyDataSourceLookupKeys = targetDataSources.keySet()
                .stream()
                .map(String::valueOf)
                .filter(lookupKey -> lookupKey.contains(READ)).toList();
        loadBalancer.setReadOnlyDataSourceLookupKeys(readOnlyDataSourceLookupKeys);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly()
                ? loadBalancer.getReadOnlyDataSourceLookupKey()
                : WRITE;
    }
}
