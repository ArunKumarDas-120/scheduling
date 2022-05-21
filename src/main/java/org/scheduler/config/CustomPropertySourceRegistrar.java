package org.scheduler.config;

import org.scheduler.annotaion.EnableCustomPropertySource;
import org.scheduler.model.PropertySourceConfig;
import org.scheduler.resource.DataBaseResource;
import org.scheduler.service.DataBasePropertiesFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;


public class CustomPropertySourceRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, BeanFactoryAware {

    private Environment environment;
    private BeanFactory beanFactory;
    private final YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
    private final PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
    private final DataBasePropertiesFactoryBean dataBasePropertiesFactoryBean = new DataBasePropertiesFactoryBean();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = new AnnotationAttributes(
                importingClassMetadata.getAnnotationAttributes(EnableCustomPropertySource.class.getCanonicalName()));
        BindResult<PropertySourceConfig> result = Binder.get(this.environment).bind("property.source", PropertySourceConfig.class);
        if (result.isBound()) {
            try {
                List<Resource> propList = new ArrayList<>();
                List<Resource> ymlPropList = new ArrayList<>();
                List<Resource> dbList = new ArrayList<>();

                result.get().getConfig().forEach(s -> {
                    this.decodeResources(s, propList, ymlPropList, dbList);
                });

                propertiesFactoryBean.setLocations(propList.toArray(new Resource[]{}));
                propertiesFactoryBean.afterPropertiesSet();
                yamlPropertiesFactoryBean.setResources(ymlPropList.toArray(new Resource[]{}));
                dataBasePropertiesFactoryBean.setLocations(dbList.toArray(new Resource[]{}));
                Properties props = new Properties();
                props.putAll(yamlPropertiesFactoryBean.getObject());
                props.putAll(propertiesFactoryBean.getObject());
                CustomPropertySource customPropertySource = new CustomPropertySource();
                registry.registerBeanDefinition("customPropertySource", BeanDefinitionBuilder.genericBeanDefinition(CustomPropertySource.class, () -> {
                    customPropertySource.setProperties(props);
                    customPropertySource.setIgnoreResourceNotFound(Boolean.TRUE);
                    customPropertySource.setValueDecoder(getBean());
                    return customPropertySource;
                }).setScope("singleton").getBeanDefinition());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    private ValueDecoder getBean() {
        ValueDecoder decode = null;
        try {
            decode = this.beanFactory.getBean(ValueDecoder.class);
        } catch (Exception e) {

        }
        return decode;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private void decodeResources(PropertySourceConfig.PropertySource propertySource,
                                 List<Resource> propList,
                                 List<Resource> ymlPropList
            , List<Resource> dbList) {
        Resource resource = null;
        if (propertySource.getFrom().equalsIgnoreCase("Classpath"))
            resource = new ClassPathResource(propertySource.getName().concat(".").concat(propertySource.getType()));
        else if (propertySource.getFrom().equalsIgnoreCase("File"))
            resource = new FileSystemResource(propertySource.getName().concat(".").concat(propertySource.getType()));
        else if (propertySource.getFrom().equalsIgnoreCase("DataBase"))
            resource = new DataBaseResource();
        Optional.ofNullable(resource).ifPresent(r -> {
            if (propertySource.getFrom().equalsIgnoreCase("Classpath")
                    || propertySource.getFrom().equalsIgnoreCase("File")) {
                if (propertySource.getType().equalsIgnoreCase("properties"))
                    propList.add(r);
                else if (propertySource.getType().equalsIgnoreCase("yml"))
                    ymlPropList.add(r);
            } else if (propertySource.getFrom().equalsIgnoreCase("DataBase")) {
                dbList.add(r);
            }
        });


    }
}
