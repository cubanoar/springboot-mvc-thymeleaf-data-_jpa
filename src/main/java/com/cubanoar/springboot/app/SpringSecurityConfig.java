package com.cubanoar.springboot.app;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cubanoar.springboot.app.auth.handler.LoginSuccessHandler;
import com.cubanoar.springboot.app.models.service.JpaUserDetailsService;

@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)//Habilitando las anotaciones de seguridad de los controladores
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private LoginSuccessHandler successHandler;
	
	@Autowired
	private JpaUserDetailsService jpaUserDetailsService;
	
	/*Para JDBC
	 * @Autowired private DataSource dataSource;
	 */
	
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/","/css/**","/js/**","/images/**","/listar").permitAll()
		/*Cambiamos por anotaciones en los controller*/
		/*.antMatchers("/ver/**").hasAnyRole("USER")*/
		/*.antMatchers("/uploads/**").hasAnyRole("USER")*/
		/*.antMatchers("/form/**").hasAnyRole("ADMIN")*/
		/*.antMatchers("/eliminar/**").hasAnyRole("ADMIN")*/
		/*.antMatchers("/factura/**").hasAnyRole("ADMIN")*/
		.anyRequest().authenticated()
		.and()
			.formLogin()
				.successHandler(successHandler)
				.loginPage("/login")
			.permitAll()
		.and()
		.logout().permitAll()
		.and()
		.exceptionHandling().accessDeniedPage("/error_403");

	}
	
	/*Este lo pasamos a MvcConfig y aca lo inyectamos con @Autowired y le sacamos los ()*/
	/*
	 * @Bean public static BCryptPasswordEncoder passwordEncoder() { return new
	 * BCryptPasswordEncoder(); }
	 */
	
	@Autowired
	public void configurerGlobal(AuthenticationManagerBuilder builder) throws Exception{
		
		/*
		 * PasswordEncoder encoder = passwordEncoder; UserBuilder users =
		 * User.builder().passwordEncoder(encoder::encode);
		 */
			/*
			 * Lo mismo que UserBuilder users = User.builder().passwordEncoder(password -> {
			 * return encoder.encode(password); });
			 */
			/*
			 * builder.inMemoryAuthentication()
			 * .withUser(users.username("admin").password("12345").roles("ADMIN", "USER"))
			 * .withUser(users.username("dasiel").password("12345").roles("USER"));
			 */
		
		/*Con JDBC
		builder.jdbcAuthentication()
		.dataSource(dataSource)
		.passwordEncoder(encoder)
		.usersByUsernameQuery("select username,password, enabled from users where username=? ")
		.authoritiesByUsernameQuery("select u.username,a.authority from authorities a inner join users u on (a.user_id=u.id) where username=? ");
		*/
		/*Con JPA*/
		builder.userDetailsService(jpaUserDetailsService)
		.passwordEncoder(passwordEncoder);
	
	
	}

	
}
