package com.github.bilak.configuration;

import com.github.bilak.jpa.model.Accessor;
import com.github.bilak.jpa.repository.AccessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * @author lvasek.
 */
@Configuration
public class SecurityConfiguration {

	@Bean
	PasswordEncoder customPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	UserDetailsService customUserDetailsService(AccessorRepository accessorRepository) {
		return userIdentifier -> {
			Optional<Accessor> accessorOptional;
			if (userIdentifier.contains("@"))
				accessorOptional = accessorRepository.findOneByEmail(userIdentifier);
			else
				accessorOptional = accessorRepository.findOneById(userIdentifier);

			return accessorOptional
					.map(a -> new User(
							a.getEmail(),
							a.getPassword(),
							true, true, true, true,
							AuthorityUtils.createAuthorityList(a.getRole())))
					.orElseThrow(() -> new UsernameNotFoundException(String.format("User %s was not found", userIdentifier)));
		};
	}

	@Configuration
	@EnableAuthorizationServer
	public static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

		private DataSource dataSource;
		private AuthenticationManager authenticationManager;
		private PasswordEncoder passwordEncoder;

		@Autowired
		public AuthorizationServerConfiguration(DataSource dataSource, AuthenticationManager authenticationManager,
				PasswordEncoder passwordEncoder) {
			this.dataSource = dataSource;
			this.authenticationManager = authenticationManager;
			this.passwordEncoder = passwordEncoder;
		}

		@Bean
		protected JdbcTokenStore tokenStore() {
			return new JdbcTokenStore(this.dataSource);
		}

		@Bean
		protected ApprovalStore jdbcApprovalStore() {
			return new JdbcApprovalStore(this.dataSource);
		}

		@Bean
		@Primary
		protected AuthorizationCodeServices authorizationCodeServices() {
			return new JdbcAuthorizationCodeServices(this.dataSource);
		}

		@Bean
		ClientDetailsService cloudJdbcClientDetailsService() {
			JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(this.dataSource);
			clientDetailsService.setPasswordEncoder(this.passwordEncoder);
			return clientDetailsService;
		}

		@Bean
		public UserAuthenticationConverter userAuthenticationConverter(UserDetailsService userDetailsService) {
			DefaultUserAuthenticationConverter defaultUserAuthenticationConverter = new DefaultUserAuthenticationConverter();
			defaultUserAuthenticationConverter.setUserDetailsService(userDetailsService);
			return defaultUserAuthenticationConverter;
		}

		@Bean
		@Primary
		public AuthorizationServerTokenServices authorizationServerTokenServices() {
			DefaultTokenServices tokenServices = new DefaultTokenServices();
			tokenServices.setAuthenticationManager(this.authenticationManager);
			tokenServices.setTokenStore(tokenStore());
			tokenServices.setSupportRefreshToken(true);
			return tokenServices;
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
			security
					.tokenKeyAccess("permitAll()")
					.checkTokenAccess("isAuthenticated()")
					.passwordEncoder(this.passwordEncoder);
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.authorizationCodeServices(authorizationCodeServices())
					.authenticationManager(this.authenticationManager)
					.tokenStore(tokenStore())
					.approvalStore(jdbcApprovalStore())
			;
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.withClientDetails(cloudJdbcClientDetailsService());
		}
	}

	@Configuration
	@EnableResourceServer
	public static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		@Override
		public void configure(HttpSecurity http) throws Exception {
			http
					.antMatcher("/user")
					.authorizeRequests().anyRequest().authenticated()
			;
		}
	}
}
