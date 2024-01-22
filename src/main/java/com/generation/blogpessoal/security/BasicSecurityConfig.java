package com.generation.blogpessoal.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration //classe de configuração
@EnableWebSecurity //habilita a segurança de forma global para toda a api
public class BasicSecurityConfig {
	
	/*
	 * A Classe BasicSecurityConfig é responsável por sobrescrever a configuração padrão 
	 * da Spring Security e definir como ela irá funcionar. Nesta Classe vamos definir 
	 * quais serão as formas de autenticação, quais endpoints serão protegidos pelo 
	 * Token JWT, entre outras configurações. Depois da implementação desta Classe, aquela 
	 * tela de login, que apareceu no Navegador vai ser desativada e uma nova configuração 
	 * será definida como padrão.
	 */

    @Autowired
    private JwtAuthFilter authFilter;

    /*
     * No Spring, os objetos que formam a espinha dorsal da sua aplicação e que são 
     * gerenciados pelo Spring são chamados de Beans. 
     * Um Bean é um objeto que é instanciado, montado e gerenciado pelo Spring.
     */
    @Bean //transformar a instância retornada pelo Método em um Objeto gerenciado pelo Spring
    UserDetailsService userDetailsService() {

        return new UserDetailsServiceImpl();
    }
    /*
     * instância da Classe UserDetailsServiceImpl, que implementa a Interface userDetailsService.
     *  Nós utilizaremos este Método para validar se o usuário que está tentando se autenticar 
     *  está persistido no Banco de dados da aplicação.
     */

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /*
     * retornará uma instância da Classe BCryptPasswordEncoder(), que utiliza o algoritmo de criptografia 
     * do tipo hash, chamado BCrypt. Nós utilizaremos este Método para Criptografar e 
     * Validar a senha do usuário.
     */

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    /*
     * Cria um Objeto da Classe DaoAuthenticationProvider, chamado authenticationProvider. A 
     * Classe DaoAuthenticationProvider é utilizada para autenticar um Objeto da Classe Usuario 
     * através do usuario (e-mail) e a senha, validando os dados no Banco de dados de aplicação, 
     * através da Classe UserDetailsServiceImpl.
     * 
     * Adiciona um Objeto da Classe UserDetailsServiceImpl através do Método setUserDetailsService(),
     *  que será utilizado para validar o usuario (e-mail) do Objeto da Classe Usuario.
     *  
     *  Adiciona um Objeto da Classe PasswordEncoder através do Método setPasswordEncoder(), que será 
     *  utilizado para validar a senha do Usuário.
     *  
     *  Retorna o Objeto authenticationProvider.
     */

    
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    /*
     * Método authenticationManager(AuthenticationConfiguration authenticationConfiguration), implementa 
     * a confguração de autenticação. Este Método utiliza o Método 
     * authenticationConfiguration.getAuthenticationManager() para procurar uma implementação 
     * da Interface UserDetailsService e utilizá-la para identificar se o usuário é válido ou não. 
     * Em nosso projeto Blog Pessoal, será utilizada a Classe UserDetailsServiceImpl, que valida o 
     * usuário no Banco de dados.
     */

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	/*
    	 * O Método SecurityFilterChain filterChain(HttpSecurity http), estamos informando ao Spring 
    	 * que a configuração padrão da Spring Security será substituída por uma nova configuração. 
    	 * Nesta configuração iremos customizar a autenticação da aplicação desabilitando o formulário 
    	 * de login e habilitando a autenticação via HTTP.
    	 */
    	http
	        .sessionManagement(management -> management
	                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        		.csrf(csrf -> csrf.disable())
	        		.cors(withDefaults());
    	/*
    	 * vamos liberar o acesso de outras origens (Requisições de outros servidores HTTP),
    	 * 
    	 * atenção as linha seguintes:
    	 * requestMatchers permite as rotas que o usuário pode ver antes de estar logado
    	 * 
    	 * 
    	 */
    	http
	        .authorizeHttpRequests((auth) -> auth
	                .requestMatchers("/usuarios/logar").permitAll()
	                .requestMatchers("/usuarios/cadastrar").permitAll()
	                .requestMatchers("/error/**").permitAll()
	                .requestMatchers(HttpMethod.OPTIONS).permitAll()
	                .anyRequest().authenticated())
	        .authenticationProvider(authenticationProvider())
	        
	        .httpBasic(withDefaults())
	        .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();

    }

}