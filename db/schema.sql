-- ============================================================
-- Gear Gest Garage - Banco GGG02 (base) + ajustes minimos
-- Banco: GearGestGarage (conforme o script original enviado)
-- ============================================================
-- Execute:  mysql -u root -p < db/schema.sql
--
-- AJUSTES aplicados sobre o GGG02 original (marcados com  -- [AJUSTE]):
--   * servico.status        : status da Ordem de Servico (ABERTA/EM_ANDAMENTO/CONCLUIDA)
--   * item_servico.status   : status de cada etapa (PENDENTE/CONCLUIDO)
--   * item_servico.data_realizacao : data de execucao da etapa
-- Estes campos sao necessarios para as telas de Servicos/O.S. do sistema.
-- ============================================================

DROP DATABASE IF EXISTS GearGestGarage;
CREATE DATABASE GearGestGarage DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;
USE GearGestGarage;

CREATE TABLE montadora (
    id_montadora INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    pais_origem VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_montadora)
);

CREATE TABLE modelo (
    id_modelo INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    ano INT NOT NULL,
    id_montadora INT NOT NULL,
    PRIMARY KEY (id_modelo),
    CONSTRAINT fk_modelo_montadora FOREIGN KEY (id_montadora) REFERENCES montadora (id_montadora)
);

CREATE TABLE oficina (
    id_oficina INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    cnpj VARCHAR(20) NOT NULL,
    PRIMARY KEY (id_oficina)
);

CREATE TABLE fabricante_peca (
    id_fabricante_peca INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    pais VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_fabricante_peca)
);

CREATE TABLE usuario (
    id_usuario INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    id_oficina INT NOT NULL,
    PRIMARY KEY (id_usuario),
    CONSTRAINT fk_usuario_oficina FOREIGN KEY (id_oficina) REFERENCES oficina (id_oficina)
);

CREATE TABLE cliente (
    id_cliente INT NOT NULL AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    PRIMARY KEY (id_cliente),
    CONSTRAINT fk_cliente_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
);

CREATE TABLE funcionario (
    id_funcionario INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    cargo VARCHAR(100) NOT NULL,
    id_usuario INT NOT NULL,
    PRIMARY KEY (id_funcionario),
    CONSTRAINT fk_funcionario_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
);

CREATE TABLE veiculo (
    id_veiculo INT NOT NULL AUTO_INCREMENT,
    placa VARCHAR(10) NOT NULL,
    tipo_veiculo VARCHAR(50) NOT NULL,
    id_cliente INT NOT NULL,
    id_modelo INT NOT NULL,
    PRIMARY KEY (id_veiculo),
    CONSTRAINT fk_veiculo_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente),
    CONSTRAINT fk_veiculo_modelo FOREIGN KEY (id_modelo) REFERENCES modelo (id_modelo)
);

CREATE TABLE detalhes_veiculo (
    id_detalhes INT NOT NULL AUTO_INCREMENT,
    id_veiculo INT NOT NULL,
    motor VARCHAR(100),
    cambio VARCHAR(100),
    direcao VARCHAR(100),
    sistema_freios VARCHAR(100),
    cor VARCHAR(50),
    vin VARCHAR(50),
    PRIMARY KEY (id_detalhes),
    CONSTRAINT fk_detalhes_veiculo FOREIGN KEY (id_veiculo) REFERENCES veiculo (id_veiculo) ON DELETE CASCADE
);

CREATE TABLE peca (
    id_peca INT NOT NULL AUTO_INCREMENT,
    nome_peca VARCHAR(150) NOT NULL,
    vida_util_tempo VARCHAR(50) NOT NULL,
    vida_util_km VARCHAR(50) NOT NULL,
    id_fabricante_peca INT NOT NULL,
    id_veiculo INT NOT NULL,
    PRIMARY KEY (id_peca),
    CONSTRAINT fk_peca_fabricantepeca FOREIGN KEY (id_fabricante_peca) REFERENCES fabricante_peca (id_fabricante_peca),
    CONSTRAINT fk_peca_veiculo FOREIGN KEY (id_veiculo) REFERENCES veiculo (id_veiculo)
);

CREATE TABLE orcamento (
    id_orcamento INT NOT NULL AUTO_INCREMENT,
    valor DECIMAL(10, 2) NOT NULL,
    id_peca INT NOT NULL,
    id_veiculo INT NOT NULL,
    id_cliente INT NOT NULL,
    id_funcionario INT NOT NULL,
    PRIMARY KEY (id_orcamento),
    CONSTRAINT fk_orcamento_peca FOREIGN KEY (id_peca) REFERENCES peca (id_peca),
    CONSTRAINT fk_orcamento_veiculo FOREIGN KEY (id_veiculo) REFERENCES veiculo (id_veiculo),
    CONSTRAINT fk_orcamento_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente),
    CONSTRAINT fk_orcamento_funcionario FOREIGN KEY (id_funcionario) REFERENCES funcionario (id_funcionario)
);

CREATE TABLE servico (
    id_servico INT NOT NULL AUTO_INCREMENT,
    data_servico DATE NOT NULL,
    km_servico INT NOT NULL,
    titulo VARCHAR(45) NOT NULL,
    tipo_servico VARCHAR(100) NOT NULL,
    status VARCHAR(30) DEFAULT 'ABERTA',          -- [AJUSTE] status da O.S.
    id_veiculo INT NOT NULL,
    id_oficina INT NOT NULL,
    id_orcamento INT NOT NULL,
    PRIMARY KEY (id_servico),
    CONSTRAINT fk_servico_veiculo FOREIGN KEY (id_veiculo) REFERENCES veiculo (id_veiculo),
    CONSTRAINT fk_servico_oficina FOREIGN KEY (id_oficina) REFERENCES oficina (id_oficina),
    CONSTRAINT fk_servico_orcamento FOREIGN KEY (id_orcamento) REFERENCES orcamento (id_orcamento)
);

CREATE TABLE item_servico (
    id_item_servico INT NOT NULL AUTO_INCREMENT,
    descricao VARCHAR(255) NOT NULL,
    status VARCHAR(30) DEFAULT 'PENDENTE',         -- [AJUSTE] status da etapa
    data_realizacao DATE,                          -- [AJUSTE] data de execucao
    id_peca INT NOT NULL,
    id_servico INT NOT NULL,
    id_funcionario INT NOT NULL,
    PRIMARY KEY (id_item_servico),
    CONSTRAINT fk_itemservico_peca FOREIGN KEY (id_peca) REFERENCES peca (id_peca),
    CONSTRAINT fk_itemservico_servico FOREIGN KEY (id_servico) REFERENCES servico (id_servico),
    CONSTRAINT fk_itemservico_funcionario FOREIGN KEY (id_funcionario) REFERENCES funcionario (id_funcionario)
);
