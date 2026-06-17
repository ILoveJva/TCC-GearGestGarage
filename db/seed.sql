-- Dados de teste opcionais (a aplicacao tambem popula via Seed.java se o banco estiver vazio).
-- Rode depois do schema.sql:  mysql -u root -p GearGestGarage < db/seed.sql
USE GearGestGarage;

INSERT INTO oficina (nome, endereco, telefone, cnpj)
VALUES ('Gear Gest Garage', 'Av. Brasil, 1000', '(11) 3000-0000', '12.345.678/0001-90');

INSERT INTO usuario (nome,cpf, email, senha, telefone, id_oficina) VALUES
 ('Administrador','oficina@geargest.com','123456','(11) 90000-0000',1),
 ('Joao da Silva','joao@email.com','1234','(11) 98888-7777',1),
 ('Maria Souza', 'maria@email.com','1234','(11) 97777-6666',1);

INSERT INTO funcionario (nome, cargo, id_usuario) VALUES ('Administrador','Gerente',1);
INSERT INTO cliente (id_usuario) VALUES (2),(3);

INSERT INTO montadora (nome, pais_origem) VALUES ('Volkswagen','Alemanha'),('Fiat','Italia');
INSERT INTO modelo (nome, ano, id_montadora) VALUES ('Gol',2020,1),('Polo',2022,1),('Uno',2018,2);

INSERT INTO veiculo (placa, tipo_veiculo, id_cliente, id_modelo) VALUES
 ('ABC-1D23','Hatch',1,1),('XYZ-9K88','Sedan',2,2);

INSERT INTO fabricante_peca (nome, pais) VALUES ('Bosch','Alemanha');
INSERT INTO peca (nome_peca, vida_util_tempo, vida_util_km, id_fabricante_peca, id_veiculo)
VALUES ('Pastilha de Freio','12 meses','30000 km',1,1);

INSERT INTO orcamento (valor, id_peca, id_veiculo, id_cliente, id_funcionario)
VALUES (450.00, 1, 1, 1, 1);

INSERT INTO servico (data_servico, km_servico, titulo, tipo_servico, status, id_veiculo, id_oficina, id_orcamento)
VALUES ('2026-06-09', 45000, 'Revisao + Freios', 'Corretiva', 'ABERTA', 1, 1, 1);

INSERT INTO item_servico (descricao, status, id_peca, id_servico, id_funcionario)
VALUES ('Troca de pastilhas de freio','PENDENTE',1,1,1);
