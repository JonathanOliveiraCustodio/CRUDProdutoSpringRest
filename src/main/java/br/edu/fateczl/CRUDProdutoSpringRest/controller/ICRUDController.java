package br.edu.fateczl.CRUDProdutoSpringRest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

public interface ICRUDController<T> {
	public List<T> lista();

	public ResponseEntity<T> busca(int cod);
	public ResponseEntity<String>  adiciona(T t);
	public ResponseEntity<String>  atualiza(T t);
	public ResponseEntity<String>  exclui(T t);
}
