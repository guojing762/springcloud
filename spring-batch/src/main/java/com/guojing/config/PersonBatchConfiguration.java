package com.guojing.config;

import javax.sql.DataSource;

import com.guojing.entity.Person;
import com.guojing.process.PersonItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理具体工作业务  主要包含三个部分:读数据、处理数据、写数据
 * @author wbw
 *
 */
@Configuration
@EnableBatchProcessing
public class PersonBatchConfiguration {
    //插入语句
   private static final String PERSON_INSERT = "INSERT INTO Person (personName, personAge,personSex) VALUES (:personName, :personAge,:personSex)";
   public static final String Person_INSERT = "INSERT INTO Person (id, name,description,quantity) VALUES (:id, :name,:description,:quantity)";
   // tag::readerwriterprocessor[] 1.读数据
    @Bean
    public ItemReader<Person> reader() {
        FlatFileItemReader<Person> reader = new FlatFileItemReader<Person>();
        //加载外部文件数据 文件类型:CSV
        reader.setResource(new ClassPathResource("sample-data.csv"));
        reader.setLineMapper(new DefaultLineMapper<Person>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "personName","personAge","personSex" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                setTargetType(Person.class);
            }});
        }});
        return reader;
    }
    //2.处理数据
    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }
    //3.写数据
    @Bean
    public StaxEventItemWriter<Person> writer() {
        StaxEventItemWriter<Person> xmlItemWriter = new StaxEventItemWriter<>();
        xmlItemWriter.setRootTagName("root");
        xmlItemWriter.setSaveState(true);
        xmlItemWriter.setEncoding("UTF-8");
        xmlItemWriter.setResource(new ClassPathResource("/sample-data.xml"));
        xmlItemWriter.setMarshaller(new XStreamMarshaller() {{
            Map<String, Class<Person>> map = new HashMap<>();
            map.put("person",Person.class);
            setAliases(map);
        }});
        return xmlItemWriter;
    }
//      @Bean
//    public ItemWriter<Person> writer(DataSource dataSource) {
//        JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<Person>();
//        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Person>());
//        writer.setSql(PERSON_INSERT);
//        writer.setDataSource(dataSource);
//        return writer;
//    }
    // end::readerwriterprocessor[]


    // tag::jobstep[]
    @Bean
    public Job importUserJob(JobBuilderFactory jobs, @Qualifier("step1")Step s1, JobExecutionListener listener) {
        return jobs.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(s1)
                .end()
                .build();
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<Person> reader,
            ItemWriter<Person> writer, ItemProcessor<Person, Person> processor) {
//          使用串联的process
//        CompositeItemProcessor<Person,Person> compositeProcessor = new  CompositeItemProcessor<Person,Person>();
//        List itemProcessors = new ArrayList();
//        itemProcessors.add(new PersonItemProcessor());
//        itemProcessors.add(new PersonItemProcessor());
//        compositeProcessor.setDelegates(itemProcessors);

        return stepBuilderFactory.get("step1")
                .<Person, Person> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
    // end::jobstep[]

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}