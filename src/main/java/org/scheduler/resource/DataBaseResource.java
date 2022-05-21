package org.scheduler.resource;

import org.springframework.core.io.AbstractFileResolvingResource;
import org.springframework.core.io.Resource;

import javax.activation.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class DataBaseResource extends AbstractFileResolvingResource {

    private String sql;
    private String keyColumn;
    private String valueColumn;
    private String type;
    private DataSource dataSource;

   /* public DataBaseResource(String sql, String keyColumn, String valueColumn, String type, DataSource dataSource) {
        this.sql = sql;
        this.keyColumn = keyColumn;
        this.valueColumn = valueColumn;
        this.type = type;
        this.dataSource = dataSource;
    }*/

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

}
