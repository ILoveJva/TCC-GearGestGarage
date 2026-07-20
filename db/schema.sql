-- ============================================================
-- Gear Gest Garage — Schema + Seed Unificado
-- Recria o banco do zero com a estrutura final e os dados iniciais.
--
-- Executar:
--   Get-Content db/schema.sql | docker exec -i gear_gest_mysql mysql -u root -proot
--
-- Ou recriar o volume Docker do zero:
--   docker compose down -v && docker compose up -d
-- ============================================================

DROP DATABASE IF EXISTS GearGestGarage;
CREATE DATABASE GearGestGarage DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;
USE GearGestGarage;

-- ============================================================
-- SCHEMA
-- ============================================================

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

-- ============================================================
-- SEED: Oficina, usuário e funcionário
-- ============================================================

INSERT INTO oficina (nome, endereco, telefone, cnpj)
VALUES ('Garagem Cecconi', 'Rua Itapetinga, 794 - Santa Luzia', '', '');

INSERT INTO usuario (nome, cpf, email, senha, telefone, id_oficina)
VALUES ('Arthur Cecconi', '00000000000', 'oficina@geargest.com', '123456', '(11) 90000-0000', 1);

INSERT INTO funcionario (nome, cargo, endereco, id_usuario)
VALUES ('Arthur Cecconi', 'Gerente', '', 1);

-- ============================================================
-- SEED: Catálogo de Serviços
-- ============================================================

INSERT INTO catalogo_servico
    (nome, descricao, valor, tipo, tipo_manutencao, sistema, validade_km, validade_meses)
VALUES

-- PREVENTIVA — MOTOR
('Troca de óleo e filtro de óleo',                     '', 120.00, 'PADRAO', 'PREVENTIVA', 'MOTOR',          5000,  6),
('Troca de filtro de ar',                              '', 80.00,  'PADRAO', 'PREVENTIVA', 'MOTOR',          15000, 12),
('Troca de filtro de combustível',                     '', 100.00, 'PADRAO', 'PREVENTIVA', 'MOTOR',          20000, 24),
('Troca de velas de ignição',                          '', 200.00, 'PADRAO', 'PREVENTIVA', 'MOTOR',          20000, 24),
('Troca de correia dentada (kit distribuição)',        '', 600.00, 'PADRAO', 'PREVENTIVA', 'MOTOR',          60000, 48),
('Troca de correia do alternador',                     '', 250.00, 'PADRAO', 'PREVENTIVA', 'MOTOR',          40000, 36),
('Troca de filtro de cabine (ar-cond.)',               '', 90.00,  'PADRAO', 'PREVENTIVA', 'MOTOR',          15000, 12),
('Regulagem de válvulas',                              '', 350.00, 'PADRAO', 'PREVENTIVA', 'MOTOR',          30000, 24),
('Limpeza de bicos injetores',                         '', 300.00, 'PADRAO', 'PREVENTIVA', 'MOTOR',          20000, 18),
('Inspeção de correia dentada',                        '', 80.00,  'PADRAO', 'PREVENTIVA', 'MOTOR',          30000, 24),
('Inspeção de corrente de comando',                    '', 100.00, 'PADRAO', 'PREVENTIVA', 'MOTOR',          30000, 24),
('Troca de kit corrente de comando (corrente+tensor+guia)', '', 1200.00, 'PADRAO', 'PREVENTIVA', 'MOTOR',   80000, 60),
('Troca de tensor da correia dentada',                 '', 280.00, 'PADRAO', 'PREVENTIVA', 'MOTOR',          60000, 48),
('Troca de rolamento tensionador da correia',          '', 200.00, 'PADRAO', 'PREVENTIVA', 'MOTOR',          60000, 48),
('Troca de kit correia dentada com bomba d\'água',     '', 900.00, 'PADRAO', 'PREVENTIVA', 'MOTOR',          60000, 48),
('Limpeza de bicos injetores multiponto (ultrassom)',  '', 380.00, 'PADRAO', 'PREVENTIVA', 'MOTOR',          20000, 18),
('Limpeza de válvulas de admissão (injeção direta)',   '', 500.00, 'PADRAO', 'PREVENTIVA', 'MOTOR',          30000, 24),
('Troca de filtro de combustível diesel',              '', 150.00, 'PADRAO', 'PREVENTIVA', 'MOTOR',          10000, 12),
('Limpeza de filtro de partículas diesel (DPF)',       '', 450.00, 'PADRAO', 'PREVENTIVA', 'MOTOR',          40000, 24),
('Limpeza e regulagem de carburador',                  '', 200.00, 'PADRAO', 'PREVENTIVA', 'MOTOR',          10000, 12),

-- PREVENTIVA — TRANSMISSÃO
('Troca de óleo de câmbio manual',                     '', 200.00, 'PADRAO', 'PREVENTIVA', 'TRANSMISSAO',    40000, 36),
('Troca de fluido de câmbio automático (ATF)',         '', 350.00, 'PADRAO', 'PREVENTIVA', 'TRANSMISSAO',    40000, 36),
('Troca de coifas de homocinética',                    '', 400.00, 'PADRAO', 'PREVENTIVA', 'TRANSMISSAO',    60000, 48),
('Regulagem de embreagem',                             '', 120.00, 'PADRAO', 'PREVENTIVA', 'TRANSMISSAO',    20000, 18),

-- PREVENTIVA — DIREÇÃO
('Troca de fluido de direção hidráulica',              '', 150.00, 'PADRAO', 'PREVENTIVA', 'DIRECAO',        40000, 36),

-- PREVENTIVA — SUSPENSÃO
('Rodízio de pneus',                                   '', 80.00,  'PADRAO', 'PREVENTIVA', 'SUSPENSAO',      10000,  6),
('Alinhamento de rodas',                               '', 120.00, 'PADRAO', 'PREVENTIVA', 'SUSPENSAO',      10000,  6),
('Balanceamento de rodas',                             '', 100.00, 'PADRAO', 'PREVENTIVA', 'SUSPENSAO',      10000,  6),
('Inspeção de amortecedores',                          '', 80.00,  'PADRAO', 'PREVENTIVA', 'SUSPENSAO',      30000, 24),
('Inspeção de rolamentos de roda',                     '', 80.00,  'PADRAO', 'PREVENTIVA', 'SUSPENSAO',      40000, 36),

-- PREVENTIVA — FREIOS
('Troca de fluido de freio (DOT)',                     '', 130.00, 'PADRAO', 'PREVENTIVA', 'FREIOS',         20000, 24),
('Troca de pastilhas de freio',                        '', 300.00, 'PADRAO', 'PREVENTIVA', 'FREIOS',         30000, 24),

-- PREVENTIVA — ARREFECIMENTO
('Troca de fluido de arrefecimento',                   '', 150.00, 'PADRAO', 'PREVENTIVA', 'ARREFECIMENTO',  30000, 24),
('Limpeza de radiador',                                '', 200.00, 'PADRAO', 'PREVENTIVA', 'ARREFECIMENTO',  30000, 24),
('Inspeção de mangueiras de arrefecimento',            '', 80.00,  'PADRAO', 'PREVENTIVA', 'ARREFECIMENTO',  20000, 18),
('Troca de termostato',                                '', 250.00, 'PADRAO', 'PREVENTIVA', 'ARREFECIMENTO',  60000, 48),

-- PREVENTIVA — ELÉTRICA
('Teste e carga de bateria',                           '', 60.00,  'PADRAO', 'PREVENTIVA', 'ELETRICA',       20000, 12),
('Inspeção do sistema de carga (alternador)',          '', 80.00,  'PADRAO', 'PREVENTIVA', 'ELETRICA',       20000, 12),
('Recarga de ar condicionado',                         '', 200.00, 'PADRAO', 'PREVENTIVA', 'ELETRICA',       NULL,  18),
('Inspeção de lâmpadas e sinalização',                '', 50.00,  'PADRAO', 'PREVENTIVA', 'ELETRICA',       10000,  6),

-- CORRETIVA — MOTOR
('Troca de junta do cabeçote',                         '', 1200.00,'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Retífica de motor',                                  '', 3500.00,'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de bomba de óleo',                             '', 500.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de bomba de combustível',                      '', 600.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de sensor lambda (oxigênio)',                  '', 400.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de sensor de temperatura',                     '', 250.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de sensor de rotação (CKP)',                   '', 350.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de corpo de borboleta',                        '', 500.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de módulo de ignição (bobina)',                '', 350.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de ECU / módulo de injeção',                   '', 800.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de catalisador',                               '', 700.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de corrente de comando',                       '', 800.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de tensor de corrente de comando',             '', 350.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de guia de corrente de comando',               '', 280.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Reparo de ruído em corrente de comando',             '', 200.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de bicos injetores multiponto',                '', 600.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de regulador de pressão de combustível',       '', 350.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Diagnóstico de sistema de injeção multiponto',       '', 200.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de injetores de injeção direta',               '', 1000.00,'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de bomba de alta pressão (injeção direta)',    '', 1200.00,'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de rail de combustível (injeção direta)',      '', 700.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Diagnóstico de sistema de injeção direta',           '', 200.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Limpeza de injetores diesel (common rail)',          '', 700.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de injetores diesel',                          '', 1500.00,'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de bomba injetora diesel',                     '', 1800.00,'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de bomba de alta pressão diesel',              '', 1400.00,'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Reparo de sistema common rail',                      '', 900.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de filtro de partículas (DPF)',                '', 1000.00,'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de carburador',                                '', 700.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Regulagem de mistura (carburador)',                  '', 120.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),
('Troca de kit de reparo de carburador',               '', 300.00, 'REVISAO','CORRETIVA',  'MOTOR',          NULL, NULL),

-- CORRETIVA — TRANSMISSÃO
('Troca de homocinética / semi-eixo',                  '', 600.00, 'REVISAO','CORRETIVA',  'TRANSMISSAO',    NULL, NULL),
('Reparo de câmbio manual',                            '', 1500.00,'REVISAO','CORRETIVA',  'TRANSMISSAO',    NULL, NULL),
('Reparo de câmbio automático',                        '', 2500.00,'REVISAO','CORRETIVA',  'TRANSMISSAO',    NULL, NULL),
('Troca de disco e platô de embreagem',                '', 800.00, 'REVISAO','CORRETIVA',  'TRANSMISSAO',    NULL, NULL),
('Troca de rolamento de câmbio',                       '', 400.00, 'REVISAO','CORRETIVA',  'TRANSMISSAO',    NULL, NULL),

-- CORRETIVA — DIREÇÃO
('Troca de bomba de direção hidráulica',               '', 700.00, 'REVISAO','CORRETIVA',  'DIRECAO',        NULL, NULL),
('Troca de cremalheira de direção',                    '', 900.00, 'REVISAO','CORRETIVA',  'DIRECAO',        NULL, NULL),
('Troca de coluna de direção',                         '', 600.00, 'REVISAO','CORRETIVA',  'DIRECAO',        NULL, NULL),
('Troca de pivô / terminal de direção',                '', 300.00, 'REVISAO','CORRETIVA',  'DIRECAO',        NULL, NULL),
('Troca de caixa de direção',                          '', 1200.00,'REVISAO','CORRETIVA',  'DIRECAO',        NULL, NULL),

-- CORRETIVA — SUSPENSÃO
('Troca de cubo e rolamento de roda',                  '', 500.00, 'REVISAO','CORRETIVA',  'SUSPENSAO',      NULL, NULL),
('Troca de mola de suspensão',                         '', 400.00, 'REVISAO','CORRETIVA',  'SUSPENSAO',      NULL, NULL),
('Troca de amortecedor',                               '', 500.00, 'REVISAO','CORRETIVA',  'SUSPENSAO',      NULL, NULL),
('Troca de barra estabilizadora',                      '', 400.00, 'REVISAO','CORRETIVA',  'SUSPENSAO',      NULL, NULL),
('Troca de buchas de suspensão',                       '', 350.00, 'REVISAO','CORRETIVA',  'SUSPENSAO',      NULL, NULL),
('Troca de braço de suspensão / bandeja',              '', 600.00, 'REVISAO','CORRETIVA',  'SUSPENSAO',      NULL, NULL),

-- CORRETIVA — FREIOS
('Troca de disco de freio',                            '', 500.00, 'REVISAO','CORRETIVA',  'FREIOS',         NULL, NULL),
('Troca de tambor de freio',                           '', 350.00, 'REVISAO','CORRETIVA',  'FREIOS',         NULL, NULL),
('Troca de cilindro de roda',                          '', 250.00, 'REVISAO','CORRETIVA',  'FREIOS',         NULL, NULL),
('Troca de cilindro mestre de freio',                  '', 400.00, 'REVISAO','CORRETIVA',  'FREIOS',         NULL, NULL),
('Reparo de freio de estacionamento',                  '', 200.00, 'REVISAO','CORRETIVA',  'FREIOS',         NULL, NULL),

-- CORRETIVA — ARREFECIMENTO
('Troca de radiador',                                  '', 800.00, 'REVISAO','CORRETIVA',  'ARREFECIMENTO',  NULL, NULL),
('Troca de bomba d\'água',                             '', 500.00, 'REVISAO','CORRETIVA',  'ARREFECIMENTO',  NULL, NULL),
('Reparo de vazamento de arrefecimento',               '', 300.00, 'REVISAO','CORRETIVA',  'ARREFECIMENTO',  NULL, NULL),
('Troca de válvula termostática',                      '', 250.00, 'REVISAO','CORRETIVA',  'ARREFECIMENTO',  NULL, NULL),
('Troca de eletroventilador',                          '', 400.00, 'REVISAO','CORRETIVA',  'ARREFECIMENTO',  NULL, NULL),
('Troca de mangueiras de arrefecimento',               '', 200.00, 'REVISAO','CORRETIVA',  'ARREFECIMENTO',  NULL, NULL),

-- CORRETIVA — ELÉTRICA
('Troca de alternador',                                '', 800.00, 'REVISAO','CORRETIVA',  'ELETRICA',       NULL, NULL),
('Troca de motor de arranque',                         '', 600.00, 'REVISAO','CORRETIVA',  'ELETRICA',       NULL, NULL),
('Reparo de compressor de ar condicionado',            '', 700.00, 'REVISAO','CORRETIVA',  'ELETRICA',       NULL, NULL),
('Troca de compressor de ar condicionado',             '', 1200.00,'REVISAO','CORRETIVA',  'ELETRICA',       NULL, NULL),
('Reparo de curto-circuito elétrico',                  '', 300.00, 'REVISAO','CORRETIVA',  'ELETRICA',       NULL, NULL),
('Troca de sensor ABS / ESP',                          '', 500.00, 'REVISAO','CORRETIVA',  'ELETRICA',       NULL, NULL),
('Reparo de vidro elétrico',                           '', 250.00, 'REVISAO','CORRETIVA',  'ELETRICA',       NULL, NULL),
('Reparo de travas elétricas',                         '', 200.00, 'REVISAO','CORRETIVA',  'ELETRICA',       NULL, NULL),
('Diagnóstico e reparo de falhas eletrônicas',         '', 350.00, 'REVISAO','CORRETIVA',  'ELETRICA',       NULL, NULL),
('Troca de painel de instrumentos',                    '', 900.00, 'REVISAO','CORRETIVA',  'ELETRICA',       NULL, NULL),

-- CORRETIVA — OUTROS
('Reparo de escapamento / silencioso',                 '', 400.00, 'REVISAO','CORRETIVA',  'OUTROS',         NULL, NULL),
('Polimento e cristalização',                          '', 300.00, 'REVISAO','CORRETIVA',  'OUTROS',         NULL, NULL),
('Higienização de estofados e cabine',                 '', 250.00, 'REVISAO','CORRETIVA',  'OUTROS',         NULL, NULL),
('Reparo de para-choque',                              '', 350.00, 'REVISAO','CORRETIVA',  'OUTROS',         NULL, NULL),
('Reparo de lataria',                                  '', 500.00, 'REVISAO','CORRETIVA',  'OUTROS',         NULL, NULL),
('Reparo de vidros (trinca / quebrado)',               '', 300.00, 'REVISAO','CORRETIVA',  'OUTROS',         NULL, NULL);
