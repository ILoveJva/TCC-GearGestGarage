-- ============================================================
-- Migração 2026-07-11: vincular peças aos itens do catálogo de serviços
-- (tabela catalogo_servico_peca), a partir de comparação semântica
-- entre os 86 serviços cadastrados e as 138 peças cadastradas.
--
-- Substitui 3 vínculos antigos que apontavam para peças legadas
-- duplicadas/sem acentuação (ids 6, 7, 14 na tabela `peca`) pelos
-- registros corretos e corretamente categorizados por sistema.
--
-- Executar:
--   Get-Content db/migration_2026-07-11_catalogo_pecas.sql | docker exec -i gear_gest_mysql mysql -u root -proot GearGestGarage
-- ============================================================

USE GearGestGarage;

-- Remove vínculos antigos que apontavam para peças legadas duplicadas
DELETE FROM catalogo_servico_peca WHERE id_catalogo_servico_peca IN (2, 3, 4);

-- ARREFECIMENTO
INSERT INTO catalogo_servico_peca (id_catalogo_servico, id_peca) VALUES
(72, 211), (72, 216), (72, 208),   -- Reparo de vazamento de arrefecimento
(71, 210), (71, 216),              -- Troca de bomba d'água
(74, 214),                         -- Troca de eletroventilador
(75, 211),                         -- Troca de mangueiras de arrefecimento
(70, 207), (70, 212),              -- Troca de radiador
(73, 209),                         -- Troca de válvula termostática
(30, 208),                         -- Troca de fluido de arrefecimento
(33, 209);                         -- Troca de termostato

-- ELÉTRICA
INSERT INTO catalogo_servico_peca (id_catalogo_servico, id_peca) VALUES
(82, 236),                         -- Reparo de vidro elétrico -> motor do levantador de vidro
(76, 218),                         -- Troca de alternador
(79, 249),                         -- Troca de compressor de ar condicionado
(77, 220),                         -- Troca de motor de arranque
(81, 205),                         -- Troca de sensor ABS / ESP
(36, 251);                         -- Recarga de ar condicionado -> gás refrigerante R134a

-- MOTOR
INSERT INTO catalogo_servico_peca (id_catalogo_servico, id_peca) VALUES
(39, 148), (39, 150), (39, 151), (39, 152),  -- Retífica de motor
(92, 151), (92, 152),                        -- Troca das Bronzinas (substitui link legado)
(41, 238),                         -- Troca de bomba de combustível
(48, 257),                         -- Troca de catalisador
(45, 240),                         -- Troca de corpo de borboleta
(47, 232),                         -- Troca de ECU / módulo de injeção
(38, 147),                         -- Troca de junta do cabeçote
(46, 156),                         -- Troca de módulo de ignição (bobina)
(44, 158),                         -- Troca de sensor de rotação (CKP)
(43, 229),                         -- Troca de sensor de temperatura
(42, 228),                         -- Troca de sensor lambda (oxigênio)
(13, 141), (13, 142), (13, 143),   -- Troca de correia dentada (kit: correia + tensor + esticador)
(10, 145),                         -- Troca de filtro de ar
(15, 247),                         -- Troca de filtro de cabine (ar-cond.)
(11, 237),                         -- Troca de filtro de combustível
(9, 140), (9, 139),                -- Troca de oleo e Filtro (substitui links legados)
(12, 144);                         -- Troca de velas de ignição

-- OUTROS
INSERT INTO catalogo_servico_peca (id_catalogo_servico, id_peca) VALUES
(86, 258), (86, 259);              -- Reparo de escapamento / silencioso

-- SUSPENSÃO (inclui itens de freio/direção catalogados sob este sistema)
INSERT INTO catalogo_servico_peca (id_catalogo_servico, id_peca) VALUES
(64, 181), (64, 182),              -- Troca de amortecedor
(68, 186),                         -- Troca de braço de suspensão / bandeja
(67, 189),                         -- Troca de buchas de suspensão
(69, 173),                         -- Troca de caixa de direção
(59, 199),                         -- Troca de cilindro de roda
(60, 200),                         -- Troca de cilindro mestre de freio
(62, 191), (62, 190),              -- Troca de cubo e rolamento de roda
(57, 195),                         -- Troca de disco de freio
(65, 193), (65, 175),              -- Troca de pivô / terminal de direção
(58, 196),                         -- Troca de tambor de freio
(23, 198),                         -- Troca de fluido de freio (DOT)
(27, 194),                         -- Troca de pastilhas de freio
(63, 183);                         -- Troca de mola de suspensão

-- TRANSMISSÃO (inclui itens de direção catalogados sob este sistema)
INSERT INTO catalogo_servico_peca (id_catalogo_servico, id_peca) VALUES
(52, 177),                         -- Troca de bomba de direção hidráulica
(54, 179),                         -- Troca de coluna de direção
(53, 173),                         -- Troca de cremalheira de direção
(55, 163), (55, 164),              -- Troca de disco e platô de embreagem
(49, 170), (49, 169),              -- Troca de homocinética / semi-eixo
(56, 165),                         -- Troca de rolamento de câmbio
(18, 162),                         -- Troca de óleo de câmbio manual
(21, 171),                         -- Troca de coifas de homocinética
(19, 162),                         -- Troca de fluido de câmbio automático (ATF)
(20, 176);                         -- Troca de fluido de direção hidráulica
