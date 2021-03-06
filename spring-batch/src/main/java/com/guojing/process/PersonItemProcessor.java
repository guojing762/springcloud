package com.guojing.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.guojing.entity.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class PersonItemProcessor implements ItemProcessor<Person, Person> {
    //查询
	private static final String GET_PRODUCT = "select * from Person where personName = ?";
	
    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     *  过滤数据 返回null即可， 跳过 则抛出异常
     * 要过滤某条记录, 只需要 ItemProcessor 返回“null” 即可. 框架将自动检测结果为“null”的情况,
     * 不会将该item 添加到传给ItemWriter的list中。
     * 像往常一样, 在 ItemProcessor 中抛出异常将会导致跳过
     * @param person
     * @return
     * @throws Exception
     */
    @Override
    public Person process(final Person person) throws Exception {
        List<Person> personList = jdbcTemplate.query(GET_PRODUCT, new Object[] {person.getPersonName()}, new RowMapper<Person>() {
            @Override
            public Person mapRow( ResultSet resultSet, int rowNum ) throws SQLException {
            	Person p = new Person();
            	p.setPersonName(resultSet.getString(1));
            	p.setPersonAge(resultSet.getString(2));
            	p.setPersonSex(resultSet.getString(3));
                return p;
            }
        });
        if(personList.size() >0){
        	log.info("该数据已录入!!!");
        }
    	String sex = null;
        if(person.getPersonSex().equals("0")){
        	sex ="男";
        }else{
        	sex ="女";
        }
        log.info("转换 (性别："+person.getPersonSex()+") 为 (" + sex + ")");
        final Person transformedPerson = new Person(person.getPersonName(), person.getPersonAge(),sex);
        log.info("转换 (" + person + ") 为 (" + transformedPerson + ")");

        return transformedPerson;
    }

}