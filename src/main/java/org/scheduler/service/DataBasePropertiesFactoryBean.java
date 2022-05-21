package org.scheduler.service;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Properties;

public class DataBasePropertiesFactoryBean  {

    @Nullable
    private Resource[] locations;

    public void setLocations(Resource... locations) {
        this.locations = locations;
    }

    public Properties getObject() throws Exception {
        return null;
    }


}
