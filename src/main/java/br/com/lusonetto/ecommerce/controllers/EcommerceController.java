package br.com.lusonetto.ecommerce.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.lusonetto.ecommerce.models.Item;
import br.com.lusonetto.ecommerce.models.Pedido;
import br.com.lusonetto.ecommerce.models.Produto;

@Controller
public class EcommerceController {

    @Autowired
    private RestTemplate restTemplate;
    
    int quantidadeCarrinho;
    
    private int atualizarNumeroCarrinho() {
    	String url = "http://localhost:8080/items";
        String response = restTemplate.getForObject(url, String.class);
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper = new ObjectMapper();
        List<Item> itensCarrinho = null;
        int quantidadeCarrinho = 0;
        try {
            itensCarrinho = objectMapper.readValue(response, new TypeReference<List<Item>>() {});
            for(Item item: itensCarrinho) {
            	quantidadeCarrinho++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    	return quantidadeCarrinho;
    }

    @GetMapping("/ecommerce")
    public String listarProdutos(Model model) {
    	String url = "http://localhost:8080/produtos";
        String response = restTemplate.getForObject(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        List<Produto> produtos = null;
        try {
            produtos = objectMapper.readValue(response, new TypeReference<List<Produto>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("products", produtos);
        model.addAttribute("quantidadeCarrinho", atualizarNumeroCarrinho());
        return "ecommerce";
    }
    
    @GetMapping("/carrinho")
    public String listarItensCarrinho(Model model) {
        String url = "http://localhost:8080/items";
        String response = restTemplate.getForObject(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        List<Item> itensCarrinho = null;
        double totalCarrinho = 0;
        try {
            itensCarrinho = objectMapper.readValue(response, new TypeReference<List<Item>>() {});
            for(Item item: itensCarrinho) {
            	totalCarrinho += item.getProduto().getPreco();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("cartItems", itensCarrinho);
        model.addAttribute("totalCarrinho", totalCarrinho);
        model.addAttribute("quantidadeCarrinho", atualizarNumeroCarrinho());
        return "carrinho";
    }
    
    @GetMapping("/checkout")
    public String checkout(Model model) {
        int quantidadeCarrinho = 0;
        model.addAttribute("quantidadeCarrinho", atualizarNumeroCarrinho());
    	return "checkout";
    }
    
    @GetMapping("/pedido")
    public String listarPedidos(Model model) {
        String url = "http://localhost:8080/pedidos";  // Altere para a URL correta da sua API de pedidos
        String response = restTemplate.getForObject(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        List<Pedido> pedidos = null;
        try {
            pedidos = objectMapper.readValue(response, new TypeReference<List<Pedido>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        url = "http://localhost:8080/items";
        response = restTemplate.getForObject(url, String.class);

        objectMapper = new ObjectMapper();
        List<Item> itensCarrinho = null;
        double totalCarrinho = 0;
        try {
            itensCarrinho = objectMapper.readValue(response, new TypeReference<List<Item>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("itensCarrinho", itensCarrinho);
        model.addAttribute("quantidadeCarrinho", atualizarNumeroCarrinho());
        return "pedido";
    }
    
    @GetMapping("/admin")
    public String gerirPedidos(Model model) {
    	String url = "http://localhost:8080/pedidos";  // Altere para a URL correta da sua API de pedidos
        String response = restTemplate.getForObject(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        List<Pedido> pedidos = null;
        try {
            pedidos = objectMapper.readValue(response, new TypeReference<List<Pedido>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
        model.addAttribute("pedidos", pedidos);
        
        return "admin";
    }
    
    @GetMapping("/admin/editar-pedido")
    public String editarPedido(Model model) {
        
    	String url = "http://localhost:8080/pedidos/1";  // Altere para a URL correta da sua API de pedidos
        String response = restTemplate.getForObject(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        Pedido pedido = null;
        try {
            pedido = objectMapper.readValue(response, new TypeReference<Pedido>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
        model.addAttribute("pedido", pedido);
    	
        return "editar-pedido";
    }
}
