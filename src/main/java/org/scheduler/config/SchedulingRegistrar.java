package org.scheduler.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.scheduler.annotaion.EnableJobScheduling;
import org.scheduler.annotaion.JobSchedule;
import org.scheduler.model.JobScheduleInfo;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;


@Slf4j
public class SchedulingRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;
    private final Set<JobScheduleInfo> jobs = new HashSet<>();
    ObjectMapper mapper = new ObjectMapper();


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    @Override
    public void registerBeanDefinitions(final AnnotationMetadata metadata, final BeanDefinitionRegistry registry) {

        registry.registerBeanDefinition("jobStarter", BeanDefinitionBuilder.genericBeanDefinition(StartSchedule.class)
                .setScope(ConfigurableBeanFactory.SCOPE_SINGLETON).setInitMethodName("startSchedule")
                .getBeanDefinition());
        final AnnotationAttributes annotationAttributes = new AnnotationAttributes(
                metadata.getAnnotationAttributes(EnableJobScheduling.class.getCanonicalName()));
        final ClassPathScanningCandidateComponentProvider provider
                = new ClassPathScanningCandidateComponentProvider(false, environment);
        provider.addIncludeFilter(new AnnotationTypeFilter(JobSchedule.class, true));
        final Set<String> basePackages
                = getBasePackages((StandardAnnotationMetadata) metadata, annotationAttributes);
        for (final String basePackage : basePackages) {
            for (final BeanDefinition beanDefinition : provider.findCandidateComponents(basePackage)) {
                try {
                    Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                    JobSchedule metaData = clazz.getAnnotation(JobSchedule.class);
                    if (Objects.nonNull(metaData)) {
                        JobScheduleInfo info = new JobScheduleInfo();
                        info.setCronExpression(metaData.cornExpression());
                        info.setJobClass(beanDefinition.getBeanClassName());
                        info.setJobName(metaData.jobName());
                        info.setJobGroup(metaData.jobGroup());
                        jobs.add(info);
                    }
                } catch (ClassNotFoundException cf) {
                    throw new RuntimeException(cf);
                }

            }
        }
        if (Objects.nonNull(environment.getProperty("schedule.job.groups"))) {
            Arrays.stream(environment.getProperty("schedule.job.groups", String[].class)).forEach(x -> {
                String schedule = environment.getProperty(String.format("schedule.job.%s.scheduleinfo", x));
                if (Objects.nonNull(schedule)){
                    log.info("Schedule info found for job group {}",x);
                    Arrays.stream(schedule.split("~"))
                            .forEach(i -> {
                                try {
                                    JobScheduleInfo info = mapper.readValue(i, JobScheduleInfo.class);
                                    info.setJobGroup(x);
                                    jobs.add(info);
                                    log.info("Schedule info found for job name {}",info.getJobName());
                                } catch (JsonProcessingException e) {
                                    log.error("error in creating schedule",e);
                                }

                            });
                }

            });

        }
        if (!jobs.isEmpty()) {
            registry.registerBeanDefinition("jobRegistry",
                    BeanDefinitionBuilder.genericBeanDefinition(Set.class, () -> Collections.unmodifiableSet(new HashSet<>(jobs))).getBeanDefinition());
        }
    }

    private static Set<String> getBasePackages(
            final StandardAnnotationMetadata metadata,
            final AnnotationAttributes attributes) {
        final String[] basePackages = attributes.getStringArray("packageToScan");
        final Set<String> packagesToScan = new LinkedHashSet<>(Arrays.asList(basePackages));
        if (packagesToScan.isEmpty()) {
            return Collections.singleton(metadata.getIntrospectedClass().getPackage().getName());
        }
        return packagesToScan;
    }

}
