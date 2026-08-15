-- ============================================================
-- Gear Gest Garage — Schema (estrutura)
-- Recria o banco do zero, so a estrutura das tabelas (sem dados).
-- Para os dados iniciais, rode db/seed.sql em seguida.
--
-- Executar:
--   Get-Content db/schema.sql | docker exec -i gear_gest_mysql mysql -u root -proot
--   Get-Content db/seed.sql   | docker exec -i gear_gest_mysql mysql -u root -proot GearGestGarage
--
-- Ou recriar o volume Docker do zero (aplica os dois automaticamente):
--   docker compose down -v && docker compose up -d
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
    tipo VARCHAR(50) NOT NULL DEFAULT 'Carro',
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

CREATE TABLE usuario (
    id_usuario INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    cpf VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    id_oficina INT NOT NULL,
    PRIMARY KEY (cpf),
    UNIQUE KEY uq_usuario_id (id_usuario),
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
    endereco VARCHAR(255) NOT NULL DEFAULT '',
    id_usuario INT NOT NULL,
    PRIMARY KEY (id_funcionario),
    CONSTRAINT fk_funcionario_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
);

CREATE TABLE tipo_servico (
    id_tipo_servico INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_tipo_servico)
);

CREATE TABLE veiculo (
    id_veiculo INT NOT NULL AUTO_INCREMENT,
    placa VARCHAR(10) NOT NULL,
    codigo VARCHAR(20) NOT NULL DEFAULT '',
    id_cliente INT NOT NULL,
    id_modelo INT NOT NULL,
    PRIMARY KEY (placa),
    UNIQUE KEY uq_veiculo_id (id_veiculo),
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

-- Peças genéricas: independentes de modelo/veículo; aplicação específica fica no orçamento.
CREATE TABLE peca (
    id_peca INT NOT NULL AUTO_INCREMENT,
    nome_popular VARCHAR(150) NOT NULL,
    sistema VARCHAR(50) NOT NULL DEFAULT 'OUTROS',
    vida_util_tempo VARCHAR(50) NOT NULL DEFAULT 'Não informado',
    vida_util_km VARCHAR(50) NOT NULL DEFAULT 'Não informado',
    PRIMARY KEY (id_peca)
);

CREATE TABLE orcamento (
    id_orcamento INT NOT NULL AUTO_INCREMENT,
    valor DECIMAL(10, 2) NOT NULL,
    codigo VARCHAR(20) NOT NULL DEFAULT '',
    tipo VARCHAR(20) NOT NULL DEFAULT 'ENTRADA',
    responsavel VARCHAR(150) NOT NULL DEFAULT '',
    reclamacao VARCHAR(255) NOT NULL DEFAULT '',
    data_criacao DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
    id_peca INT,
    id_veiculo INT NOT NULL,
    id_cliente INT NOT NULL,
    id_funcionario INT,
    id_servico_revisao INT NULL,
    PRIMARY KEY (id_orcamento),
    CONSTRAINT fk_orcamento_peca FOREIGN KEY (id_peca) REFERENCES peca (id_peca),
    CONSTRAINT fk_orcamento_veiculo FOREIGN KEY (id_veiculo) REFERENCES veiculo (id_veiculo),
    CONSTRAINT fk_orcamento_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente),
    CONSTRAINT fk_orcamento_funcionario FOREIGN KEY (id_funcionario) REFERENCES funcionario (id_funcionario)
);

CREATE TABLE servico (
    id_servico INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL DEFAULT '',
    data_servico DATE NOT NULL,
    km_servico INT NOT NULL,
    titulo VARCHAR(45) NOT NULL,
    -- Sistema do veículo: REVISAO | MOTOR | TRANSMISSAO | DIRECAO | SUSPENSAO | FREIOS | ARREFECIMENTO | ELETRICA | OUTROS
    tipo_servico VARCHAR(30) NOT NULL DEFAULT 'OUTROS',
    -- Natureza da intervenção: PREVENTIVA | CORRETIVA
    tipo_manutencao VARCHAR(20) NOT NULL DEFAULT 'CORRETIVA',
    status VARCHAR(30) DEFAULT 'ABERTA',
    id_veiculo INT NOT NULL,
    id_oficina INT NOT NULL,
    id_orcamento INT NULL,
    PRIMARY KEY (id_servico),
    CONSTRAINT chk_tipo_servico CHECK (tipo_servico IN (
        'REVISAO','MOTOR','TRANSMISSAO','DIRECAO','SUSPENSAO','FREIOS',
        'ARREFECIMENTO','ELETRICA','OUTROS')),
    CONSTRAINT chk_tipo_manutencao CHECK (tipo_manutencao IN ('PREVENTIVA','CORRETIVA')),
    CONSTRAINT fk_servico_veiculo FOREIGN KEY (id_veiculo) REFERENCES veiculo (id_veiculo),
    CONSTRAINT fk_servico_oficina FOREIGN KEY (id_oficina) REFERENCES oficina (id_oficina),
    CONSTRAINT fk_servico_orcamento FOREIGN KEY (id_orcamento) REFERENCES orcamento (id_orcamento)
);

CREATE TABLE item_servico (
    id_item_servico INT NOT NULL AUTO_INCREMENT,
    etapa INT NOT NULL DEFAULT 0,
    codigo VARCHAR(20) NOT NULL DEFAULT '',
    descricao VARCHAR(255) NOT NULL,
    status VARCHAR(30) DEFAULT 'PENDENTE',
    tempo_gasto VARCHAR(50) NOT NULL DEFAULT '',
    data_realizacao DATE,
    id_peca INT NULL,
    id_servico INT NOT NULL,
    id_funcionario INT NULL,
    PRIMARY KEY (id_item_servico),
    CONSTRAINT fk_itemservico_peca FOREIGN KEY (id_peca) REFERENCES peca (id_peca),
    CONSTRAINT fk_itemservico_servico FOREIGN KEY (id_servico) REFERENCES servico (id_servico),
    CONSTRAINT fk_itemservico_funcionario FOREIGN KEY (id_funcionario) REFERENCES funcionario (id_funcionario)
);

-- Catálogo de serviços pré-definidos pelo mecânico
CREATE TABLE catalogo_servico (
    id_catalogo_servico INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    descricao VARCHAR(255) NOT NULL DEFAULT '',
    valor DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    tipo VARCHAR(20) NOT NULL DEFAULT 'PADRAO',           -- PADRAO | REVISAO
    tipo_manutencao VARCHAR(20) NOT NULL DEFAULT 'CORRETIVA', -- PREVENTIVA | CORRETIVA
    sistema VARCHAR(50) NOT NULL DEFAULT 'OUTROS',        -- MOTOR | TRANSMISSAO | DIRECAO | SUSPENSAO | FREIOS | ARREFECIMENTO | ELETRICA | ALIMENTACAO | OUTROS
    validade_km INT NULL,
    validade_meses INT NULL,
    PRIMARY KEY (id_catalogo_servico)
);

-- Peças padrão associadas a um item do catálogo (auto-atribuídas ao orçamento)
CREATE TABLE catalogo_servico_peca (
    id_catalogo_servico_peca INT NOT NULL AUTO_INCREMENT,
    id_catalogo_servico INT NOT NULL,
    id_peca INT NOT NULL,
    PRIMARY KEY (id_catalogo_servico_peca),
    CONSTRAINT fk_catpeca_catalogo FOREIGN KEY (id_catalogo_servico) REFERENCES catalogo_servico (id_catalogo_servico),
    CONSTRAINT fk_catpeca_peca FOREIGN KEY (id_peca) REFERENCES peca (id_peca)
);

-- Itens de serviço vinculados a um orçamento
CREATE TABLE orcamento_servico (
    id_orcamento_servico INT NOT NULL AUTO_INCREMENT,
    id_orcamento INT NOT NULL,
    id_catalogo_servico INT NOT NULL,
    valor_cobrado DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id_orcamento_servico),
    CONSTRAINT fk_orcservico_orcamento FOREIGN KEY (id_orcamento) REFERENCES orcamento (id_orcamento),
    CONSTRAINT fk_orcservico_catalogo FOREIGN KEY (id_catalogo_servico) REFERENCES catalogo_servico (id_catalogo_servico)
);

-- Peças a substituir vinculadas a um orçamento
CREATE TABLE orcamento_peca (
    id_orcamento_peca INT NOT NULL AUTO_INCREMENT,
    id_orcamento INT NOT NULL,
    id_peca INT NOT NULL,
    nome_tecnico VARCHAR(150) NOT NULL DEFAULT '',
    fabricante VARCHAR(150) NOT NULL DEFAULT '',
    valor DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (id_orcamento_peca),
    CONSTRAINT fk_orcpeca_orcamento FOREIGN KEY (id_orcamento) REFERENCES orcamento (id_orcamento),
    CONSTRAINT fk_orcpeca_peca FOREIGN KEY (id_peca) REFERENCES peca (id_peca)
);
