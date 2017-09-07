package com.guojing;
/**
 * 1、批处理定期提交。
 2、并行批处理：并行处理工作。
 3、企业消息驱动处理
 4、大规模的并行处理
 5、手动或是有计划的重启
 6、局部处理：跳过记录（如：回滚）
 技术目标：
 1、利用Spring编程模型：使程序员专注于业务处理，让Spring框架管理流程。
 2、明确分离批处理的执行环境和应用。
 3、提供核心的，共通的接口。
 4、提供开箱即用（out of the box）的简单的默认的核心执行接口。
 5、提供Spring框架中配置、自定义、和扩展服务。
 6、所有存在的核心服务可以很容的被替换和扩展，不影响基础层。
 7、提供一个简单的部署模式，利用Maven构建独立的Jar文件。
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchApplication.class, args);
	}
}
