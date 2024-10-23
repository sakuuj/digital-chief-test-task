package by.sakuuj.digital.chief.testproj.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackages = "by.sakuuj.digital.chief.testproj.repository")
public class JpaConfig {
}
