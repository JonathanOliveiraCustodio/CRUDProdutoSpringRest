package br.edu.fateczl.CRUDProdutoSpringRest.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class ProdutoDTO {
	private int codigo;
	private String nome;
	private float valorUnitario;
	private int qtdEstoque;

}
