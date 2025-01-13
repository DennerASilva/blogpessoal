package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;

import jakarta.validation.Valid;

@RestController // Define ao Spring que essa Classe é uma Controller
@RequestMapping("/postagens") // Define qual endpoint vai ser tratado por essa Classe
@CrossOrigin(origins = "*", allowedHeaders = "*") // Libera o acesso a qualquer front que não
public class PostagemController {
	
	@Autowired // O Spring dá autonomia para a Interface poder invocar os métodos
	private PostagemRepository postagemRepository;
	
	@GetMapping // Indica que esse método é chamado em Verbos/Métodos HTTP do tipo Get
	public ResponseEntity<List<Postagem>> getAll() {
		return ResponseEntity.ok(postagemRepository.findAll()); //SELECT * FROM tb_postagens
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Postagem> getById(@PathVariable Long id) {
		return postagemRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Não foi encontrado"));
				//.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)).build()); 
				
				/*Optional<Postagem> PostagemOpt = postagemRepository.findById(id);
				if(PostagemOpt.isEmpty()) {
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Postagem não encontrada.");
				}
				return ResponseEntity.status(HttpStatus.OK).body(PostagemOpt.get()); */
	}
	
	@GetMapping("/titulo/{titulo}")
	public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo){			
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));
	}
	
	@PostMapping
	public ResponseEntity<Postagem> post (@Valid @RequestBody Postagem postagem) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(postagemRepository.save(postagem));
	}
	
	@PutMapping
	public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem){
		return postagemRepository.findById(postagem.getId())
				.map(resposta -> ResponseEntity.status(HttpStatus.OK).body(postagemRepository.save(postagem)))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"A postagem a ser atualizada não foi encontrada"));
			//	.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
	
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		Optional<Postagem> postagemOpt = postagemRepository.findById(id);
		
		if(postagemOpt.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Postagem não encontrada");
		postagemRepository.deleteById(id);		
	}
	
}