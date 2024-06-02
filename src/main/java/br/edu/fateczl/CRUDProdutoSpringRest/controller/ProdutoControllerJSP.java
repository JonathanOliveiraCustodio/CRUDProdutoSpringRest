package br.edu.fateczl.CRUDProdutoSpringRest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import br.edu.fateczl.CRUDProdutoSpringRest.model.dto.ProdutoDTO;
import br.edu.fateczl.CRUDProdutoSpringRest.model.entity.Produto;

@Controller
public class ProdutoControllerJSP {

    @Autowired
    RestTemplate restTemplate;

    private final String API_URL = "http://localhost:8080/api/produto";

    @RequestMapping(name = "produto", value = "/produto", method = RequestMethod.GET)
    public ModelAndView produtoGet(@RequestParam Map<String, String> allRequestParam, ModelMap model) {

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

    @RequestMapping(name = "produto", value = "/produto", method = RequestMethod.POST)
    public ModelAndView produtoPost(@RequestParam Map<String, String> allRequestParam, ModelMap model) {

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
}

