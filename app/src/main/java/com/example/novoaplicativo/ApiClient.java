package com.example.novoaplicativo;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ApiClient {

    private static final String BASE_URL = "http://10.10.28.44:7070";
    private final RequestQueue fila;

    public ApiClient(Context context) {
        fila = Volley.newRequestQueue(context.getApplicationContext());
    }

    public void listarProdutos(Sucesso<List<Produto>> sucesso, Falha falha ){
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, BASE_URL + "/api/produtos", null, resposta -> sucesso.executar(converterLista(resposta)),erro ->falha.executar(mensagemErro(erro)));

        fila.add(request);

    }
    public void buscarProduto(long id,Sucesso<Produto> sucesso,Falha falha){
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,BASE_URL + "/api/produto/" + id, null,
                resposta -> sucesso.executar(converterProduto(resposta)),erro-> falha.executar(mensagemErro(erro))
        );
        fila.add(request);
    }
    public void criarProduto(Produto produto,Sucesso<Produto> sucesso, Falha falha){
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,BASE_URL + "/api/produto/", converterJson(produto),
                resposta -> sucesso.executar(converterProduto(resposta)),
                erro -> falha.executar(mensagemErro(erro))
        );
        fila.add(request);
    }

    public void atualizarProduto(Produto produto,Sucesso<Produto> sucesso, Falha falha){
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,BASE_URL + "/api/produto/" + produto.getId(), converterJson(produto),
                resposta -> sucesso.executar(converterProduto(resposta)),
                erro -> falha.executar(mensagemErro(erro))
        );
        fila.add(request);
    }
    public void excluirProduto(long id, Sucesso<Void>sucesso,Falha falha){
        StringRequest request = new StringRequest(
                Request.Method.DELETE,BASE_URL + "?api/produtos/" +id,
                resposta -> sucesso.executar(null),erro->
                falha.executar((mensagemErro(erro)))
        );
        fila.add(request);
    }

    private List<Produto> converterLista(JSONArray array) {
        List<Produto> produtos = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.optJSONObject(i);
            if (json != null) {
                produtos.add(converterProduto(json));
            }
        }
        return produtos;
    }
    private Produto converterProduto(JSONObject json) {
        return new Produto(
                json.optLong("id"),
                json.optString("nome"),
                json.optDouble("preco"),
                json.optInt("quantidade")
        );
    }

    private JSONObject converterJson(Produto produto){
        JSONObject json = new JSONObject();
        try {
            json.put("nome", produto.getNome());
            json.put("preco", produto.getPreco());
            json.put("quantidade", produto.getQuantidade());
        }catch (JSONException ignored) {
        }

        return json;
    }

    private String mensagemErro(VolleyError erro){
        if (erro.networkResponse != null && erro.networkResponse.data != null){
            String corpo = new String(erro.networkResponse.data, StandardCharsets.UTF_8);

            try {
                return new JSONObject(corpo).optString("erro", "Erro HTTP" + erro.networkResponse.statusCode);
            } catch (JSONException e) {
                return "Erro HTTP" + erro.networkResponse.statusCode;
            }
        }
        return "Não foi possível conectar a API. Verifique se ela está executando";
    }


        public interface Sucesso<T>{

            void executar(T resultado);


        }

        public interface Falha{

            void executar(String mensagem);
        }
    }

