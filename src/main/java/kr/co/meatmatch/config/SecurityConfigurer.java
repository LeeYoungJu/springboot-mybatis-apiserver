package kr.co.meatmatch.config;

import kr.co.meatmatch.common.constants.PATH;
import kr.co.meatmatch.config.auth.service.CustomUserDetailsService;
import kr.co.meatmatch.config.filter.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.CharacterEncodingFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    private final JwtRequestFilter jwtRequestFilter;
    private final CustomUserDetailsService customUserDetailsService;

    private String[] permitAllUrls = new String[]{
              PATH.API_PATH + "/auth/**"
            , PATH.API_PATH + "/appinfo/version"
            , PATH.API_PATH + "/ppurio"
            , PATH.API_PATH + "/id/find"
            , PATH.API_PATH + "/password/find"
            , PATH.API_PATH + "/main/custom-index"
            , PATH.API_PATH + "/stock/market-price", PATH.API_PATH + "/stock/part/**", PATH.API_PATH + "/stock/origin/**"
            , PATH.API_PATH + "/stock/brand/**", PATH.API_PATH + "/stock/est/**", PATH.API_PATH + "/stock/grade/**"
    };  // 로그인(토큰)이 필요없는 url

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());

        http.csrf().disable()
                .authorizeRequests()
                // .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers(permitAllUrls).permitAll()
                .anyRequest().authenticated()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        http.addFilterBefore(filter, CsrfFilter.class);

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
