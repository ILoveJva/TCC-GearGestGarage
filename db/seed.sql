-- ============================================================
-- Gear Gest Garage — Seed (dados iniciais)
-- Requer que db/schema.sql ja tenha sido executado (tabelas vazias).
--
-- Executar:
--   Get-Content db/seed.sql | docker exec -i gear_gest_mysql mysql -u root -proot GearGestGarage
-- ============================================================

USE GearGestGarage;

-- ============================================================
-- Oficina, usuário e funcionário
-- ============================================================

INSERT INTO oficina (nome, endereco, telefone, cnpj)
VALUES ('Garagem Cecconi', 'Rua Itapetinga, 794 - Santa Luzia', '11 1111-1111', '00.000.000/0000-00'),
('Oficina 01', 'Rua dos Bobos, 67', '11 1111-1112', '00.000.000/0000-01'),
('Oficina 02', 'Rua dos Bobos, 10', '11 1111-1113', '00.000.000/0000-02'),
('Oficina 03', 'Rua dos Bobos, 677', '11 1111-1114', '00.000.000/0000-03')
;

INSERT INTO usuario (nome, cpf, email, senha, telefone, id_oficina)
VALUES
('Arthur Cecconi', '00000000000', 'oficina@geargest.com', '123456', '(11) 90000-0000', 1),
('João Silva', '12345678900', 'joaosilva@gmail.com', 'silva123', '(11) 98765-4321', 1),
('Adriano Imperador', '12345678901', 'adrianoimperador@gmail.com', 'apa123', '(11) 98765-4322', 2),
('Roberto Carlos', '12345678902', 'robertocarlos@gmail.com', 'ime123', '(11) 98765-4323', 3)
;

INSERT INTO funcionario (nome, cargo, endereco, id_usuario)
VALUES ('Arthur Cecconi', 'Gerente', '', 1),
('Adriano Imperador', 'Gerente', '', 3),
('Roberto Carlos', 'Gerente', '', 4)
;

INSERT INTO cliente (id_usuario) VALUE (2);

-- ============================================================
-- Catálogo de Serviços
-- ============================================================

INSERT INTO montadora (nome,pais_origem)
VALUES ('Honda', 'Japão'),
('Ford', 'EUA'),
('Fiat', 'Italia')
;

INSERT INTO modelo (nome,ano,tipo,id_montadora)
VALUES ('Fit', '2006', 'carro', 1),
('Mustang', '1978', 'carro', 2),
('Palio', '2006', 'carro', 3),
('Versallies', '1993', 'carro', 2)
;

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
