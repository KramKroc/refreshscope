package com.kramkroc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.boot.test.WebIntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApigatewayApplication.class)
@WebIntegrationTest({ "server.port:0", "eureka.client.enabled:false",
        "spring.cloud.config.enabled:false", "spring.cloud.config.discovery.enabled:false",
        "security.basic.enabled:false" })
public class ApigatewayApplicationTests {

	@Test
	public void contextLoads() {
	}

}
