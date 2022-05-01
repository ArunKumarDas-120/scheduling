package org.scheduler.config;

import org.scheduler.annotaion.EnableCustomPropertySource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;

import java.util.stream.IntStream;


public class CustomPropertySourceRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, BeanFactoryAware {

    private Environment environment;
    private BeanFactory beanFactory;
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = new AnnotationAttributes(
                importingClassMetadata.getAnnotationAttributes(EnableCustomPropertySource.class.getCanonicalName()));
        BindResult<String[]> result =  Binder.get(this.environment).bind("property.source.location", String[].class);
        if(result.isBound()){
            String [] locations = result.get();
            Resource[] resource =  new Resource[locations.length];
            IntStream.range(0,result.get().length).forEach(i->{
                if(locations[i].startsWith("Classpath:")){
                    resource[i] = new ClassPathResource(locations[i].replace("Classpath:",""));
                }
                else if(locations[i].startsWith("File:")){
                    resource[i] = new FileSystemResource(locations[i].replace("File:",""));
                }
            });
            registry.registerBeanDefinition("customPropertySource",
                    BeanDefinitionBuilder.genericBeanDefinition(CustomPropertySource.class)
                            .setScope(ConfigurableListableBeanFactory.SCOPE_SINGLETON)
                            .addPropertyValue("valueDecoder", getBean())
                            .addPropertyValue("locations",resource)
                            .getBeanDefinition()
            );
        }

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    private ValueDecoder getBean(){
        ValueDecoder decode = null;
        try {
            decode = this.beanFactory.getBean(ValueDecoder.class);
        }catch (Exception e){

        }
       return decode;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
