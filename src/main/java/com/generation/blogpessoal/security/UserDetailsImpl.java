package com.generation.blogpessoal.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.generation.blogpessoal.model.Usuario;

public class UserDetailsImpl implements UserDetails {
	
	/*é um identificador da versão da Classe, ele serializa e desserializa
	 * um objeto de uma classe que implementa interface serializable
	 * 
	 * obs: A serialização é quando um objeto é transformado, em umacadeia de bytes 
	 * e desta forma pode ser manipulado de maneira mais fácil
	 * 
	 *  verificar se uma classe carregada e o objeto serializado são compatíveis,
	 *   ou seja, se o Objeto foi gerado pela mesma versão da Classe.
	 */
	private static final long serialVersionUID = 1L;
	
	private String userName;
	private String password;
	
	//atributo que vai receber os direitos de acesso do nosso usuário
	private List<GrantedAuthority> authorities;
	
	
	public UserDetailsImpl(Usuario user) {
		this.userName = user.getUsuario();
		this.password = user.getSenha();
	}
	
	public UserDetailsImpl() {
		
	}
	
	//sobreescrita do método da interface que implementamos 
	/*
	 * aqui vamos retornar o atributo que tem as autorizações/direitos de 
	 * acesso do usuário que esta logando
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities(){
		return authorities;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public String getUsername() {
		return userName;
		
	}

	/*
	 * indica que o acesso do usuário não expirou
	 */
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 * indicar que o usuário não esta bloqueado
	 */
	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 * indica que o usuario não esta com suas credenciais expiradas 
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	
	/*
	 * indica que o usuário esta habilitado
	 */
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
