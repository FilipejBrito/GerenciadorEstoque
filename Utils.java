package org.example;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Scanner;

public class Utils {
    static Scanner teclado = new Scanner(System.in);

    public static Connection conectar(){
        String CLASSE_DRIVER = "com.mysql.cj.jdbc.Driver";
        String USUARIO = "filipe";
        String SENHA = "brito";
        String URL_SERVIDOR = "jdbc:mysql://localhost:3306/jmysql?useSSL=false";

        try {
            Class.forName(CLASSE_DRIVER);
            return DriverManager.getConnection(URL_SERVIDOR, USUARIO, SENHA);
        } catch (ClassNotFoundException e) {
            System.out.println("Verifique o driver de conexão.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Verifique se o servidor está ativo.");
            e.printStackTrace();
        }
        return null;
    }

    public static void desconectar(Connection conexao){
        if(conexao != null){
            try {
                conexao.close();
                System.out.println("Desconectado.");
            } catch (SQLException e) {
                System.out.println("Não foi possível fechar a conexão.");
                e.printStackTrace();
            }
        }
    }

    public static void listar(){
        String BUSCAR_TODOS = "SELECT * FROM produtos";

        try {
            Connection conexao = Utils.conectar();
            PreparedStatement produtos = conexao.prepareStatement(BUSCAR_TODOS);
            ResultSet res = produtos.executeQuery();

            res.last();
            int qtd = res.getRow();
            res.beforeFirst();

            if (qtd > 0) {
                System.out.println("Listando produtos:");
                System.out.println("-----------------");

                while (res.next()) {
                    System.out.println("ID: " + res.getInt(1));
                    System.out.println("Produto: " + res.getString(2));
                    System.out.println("Preço: " + res.getFloat(3));
                    System.out.println("Estoque: " + res.getInt(4));
                    System.out.println("--------------------");
                }
            } else {
                System.out.println("Não existem produtos cadastrados.");
            }

            produtos.close();
            desconectar(conexao);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao buscar produtos.");
        }
    }

    public static void inserir(){
        System.out.println("Informe o nome do produto");
        String nome = teclado.nextLine();

        System.out.println("Informe o preço do produto");
        BigDecimal preco = teclado.nextBigDecimal();
        teclado.nextLine();

        System.out.println("Informe a quantidade em estoque");
        int estoque = teclado.nextInt();
        teclado.nextLine();

        String INSERIR = "INSERT INTO produtos (nome, preco, estoque) VALUES (?, ?, ?)";

        try{
            Connection conexao = conectar();
            PreparedStatement salvar = conexao.prepareStatement(INSERIR); // protege o sistema do sql injection

            salvar.setString(1, nome);
            salvar.setBigDecimal(2, preco);
            salvar.setInt(3, estoque);

            salvar.executeUpdate();
            salvar.close();
            desconectar(conexao);

            System.out.println("O produto " + nome + " foi inserido com sucesso");

        }catch(Exception e){
            e.printStackTrace();
            System.err.println("Erro ao salvar produto");
            System.exit(-42);
        }

    }

    public static void atualizar(){
        System.out.println("Informe o codigo do produto");
        int id = Integer.parseInt(teclado.nextLine());

        String BUSCAR_POR_ID = "SELECT * FROM produtos WHERE id=?";

        try{
            Connection conexao = conectar();
            PreparedStatement produto = conexao.prepareStatement(BUSCAR_POR_ID);

            produto.setInt(1, id);
            ResultSet res = produto.executeQuery();

            res.last();
            int qtd = res. getRow();
            res.beforeFirst();


            if(qtd > 0){
                System.out.println("Informe o nome do produto");
                String nome = teclado.nextLine();

                System.out.println("Informe o preço do produto");
                BigDecimal preco = teclado.nextBigDecimal();
                teclado.nextLine();

                System.out.println("Informe o estoque do produto");
                int estoque = teclado.nextInt();
                teclado.nextLine();

                String ATUALIZAR = "UPDATE produto SET nome=?, preco=?, estoque=? WHERE id=?";
                PreparedStatement upd = conexao.prepareStatement(ATUALIZAR);

                upd.setString(1, nome);
                upd.setBigDecimal(2, preco);
                upd.setInt(3, estoque);
                upd.setInt(4, id);

                upd.executeUpdate();
                upd.close();
                desconectar(conexao);
                System.out.println("Produto atualizado com sucesso");
            }else{
                System.out.println("Produto não encontrado");
            }
        }catch(Exception e){
            e.printStackTrace();
            System.err.println("Erro ao atualizar produto");
            System.exit(-42);
        }
    }

    public static void deletar() {
        String DELETAR = "DELETE FROM produtos WHERE id = ?";
        String BUSCAR_POR_ID = "SELECT * FROM produtos WHERE id = ?";

        System.out.println("Informe o código do produto:");
        int id = Integer.parseInt(teclado.nextLine());
        Connection conexao = null;
        PreparedStatement produto = null;
        PreparedStatement del = null;
        ResultSet res = null;

        try {
            conexao = conectar();

            // Consulta para verificar se o produto existe
            produto = conexao.prepareStatement(BUSCAR_POR_ID);
            produto.setInt(1, id);
            res = produto.executeQuery();

            if (res.next()) { // Verifica se o produto foi encontrado
                // Deleta o produto
                del = conexao.prepareStatement(DELETAR);
                del.setInt(1, id);
                int linhasAfetadas = del.executeUpdate();
                System.out.println("Produto deletado. Linhas afetadas: " + linhasAfetadas);
            } else {
                System.out.println("Produto não encontrado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao deletar produto");
        } finally {
            // Fecha os recursos
            try {
                if (res != null) res.close();
                if (produto != null) produto.close();
                if (del != null) del.close();
                if (conexao != null) desconectar(conexao); // Supondo que desconectar() é responsável por fechar a conexão
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static void menu() {
        System.out.println("==== Gerenciamento de Produtos ====");
        System.out.println("Selecione uma opção:");
        System.out.println("1- Listar produtos");
        System.out.println("2- Inserir produtos");
        System.out.println("3- Atualizar produtos");
        System.out.println("4- Deletar produtos");

        int opcao = Integer.parseInt(teclado.nextLine());
        switch (opcao) {
            case 1:
                listar();
                break;
            case 2:
                inserir();
                break;
            case 3:
                atualizar();
                break;
            case 4:
                deletar();
                break;
            default:
                System.out.println("Opção inválida.");
                break;
        }
    }

    public static void main(String[] args) {
        menu();
    }
}
