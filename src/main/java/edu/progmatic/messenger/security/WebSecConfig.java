package edu.progmatic.messenger.security;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@PropertySource("classpath:application.properties")
@Configuration
class WebSecConfig extends WebSecurityConfigurerAdapter {
    private static List<String> clients = Arrays.asList("google", "facebook");

    private static String CLIENT_PROPERTY_KEY
            = "spring.security.oauth2.client.registration.";

    private final Environment env;

    public WebSecConfig(Environment env) {
        this.env = env;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http    .formLogin()
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/loginsuccess")
                .and()
                .authorizeRequests()
                .antMatchers("/home","/messages","/webjars/**","/login", "/register","/messageDetails/**", "/img/**","/rest/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/home")
                .and()
                .oauth2Login()
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/loginsuccess");
//                .cors()
//                .and()
//                .oauth2Login()
//                .and()
//                .authorizeRequests()
//                .antMatchers(HttpMethod.GET,  "/login")//TODO : change foos
//                .hasAuthority("SCOPE_read")
//                .antMatchers(HttpMethod.POST, "/login")
//                .hasAuthority("SCOPE_write")
//                .anyRequest()
//                .authenticated()
//                .and()
//                .oauth2ResourceServer()
//                .jwt();
    }


    @SuppressWarnings("deprecation")
    @Bean(name = "encoder")
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = clients.stream()
                .map(c -> getRegistration(c))
                .filter(registration -> registration != null)
                .collect(Collectors.toList());

        return new InMemoryClientRegistrationRepository(registrations);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {

        return new InMemoryOAuth2AuthorizedClientService(
                clientRegistrationRepository());
    }

    @Bean
    public DozerBeanMapper getDozerMapper () {
        return new DozerBeanMapper();
    }

    private ClientRegistration getRegistration(String client) {
        String clientId = env.getProperty(
                CLIENT_PROPERTY_KEY + client + ".client-id");

        if (clientId == null) {
            return null;
        }

        String clientSecret = env.getProperty(
                CLIENT_PROPERTY_KEY + client + ".client-secret");

        if (client.equals("google")) {
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(clientId).clientSecret(clientSecret).build();
        }
        if (client.equals("facebook")) {
            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                    .clientId(clientId).clientSecret(clientSecret).build();
        }
        return null;
    }

}
