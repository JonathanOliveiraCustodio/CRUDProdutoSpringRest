package br.edu.fateczl.CRUDProdutoSpringRest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.ModelMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import br.edu.fateczl.CRUDProdutoSpringRest.model.dto.ProdutoDTO;
import br.edu.fateczl.CRUDProdutoSpringRest.model.entity.Produto;
import br.edu.fateczl.CRUDProdutoSpringRest.repository.IProdutoRepository;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/produto")
public class ProdutoController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IProdutoRepository pRep;

    private final String API_URL = "http://localhost:8080/api/produto";

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView indexGet(@RequestParam Map<String, String> allRequestParam, ModelMap model) {

        String cmd = allRequestParam.get("cmd");
        String codigo = allRequestParam.get("codigo");

        if (cmd != null) {
            Produto p = new Produto();
            if (codigo != null && !codigo.isEmpty()) {
                p.setCodigo(Integer.parseInt(codigo));
            }

            String saida = "";
            String erro = "";
            List<Produto> produtos = new ArrayList<>();

            try {
                if (cmd.contains("alterar") || cmd.contains("buscar")) {
                    ProdutoDTO response = restTemplate.getForObject(API_URL + "/" + codigo, ProdutoDTO.class);
                    p = convProdDTOProd(response);
                } else if (cmd.contains("excluir")) {
                    restTemplate.delete(API_URL + "/" + codigo);
                    saida = "Produto excluído com sucesso";
                }

                ProdutoDTO[] response = restTemplate.getForObject(API_URL, ProdutoDTO[].class);
                produtos = convListProdProdDTO(response);

            } catch (Exception e) {
                erro = e.getMessage();
            } finally {
                model.addAttribute("saida", saida);
                model.addAttribute("erro", erro);
                model.addAttribute("produto", p);
                model.addAttribute("produtos", produtos);
            }
        }
        return new ModelAndView("produto");
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView indexPost(@RequestParam Map<String, String> allRequestParam, ModelMap model) {

        String cmd = allRequestParam.get("botao");
        String codigo = allRequestParam.get("codigo");
        String nome = allRequestParam.get("nome");
        String valorUnitario = allRequestParam.get("valorUnitario");
        String qtdEstoque = allRequestParam.get("qtdEstoque");

        String saida = "";
        String erro = "";
        Produto p = new Produto();
        List<Produto> produtos = new ArrayList<>();

        if (codigo != null && !codigo.isEmpty() && !cmd.contains("Listar")) {
            p.setCodigo(Integer.parseInt(codigo));
        }
        if (cmd.contains("Cadastrar") || cmd.contains("Alterar")) {
            p.setNome(nome);
            p.setValorUnitario(Float.parseFloat(valorUnitario));
            p.setQtdEstoque(Integer.parseInt(qtdEstoque));
        }

        try {
            if (cmd.contains("Cadastrar")) {
                restTemplate.postForEntity(API_URL, convProdProdDTO(p), String.class);
                saida = "Produto cadastrado com sucesso";
            }
            if (cmd.contains("Alterar")) {
                restTemplate.put(API_URL, convProdProdDTO(p));
                saida = "Produto alterado com sucesso";
            }
            if (cmd.contains("Excluir")) {
                restTemplate.delete(API_URL, convProdProdDTO(p));
                saida = "Produto excluído com sucesso";
            }
            if (cmd.contains("Buscar")) {
                ProdutoDTO response = restTemplate.getForObject(API_URL + "/" + codigo, ProdutoDTO.class);
                p = convProdDTOProd(response);
            }
            if (cmd.contains("Listar")) {
                ProdutoDTO[] response = restTemplate.getForObject(API_URL, ProdutoDTO[].class);
                produtos = convListProdProdDTO(response);
            }

        } catch (Exception e) {
            erro = e.getMessage();
        } finally {
            model.addAttribute("saida", saida);
            model.addAttribute("erro", erro);
            model.addAttribute("produto", p);
            model.addAttribute("produtos", produtos);
        }

        return new ModelAndView("produto");
    }

    private List<Produto> convListProdProdDTO(ProdutoDTO[] produtosDTO) {
        List<Produto> produtos = new ArrayList<>();
        for (ProdutoDTO pDTO : produtosDTO) {
            Produto p = new Produto();
            p.setCodigo(pDTO.getCodigo());
            p.setNome(pDTO.getNome());
            p.setValorUnitario(pDTO.getValorUnitario());
            p.setQtdEstoque(pDTO.getQtdEstoque());
            produtos.add(p);
        }
        return produtos;
    }

    private Produto convProdDTOProd(ProdutoDTO pDTO) {
        Produto p = new Produto();
        p.setCodigo(pDTO.getCodigo());
        p.setNome(pDTO.getNome());
        p.setValorUnitario(pDTO.getValorUnitario());
        p.setQtdEstoque(pDTO.getQtdEstoque());
        return p;
    }

    private ProdutoDTO convProdProdDTO(Produto p) {
        ProdutoDTO pDTO = new ProdutoDTO();
        pDTO.setCodigo(p.getCodigo());
        pDTO.setNome(p.getNome());
        pDTO.setValorUnitario(p.getValorUnitario());
        pDTO.setQtdEstoque(p.getQtdEstoque());
        return pDTO;
    }

    // Métodos REST

    @GetMapping("/api")
    public List<ProdutoDTO> lista() {
        List<Produto> produtos = pRep.findAll();
        return convListProdProdDTO(produtos);
    }

    @GetMapping("/api/{codigoProduto}")
    public ResponseEntity<ProdutoDTO> busca(@PathVariable(value = "codigoProduto") int cod) {
        Produto p = pRep.findById(cod).orElseThrow(() -> new ResourceNotFoundException("Código Inválido"));
        ProdutoDTO pDTO = convProdProdDTO(p);
        return ResponseEntity.ok().body(pDTO);
    }

    @PostMapping("/api")
    public ResponseEntity<String> adiciona(@Valid @RequestBody ProdutoDTO pDTO) {
        Produto p = convProdDTOProd(pDTO);
        pRep.save(p);
        return ResponseEntity.ok().body("Produto Adicionado com sucesso");
    }

    @PutMapping("/api")
    public ResponseEntity<String> atualiza(@Valid @RequestBody ProdutoDTO pDTO) {
        Produto p = convProdDTOProd(pDTO);
        pRep.save(p);
        return ResponseEntity.ok().body("Produto atualizado com sucesso");
    }

    @DeleteMapping("/api")
    public ResponseEntity<String> exclui(@Valid @RequestBody ProdutoDTO pDTO) {
        Produto p = convProdDTOProd(pDTO);
        pRep.delete(p);
        return ResponseEntity.ok().body("Produto excluído com sucesso");
    }

    private List<ProdutoDTO> convListProdProdDTO(List<Produto> produtos) {
        List<ProdutoDTO> produtosDTO = new ArrayList<>();
        for (Produto p : produtos) {
            ProdutoDTO pDTO = new ProdutoDTO();
            pDTO.setCodigo(p.getCodigo());
            pDTO.setNome(p.getNome());
            pDTO.setValorUnitario(p.getValorUnitario());
            pDTO.setQtdEstoque(p.getQtdEstoque());
            produtosDTO.add(pDTO);
        }
        return produtosDTO;
    }
}
