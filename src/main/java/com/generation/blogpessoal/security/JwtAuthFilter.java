package com.generation.blogpessoal.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	/*
	 * é responsável por pré-processar todas as Requisições HTTP, 
	 * em busca do Token JWT enviado no Cabeçalho (header) da Requisição
	 */
	
	//injeção de dependencia
	@Autowired
	private JwtService jwtService;
	
	//injeção de dependencia
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	/*
	 * HttpServletRequest request: É a Requisição HTTP que será analisada pelo filtro.
	 * 
	 * HttpServletResponse response: É a Resposta da Requisição HTTP que foi analisada pelo Filtro de Servlet
	 * 
	 * FilterChain filterChain: É um Objeto fornecido pela Spring Security, 
	 * indicando qual será o próximo filtro que será executado.
	 * 
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		
		/*
		 * Criamos a variável do tipo String, chamada authHeader, que receberá o Token JWT presente no Cabeçalho da 
		 * Requisição HTTP, na propriedade Authorization, através do Método getHeader("Authorization"), 
		 * da Interface HttpServletRequest.
		 */
		String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
		
		try {
			if(authHeader != null && authHeader.startsWith("Bearer ")) {
				/*
				 * Foi criado um laço condicional que verifica se o Token JWT foi encontrado no Cabeçalho da Requisição 
				 * e se ele inicia com a palavra "Bearer ", através do Método startsWith() da Classe String.
				 * 
				 */
				token = authHeader.substring(7); // remove o "Bearer "
				username = jwtService.extractUsername(token); // extrair o username do token
			}
			
			/*
			 * Verifica se a variável username é diferente de nulo, ou seja, se foi encontrado o usuario (e-mail) no 
			 * Payload do Token JWT e Checa o Security Context (Contexto de Segurança), através do Método getContext()
			 *  da Classe SecurityContextHolder, que retornará o Contexto de Segurança atual, para verificar se o
			 *   usuario não está autenticado na Security Context, através do Método getAuthentication() da Interface
			 *    SecurityContext.
			 */
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				/*
				 *  Se a condição for verdadeira, inicia o processo de construção do Objeto da Classe UserDetails,
				 *   que armazenará as informações do usuário autenticado, através da Classe UserDetailsServiceImpl,
				 *    que checará se o usuário existe no Banco de dados.
				 */
				
                if (jwtService.validateToken(token, userDetails)) {
					/*
					 *  Valida Token JWT através do Método validateToken(), da Classe JwtService.
					 */
					
                    UsernamePasswordAuthenticationToken authToken = 
                    		new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());					/*
					 * Se o Token JWT for validado, construiremos um novo objeto da Classe UsernamePasswordAuthenticationToken, 
					 * chamado authToken, que será responsável por autenticar um usuário na Spring Security
					 * e definir um Objeto Authentication, que será itilizado para autenticar o Usuário na 
					 * Security Context, com o objetivo de manter o usuário conectado na Security Context 
					 * até o Token JWT expirar. O Construtor da Classe UsernamePasswordAuthenticationToken 
					 * solicita 3 parâmetros:

    userDetails: Objeto da Classe UserDetails, que contém os dados do usuário autenticado na Spring Security.
    credentials: Senha do usuário. Geralmente enviamos null neste parâmetro, porque o Objeto UserDetails já possui 
    a senha criptografada.
    authorities: São as Autorizações do usuário (Roles), que será obtida através do Método getAuthorities() 
    da Classe UserDetails, implementado na Classe UserDetailsImpl. Como não estamos implementando as Autorizações, 
    será enviada uma Collection vazia.


					 */
					
					/*
					 * Através do Método setDetails() da Classe UsernamePasswordAuthenticationToken, vamos adicionar 
					 * a Requisição HTTP dentro do Objeto authToken.
					 */
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    /*
                     * Com o Objeto authToken configurado, ele será utilizado para autenticar o usuário na Security 
                     * Context, através do Método setAuthentication().
                     */
				}
				
			}
            filterChain.doFilter(request, response);			/*
			 * Chamamos o próximo Filtro de Servlet através do método doFilter().
			 */

			/*
			 *  Se o processo de validação do Token JWT falhar, uma das 5 Exceptions abaixo será lançada:
			 *  ExpiredJwtException: O Token JWT Expirou!
				UnsupportedJwtException: O Token não está no formato JWT.
				MalformedJwtException: A construção do Token está errada e ele deve ser rejeitado.
				SignatureException: A assinatura do Token JWT não confere.
				ResponseStatusException: Retorna um HTTP Status em conjunto com uma Exceptio
			 */
        }catch(ExpiredJwtException | UnsupportedJwtException | MalformedJwtException 
                | SignatureException | ResponseStatusException e){
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }
            /*
             * O Filtro Retornará o HTTP Status 403 🡪 FORBIDDEN. Este Status indica que o Token é inválido
             *  e por isso o acesso não foi permitido. 
             */
        
		
		
	}
}
