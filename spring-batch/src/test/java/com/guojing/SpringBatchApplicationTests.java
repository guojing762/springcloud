package com.guojing;

import com.guojing.entity.Person;
import com.guojing.process.PersonItemProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBatchApplicationTests {

	//查询
	private static final String GET_PRODUCT = "select * from person where personName = ?";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	public void testq(){
		List<Person> personList = jdbcTemplate.query(GET_PRODUCT, new Object[] {"2"}, new RowMapper<Person>() {
			@Override
			public Person mapRow(ResultSet resultSet, int rowNum ) throws SQLException {
				Person p = new Person();
				p.setPersonName(resultSet.getString(1));
				p.setPersonAge(resultSet.getString(2));
				p.setPersonSex(resultSet.getString(3));
				return p;
			}
		});
		Assert.assertNotNull(personList);
	}

	@Test
	public void contextLoads() {
	}

}
