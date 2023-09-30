package petdori.apiserver.global.config;

import java.util.List;

public class ReadOnlyDataSourceLoadBalancer<T> {
    private List<T> readOnlyDataSourceLookupKeys;
    private int index = 0;

    public void setReadOnlyDataSourceLookupKeys(List<T> readOnlyDataSourceLookupKeys) {
        this.readOnlyDataSourceLookupKeys = readOnlyDataSourceLookupKeys;
    }

    public T getReadOnlyDataSourceLookupKey() {
        if (index >= readOnlyDataSourceLookupKeys.size()) {
            index = 0;
        }
        return readOnlyDataSourceLookupKeys.get(index++);
    }
}
