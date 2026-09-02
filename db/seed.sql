-- ============================================================
-- Gear Gest Garage - Seed (dados iniciais) - versao completa
-- Requer que db/schema.sql ja tenha sido executado (tabelas vazias).
--
-- Executar:
--   Get-Content db/seed.sql | docker exec -i gear_gest_mysql mysql -u root -proot GearGestGarage
-- ============================================================

USE GearGestGarage;

-- ============================================================
-- Oficinas
-- ============================================================

INSERT INTO oficina (nome, endereco, telefone, cnpj)
VALUES ('Garagem Cecconi', 'Rua Itapetinga, 794 - Santa Luzia', '11 1111-1111', '00.000.000/0000-00'),
('Oficina 01', 'Rua dos Bobos, 67', '11 1111-1112', '00.000.000/0000-01'),
('Oficina 02', 'Rua dos Bobos, 10', '11 1111-1113', '00.000.000/0000-02'),
('Oficina 03', 'Rua dos Bobos, 677', '11 1111-1114', '00.000.000/0000-03');

-- ============================================================
-- Usuarios (1-4 originais, 5-9 equipe da oficina 1, 10-13 clientes da oficina 1)
-- ============================================================

INSERT INTO usuario (nome, cpf, email, senha, telefone, id_oficina)
VALUES
('Arthur Cecconi', '00000000000', 'oficina@geargest.com', '123456', '(11) 90000-0000', 1),
('João Silva', '12345678900', 'joaosilva@gmail.com', 'silva123', '(11) 98765-4321', 1),
('Adriano Imperador', '12345678901', 'adrianoimperador@gmail.com', 'apa123', '(11) 98765-4322', 2),
('Roberto Carlos', '12345678902', 'robertocarlos@gmail.com', 'ime123', '(11) 98765-4323', 3),
('Marcos Vinícius Prado', '31122233344', 'marcos.prado@geargest.com', 'marcos123', '(11) 97111-0001', 1),
('Fábio Nakamura', '31122233355', 'fabio.nakamura@geargest.com', 'fabio123', '(11) 97111-0002', 1),
('Diego Barbosa', '31122233366', 'diego.barbosa@geargest.com', 'diego123', '(11) 97111-0003', 1),
('Leandro Souza', '31122233377', 'leandro.souza@geargest.com', 'leandro123', '(11) 97111-0004', 1),
('Paulo Henrique Dias', '31122233388', 'paulo.dias@geargest.com', 'paulo123', '(11) 97111-0005', 1),
('Maria Fernanda Lopes', '45566677788', 'mariaflopes@gmail.com', 'maria123', '(11) 98120-1122', 1),
('Carlos Eduardo Ramos', '45566677799', 'carlosramos@gmail.com', 'carlos123', '(11) 98120-1133', 1),
('Beatriz Almeida Nunes', '45566677800', 'beatriznunes@gmail.com', 'beatriz123', '(11) 98120-1144', 1),
('Ricardo Tanaka', '45566677811', 'ricardotanaka@gmail.com', 'ricardo123', '(11) 98120-1155', 1);

-- ============================================================
-- Funcionarios (todos com endereco preenchido)
-- ============================================================

INSERT INTO funcionario (nome, cargo, endereco, id_usuario)
VALUES
('Arthur Cecconi', 'Gerente', 'Rua Itapetinga, 800 - Santa Luzia', 1),
('Adriano Imperador', 'Gerente', 'Rua dos Bobos, 67 - Centro', 3),
('Roberto Carlos', 'Gerente', 'Rua dos Bobos, 10 - Centro', 4),
('Marcos Vinícius Prado', 'Mecânico Chefe', 'Rua das Oliveiras, 120 - Santa Luzia', 5),
('Fábio Nakamura', 'Mecânico', 'Av. Brasil, 455 - Centro', 6),
('Diego Barbosa', 'Mecânico', 'Rua Piratininga, 88 - Vila Nova', 7),
('Leandro Souza', 'Eletricista Automotivo', 'Rua Dom Pedro I, 341 - Jardim Aurora', 8),
('Paulo Henrique Dias', 'Auxiliar Mecânico', 'Rua das Acácias, 27 - Santa Luzia', 9);

-- cliente 1 = Joao Silva (usuario 2); clientes 2-5 = usuarios 10-13
INSERT INTO cliente (id_usuario) VALUES (2), (10), (11), (12), (13);

-- ============================================================
-- Montadoras e modelos
-- ============================================================

INSERT INTO montadora (nome, pais_origem)
VALUES ('Honda', 'Japão'),
('Ford', 'EUA'),
('Fiat', 'Itália'),
('Volkswagen', 'Alemanha'),
('Chevrolet', 'EUA'),
('Toyota', 'Japão');

INSERT INTO modelo (nome, ano, tipo, id_montadora)
VALUES ('Fit', 2006, 'Carro', 1),
('Mustang', 1978, 'Carro', 2),
('Palio', 2006, 'Carro', 3),
('Versalles', 1993, 'Carro', 2),
('Gol', 2014, 'Carro', 4),
('Onix', 2019, 'Carro', 5),
('Corolla', 2018, 'Carro', 6),
('Civic', 2015, 'Carro', 1),
('Ka', 2017, 'Carro', 2),
('Uno', 2012, 'Carro', 3);

-- ============================================================
-- Tipos de servico (dominio usado em servico.tipo_servico)
-- ============================================================

INSERT INTO tipo_servico (nome)
VALUES ('REVISAO'), ('MOTOR'), ('TRANSMISSAO'), ('DIRECAO'), ('SUSPENSAO'),
('FREIOS'), ('ARREFECIMENTO'), ('ELETRICA'), ('OUTROS');

-- ============================================================
-- Catalogo de servicos (114 itens, todos com descricao)
-- ============================================================

INSERT INTO catalogo_servico
    (nome, descricao, valor, tipo, tipo_manutencao, sistema, validade_km, validade_meses)
VALUES
('Troca de óleo e filtro de óleo', 'Drenagem do óleo usado, substituição do filtro e reposição com óleo na especificação do fabricante.', 120.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 5000, 6),
('Troca de filtro de ar', 'Substituição do elemento filtrante de ar e limpeza da caixa do filtro.', 80.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 15000, 12),
('Troca de filtro de combustível', 'Substituição do filtro de combustível da linha de alimentação e verificação de vazamentos.', 100.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 20000, 24),
('Troca de velas de ignição', 'Substituição do jogo de velas com aferição da folga dos eletrodos e torque especificado.', 200.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 20000, 24),
('Troca de correia dentada (kit distribuição)', 'Substituição da correia dentada, tensor e rolamentos, com sincronismo do motor no PMS.', 600.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 60000, 48),
('Troca de correia do alternador', 'Substituição da correia poli V de acionamento do alternador e acessórios.', 250.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 40000, 36),
('Troca de filtro de cabine (ar-cond.)', 'Substituição do filtro de ar da cabine e higienização das saídas de ventilação.', 90.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 15000, 12),
('Regulagem de válvulas', 'Aferição e ajuste da folga de válvulas conforme especificação do fabricante.', 350.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 30000, 24),
('Limpeza de bicos injetores', 'Limpeza dos bicos injetores por aditivo em linha, com teste de vazão e estanqueidade.', 300.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 20000, 18),
('Inspeção de correia dentada', 'Inspeção visual do estado, tensão e alinhamento da correia dentada.', 80.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 30000, 24),
('Inspeção de corrente de comando', 'Inspeção do estiramento da corrente de comando, tensor e guias por ruído e folga.', 100.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 30000, 24),
('Troca de kit corrente de comando (corrente+tensor+guia)', 'Substituição completa do kit de corrente de comando com sincronismo do motor.', 1200.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 80000, 60),
('Troca de tensor da correia dentada', 'Substituição do tensor da correia dentada com verificação da tensão de trabalho.', 280.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 60000, 48),
('Troca de rolamento tensionador da correia', 'Substituição do rolamento tensionador da correia e teste de ruído.', 200.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 60000, 48),
('Troca de kit correia dentada com bomba d\'água', 'Substituição do kit de distribuição incluindo a bomba d\'água acionada pela correia.', 900.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 60000, 48),
('Limpeza de bicos injetores multiponto (ultrassom)', 'Remoção dos bicos injetores e limpeza em cuba de ultrassom com teste de vazão.', 380.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 20000, 18),
('Limpeza de válvulas de admissão (injeção direta)', 'Descarbonização das válvulas de admissão em motores de injeção direta.', 500.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 30000, 24),
('Troca de filtro de combustível diesel', 'Substituição do filtro de combustível diesel e drenagem do separador de água.', 150.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 10000, 12),
('Limpeza de filtro de partículas diesel (DPF)', 'Regeneração forçada e limpeza química do filtro de partículas diesel.', 450.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 40000, 24),
('Limpeza e regulagem de carburador', 'Desmontagem, limpeza e regulagem do carburador com ajuste de marcha lenta.', 200.00, 'PADRAO', 'PREVENTIVA', 'MOTOR', 10000, 12),
('Troca de óleo de câmbio manual', 'Drenagem e reposição do óleo da caixa de câmbio manual.', 200.00, 'PADRAO', 'PREVENTIVA', 'TRANSMISSAO', 40000, 36),
('Troca de fluido de câmbio automático (ATF)', 'Troca do fluido ATF do câmbio automático com verificação de nível e temperatura.', 350.00, 'PADRAO', 'PREVENTIVA', 'TRANSMISSAO', 40000, 36),
('Troca de coifas de homocinética', 'Substituição das coifas das juntas homocinéticas e reposição de graxa.', 400.00, 'PADRAO', 'PREVENTIVA', 'TRANSMISSAO', 60000, 48),
('Regulagem de embreagem', 'Ajuste do curso e do ponto de acionamento do pedal de embreagem.', 120.00, 'PADRAO', 'PREVENTIVA', 'TRANSMISSAO', 20000, 18),
('Troca de fluido de direção hidráulica', 'Drenagem e reposição do fluido do sistema de direção hidráulica.', 150.00, 'PADRAO', 'PREVENTIVA', 'DIRECAO', 40000, 36),
('Rodízio de pneus', 'Inversão das posições dos pneus para uniformizar o desgaste da banda de rodagem.', 80.00, 'PADRAO', 'PREVENTIVA', 'SUSPENSAO', 10000, 6),
('Alinhamento de rodas', 'Ajuste da geometria de direção (cambagem, cáster e convergência) em rampa.', 120.00, 'PADRAO', 'PREVENTIVA', 'SUSPENSAO', 10000, 6),
('Balanceamento de rodas', 'Balanceamento das rodas com contrapesos para eliminar vibração em velocidade.', 100.00, 'PADRAO', 'PREVENTIVA', 'SUSPENSAO', 10000, 6),
('Inspeção de amortecedores', 'Inspeção de vazamentos, batentes e eficiência dos amortecedores.', 80.00, 'PADRAO', 'PREVENTIVA', 'SUSPENSAO', 30000, 24),
('Inspeção de rolamentos de roda', 'Inspeção de folga e ruído nos rolamentos das quatro rodas.', 80.00, 'PADRAO', 'PREVENTIVA', 'SUSPENSAO', 40000, 36),
('Troca de fluido de freio (DOT)', 'Substituição do fluido de freio com sangria completa do circuito.', 130.00, 'PADRAO', 'PREVENTIVA', 'FREIOS', 20000, 24),
('Troca de pastilhas de freio', 'Substituição das pastilhas de freio e limpeza das pinças.', 300.00, 'PADRAO', 'PREVENTIVA', 'FREIOS', 30000, 24),
('Troca de fluido de arrefecimento', 'Drenagem e reposição do fluido de arrefecimento com sangria do sistema.', 150.00, 'PADRAO', 'PREVENTIVA', 'ARREFECIMENTO', 30000, 24),
('Limpeza de radiador', 'Limpeza externa e interna das colmeias do radiador para restabelecer a troca térmica.', 200.00, 'PADRAO', 'PREVENTIVA', 'ARREFECIMENTO', 30000, 24),
('Inspeção de mangueiras de arrefecimento', 'Inspeção de trincas, ressecamento e vazamento nas mangueiras de arrefecimento.', 80.00, 'PADRAO', 'PREVENTIVA', 'ARREFECIMENTO', 20000, 18),
('Troca de termostato', 'Substituição da válvula termostática e verificação da temperatura de abertura.', 250.00, 'PADRAO', 'PREVENTIVA', 'ARREFECIMENTO', 60000, 48),
('Teste e carga de bateria', 'Teste de carga e densidade da bateria com recarga quando necessário.', 60.00, 'PADRAO', 'PREVENTIVA', 'ELETRICA', 20000, 12),
('Inspeção do sistema de carga (alternador)', 'Medição da tensão de carga do alternador e do consumo parasita do sistema.', 80.00, 'PADRAO', 'PREVENTIVA', 'ELETRICA', 20000, 12),
('Recarga de ar condicionado', 'Recarga do gás refrigerante do ar-condicionado com teste de estanqueidade.', 200.00, 'PADRAO', 'PREVENTIVA', 'ELETRICA', NULL, 18),
('Inspeção de lâmpadas e sinalização', 'Verificação de faróis, lanternas, setas e luz de freio, com troca de lâmpadas queimadas.', 50.00, 'PADRAO', 'PREVENTIVA', 'ELETRICA', 10000, 6),
('Troca de junta do cabeçote', 'Substituição da junta do cabeçote com planificação e teste de compressão.', 1200.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Retífica de motor', 'Retífica completa do motor com usinagem, substituição de pistões, bronzinas e juntas.', 3500.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de bomba de óleo', 'Substituição da bomba de óleo e verificação da pressão de lubrificação.', 500.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de bomba de combustível', 'Substituição da bomba de combustível e teste de pressão da linha.', 600.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de sensor lambda (oxigênio)', 'Substituição da sonda lambda e apagamento dos códigos de falha.', 400.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de sensor de temperatura', 'Substituição do sensor de temperatura do motor e teste de leitura no scanner.', 250.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de sensor de rotação (CKP)', 'Substituição do sensor de rotação do virabrequim e verificação do sinal.', 350.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de corpo de borboleta', 'Substituição do corpo de borboleta com aprendizado eletrônico da marcha lenta.', 500.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de módulo de ignição (bobina)', 'Substituição da bobina de ignição e teste de faísca nos cilindros.', 350.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de ECU / módulo de injeção', 'Substituição e codificação do módulo de injeção eletrônica.', 800.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de catalisador', 'Substituição do catalisador e verificação da emissão de gases.', 700.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de corrente de comando', 'Substituição da corrente de comando com sincronismo do motor.', 800.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de tensor de corrente de comando', 'Substituição do tensor da corrente de comando e verificação da pressão de óleo.', 350.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de guia de corrente de comando', 'Substituição das guias da corrente de comando.', 280.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Reparo de ruído em corrente de comando', 'Diagnóstico e correção de ruído de estiramento na corrente de comando.', 200.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de bicos injetores multiponto', 'Substituição dos bicos injetores em motores de injeção multiponto.', 600.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de regulador de pressão de combustível', 'Substituição do regulador de pressão e aferição da pressão da linha.', 350.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Diagnóstico de sistema de injeção multiponto', 'Diagnóstico eletrônico completo do sistema de injeção multiponto com scanner.', 200.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de injetores de injeção direta', 'Substituição dos injetores de injeção direta com codificação no módulo.', 1000.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de bomba de alta pressão (injeção direta)', 'Substituição da bomba de alta pressão do sistema de injeção direta.', 1200.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de rail de combustível (injeção direta)', 'Substituição do rail (flauta) de combustível e teste de estanqueidade.', 700.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Diagnóstico de sistema de injeção direta', 'Diagnóstico do sistema de injeção direta com leitura de pressão de rail.', 200.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Limpeza de injetores diesel (common rail)', 'Limpeza e teste dos injetores diesel common rail em bancada.', 700.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de injetores diesel', 'Substituição dos injetores diesel com codificação IMA no módulo.', 1500.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de bomba injetora diesel', 'Substituição da bomba injetora diesel e calibração do ponto de injeção.', 1800.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de bomba de alta pressão diesel', 'Substituição da bomba de alta pressão do sistema diesel.', 1400.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Reparo de sistema common rail', 'Diagnóstico e reparo de perda de pressão no sistema common rail.', 900.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de filtro de partículas (DPF)', 'Substituição do filtro de partículas diesel e reprogramação do módulo.', 1000.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de carburador', 'Substituição do carburador com regulagem de marcha lenta e mistura.', 700.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Regulagem de mistura (carburador)', 'Ajuste da mistura ar-combustível do carburador com analisador de gases.', 120.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de kit de reparo de carburador', 'Desmontagem do carburador e substituição do kit de reparo (juntas e agulhas).', 300.00, 'REVISAO', 'CORRETIVA', 'MOTOR', NULL, NULL),
('Troca de homocinética / semi-eixo', 'Substituição da junta homocinética ou do semi-eixo completo com coifa e graxa.', 600.00, 'REVISAO', 'CORRETIVA', 'TRANSMISSAO', NULL, NULL),
('Reparo de câmbio manual', 'Desmontagem e reparo interno do câmbio manual com troca de rolamentos e sincronizadores.', 1500.00, 'REVISAO', 'CORRETIVA', 'TRANSMISSAO', NULL, NULL),
('Reparo de câmbio automático', 'Desmontagem e reparo do câmbio automático com troca de fluido e filtro.', 2500.00, 'REVISAO', 'CORRETIVA', 'TRANSMISSAO', NULL, NULL),
('Troca de disco e platô de embreagem', 'Substituição do kit de embreagem com remoção da caixa de câmbio.', 800.00, 'REVISAO', 'CORRETIVA', 'TRANSMISSAO', NULL, NULL),
('Troca de rolamento de câmbio', 'Substituição do rolamento da caixa de câmbio e verificação de ruído.', 400.00, 'REVISAO', 'CORRETIVA', 'TRANSMISSAO', NULL, NULL),
('Troca de bomba de direção hidráulica', 'Substituição da bomba de direção hidráulica e sangria do sistema.', 700.00, 'REVISAO', 'CORRETIVA', 'DIRECAO', NULL, NULL),
('Troca de cremalheira de direção', 'Substituição da cremalheira de direção seguida de alinhamento.', 900.00, 'REVISAO', 'CORRETIVA', 'DIRECAO', NULL, NULL),
('Troca de coluna de direção', 'Substituição da coluna de direção com verificação do travamento e do curso.', 600.00, 'REVISAO', 'CORRETIVA', 'DIRECAO', NULL, NULL),
('Troca de pivô / terminal de direção', 'Substituição dos pivôs e terminais de direção seguida de alinhamento.', 300.00, 'REVISAO', 'CORRETIVA', 'DIRECAO', NULL, NULL),
('Troca de caixa de direção', 'Substituição da caixa de direção completa seguida de alinhamento.', 1200.00, 'REVISAO', 'CORRETIVA', 'DIRECAO', NULL, NULL),
('Troca de cubo e rolamento de roda', 'Substituição do cubo e do rolamento de roda com torque especificado.', 500.00, 'REVISAO', 'CORRETIVA', 'SUSPENSAO', NULL, NULL),
('Troca de mola de suspensão', 'Substituição das molas helicoidais da suspensão em par.', 400.00, 'REVISAO', 'CORRETIVA', 'SUSPENSAO', NULL, NULL),
('Troca de amortecedor', 'Substituição dos amortecedores em par com batentes e coifas.', 500.00, 'REVISAO', 'CORRETIVA', 'SUSPENSAO', NULL, NULL),
('Troca de barra estabilizadora', 'Substituição da barra estabilizadora e das bieletas.', 400.00, 'REVISAO', 'CORRETIVA', 'SUSPENSAO', NULL, NULL),
('Troca de buchas de suspensão', 'Substituição das buchas da bandeja e demais buchas de suspensão.', 350.00, 'REVISAO', 'CORRETIVA', 'SUSPENSAO', NULL, NULL),
('Troca de braço de suspensão / bandeja', 'Substituição do braço de suspensão (bandeja) seguida de alinhamento.', 600.00, 'REVISAO', 'CORRETIVA', 'SUSPENSAO', NULL, NULL),
('Troca de disco de freio', 'Substituição dos discos de freio em par com verificação da espessura mínima.', 500.00, 'REVISAO', 'CORRETIVA', 'FREIOS', NULL, NULL),
('Troca de tambor de freio', 'Substituição dos tambores de freio e regulagem das lonas.', 350.00, 'REVISAO', 'CORRETIVA', 'FREIOS', NULL, NULL),
('Troca de cilindro de roda', 'Substituição do cilindro de roda e sangria do circuito traseiro.', 250.00, 'REVISAO', 'CORRETIVA', 'FREIOS', NULL, NULL),
('Troca de cilindro mestre de freio', 'Substituição do cilindro mestre de freio e sangria completa do sistema.', 400.00, 'REVISAO', 'CORRETIVA', 'FREIOS', NULL, NULL),
('Reparo de freio de estacionamento', 'Regulagem ou substituição do cabo do freio de estacionamento.', 200.00, 'REVISAO', 'CORRETIVA', 'FREIOS', NULL, NULL),
('Troca de radiador', 'Substituição do radiador com reposição do fluido e sangria do sistema.', 800.00, 'REVISAO', 'CORRETIVA', 'ARREFECIMENTO', NULL, NULL),
('Troca de bomba d\'água', 'Substituição da bomba d\'água com troca da junta e do fluido de arrefecimento.', 500.00, 'REVISAO', 'CORRETIVA', 'ARREFECIMENTO', NULL, NULL),
('Reparo de vazamento de arrefecimento', 'Localização e correção de vazamento no sistema de arrefecimento por teste de pressão.', 300.00, 'REVISAO', 'CORRETIVA', 'ARREFECIMENTO', NULL, NULL),
('Troca de válvula termostática', 'Substituição da válvula termostática e teste de temperatura de trabalho.', 250.00, 'REVISAO', 'CORRETIVA', 'ARREFECIMENTO', NULL, NULL),
('Troca de eletroventilador', 'Substituição do eletroventilador e verificação do relé de acionamento.', 400.00, 'REVISAO', 'CORRETIVA', 'ARREFECIMENTO', NULL, NULL),
('Troca de mangueiras de arrefecimento', 'Substituição das mangueiras de arrefecimento e abraçadeiras.', 200.00, 'REVISAO', 'CORRETIVA', 'ARREFECIMENTO', NULL, NULL),
('Troca de alternador', 'Substituição do alternador e teste da tensão de carga.', 800.00, 'REVISAO', 'CORRETIVA', 'ELETRICA', NULL, NULL),
('Troca de motor de arranque', 'Substituição do motor de arranque e verificação do circuito de partida.', 600.00, 'REVISAO', 'CORRETIVA', 'ELETRICA', NULL, NULL),
('Reparo de compressor de ar condicionado', 'Reparo do compressor do ar-condicionado com recarga de gás e óleo.', 700.00, 'REVISAO', 'CORRETIVA', 'ELETRICA', NULL, NULL),
('Troca de compressor de ar condicionado', 'Substituição do compressor do ar-condicionado com vácuo e recarga do sistema.', 1200.00, 'REVISAO', 'CORRETIVA', 'ELETRICA', NULL, NULL),
('Reparo de curto-circuito elétrico', 'Localização e reparo de curto-circuito no chicote elétrico.', 300.00, 'REVISAO', 'CORRETIVA', 'ELETRICA', NULL, NULL),
('Troca de sensor ABS / ESP', 'Substituição do sensor de rotação de roda do sistema ABS/ESP.', 500.00, 'REVISAO', 'CORRETIVA', 'ELETRICA', NULL, NULL),
('Reparo de vidro elétrico', 'Reparo ou substituição do motor e da máquina do vidro elétrico.', 250.00, 'REVISAO', 'CORRETIVA', 'ELETRICA', NULL, NULL),
('Reparo de travas elétricas', 'Reparo ou substituição dos atuadores de trava elétrica das portas.', 200.00, 'REVISAO', 'CORRETIVA', 'ELETRICA', NULL, NULL),
('Diagnóstico e reparo de falhas eletrônicas', 'Diagnóstico eletrônico com scanner e reparo das falhas registradas.', 350.00, 'REVISAO', 'CORRETIVA', 'ELETRICA', NULL, NULL),
('Troca de painel de instrumentos', 'Substituição do painel de instrumentos com codificação da quilometragem.', 900.00, 'REVISAO', 'CORRETIVA', 'ELETRICA', NULL, NULL),
('Reparo de escapamento / silencioso', 'Reparo ou substituição do silencioso e das conexões do escapamento.', 400.00, 'REVISAO', 'CORRETIVA', 'OUTROS', NULL, NULL),
('Polimento e cristalização', 'Polimento da pintura com cristalização para proteção e brilho.', 300.00, 'REVISAO', 'CORRETIVA', 'OUTROS', NULL, NULL),
('Higienização de estofados e cabine', 'Higienização a vapor dos estofados, carpetes e cabine.', 250.00, 'REVISAO', 'CORRETIVA', 'OUTROS', NULL, NULL),
('Reparo de para-choque', 'Reparo ou substituição do para-choque com pintura de acabamento.', 350.00, 'REVISAO', 'CORRETIVA', 'OUTROS', NULL, NULL),
('Reparo de lataria', 'Reparo de amassados e riscos na lataria com massa, lixamento e pintura.', 500.00, 'REVISAO', 'CORRETIVA', 'OUTROS', NULL, NULL),
('Reparo de vidros (trinca / quebrado)', 'Reparo ou substituição de vidros trincados ou quebrados.', 300.00, 'REVISAO', 'CORRETIVA', 'OUTROS', NULL, NULL);

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
('Filtro de combustível diesel (separador de água)', 'MOTOR', '12 meses', '10.000 km'),
('Vela de ignição', 'MOTOR', '24 meses', '20.000 km'),
('Cabo de vela', 'MOTOR', '48 meses', '40.000 km'),
('Bobina de ignição', 'MOTOR', '60 meses', '80.000 km'),
('Correia dentada', 'MOTOR', '48 meses', '60.000 km'),
('Tensor da correia dentada', 'MOTOR', '48 meses', '60.000 km'),
('Rolamento tensionador da correia', 'MOTOR', '48 meses', '60.000 km'),
('Kit correia dentada com bomba d\'água', 'MOTOR', '48 meses', '60.000 km'),
('Correia do alternador', 'MOTOR', '36 meses', '40.000 km'),
('Corrente de comando', 'MOTOR', '60 meses', '80.000 km'),
('Tensor da corrente de comando', 'MOTOR', '60 meses', '80.000 km'),
('Guia da corrente de comando', 'MOTOR', '60 meses', '80.000 km'),
('Kit corrente de comando', 'MOTOR', '60 meses', '80.000 km'),
('Junta do cabeçote', 'MOTOR', 'Não informado', '120.000 km'),
('Jogo de juntas do motor', 'MOTOR', 'Não informado', '120.000 km'),
('Retentor de válvula', 'MOTOR', 'Não informado', '120.000 km'),
('Bomba de óleo', 'MOTOR', 'Não informado', '150.000 km'),
('Bomba de combustível', 'MOTOR', 'Não informado', '120.000 km'),
('Sensor lambda (sonda)', 'MOTOR', '60 meses', '80.000 km'),
('Sensor de temperatura do motor', 'MOTOR', 'Não informado', '100.000 km'),
('Sensor de rotação (CKP)', 'MOTOR', 'Não informado', '100.000 km'),
('Corpo de borboleta', 'MOTOR', 'Não informado', '150.000 km'),
('Módulo de injeção (ECU)', 'MOTOR', 'Não informado', 'Não informado'),
('Catalisador', 'MOTOR', 'Não informado', '100.000 km'),
('Bico injetor multiponto', 'MOTOR', '60 meses', '100.000 km'),
('Kit de reparo de bico injetor', 'MOTOR', '24 meses', '20.000 km'),
('Regulador de pressão de combustível', 'MOTOR', 'Não informado', '100.000 km'),
('Injetor de injeção direta', 'MOTOR', 'Não informado', '120.000 km'),
('Bomba de alta pressão (injeção direta)', 'MOTOR', 'Não informado', '150.000 km'),
('Rail de combustível', 'MOTOR', 'Não informado', '150.000 km'),
('Injetor diesel common rail', 'MOTOR', 'Não informado', '120.000 km'),
('Bomba injetora diesel', 'MOTOR', 'Não informado', '150.000 km'),
('Bomba de alta pressão diesel', 'MOTOR', 'Não informado', '150.000 km'),
('Filtro de partículas diesel (DPF)', 'MOTOR', 'Não informado', '120.000 km'),
('Carburador', 'MOTOR', 'Não informado', 'Não informado'),
('Kit de reparo de carburador', 'MOTOR', '12 meses', '10.000 km'),
('Aditivo para limpeza de bicos injetores', 'MOTOR', '18 meses', '20.000 km'),
('Kit de retífica do motor (pistões e bronzinas)', 'MOTOR', 'Não informado', '200.000 km'),
('Óleo de câmbio manual 75W80', 'TRANSMISSAO', '36 meses', '40.000 km'),
('Fluido de câmbio automático (ATF)', 'TRANSMISSAO', '36 meses', '40.000 km'),
('Kit de embreagem (disco, platô e atuador)', 'TRANSMISSAO', 'Não informado', '80.000 km'),
('Junta homocinética', 'TRANSMISSAO', 'Não informado', '80.000 km'),
('Coifa de homocinética', 'TRANSMISSAO', '48 meses', '60.000 km'),
('Semi-eixo', 'TRANSMISSAO', 'Não informado', '120.000 km'),
('Rolamento de câmbio', 'TRANSMISSAO', 'Não informado', '100.000 km'),
('Kit de reparo de câmbio manual', 'TRANSMISSAO', 'Não informado', '150.000 km'),
('Kit de reparo de câmbio automático', 'TRANSMISSAO', 'Não informado', '150.000 km'),
('Fluido de direção hidráulica', 'DIRECAO', '36 meses', '40.000 km'),
('Terminal de direção', 'DIRECAO', 'Não informado', '60.000 km'),
('Bomba de direção hidráulica', 'DIRECAO', 'Não informado', '120.000 km'),
('Cremalheira de direção', 'DIRECAO', 'Não informado', '150.000 km'),
('Caixa de direção', 'DIRECAO', 'Não informado', '150.000 km'),
('Coluna de direção', 'DIRECAO', 'Não informado', 'Não informado'),
('Amortecedor dianteiro', 'SUSPENSAO', 'Não informado', '60.000 km'),
('Amortecedor traseiro', 'SUSPENSAO', 'Não informado', '60.000 km'),
('Mola helicoidal', 'SUSPENSAO', 'Não informado', '100.000 km'),
('Kit batente e coifa do amortecedor', 'SUSPENSAO', 'Não informado', '60.000 km'),
('Bucha da bandeja', 'SUSPENSAO', 'Não informado', '60.000 km'),
('Pivô de suspensão', 'SUSPENSAO', 'Não informado', '60.000 km'),
('Bieleta da barra estabilizadora', 'SUSPENSAO', 'Não informado', '50.000 km'),
('Barra estabilizadora', 'SUSPENSAO', 'Não informado', '100.000 km'),
('Braço de suspensão (bandeja)', 'SUSPENSAO', 'Não informado', '100.000 km'),
('Cubo de roda', 'SUSPENSAO', 'Não informado', '80.000 km'),
('Rolamento de roda', 'SUSPENSAO', 'Não informado', '80.000 km'),
('Pneu aro 14', 'SUSPENSAO', '60 meses', '40.000 km'),
('Pastilha de freio dianteira', 'FREIOS', '24 meses', '30.000 km'),
('Lona de freio traseira', 'FREIOS', '36 meses', '40.000 km'),
('Disco de freio ventilado', 'FREIOS', 'Não informado', '60.000 km'),
('Tambor de freio', 'FREIOS', 'Não informado', '80.000 km'),
('Fluido de freio DOT 4', 'FREIOS', '24 meses', '20.000 km'),
('Cilindro de roda', 'FREIOS', 'Não informado', '80.000 km'),
('Cilindro mestre de freio', 'FREIOS', 'Não informado', '100.000 km'),
('Cabo do freio de estacionamento', 'FREIOS', 'Não informado', '80.000 km'),
('Fluido de arrefecimento (aditivo)', 'ARREFECIMENTO', '24 meses', '30.000 km'),
('Bomba d\'água', 'ARREFECIMENTO', '48 meses', '60.000 km'),
('Válvula termostática', 'ARREFECIMENTO', '48 meses', '60.000 km'),
('Radiador', 'ARREFECIMENTO', 'Não informado', '120.000 km'),
('Mangueira superior do radiador', 'ARREFECIMENTO', '48 meses', '60.000 km'),
('Mangueira inferior do radiador', 'ARREFECIMENTO', '48 meses', '60.000 km'),
('Eletroventilador do radiador', 'ARREFECIMENTO', 'Não informado', '120.000 km'),
('Reservatório de expansão', 'ARREFECIMENTO', 'Não informado', '100.000 km'),
('Bateria 60Ah', 'ELETRICA', '24 meses', 'Não informado'),
('Alternador', 'ELETRICA', 'Não informado', '150.000 km'),
('Motor de arranque', 'ELETRICA', 'Não informado', '150.000 km'),
('Lâmpada de farol H4', 'ELETRICA', '12 meses', 'Não informado'),
('Compressor do ar-condicionado', 'ELETRICA', 'Não informado', '120.000 km'),
('Gás refrigerante R134a', 'ELETRICA', '18 meses', 'Não informado'),
('Sensor ABS', 'ELETRICA', 'Não informado', '100.000 km'),
('Motor do vidro elétrico', 'ELETRICA', 'Não informado', 'Não informado'),
('Atuador de trava elétrica', 'ELETRICA', 'Não informado', 'Não informado'),
('Chicote elétrico', 'ELETRICA', 'Não informado', 'Não informado'),
('Fusível 20A', 'ELETRICA', 'Não informado', 'Não informado'),
('Painel de instrumentos', 'ELETRICA', 'Não informado', 'Não informado'),
('Filtro de cabine (ar-condicionado)', 'OUTROS', '12 meses', '15.000 km'),
('Palheta do limpador de para-brisa', 'OUTROS', '12 meses', 'Não informado'),
('Silencioso traseiro do escapamento', 'OUTROS', 'Não informado', '80.000 km'),
('Coxim do escapamento', 'OUTROS', '36 meses', '60.000 km'),
('Para-choque dianteiro', 'OUTROS', 'Não informado', 'Não informado'),
('Massa plástica e primer (funilaria)', 'OUTROS', 'Não informado', 'Não informado'),
('Vidro lateral', 'OUTROS', 'Não informado', 'Não informado'),
('Para-brisa', 'OUTROS', 'Não informado', 'Não informado'),
('Cera de polimento automotivo', 'OUTROS', '12 meses', 'Não informado'),
('Produto para higienização de estofados', 'OUTROS', '12 meses', 'Não informado');

-- ============================================================
-- Pecas padrao por item do catalogo de servicos
-- (itens de inspecao, regulagem e diagnostico nao consomem peca)
-- ============================================================

INSERT INTO catalogo_servico_peca (id_catalogo_servico, id_peca)
VALUES
(1, 1),
(1, 2),
(2, 4),
(3, 5),
(4, 7),
(5, 10),
(5, 11),
(5, 12),
(6, 14),
(7, 99),
(8, 20),
(9, 42),
(12, 18),
(13, 11),
(14, 12),
(15, 13),
(16, 31),
(18, 6),
(20, 41),
(21, 44),
(22, 45),
(23, 48),
(25, 53),
(31, 75),
(32, 71),
(33, 79),
(36, 81),
(39, 92),
(40, 90),
(41, 19),
(41, 20),
(42, 43),
(42, 20),
(42, 21),
(43, 22),
(44, 23),
(45, 24),
(46, 25),
(47, 26),
(48, 27),
(49, 9),
(49, 8),
(50, 28),
(51, 29),
(52, 15),
(53, 16),
(54, 17),
(56, 30),
(57, 32),
(59, 33),
(60, 34),
(61, 35),
(63, 31),
(64, 36),
(65, 37),
(66, 38),
(68, 39),
(69, 40),
(71, 41),
(72, 47),
(72, 48),
(72, 49),
(73, 51),
(73, 50),
(74, 52),
(74, 45),
(75, 46),
(76, 50),
(77, 55),
(77, 53),
(78, 56),
(79, 58),
(80, 54),
(80, 64),
(81, 57),
(82, 68),
(82, 69),
(83, 61),
(84, 59),
(84, 60),
(84, 62),
(85, 66),
(85, 65),
(86, 63),
(87, 67),
(88, 73),
(89, 74),
(89, 72),
(90, 76),
(91, 77),
(91, 75),
(92, 78),
(93, 82),
(93, 79),
(94, 80),
(94, 79),
(95, 83),
(95, 84),
(95, 79),
(96, 81),
(97, 85),
(98, 83),
(98, 84),
(99, 88),
(99, 14),
(100, 89),
(101, 92),
(102, 91),
(102, 92),
(103, 96),
(103, 97),
(104, 93),
(105, 94),
(106, 95),
(108, 98),
(109, 101),
(109, 102),
(110, 107),
(111, 108),
(112, 103),
(113, 104),
(114, 106),
(114, 105);

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
-- Orcamentos
-- valor      = soma de orcamento_servico + orcamento_peca
-- id_peca    = peca principal (de maior valor) do orcamento
-- ============================================================

INSERT INTO orcamento (codigo, valor, tipo, responsavel, reclamacao, data_criacao, status,
                       id_peca, id_veiculo, id_cliente, id_funcionario)
VALUES
('0001', 705.00, 'ENTRADA', 'Marcos Vinícius Prado', 'Revisão preventiva dos 60.000 km', '2026-03-05', 'APROVADO', 2, 1, 1, 4),
('0002', 1590.00, 'ENTRADA', 'Fábio Nakamura', 'Ruído metálico ao frear e pedal de freio baixo', '2026-03-19', 'APROVADO', 73, 3, 2, 5),
('0003', 1000.00, 'ENTRADA', 'Marcos Vinícius Prado', 'Motor falhando em baixa rotação e consumo elevado', '2026-04-08', 'APROVADO', 7, 5, 3, 4),
('0004', 1910.00, 'ENTRADA', 'Diego Barbosa', 'Barulho na suspensão dianteira ao passar em lombadas', '2026-04-22', 'RECUSADO', 59, 2, 1, 6),
('0005', 2400.00, 'ENTRADA', 'Leandro Souza', 'Veículo não liga pela manhã e luz da bateria acesa no painel', '2026-05-06', 'APROVADO', 88, 7, 4, 7),
('0006', 1410.00, 'ENTRADA', 'Marcos Vinícius Prado', 'Superaquecimento e perda de líquido de arrefecimento', '2026-05-20', 'APROVADO', 80, 8, 5, 4),
('0007', 860.00, 'ENTRADA', 'Fábio Nakamura', 'Revisão geral antes de viagem longa', '2026-06-10', 'APROVADO', 3, 4, 2, 5),
('0008', 1435.00, 'REVISAO', 'Fábio Nakamura', 'Itens reprovados na revisão: coifa rasgada e folga no terminal', '2026-06-12', 'APROVADO', 47, 4, 2, 5),
('0009', 1520.00, 'ENTRADA', 'Diego Barbosa', 'Embreagem patinando em subidas e cheiro de queimado', '2026-06-24', 'APROVADO', 46, 6, 3, 6),
('0010', 2970.00, 'ENTRADA', 'Leandro Souza', 'Ar-condicionado não gela', '2026-07-08', 'RECUSADO', 91, 1, 1, 7),
('0011', 1775.00, 'ENTRADA', 'Marcos Vinícius Prado', 'Troca preventiva da correia dentada aos 120.000 km', '2026-07-22', 'APROVADO', 10, 7, 4, 4),
('0012', 785.00, 'ENTRADA', 'Diego Barbosa', 'Escapamento furado com ruído excessivo', '2026-08-24', 'APROVADO', 101, 8, 5, 6),
('0013', 1220.00, 'ENTRADA', 'Marcos Vinícius Prado', 'Revisão preventiva dos 90.000 km', '2026-08-28', 'APROVADO', 2, 2, 1, 4),
('0014', 890.00, 'ENTRADA', 'Leandro Souza', 'Vidro elétrico do motorista não sobe', '2026-09-01', 'PENDENTE', 94, 7, 4, 7),
('0015', 1220.00, 'ENTRADA', 'Fábio Nakamura', 'Ruído no rolamento da roda dianteira direita', '2026-09-02', 'PENDENTE', 68, 5, 3, 5);

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
(1, 75, 'DOT4 500ml', 'Bosch', 60.00),
(2, 71, 'BB-1234', 'Bosch', 220.00),
(2, 73, 'BD-7745 (par)', 'Fremax', 380.00),
(2, 75, 'DOT4 500ml', 'Bosch', 60.00),
(3, 7, 'IFR6T11 (jogo 4un)', 'NGK', 240.00),
(3, 42, 'Injec Clean 300ml', 'Bardahl', 70.00),
(3, 4, 'ARL-4110', 'Tecfil', 110.00),
(4, 59, 'GP-32456 (par)', 'Cofap', 640.00),
(4, 62, 'KB-1102', 'Cofap', 180.00),
(4, 63, 'BS-4409', 'Nakata', 120.00),
(5, 88, 'ALT-14V-90A', 'Bosch', 890.00),
(5, 87, 'M60GD 60Ah', 'Moura', 520.00),
(5, 14, '6PK1120', 'Gates', 130.00),
(6, 80, 'BA-2210', 'Urba', 260.00),
(6, 81, 'VT-88C', 'Wahler', 95.00),
(6, 79, 'Orgânico rosa 1L', 'Paraflu', 70.00),
(6, 83, 'MS-3390', 'Cofap', 85.00),
(7, 1, 'OC-264', 'Fram', 40.00),
(7, 3, '15W40 SL 4L', 'Ipiranga', 150.00),
(7, 5, 'GI-06/7', 'Tecfil', 65.00),
(7, 90, 'H4 12V 60/55W', 'Osram', 35.00),
(8, 47, 'JH-5521', 'Nakata', 310.00),
(8, 48, 'CF-1180', 'Nakata', 85.00),
(8, 54, 'TD-9902', 'Viemar', 140.00),
(9, 46, 'KE-6620', 'Luk', 720.00),
(10, 91, 'CP-8802', 'Denso', 1450.00),
(10, 92, 'R134a 750g', 'Dupont', 120.00),
(11, 10, 'CT-1088', 'Gates', 480.00),
(11, 11, 'TN-5540', 'Gates', 190.00),
(11, 12, 'RT-2214', 'Gates', 160.00),
(11, 1, 'OC-90915-YZZE1', 'Fram', 45.00),
(11, 2, '5W30 SN 4L', 'Mobil', 180.00),
(12, 101, 'SL-4471', 'Tuper', 340.00),
(12, 102, 'CX-1120', 'Borflex', 45.00),
(13, 1, 'OC-90915-YZZE1', 'Fram', 45.00),
(13, 2, '5W30 SN 4L', 'Mobil', 180.00),
(13, 4, 'ARL-3021', 'Tecfil', 90.00),
(13, 5, 'GI-06/7', 'Tecfil', 65.00),
(13, 44, '75W80 GL4 2L', 'Motul', 140.00),
(14, 94, 'MV-7781', 'Bosch', 290.00),
(15, 68, 'CB-4410', 'SKF', 320.00),
(15, 69, 'RL-3308', 'SKF', 280.00);

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

-- O orcamento 0008 nasceu dos itens reprovados na revisao da OS 0006 (id_servico = 6).
UPDATE orcamento SET id_servico_revisao = 6 WHERE id_orcamento = 8;

-- ============================================================
-- Itens de servico (etapas de cada ordem de servico)
-- ============================================================

INSERT INTO item_servico (codigo, etapa, descricao, status, tempo_gasto, data_realizacao,
                          id_peca, id_servico, id_funcionario)
VALUES
('0001', 1, 'Drenagem do óleo e troca do filtro de óleo', 'CONCLUIDA', '1h00', '2026-03-06', 1, 1, 4),
('0002', 2, 'Substituição do filtro de ar do motor', 'CONCLUIDA', '0h20', '2026-03-06', 4, 1, 8),
('0003', 3, 'Sangria e troca do fluido de freio', 'CONCLUIDA', '0h50', '2026-03-06', 75, 1, 4),
('0004', 4, 'Inspeção geral e teste de rodagem', 'CONCLUIDA', '0h30', '2026-03-06', NULL, 1, 4),
('0005', 1, 'Desmontagem das rodas e diagnóstico dos freios', 'CONCLUIDA', '0h40', '2026-03-20', NULL, 2, 5),
('0006', 2, 'Substituição das pastilhas dianteiras', 'CONCLUIDA', '1h10', '2026-03-20', 71, 2, 5),
('0007', 3, 'Substituição dos discos de freio ventilados', 'CONCLUIDA', '1h30', '2026-03-20', 73, 2, 5),
('0008', 4, 'Troca do fluido de freio e sangria do sistema', 'CONCLUIDA', '0h50', '2026-03-20', 75, 2, 8),
('0009', 1, 'Leitura de códigos de falha com scanner', 'CONCLUIDA', '0h40', '2026-04-09', NULL, 3, 4),
('0010', 2, 'Substituição do jogo de velas de ignição', 'CONCLUIDA', '1h20', '2026-04-09', 7, 3, 4),
('0011', 3, 'Limpeza dos bicos injetores em ultrassom', 'CONCLUIDA', '2h00', '2026-04-09', 42, 3, 4),
('0012', 4, 'Substituição do filtro de ar do motor', 'CONCLUIDA', '0h20', '2026-04-09', 4, 3, 8),
('0013', 1, 'Teste de carga do alternador e da bateria', 'CONCLUIDA', '0h50', '2026-05-07', NULL, 4, 7),
('0014', 2, 'Substituição do alternador', 'CONCLUIDA', '2h10', '2026-05-07', 88, 4, 7),
('0015', 3, 'Substituição da correia do alternador', 'CONCLUIDA', '0h40', '2026-05-07', 14, 4, 7),
('0016', 4, 'Substituição da bateria 60Ah', 'CONCLUIDA', '0h20', '2026-05-07', 87, 4, 8),
('0017', 1, 'Teste de pressão do sistema de arrefecimento', 'CONCLUIDA', '0h50', '2026-05-21', NULL, 5, 4),
('0018', 2, 'Substituição da bomba d\'água', 'CONCLUIDA', '2h30', '2026-05-21', 80, 5, 4),
('0019', 3, 'Substituição da válvula termostática', 'CONCLUIDA', '0h50', '2026-05-21', 81, 5, 4),
('0020', 4, 'Substituição da mangueira superior do radiador', 'CONCLUIDA', '0h30', '2026-05-21', 83, 5, 8),
('0021', 5, 'Troca do fluido e sangria do sistema', 'CONCLUIDA', '0h40', '2026-05-21', 79, 5, 8),
('0022', 1, 'Troca do óleo do motor e do filtro de óleo', 'CONCLUIDA', '1h00', '2026-06-11', 1, 6, 5),
('0023', 2, 'Substituição do filtro de combustível', 'CONCLUIDA', '0h40', '2026-06-11', 5, 6, 5),
('0024', 3, 'Alinhamento da geometria de direção', 'CONCLUIDA', '0h50', '2026-06-11', NULL, 6, 8),
('0025', 4, 'Balanceamento das quatro rodas', 'CONCLUIDA', '0h40', '2026-06-11', NULL, 6, 8),
('0026', 5, 'Rodízio dos pneus', 'CONCLUIDA', '0h30', '2026-06-11', NULL, 6, 8),
('0027', 6, 'Troca da lâmpada do farol baixo direito', 'CONCLUIDA', '0h20', '2026-06-11', 90, 6, 8),
('0028', 1, 'Substituição da junta homocinética do semi-eixo', 'CONCLUIDA', '2h40', '2026-06-15', 47, 7, 5),
('0029', 2, 'Substituição da coifa de homocinética', 'CONCLUIDA', '1h00', '2026-06-15', 48, 7, 5),
('0030', 3, 'Substituição do terminal de direção', 'CONCLUIDA', '1h10', '2026-06-15', 54, 7, 5),
('0031', 4, 'Alinhamento após a troca do terminal', 'CONCLUIDA', '0h50', '2026-06-15', NULL, 7, 8),
('0032', 1, 'Remoção da caixa de câmbio', 'CANCELADA', '', NULL, NULL, 8, 6),
('0033', 2, 'Substituição do kit de embreagem', 'CANCELADA', '', NULL, 46, 8, 6),
('0034', 1, 'Remoção das capas e travamento do motor no PMS', 'CONCLUIDA', '1h20', '2026-07-23', NULL, 9, 4),
('0035', 2, 'Substituição da correia dentada', 'CONCLUIDA', '2h30', '2026-07-23', 10, 9, 4),
('0036', 3, 'Substituição do tensor da correia dentada', 'CONCLUIDA', '0h50', '2026-07-23', 11, 9, 4),
('0037', 4, 'Substituição do rolamento tensionador', 'CONCLUIDA', '0h40', '2026-07-23', 12, 9, 4),
('0038', 5, 'Troca do óleo do motor e do filtro de óleo', 'CONCLUIDA', '1h00', '2026-07-23', 1, 9, 8),
('0039', 1, 'Diagnóstico e remoção do escapamento furado', 'CONCLUIDA', '1h00', '2026-08-26', NULL, 10, 6),
('0040', 2, 'Substituição do coxim do escapamento', 'CONCLUIDA', '0h30', '2026-08-26', 102, 10, 6),
('0041', 3, 'Instalação do silencioso traseiro novo', 'EM_ANDAMENTO', '', NULL, 101, 10, 6),
('0042', 1, 'Troca do óleo do motor e do filtro de óleo', 'PENDENTE', '', NULL, 1, 11, 4),
('0043', 2, 'Substituição do filtro de ar do motor', 'PENDENTE', '', NULL, 4, 11, 4),
('0044', 3, 'Substituição do filtro de combustível', 'PENDENTE', '', NULL, 5, 11, 4),
('0045', 4, 'Troca do óleo da caixa de câmbio manual', 'PENDENTE', '', NULL, 44, 11, 4),
('0046', 5, 'Rodízio dos pneus', 'PENDENTE', '', NULL, NULL, 11, 8),
('0047', 6, 'Alinhamento da geometria de direção', 'PENDENTE', '', NULL, NULL, 11, 8);

-- ============================================================
-- Resumo
--   oficinas 4 | usuarios 13 | funcionarios 8 | clientes 5 | veiculos 8
--   catalogo_servico 114 | pecas 108 | catalogo_servico_peca 123
--   orcamentos 15 | ordens de servico 11 | itens de servico 47
--   ORC 0001 | 2026-03-05 | cli 1 | vei 1 | APROVADO  | R$   705.00
--   ORC 0002 | 2026-03-19 | cli 2 | vei 3 | APROVADO  | R$  1590.00
--   ORC 0003 | 2026-04-08 | cli 3 | vei 5 | APROVADO  | R$  1000.00
--   ORC 0004 | 2026-04-22 | cli 1 | vei 2 | RECUSADO  | R$  1910.00
--   ORC 0005 | 2026-05-06 | cli 4 | vei 7 | APROVADO  | R$  2400.00
--   ORC 0006 | 2026-05-20 | cli 5 | vei 8 | APROVADO  | R$  1410.00
--   ORC 0007 | 2026-06-10 | cli 2 | vei 4 | APROVADO  | R$   860.00
--   ORC 0008 | 2026-06-12 | cli 2 | vei 4 | APROVADO  | R$  1435.00
--   ORC 0009 | 2026-06-24 | cli 3 | vei 6 | APROVADO  | R$  1520.00
--   ORC 0010 | 2026-07-08 | cli 1 | vei 1 | RECUSADO  | R$  2970.00
--   ORC 0011 | 2026-07-22 | cli 4 | vei 7 | APROVADO  | R$  1775.00
--   ORC 0012 | 2026-08-24 | cli 5 | vei 8 | APROVADO  | R$   785.00
--   ORC 0013 | 2026-08-28 | cli 1 | vei 2 | APROVADO  | R$  1220.00
--   ORC 0014 | 2026-09-01 | cli 4 | vei 7 | PENDENTE  | R$   890.00
--   ORC 0015 | 2026-09-02 | cli 3 | vei 5 | PENDENTE  | R$  1220.00
-- ============================================================