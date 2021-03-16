package com.shepherd.mallauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableAuthorizationServer
public class AuthServer extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailImpl;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtAccessTokenConverter accessTokenConverter;

    @Autowired
    @Qualifier("jwtTokenEnhancer")
    private TokenEnhancer tokenEnhancer;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients();
        security.tokenKeyAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
       clients.inMemory()
               .withClient("mall-user")
               .secret(passwordEncoder.encode("admin"))
               .authorizedGrantTypes("password", "refresh_token")
               .scopes("all")
               .accessTokenValiditySeconds(3600*24)
               .refreshTokenValiditySeconds(3600*24*7);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> delegate = new ArrayList<>();
        delegate.add(tokenEnhancer);
        delegate.add(accessTokenConverter);
        tokenEnhancerChain.setTokenEnhancers(delegate);
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userDetailImpl)
                .accessTokenConverter(accessTokenConverter)
                .tokenEnhancer(tokenEnhancerChain);
    }
}
