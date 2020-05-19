package com.fdmgroup.backend;

import lombok.extern.log4j.Log4j2;
import org.h2.tools.Server;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

@Log4j2
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
//        Jedis jedis = new Jedis("localhost");
//        jedis.flushAll();
//        log.info("Flushed redis cache");
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile({"!test"})
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        return mapper;
    }

}
