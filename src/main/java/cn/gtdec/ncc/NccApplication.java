package cn.gtdec.ncc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan(basePackages={"cn.gtdec.ncc.dao"})
public class NccApplication {

	public static void main(String[] args) {
		SpringApplication.run(NccApplication.class, args);
	}

}
