package com.example.blog.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.blog.model.Usuario;
import com.example.blog.repository.UsuarioRepository;
import com.example.blog.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@BeforeAll
	void start(){
		usuarioRepository.deleteAll();
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Root", "root@root.com", "rootroot", "-"));
	}
	
	@Test
	@DisplayName("Cadastra usuário")
	public void deveCriarUsuario() {
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>( new Usuario(0L, 
				"Administrador", "admin@email.com.br", "admin123", "https://i.imgur.com/Tk9f19K.png"));

		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
				
	}
	
	@Test
	@DisplayName("Impede usuário duplicado")
	public void naoDeveDuplicarUsuario() {
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Administrador", "admin@email.com.br", "admin123", "https://i.imgur.com/Tk9f19K.png"));
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>( new Usuario(0L,
				"Administrador", "admin", "admin123", "https://i.imgur.com/Tk9f19K.png"));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
		
	}
	
	@Test
	@DisplayName("Atualiza usuário")
	public void deveAtualizarUsuario () {
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,
				"Administrador1", "admin1@email.com.br", "admin123", "https://i.imgur.com/Tk9f19K.png"));
		
		Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(),
				"Administrador2", "admin2@email.com.br", "admin123", "https://i.imgur.com/Tk9f19K.png");
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
	
	
	@Test
	@DisplayName("Listar todos usuários")
	public void deveMostrarTodosUsers() {
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Matheus", "matheus@email.com.br", "12345678", "-"));
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Mariana", "mariana@email.com.br", "12345678", "-"));
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
}
