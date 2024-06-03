USE master
CREATE DATABASE CrudProdutoSpringRest
USE CrudProdutoSpringRest

INSERT INTO produto VALUES
(1, 'Smart TV', 50, 1299.99),
(2, 'Notebook', 30, 2399.00),
(3, 'Forno Micro-ondas',80 , 299.90),
(4, 'Liquidificador',100, 79.99),
(5, 'Geladeira',20,  2599.00),
(6, 'Máquina de Lavar Roupa',40, 1899.99),
(7, 'Aspirador de Pó',70, 159.50),
(8, 'Secador de Cabelo',60, 49.90),
(9, 'Câmera Fotográfica',15, 899.00),
(10, 'Console de Videogame',25, 499.99),
(11, 'Roteador Wi-Fi',55, 89.90),
(12, 'Fone de Ouvido Bluetooth',85, 129.99),
(13, 'Tablet',45, 349.00),
(14, 'Monitor de Computador',35, 299.90),
(15, 'Caixa de Som Bluetooth',75, 79.99),
(16, 'Ventilador',90, 129.90),
(17, 'Câmera de Segurança',10, 199.00),
(18, 'Carregador Portátil',65, 39.90),
(19, 'Impressora',30, 199.99),
(20, 'Forno Elétrico',20, 399.90);
GO

CREATE PROCEDURE sp_iud_produto 
    @acao CHAR(1), 
    @codigo INT, 
    @nome VARCHAR(30), 
    @qtdEstoque INT,
	@valorUnitario DECIMAL(7,2),
    @saida VARCHAR(100) OUTPUT
AS
BEGIN
    IF (@acao = 'I')
    BEGIN
        IF EXISTS (SELECT 1 FROM produto WHERE codigo = @codigo)
        BEGIN
            SET @saida = 'Erro: Produto já existe com o código especificado'
            RETURN
        END
        ELSE
        BEGIN
            INSERT INTO produto (codigo, nome, qtdEstoque, valorUnitario) 
            VALUES (@codigo, @nome,@qtdEstoque, @valorUnitario)
            SET @saida = 'Produto inserido com sucesso'
        END
    END
    ELSE IF (@acao = 'U')
    BEGIN
        IF EXISTS (SELECT 1 FROM produto WHERE codigo = @codigo)
        BEGIN
            UPDATE produto 
            SET nome = @nome, qtdEstoque = @qtdEstoque, valorUnitario = @valorUnitario
            WHERE codigo = @codigo
            SET @saida = 'Produto alterado com sucesso'
        END
        ELSE
        BEGIN
            SET @saida = 'Erro: Produto não encontrado para alteração'
            RETURN
        END
    END
    ELSE IF (@acao = 'D')
    BEGIN
        IF EXISTS (SELECT 1 FROM produto WHERE codigo = @codigo)
        BEGIN
            DELETE FROM produto WHERE codigo = @codigo
            SET @saida = 'Produto excluído com sucesso'
        END
        ELSE
        BEGIN
            SET @saida = 'Erro: Produto não encontrado para exclusão'
            RETURN
        END
    END
    ELSE
    BEGIN
        SET @saida = 'Erro: Operação inválida'
        RETURN
    END
END

CREATE FUNCTION dbo.fn_quantidadeEstoque (@qtdMinima INT)
RETURNS INT
AS
BEGIN
    DECLARE @qtdEstoqueAbaixo INT;
    
    SELECT @qtdEstoqueAbaixo = COUNT(*)
    FROM produto
    WHERE qtdEstoque < @qtdMinima;

    RETURN @qtdEstoqueAbaixo;
END;

SELECT dbo.fn_quantidadeEstoque(100) AS codigo;

CREATE FUNCTION fn_produtosEstoque (@valor INT)
RETURNS TABLE
AS
RETURN (
    SELECT codigo, nome, qtdEstoque, valorUnitario
    FROM produto
    WHERE qtdEstoque < @valor
);

SELECT * FROM fn_produtosEstoque(16)