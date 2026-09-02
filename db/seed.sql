-- ============================================================
-- BLOCO ADICIONAL - dados operacionais da OFICINA 1 (Garagem Cecconi)
-- Mecanicos, clientes, veiculos, pecas, orcamentos e ordens de servico.
-- Anexar ao final de db/seed.sql (depois do catalogo_servico).
-- ============================================================

USE GearGestGarage;

-- ============================================================
-- Mecanicos e demais funcionarios da oficina 1 (usuario ids 5..9)
-- ============================================================

INSERT INTO usuario (nome, cpf, email, senha, telefone, id_oficina)
VALUES
('Marcos Vinícius Prado', '31122233344', 'marcos.prado@geargest.com', 'marcos123', '(11) 97111-0001', 1),
('Fábio Nakamura', '31122233355', 'fabio.nakamura@geargest.com', 'fabio123', '(11) 97111-0002', 1),
('Diego Barbosa', '31122233366', 'diego.barbosa@geargest.com', 'diego123', '(11) 97111-0003', 1),
('Leandro Souza', '31122233377', 'leandro.souza@geargest.com', 'leandro123', '(11) 97111-0004', 1),
('Paulo Henrique Dias', '31122233388', 'paulo.dias@geargest.com', 'paulo123', '(11) 97111-0005', 1);

INSERT INTO funcionario (nome, cargo, endereco, id_usuario)
VALUES
('Marcos Vinícius Prado', 'Mecânico Chefe', 'Rua das Oliveiras, 120 - Santa Luzia', 5),
('Fábio Nakamura', 'Mecânico', 'Av. Brasil, 455 - Centro', 6),
('Diego Barbosa', 'Mecânico', 'Rua Piratininga, 88 - Vila Nova', 7),
('Leandro Souza', 'Eletricista Automotivo', 'Rua Dom Pedro I, 341 - Jardim Aurora', 8),
('Paulo Henrique Dias', 'Auxiliar Mecânico', 'Rua das Acácias, 27 - Santa Luzia', 9);

-- ============================================================
-- Clientes da oficina 1 (usuario ids 10..13 -> cliente ids 2..5)
-- ============================================================

INSERT INTO usuario (nome, cpf, email, senha, telefone, id_oficina)
VALUES
('Maria Fernanda Lopes', '45566677788', 'mariaflopes@gmail.com', 'maria123', '(11) 98120-1122', 1),
('Carlos Eduardo Ramos', '45566677799', 'carlosramos@gmail.com', 'carlos123', '(11) 98120-1133', 1),
('Beatriz Almeida Nunes', '45566677800', 'beatriznunes@gmail.com', 'beatriz123', '(11) 98120-1144', 1),
('Ricardo Tanaka', '45566677811', 'ricardotanaka@gmail.com', 'ricardo123', '(11) 98120-1155', 1);

-- cliente 1 (Joao Silva, usuario 2) ja existe no seed original.
INSERT INTO cliente (id_usuario) VALUES (10), (11), (12), (13);

-- ============================================================
-- Montadoras e modelos complementares
-- ============================================================

INSERT INTO montadora (nome, pais_origem)
VALUES ('Volkswagen', 'Alemanha'),
('Chevrolet', 'EUA'),
('Toyota', 'Japão');

INSERT INTO modelo (nome, ano, tipo, id_montadora)
VALUES ('Gol', 2014, 'Carro', 4),
('Onix', 2019, 'Carro', 5),
('Corolla', 2018, 'Carro', 6),
('Civic', 2015, 'Carro', 1),
('Ka', 2017, 'Carro', 2),
('Uno', 2012, 'Carro', 3);

-- ============================================================
-- Veiculos dos clientes da oficina 1
-- ============================================================

INSERT INTO veiculo (placa, codigo, id_cliente, id_modelo)
VALUES
('GGG1A23', '0001', 1, 1),
('JKL2B34', '0002', 1, 5),
('MNO3C45', '0003', 2, 6),
('PQR4D56', '0004', 2, 3),
('STU5E67', '0005', 3, 7),
('VWX6F78', '0006', 3, 9),
('YZA7G89', '0007', 4, 8),
('BCD8H90', '0008', 5, 10);

INSERT INTO detalhes_veiculo (id_veiculo, motor, cambio, direcao, sistema_freios, cor, vin)
VALUES
(1, '1.4 8V Flex', 'Manual 5 marchas', 'Hidráulica', 'Disco / Tambor', 'Prata', '9BHZC13406P100411'),
(2, '1.0 8V Flex', 'Manual 5 marchas', 'Elétrica', 'Disco / Tambor', 'Branco', '9BWAA45U0EP512337'),
(3, '1.0 Turbo Flex', 'Automática 6 marchas', 'Elétrica', 'Disco ventilado / Tambor', 'Preto', '9BGKS48T0KG227118'),
(4, '1.0 8V Fire Flex', 'Manual 5 marchas', 'Mecânica', 'Disco / Tambor', 'Vermelho', '9BD17106G62310945'),
(5, '2.0 16V Flex', 'Automática CVT', 'Elétrica', 'Disco ventilado / Disco', 'Cinza', '9BRBLWHE1J0330872'),
(6, '1.0 12V Flex', 'Manual 5 marchas', 'Elétrica', 'Disco / Tambor', 'Azul', '9BFZH55L1H8441209'),
(7, '2.0 16V Flexone', 'Automática CVT', 'Elétrica', 'Disco ventilado / Disco', 'Branco', '93HFB2650FZ118874'),
(8, '1.0 8V Fire Evo', 'Manual 5 marchas', 'Mecânica', 'Disco / Tambor', 'Prata', '9BD195132C2660513');

-- ============================================================
-- Tipos de servico (dominio usado em servico.tipo_servico)
-- ============================================================

INSERT INTO tipo_servico (nome)
VALUES ('REVISAO'), ('MOTOR'), ('TRANSMISSAO'), ('DIRECAO'), ('SUSPENSAO'),
('FREIOS'), ('ARREFECIMENTO'), ('ELETRICA'), ('OUTROS');

-- ============================================================
-- Pecas (catalogo generico, independente de modelo/veiculo)
-- ============================================================

INSERT INTO peca (nome_popular, sistema, vida_util_tempo, vida_util_km)
VALUES
('Filtro de óleo', 'MOTOR', '6 meses', '10.000 km'),
('Óleo do motor 5W30 sintético', 'MOTOR', '12 meses', '10.000 km'),
('Óleo do motor 15W40 mineral', 'MOTOR', '6 meses', '5.000 km'),
('Filtro de ar do motor', 'MOTOR', '12 meses', '15.000 km'),
('Filtro de combustível', 'MOTOR', '24 meses', '20.000 km'),
('Vela de ignição', 'MOTOR', '24 meses', '20.000 km'),
('Cabo de vela', 'MOTOR', '48 meses', '40.000 km'),
('Bobina de ignição', 'MOTOR', '60 meses', '80.000 km'),
('Correia dentada', 'MOTOR', '48 meses', '60.000 km'),
('Tensor da correia dentada', 'MOTOR', '48 meses', '60.000 km'),
('Correia do alternador', 'MOTOR', '36 meses', '40.000 km'),
('Junta do cabeçote', 'MOTOR', 'Não informado', '120.000 km'),
('Bico injetor', 'MOTOR', '60 meses', '100.000 km'),
('Sensor lambda (sonda)', 'MOTOR', '60 meses', '80.000 km'),
('Sensor de rotação (CKP)', 'MOTOR', 'Não informado', '100.000 km'),
('Bomba de combustível', 'MOTOR', 'Não informado', '120.000 km'),
('Corpo de borboleta', 'MOTOR', 'Não informado', 'Não informado'),
('Óleo de câmbio manual 75W80', 'TRANSMISSAO', '36 meses', '40.000 km'),
('Fluido de câmbio automático (ATF)', 'TRANSMISSAO', '36 meses', '40.000 km'),
('Kit de embreagem (disco, platô e atuador)', 'TRANSMISSAO', 'Não informado', '80.000 km'),
('Junta homocinética', 'TRANSMISSAO', 'Não informado', '80.000 km'),
('Coifa de homocinética', 'TRANSMISSAO', '48 meses', '60.000 km'),
('Rolamento de câmbio', 'TRANSMISSAO', 'Não informado', '100.000 km'),
('Fluido de direção hidráulica', 'DIRECAO', '36 meses', '40.000 km'),
('Terminal de direção', 'DIRECAO', 'Não informado', '60.000 km'),
('Bomba de direção hidráulica', 'DIRECAO', 'Não informado', '120.000 km'),
('Caixa de direção', 'DIRECAO', 'Não informado', '150.000 km'),
('Amortecedor dianteiro', 'SUSPENSAO', 'Não informado', '60.000 km'),
('Amortecedor traseiro', 'SUSPENSAO', 'Não informado', '60.000 km'),
('Mola helicoidal', 'SUSPENSAO', 'Não informado', '100.000 km'),
('Kit batente e coifa do amortecedor', 'SUSPENSAO', 'Não informado', '60.000 km'),
('Bucha da bandeja', 'SUSPENSAO', 'Não informado', '60.000 km'),
('Pivô de suspensão', 'SUSPENSAO', 'Não informado', '60.000 km'),
('Bieleta da barra estabilizadora', 'SUSPENSAO', 'Não informado', '50.000 km'),
('Rolamento de roda', 'SUSPENSAO', 'Não informado', '80.000 km'),
('Pneu aro 14', 'SUSPENSAO', '60 meses', '40.000 km'),
('Pastilha de freio dianteira', 'FREIOS', '24 meses', '30.000 km'),
('Lona de freio traseira', 'FREIOS', '36 meses', '40.000 km'),
('Disco de freio ventilado', 'FREIOS', 'Não informado', '60.000 km'),
('Tambor de freio', 'FREIOS', 'Não informado', '80.000 km'),
('Fluido de freio DOT 4', 'FREIOS', '24 meses', '20.000 km'),
('Cilindro de roda', 'FREIOS', 'Não informado', '80.000 km'),
('Cilindro mestre de freio', 'FREIOS', 'Não informado', '100.000 km'),
('Fluido de arrefecimento (aditivo)', 'ARREFECIMENTO', '24 meses', '30.000 km'),
('Bomba d\'água', 'ARREFECIMENTO', '48 meses', '60.000 km'),
('Válvula termostática', 'ARREFECIMENTO', '48 meses', '60.000 km'),
('Radiador', 'ARREFECIMENTO', 'Não informado', '120.000 km'),
('Mangueira superior do radiador', 'ARREFECIMENTO', '48 meses', '60.000 km'),
('Eletroventilador do radiador', 'ARREFECIMENTO', 'Não informado', '120.000 km'),
('Bateria 60Ah', 'ELETRICA', '24 meses', 'Não informado'),
('Alternador', 'ELETRICA', 'Não informado', '150.000 km'),
('Motor de arranque', 'ELETRICA', 'Não informado', '150.000 km'),
('Lâmpada de farol H4', 'ELETRICA', '12 meses', 'Não informado'),
('Compressor do ar-condicionado', 'ELETRICA', 'Não informado', '120.000 km'),
('Sensor ABS', 'ELETRICA', 'Não informado', '100.000 km'),
('Motor do vidro elétrico', 'ELETRICA', 'Não informado', 'Não informado'),
('Filtro de cabine (ar-condicionado)', 'OUTROS', '12 meses', '15.000 km'),
('Palheta do limpador de para-brisa', 'OUTROS', '12 meses', 'Não informado'),
('Silencioso traseiro do escapamento', 'OUTROS', 'Não informado', '80.000 km');

-- ============================================================
-- Pecas padrao por item do catalogo de servicos
-- ============================================================

INSERT INTO catalogo_servico_peca (id_catalogo_servico, id_peca)
VALUES
(1, 1),
(1, 2),
(2, 4),
(3, 5),
(4, 6),
(5, 9),
(5, 10),
(6, 11),
(7, 57),
(21, 18),
(22, 19),
(23, 22),
(25, 24),
(31, 41),
(32, 37),
(33, 44),
(36, 46),
(40, 53),
(72, 21),
(72, 22),
(75, 20),
(80, 25),
(80, 33),
(82, 35),
(84, 28),
(84, 31),
(88, 39),
(93, 47),
(94, 45),
(99, 51),
(100, 52),
(102, 54),
(104, 55),
(105, 56),
(109, 59);

-- ============================================================
-- Orcamentos
-- valor = soma dos itens de orcamento_servico + orcamento_peca
-- ============================================================

INSERT INTO orcamento (codigo, valor, tipo, responsavel, reclamacao, data_criacao, status,
                       id_peca, id_veiculo, id_cliente, id_funcionario)
VALUES
('0001', 705.00, 'ENTRADA', 'Marcos Vinícius Prado', 'Revisão preventiva dos 60.000 km', '2026-03-05', 'APROVADO', NULL, 1, 1, 4),
('0002', 1590.00, 'ENTRADA', 'Fábio Nakamura', 'Ruído metálico ao frear e pedal de freio baixo', '2026-03-19', 'APROVADO', NULL, 3, 2, 5),
('0003', 930.00, 'ENTRADA', 'Marcos Vinícius Prado', 'Motor falhando em baixa rotação e consumo elevado', '2026-04-08', 'APROVADO', NULL, 5, 3, 4),
('0004', 1910.00, 'ENTRADA', 'Diego Barbosa', 'Barulho na suspensão dianteira ao passar em lombadas', '2026-04-22', 'RECUSADO', NULL, 2, 1, 6),
('0005', 2400.00, 'ENTRADA', 'Leandro Souza', 'Veículo não liga pela manhã e luz da bateria acesa no painel', '2026-05-06', 'APROVADO', NULL, 7, 4, 7),
('0006', 1410.00, 'ENTRADA', 'Marcos Vinícius Prado', 'Superaquecimento e perda de líquido de arrefecimento', '2026-05-20', 'APROVADO', NULL, 8, 5, 4),
('0007', 825.00, 'ENTRADA', 'Fábio Nakamura', 'Revisão geral antes de viagem longa', '2026-06-10', 'APROVADO', NULL, 4, 2, 5),
('0008', 1435.00, 'REVISAO', 'Fábio Nakamura', 'Itens reprovados na revisão: coifa rasgada e folga no terminal', '2026-06-12', 'APROVADO', NULL, 4, 2, 5),
('0009', 1520.00, 'ENTRADA', 'Diego Barbosa', 'Embreagem patinando em subidas e cheiro de queimado', '2026-06-24', 'APROVADO', NULL, 6, 3, 6),
('0010', 2850.00, 'ENTRADA', 'Leandro Souza', 'Ar-condicionado não gela', '2026-07-08', 'RECUSADO', NULL, 1, 1, 7),
('0011', 1615.00, 'ENTRADA', 'Marcos Vinícius Prado', 'Troca preventiva da correia dentada aos 120.000 km', '2026-07-22', 'APROVADO', NULL, 7, 4, 4),
('0012', 740.00, 'ENTRADA', 'Diego Barbosa', 'Escapamento furado com ruído excessivo', '2026-08-24', 'APROVADO', NULL, 8, 5, 6),
('0013', 1220.00, 'ENTRADA', 'Marcos Vinícius Prado', 'Revisão preventiva dos 90.000 km', '2026-08-28', 'APROVADO', NULL, 2, 1, 4),
('0014', 600.00, 'ENTRADA', 'Leandro Souza', 'Vidro elétrico do motorista não sobe', '2026-09-01', 'PENDENTE', NULL, 7, 4, 7),
('0015', 900.00, 'ENTRADA', 'Fábio Nakamura', 'Ruído no rolamento da roda dianteira direita', '2026-09-02', 'PENDENTE', NULL, 5, 3, 5);

INSERT INTO orcamento_servico (id_orcamento, id_catalogo_servico, valor_cobrado)
VALUES
(1, 1, 120.00),
(1, 2, 80.00),
(1, 31, 130.00),
(2, 32, 300.00),
(2, 88, 500.00),
(2, 31, 130.00),
(3, 4, 200.00),
(3, 9, 300.00),
(3, 2, 80.00),
(4, 84, 500.00),
(4, 86, 350.00),
(4, 27, 120.00),
(5, 99, 800.00),
(5, 37, 60.00),
(6, 94, 500.00),
(6, 96, 250.00),
(6, 33, 150.00),
(7, 1, 120.00),
(7, 3, 100.00),
(7, 27, 120.00),
(7, 28, 100.00),
(7, 26, 80.00),
(7, 40, 50.00),
(8, 72, 600.00),
(8, 80, 300.00),
(9, 75, 800.00),
(10, 102, 1200.00),
(10, 39, 200.00),
(11, 5, 600.00),
(11, 1, 120.00),
(12, 109, 400.00),
(13, 1, 120.00),
(13, 2, 80.00),
(13, 3, 100.00),
(13, 21, 200.00),
(13, 26, 80.00),
(13, 27, 120.00),
(14, 105, 250.00),
(14, 107, 350.00),
(15, 82, 500.00),
(15, 27, 120.00);

INSERT INTO orcamento_peca (id_orcamento, id_peca, nome_tecnico, fabricante, valor)
VALUES
(1, 1, 'OC-90915-YZZE1', 'Fram', 45.00),
(1, 2, '5W30 SN 4L', 'Mobil', 180.00),
(1, 4, 'ARL-3021', 'Tecfil', 90.00),
(1, 41, 'DOT4 500ml', 'Bosch', 60.00),
(2, 37, 'BB-1234', 'Bosch', 220.00),
(2, 39, 'BD-7745 (par)', 'Fremax', 380.00),
(2, 41, 'DOT4 500ml', 'Bosch', 60.00),
(3, 6, 'IFR6T11 (jogo 4un)', 'NGK', 240.00),
(3, 4, 'ARL-4110', 'Tecfil', 110.00),
(4, 28, 'GP-32456 (par)', 'Cofap', 640.00),
(4, 31, 'KB-1102', 'Cofap', 180.00),
(4, 32, 'BS-4409', 'Nakata', 120.00),
(5, 51, 'ALT-14V-90A', 'Bosch', 890.00),
(5, 50, 'M60GD 60Ah', 'Moura', 520.00),
(5, 11, '6PK1120', 'Gates', 130.00),
(6, 45, 'BA-2210', 'Urba', 260.00),
(6, 46, 'VT-88C', 'Wahler', 95.00),
(6, 44, 'Orgânico rosa 1L', 'Paraflu', 70.00),
(6, 48, 'MS-3390', 'Cofap', 85.00),
(7, 1, 'OC-264', 'Fram', 40.00),
(7, 3, '15W40 SL 4L', 'Ipiranga', 150.00),
(7, 5, 'GI-06/7', 'Tecfil', 65.00),
(8, 21, 'JH-5521', 'Nakata', 310.00),
(8, 22, 'CF-1180', 'Nakata', 85.00),
(8, 25, 'TD-9902', 'Viemar', 140.00),
(9, 20, 'KE-6620', 'Luk', 720.00),
(10, 54, 'CP-8802', 'Denso', 1450.00),
(11, 9, 'CT-1088', 'Gates', 480.00),
(11, 10, 'TN-5540', 'Gates', 190.00),
(11, 1, 'OC-90915-YZZE1', 'Fram', 45.00),
(11, 2, '5W30 SN 4L', 'Mobil', 180.00),
(12, 59, 'SL-4471', 'Tuper', 340.00),
(13, 1, 'OC-90915-YZZE1', 'Fram', 45.00),
(13, 2, '5W30 SN 4L', 'Mobil', 180.00),
(13, 4, 'ARL-3021', 'Tecfil', 90.00),
(13, 5, 'GI-06/7', 'Tecfil', 65.00),
(13, 18, '75W80 GL4 2L', 'Motul', 140.00),
(15, 35, 'RL-3308', 'SKF', 280.00);

-- ============================================================
-- Ordens de servico (geradas a partir dos orcamentos APROVADOS)
-- ============================================================

INSERT INTO servico (codigo, data_servico, km_servico, titulo, tipo_servico, tipo_manutencao,
                     status, id_veiculo, id_oficina, id_orcamento)
VALUES
('0001', '2026-03-06', 60800, 'Revisão preventiva de 60.000 km', 'REVISAO', 'PREVENTIVA', 'CONCLUIDA', 1, 1, 1),
('0002', '2026-03-20', 48200, 'Reparo do sistema de freios dianteiro', 'FREIOS', 'CORRETIVA', 'CONCLUIDA', 3, 1, 2),
('0003', '2026-04-09', 92400, 'Reparo de falha de ignição do motor', 'MOTOR', 'CORRETIVA', 'CONCLUIDA', 5, 1, 3),
('0004', '2026-05-07', 118500, 'Reparo do sistema de carga elétrica', 'ELETRICA', 'CORRETIVA', 'CONCLUIDA', 7, 1, 5),
('0005', '2026-05-21', 143700, 'Reparo do sistema de arrefecimento', 'ARREFECIMENTO', 'CORRETIVA', 'CONCLUIDA', 8, 1, 6),
('0006', '2026-06-11', 187300, 'Revisão geral pré-viagem', 'REVISAO', 'PREVENTIVA', 'CONCLUIDA', 4, 1, 7),
('0007', '2026-06-15', 187450, 'Reparo de homocinética e terminal', 'TRANSMISSAO', 'CORRETIVA', 'CONCLUIDA', 4, 1, 8),
('0008', '2026-06-25', 76900, 'Troca do kit de embreagem', 'TRANSMISSAO', 'CORRETIVA', 'CANCELADA', 6, 1, 9),
('0009', '2026-07-23', 121000, 'Troca da correia dentada', 'MOTOR', 'PREVENTIVA', 'CONCLUIDA', 7, 1, 11),
('0010', '2026-08-26', 145200, 'Reparo do escapamento', 'OUTROS', 'CORRETIVA', 'EM_ANDAMENTO', 8, 1, 12),
('0011', '2026-09-01', 89600, 'Revisão preventiva de 90.000 km', 'REVISAO', 'PREVENTIVA', 'ABERTA', 2, 1, 13);

-- Orcamento 8 nasceu dos itens reprovados na revisao da OS 0007 (id_servico = 6).
UPDATE orcamento SET id_servico_revisao = 6 WHERE id_orcamento = 8;

-- ============================================================
-- Itens de servico (etapas de cada ordem de servico)
-- ============================================================

INSERT INTO item_servico (codigo, etapa, descricao, status, tempo_gasto, data_realizacao,
                          id_peca, id_servico, id_funcionario)
VALUES
('0001', 1, 'Drenagem do óleo e troca do filtro de óleo', 'CONCLUIDA', '1h00', '2026-03-06', 1, 1, 4),
('0002', 2, 'Substituição do filtro de ar do motor', 'CONCLUIDA', '0h20', '2026-03-06', 4, 1, 8),
('0003', 3, 'Sangria e troca do fluido de freio', 'CONCLUIDA', '0h50', '2026-03-06', 41, 1, 4),
('0004', 4, 'Inspeção geral e teste de rodagem', 'CONCLUIDA', '0h30', '2026-03-06', NULL, 1, 4),
('0005', 1, 'Desmontagem das rodas e diagnóstico dos freios', 'CONCLUIDA', '0h40', '2026-03-20', NULL, 2, 5),
('0006', 2, 'Substituição das pastilhas dianteiras', 'CONCLUIDA', '1h10', '2026-03-20', 37, 2, 5),
('0007', 3, 'Substituição dos discos de freio ventilados', 'CONCLUIDA', '1h30', '2026-03-20', 39, 2, 5),
('0008', 4, 'Troca do fluido de freio e sangria do sistema', 'CONCLUIDA', '0h50', '2026-03-20', 41, 2, 8),
('0009', 1, 'Leitura de códigos de falha com scanner', 'CONCLUIDA', '0h40', '2026-04-09', NULL, 3, 4),
('0010', 2, 'Substituição do jogo de velas de ignição', 'CONCLUIDA', '1h20', '2026-04-09', 6, 3, 4),
('0011', 3, 'Limpeza dos bicos injetores em ultrassom', 'CONCLUIDA', '2h00', '2026-04-09', NULL, 3, 4),
('0012', 4, 'Substituição do filtro de ar do motor', 'CONCLUIDA', '0h20', '2026-04-09', 4, 3, 8),
('0013', 1, 'Teste de carga do alternador e da bateria', 'CONCLUIDA', '0h50', '2026-05-07', NULL, 4, 7),
('0014', 2, 'Substituição do alternador', 'CONCLUIDA', '2h10', '2026-05-07', 51, 4, 7),
('0015', 3, 'Substituição da correia do alternador', 'CONCLUIDA', '0h40', '2026-05-07', 11, 4, 7),
('0016', 4, 'Substituição da bateria 60Ah', 'CONCLUIDA', '0h20', '2026-05-07', 50, 4, 8),
('0017', 1, 'Teste de pressão do sistema de arrefecimento', 'CONCLUIDA', '0h50', '2026-05-21', NULL, 5, 4),
('0018', 2, 'Substituição da bomba d\'água', 'CONCLUIDA', '2h30', '2026-05-21', 45, 5, 4),
('0019', 3, 'Substituição da válvula termostática', 'CONCLUIDA', '0h50', '2026-05-21', 46, 5, 4),
('0020', 4, 'Substituição da mangueira superior do radiador', 'CONCLUIDA', '0h30', '2026-05-21', 48, 5, 8),
('0021', 5, 'Troca do fluido e sangria do sistema', 'CONCLUIDA', '0h40', '2026-05-21', 44, 5, 8),
('0022', 1, 'Troca do óleo do motor e do filtro de óleo', 'CONCLUIDA', '1h00', '2026-06-11', 1, 6, 5),
('0023', 2, 'Substituição do filtro de combustível', 'CONCLUIDA', '0h40', '2026-06-11', 5, 6, 5),
('0024', 3, 'Alinhamento da geometria de direção', 'CONCLUIDA', '0h50', '2026-06-11', NULL, 6, 8),
('0025', 4, 'Balanceamento das quatro rodas', 'CONCLUIDA', '0h40', '2026-06-11', NULL, 6, 8),
('0026', 5, 'Rodízio dos pneus', 'CONCLUIDA', '0h30', '2026-06-11', NULL, 6, 8),
('0027', 6, 'Inspeção de lâmpadas e sinalização', 'CONCLUIDA', '0h20', '2026-06-11', NULL, 6, 8),
('0028', 1, 'Substituição da junta homocinética do semi-eixo', 'CONCLUIDA', '2h40', '2026-06-15', 21, 7, 5),
('0029', 2, 'Substituição da coifa de homocinética', 'CONCLUIDA', '1h00', '2026-06-15', 22, 7, 5),
('0030', 3, 'Substituição do terminal de direção', 'CONCLUIDA', '1h10', '2026-06-15', 25, 7, 5),
('0031', 4, 'Alinhamento após a troca do terminal', 'CONCLUIDA', '0h50', '2026-06-15', NULL, 7, 8),
('0032', 1, 'Remoção da caixa de câmbio', 'CANCELADA', '', NULL, NULL, 8, 6),
('0033', 2, 'Substituição do kit de embreagem', 'CANCELADA', '', NULL, 20, 8, 6),
('0034', 1, 'Remoção das capas e travamento do motor no PMS', 'CONCLUIDA', '1h20', '2026-07-23', NULL, 9, 4),
('0035', 2, 'Substituição da correia dentada', 'CONCLUIDA', '2h30', '2026-07-23', 9, 9, 4),
('0036', 3, 'Substituição do tensor da correia dentada', 'CONCLUIDA', '0h50', '2026-07-23', 10, 9, 4),
('0037', 4, 'Troca do óleo do motor e do filtro de óleo', 'CONCLUIDA', '1h00', '2026-07-23', 1, 9, 8),
('0038', 1, 'Diagnóstico e remoção do escapamento furado', 'CONCLUIDA', '1h00', '2026-08-26', NULL, 10, 6),
('0039', 2, 'Instalação do silencioso traseiro novo', 'EM_ANDAMENTO', '', NULL, 59, 10, 6),
('0040', 1, 'Troca do óleo do motor e do filtro de óleo', 'PENDENTE', '', NULL, 1, 11, 4),
('0041', 2, 'Substituição do filtro de ar do motor', 'PENDENTE', '', NULL, 4, 11, 4),
('0042', 3, 'Substituição do filtro de combustível', 'PENDENTE', '', NULL, 5, 11, 4),
('0043', 4, 'Troca do óleo da caixa de câmbio manual', 'PENDENTE', '', NULL, 18, 11, 4),
('0044', 5, 'Rodízio dos pneus', 'PENDENTE', '', NULL, NULL, 11, 8),
('0045', 6, 'Alinhamento da geometria de direção', 'PENDENTE', '', NULL, NULL, 11, 8);

-- ============================================================
-- Resumo dos orcamentos gerados
--   0001 | 2026-03-05 | cli 1 | vei 1 | APROVADO  | R$   705.00
--   0002 | 2026-03-19 | cli 2 | vei 3 | APROVADO  | R$  1590.00
--   0003 | 2026-04-08 | cli 3 | vei 5 | APROVADO  | R$   930.00
--   0004 | 2026-04-22 | cli 1 | vei 2 | RECUSADO  | R$  1910.00
--   0005 | 2026-05-06 | cli 4 | vei 7 | APROVADO  | R$  2400.00
--   0006 | 2026-05-20 | cli 5 | vei 8 | APROVADO  | R$  1410.00
--   0007 | 2026-06-10 | cli 2 | vei 4 | APROVADO  | R$   825.00
--   0008 | 2026-06-12 | cli 2 | vei 4 | APROVADO  | R$  1435.00
--   0009 | 2026-06-24 | cli 3 | vei 6 | APROVADO  | R$  1520.00
--   0010 | 2026-07-08 | cli 1 | vei 1 | RECUSADO  | R$  2850.00
--   0011 | 2026-07-22 | cli 4 | vei 7 | APROVADO  | R$  1615.00
--   0012 | 2026-08-24 | cli 5 | vei 8 | APROVADO  | R$   740.00
--   0013 | 2026-08-28 | cli 1 | vei 2 | APROVADO  | R$  1220.00
--   0014 | 2026-09-01 | cli 4 | vei 7 | PENDENTE  | R$   600.00
--   0015 | 2026-09-02 | cli 3 | vei 5 | PENDENTE  | R$   900.00
-- ============================================================