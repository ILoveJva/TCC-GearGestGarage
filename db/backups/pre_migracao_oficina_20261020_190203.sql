mysqldump: [Warning] Using a password on the command line interface can be insecure.
-- MySQL dump 10.13  Distrib 8.4.10, for Linux (x86_64)
--
-- Host: localhost    Database: GearGestGarage
-- ------------------------------------------------------
-- Server version	8.4.10

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `catalogo_peca`
--

DROP TABLE IF EXISTS `catalogo_peca`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `catalogo_peca` (
  `id_catalogo_peca` int NOT NULL AUTO_INCREMENT,
  `nome_popular` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sistema` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'OUTROS',
  `vida_util_tempo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Não informado',
  `vida_util_km` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Não informado',
  PRIMARY KEY (`id_catalogo_peca`)
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `catalogo_peca`
--

LOCK TABLES `catalogo_peca` WRITE;
/*!40000 ALTER TABLE `catalogo_peca` DISABLE KEYS */;
INSERT INTO `catalogo_peca` VALUES (1,'Filtro de óleo','MOTOR','6 meses','10.000 km'),(2,'Óleo do motor 5W30 sintético','MOTOR','12 meses','10.000 km'),(3,'Óleo do motor 15W40 mineral','MOTOR','6 meses','5.000 km'),(4,'Filtro de ar do motor','MOTOR','12 meses','15.000 km'),(5,'Filtro de combustível','MOTOR','24 meses','20.000 km'),(6,'Filtro de combustível diesel (separador de água)','MOTOR','12 meses','10.000 km'),(7,'Vela de ignição','MOTOR','24 meses','20.000 km'),(8,'Cabo de vela','MOTOR','48 meses','40.000 km'),(9,'Bobina de ignição','MOTOR','60 meses','80.000 km'),(10,'Correia dentada','MOTOR','48 meses','60.000 km'),(11,'Tensor da correia dentada','MOTOR','48 meses','60.000 km'),(12,'Rolamento tensionador da correia','MOTOR','48 meses','60.000 km'),(13,'Kit correia dentada com bomba d\'água','MOTOR','48 meses','60.000 km'),(14,'Correia do alternador','MOTOR','36 meses','40.000 km'),(15,'Corrente de comando','MOTOR','60 meses','80.000 km'),(16,'Tensor da corrente de comando','MOTOR','60 meses','80.000 km'),(17,'Guia da corrente de comando','MOTOR','60 meses','80.000 km'),(18,'Kit corrente de comando','MOTOR','60 meses','80.000 km'),(19,'Junta do cabeçote','MOTOR','Não informado','120.000 km'),(20,'Jogo de juntas do motor','MOTOR','Não informado','120.000 km'),(21,'Retentor de válvula','MOTOR','Não informado','120.000 km'),(22,'Bomba de óleo','MOTOR','Não informado','150.000 km'),(23,'Bomba de combustível','MOTOR','Não informado','120.000 km'),(24,'Sensor lambda (sonda)','MOTOR','60 meses','80.000 km'),(25,'Sensor de temperatura do motor','MOTOR','Não informado','100.000 km'),(26,'Sensor de rotação (CKP)','MOTOR','Não informado','100.000 km'),(27,'Corpo de borboleta','MOTOR','Não informado','150.000 km'),(28,'Módulo de injeção (ECU)','MOTOR','Não informado','Não informado'),(29,'Catalisador','MOTOR','Não informado','100.000 km'),(30,'Bico injetor multiponto','MOTOR','60 meses','100.000 km'),(31,'Kit de reparo de bico injetor','MOTOR','24 meses','20.000 km'),(32,'Regulador de pressão de combustível','MOTOR','Não informado','100.000 km'),(33,'Injetor de injeção direta','MOTOR','Não informado','120.000 km'),(34,'Bomba de alta pressão (injeção direta)','MOTOR','Não informado','150.000 km'),(35,'Rail de combustível','MOTOR','Não informado','150.000 km'),(36,'Injetor diesel common rail','MOTOR','Não informado','120.000 km'),(37,'Bomba injetora diesel','MOTOR','Não informado','150.000 km'),(38,'Bomba de alta pressão diesel','MOTOR','Não informado','150.000 km'),(39,'Filtro de partículas diesel (DPF)','MOTOR','Não informado','120.000 km'),(40,'Carburador','MOTOR','Não informado','Não informado'),(41,'Kit de reparo de carburador','MOTOR','12 meses','10.000 km'),(42,'Aditivo para limpeza de bicos injetores','MOTOR','18 meses','20.000 km'),(43,'Kit de retífica do motor (pistões e bronzinas)','MOTOR','Não informado','200.000 km'),(44,'Óleo de câmbio manual 75W80','TRANSMISSAO','36 meses','40.000 km'),(45,'Fluido de câmbio automático (ATF)','TRANSMISSAO','36 meses','40.000 km'),(46,'Kit de embreagem (disco, platô e atuador)','TRANSMISSAO','Não informado','80.000 km'),(47,'Junta homocinética','TRANSMISSAO','Não informado','80.000 km'),(48,'Coifa de homocinética','TRANSMISSAO','48 meses','60.000 km'),(49,'Semi-eixo','TRANSMISSAO','Não informado','120.000 km'),(50,'Rolamento de câmbio','TRANSMISSAO','Não informado','100.000 km'),(51,'Kit de reparo de câmbio manual','TRANSMISSAO','Não informado','150.000 km'),(52,'Kit de reparo de câmbio automático','TRANSMISSAO','Não informado','150.000 km'),(53,'Fluido de direção hidráulica','DIRECAO','36 meses','40.000 km'),(54,'Terminal de direção','DIRECAO','Não informado','60.000 km'),(55,'Bomba de direção hidráulica','DIRECAO','Não informado','120.000 km'),(56,'Cremalheira de direção','DIRECAO','Não informado','150.000 km'),(57,'Caixa de direção','DIRECAO','Não informado','150.000 km'),(58,'Coluna de direção','DIRECAO','Não informado','Não informado'),(59,'Amortecedor dianteiro','SUSPENSAO','Não informado','60.000 km'),(60,'Amortecedor traseiro','SUSPENSAO','Não informado','60.000 km'),(61,'Mola helicoidal','SUSPENSAO','Não informado','100.000 km'),(62,'Kit batente e coifa do amortecedor','SUSPENSAO','Não informado','60.000 km'),(63,'Bucha da bandeja','SUSPENSAO','Não informado','60.000 km'),(64,'Pivô de suspensão','SUSPENSAO','Não informado','60.000 km'),(65,'Bieleta da barra estabilizadora','SUSPENSAO','Não informado','50.000 km'),(66,'Barra estabilizadora','SUSPENSAO','Não informado','100.000 km'),(67,'Braço de suspensão (bandeja)','SUSPENSAO','Não informado','100.000 km'),(68,'Cubo de roda','SUSPENSAO','Não informado','80.000 km'),(69,'Rolamento de roda','SUSPENSAO','Não informado','80.000 km'),(70,'Pneu aro 14','SUSPENSAO','60 meses','40.000 km'),(71,'Pastilha de freio dianteira','FREIOS','24 meses','30.000 km'),(72,'Lona de freio traseira','FREIOS','36 meses','40.000 km'),(73,'Disco de freio ventilado','FREIOS','Não informado','60.000 km'),(74,'Tambor de freio','FREIOS','Não informado','80.000 km'),(75,'Fluido de freio DOT 4','FREIOS','24 meses','20.000 km'),(76,'Cilindro de roda','FREIOS','Não informado','80.000 km'),(77,'Cilindro mestre de freio','FREIOS','Não informado','100.000 km'),(78,'Cabo do freio de estacionamento','FREIOS','Não informado','80.000 km'),(79,'Fluido de arrefecimento (aditivo)','ARREFECIMENTO','24 meses','30.000 km'),(80,'Bomba d\'água','ARREFECIMENTO','48 meses','60.000 km'),(81,'Válvula termostática','ARREFECIMENTO','48 meses','60.000 km'),(82,'Radiador','ARREFECIMENTO','Não informado','120.000 km'),(83,'Mangueira superior do radiador','ARREFECIMENTO','48 meses','60.000 km'),(84,'Mangueira inferior do radiador','ARREFECIMENTO','48 meses','60.000 km'),(85,'Eletroventilador do radiador','ARREFECIMENTO','Não informado','120.000 km'),(86,'Reservatório de expansão','ARREFECIMENTO','Não informado','100.000 km'),(87,'Bateria 60Ah','ELETRICA','24 meses','Não informado'),(88,'Alternador','ELETRICA','Não informado','150.000 km'),(89,'Motor de arranque','ELETRICA','Não informado','150.000 km'),(90,'Lâmpada de farol H4','ELETRICA','12 meses','Não informado'),(91,'Compressor do ar-condicionado','ELETRICA','Não informado','120.000 km'),(92,'Gás refrigerante R134a','ELETRICA','18 meses','Não informado'),(93,'Sensor ABS','ELETRICA','Não informado','100.000 km'),(94,'Motor do vidro elétrico','ELETRICA','Não informado','Não informado'),(95,'Atuador de trava elétrica','ELETRICA','Não informado','Não informado'),(96,'Chicote elétrico','ELETRICA','Não informado','Não informado'),(97,'Fusível 20A','ELETRICA','Não informado','Não informado'),(98,'Painel de instrumentos','ELETRICA','Não informado','Não informado'),(99,'Filtro de cabine (ar-condicionado)','OUTROS','12 meses','15.000 km'),(100,'Palheta do limpador de para-brisa','OUTROS','12 meses','Não informado'),(101,'Silencioso traseiro do escapamento','OUTROS','Não informado','80.000 km'),(102,'Coxim do escapamento','OUTROS','36 meses','60.000 km'),(103,'Para-choque dianteiro','OUTROS','Não informado','Não informado'),(104,'Massa plástica e primer (funilaria)','OUTROS','Não informado','Não informado'),(105,'Vidro lateral','OUTROS','Não informado','Não informado'),(106,'Para-brisa','OUTROS','Não informado','Não informado'),(107,'Cera de polimento automotivo','OUTROS','12 meses','Não informado'),(108,'Produto para higienização de estofados','OUTROS','12 meses','Não informado');
/*!40000 ALTER TABLE `catalogo_peca` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `catalogo_servico`
--

DROP TABLE IF EXISTS `catalogo_servico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `catalogo_servico` (
  `id_catalogo_servico` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descricao` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `valor` decimal(10,2) NOT NULL DEFAULT '0.00',
  `tipo` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PADRAO',
  `tipo_manutencao` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CORRETIVA',
  `sistema` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'OUTROS',
  `validade_km` int DEFAULT NULL,
  `validade_meses` int DEFAULT NULL,
  PRIMARY KEY (`id_catalogo_servico`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `catalogo_servico`
--

LOCK TABLES `catalogo_servico` WRITE;
/*!40000 ALTER TABLE `catalogo_servico` DISABLE KEYS */;
INSERT INTO `catalogo_servico` VALUES (1,'Troca de óleo e filtro de óleo','Drenagem do óleo usado, substituição do filtro e reposição com óleo na especificação do fabricante.',120.00,'PADRAO','PREVENTIVA','MOTOR',5000,6),(2,'Troca de filtro de ar','Substituição do elemento filtrante de ar e limpeza da caixa do filtro.',80.00,'PADRAO','PREVENTIVA','MOTOR',15000,12),(3,'Troca de filtro de combustível','Substituição do filtro de combustível da linha de alimentação e verificação de vazamentos.',100.00,'PADRAO','PREVENTIVA','MOTOR',20000,24),(4,'Troca de velas de ignição','Substituição do jogo de velas com aferição da folga dos eletrodos e torque especificado.',200.00,'PADRAO','PREVENTIVA','MOTOR',20000,24),(5,'Troca de correia dentada (kit distribuição)','Substituição da correia dentada, tensor e rolamentos, com sincronismo do motor no PMS.',600.00,'PADRAO','PREVENTIVA','MOTOR',60000,48),(6,'Troca de correia do alternador','Substituição da correia poli V de acionamento do alternador e acessórios.',250.00,'PADRAO','PREVENTIVA','MOTOR',40000,36),(7,'Troca de filtro de cabine (ar-cond.)','Substituição do filtro de ar da cabine e higienização das saídas de ventilação.',90.00,'PADRAO','PREVENTIVA','MOTOR',15000,12),(8,'Regulagem de válvulas','Aferição e ajuste da folga de válvulas conforme especificação do fabricante.',350.00,'PADRAO','PREVENTIVA','MOTOR',30000,24),(9,'Limpeza de bicos injetores','Limpeza dos bicos injetores por aditivo em linha, com teste de vazão e estanqueidade.',300.00,'PADRAO','PREVENTIVA','MOTOR',20000,18),(10,'Inspeção de correia dentada','Inspeção visual do estado, tensão e alinhamento da correia dentada.',80.00,'PADRAO','PREVENTIVA','MOTOR',30000,24),(11,'Inspeção de corrente de comando','Inspeção do estiramento da corrente de comando, tensor e guias por ruído e folga.',100.00,'PADRAO','PREVENTIVA','MOTOR',30000,24),(12,'Troca de kit corrente de comando (corrente+tensor+guia)','Substituição completa do kit de corrente de comando com sincronismo do motor.',1200.00,'PADRAO','PREVENTIVA','MOTOR',80000,60),(13,'Troca de tensor da correia dentada','Substituição do tensor da correia dentada com verificação da tensão de trabalho.',280.00,'PADRAO','PREVENTIVA','MOTOR',60000,48),(14,'Troca de rolamento tensionador da correia','Substituição do rolamento tensionador da correia e teste de ruído.',200.00,'PADRAO','PREVENTIVA','MOTOR',60000,48),(15,'Troca de kit correia dentada com bomba d\'água','Substituição do kit de distribuição incluindo a bomba d\'água acionada pela correia.',900.00,'PADRAO','PREVENTIVA','MOTOR',60000,48),(16,'Limpeza de bicos injetores multiponto (ultrassom)','Remoção dos bicos injetores e limpeza em cuba de ultrassom com teste de vazão.',380.00,'PADRAO','PREVENTIVA','MOTOR',20000,18),(17,'Limpeza de válvulas de admissão (injeção direta)','Descarbonização das válvulas de admissão em motores de injeção direta.',500.00,'PADRAO','PREVENTIVA','MOTOR',30000,24),(18,'Troca de filtro de combustível diesel','Substituição do filtro de combustível diesel e drenagem do separador de água.',150.00,'PADRAO','PREVENTIVA','MOTOR',10000,12),(19,'Limpeza de filtro de partículas diesel (DPF)','Regeneração forçada e limpeza química do filtro de partículas diesel.',450.00,'PADRAO','PREVENTIVA','MOTOR',40000,24),(20,'Limpeza e regulagem de carburador','Desmontagem, limpeza e regulagem do carburador com ajuste de marcha lenta.',200.00,'PADRAO','PREVENTIVA','MOTOR',10000,12),(21,'Troca de óleo de câmbio manual','Drenagem e reposição do óleo da caixa de câmbio manual.',200.00,'PADRAO','PREVENTIVA','TRANSMISSAO',40000,36),(22,'Troca de fluido de câmbio automático (ATF)','Troca do fluido ATF do câmbio automático com verificação de nível e temperatura.',350.00,'PADRAO','PREVENTIVA','TRANSMISSAO',40000,36),(23,'Troca de coifas de homocinética','Substituição das coifas das juntas homocinéticas e reposição de graxa.',400.00,'PADRAO','PREVENTIVA','TRANSMISSAO',60000,48),(24,'Regulagem de embreagem','Ajuste do curso e do ponto de acionamento do pedal de embreagem.',120.00,'PADRAO','PREVENTIVA','TRANSMISSAO',20000,18),(25,'Troca de fluido de direção hidráulica','Drenagem e reposição do fluido do sistema de direção hidráulica.',150.00,'PADRAO','PREVENTIVA','DIRECAO',40000,36),(26,'Rodízio de pneus','Inversão das posições dos pneus para uniformizar o desgaste da banda de rodagem.',80.00,'PADRAO','PREVENTIVA','SUSPENSAO',10000,6),(27,'Alinhamento de rodas','Ajuste da geometria de direção (cambagem, cáster e convergência) em rampa.',120.00,'PADRAO','PREVENTIVA','SUSPENSAO',10000,6),(28,'Balanceamento de rodas','Balanceamento das rodas com contrapesos para eliminar vibração em velocidade.',100.00,'PADRAO','PREVENTIVA','SUSPENSAO',10000,6),(29,'Inspeção de amortecedores','Inspeção de vazamentos, batentes e eficiência dos amortecedores.',80.00,'PADRAO','PREVENTIVA','SUSPENSAO',30000,24),(30,'Inspeção de rolamentos de roda','Inspeção de folga e ruído nos rolamentos das quatro rodas.',80.00,'PADRAO','PREVENTIVA','SUSPENSAO',40000,36),(31,'Troca de fluido de freio (DOT)','Substituição do fluido de freio com sangria completa do circuito.',130.00,'PADRAO','PREVENTIVA','FREIOS',20000,24),(32,'Troca de pastilhas de freio','Substituição das pastilhas de freio e limpeza das pinças.',300.00,'PADRAO','PREVENTIVA','FREIOS',30000,24),(33,'Troca de fluido de arrefecimento','Drenagem e reposição do fluido de arrefecimento com sangria do sistema.',150.00,'PADRAO','PREVENTIVA','ARREFECIMENTO',30000,24),(34,'Limpeza de radiador','Limpeza externa e interna das colmeias do radiador para restabelecer a troca térmica.',200.00,'PADRAO','PREVENTIVA','ARREFECIMENTO',30000,24),(35,'Inspeção de mangueiras de arrefecimento','Inspeção de trincas, ressecamento e vazamento nas mangueiras de arrefecimento.',80.00,'PADRAO','PREVENTIVA','ARREFECIMENTO',20000,18),(36,'Troca de termostato','Substituição da válvula termostática e verificação da temperatura de abertura.',250.00,'PADRAO','PREVENTIVA','ARREFECIMENTO',60000,48),(37,'Teste e carga de bateria','Teste de carga e densidade da bateria com recarga quando necessário.',60.00,'PADRAO','PREVENTIVA','ELETRICA',20000,12),(38,'Inspeção do sistema de carga (alternador)','Medição da tensão de carga do alternador e do consumo parasita do sistema.',80.00,'PADRAO','PREVENTIVA','ELETRICA',20000,12),(39,'Recarga de ar condicionado','Recarga do gás refrigerante do ar-condicionado com teste de estanqueidade.',200.00,'PADRAO','PREVENTIVA','ELETRICA',NULL,18),(40,'Inspeção de lâmpadas e sinalização','Verificação de faróis, lanternas, setas e luz de freio, com troca de lâmpadas queimadas.',50.00,'PADRAO','PREVENTIVA','ELETRICA',10000,6),(41,'Troca de junta do cabeçote','Substituição da junta do cabeçote com planificação e teste de compressão.',1200.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(42,'Retífica de motor','Retífica completa do motor com usinagem, substituição de pistões, bronzinas e juntas.',3500.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(43,'Troca de bomba de óleo','Substituição da bomba de óleo e verificação da pressão de lubrificação.',500.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(44,'Troca de bomba de combustível','Substituição da bomba de combustível e teste de pressão da linha.',600.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(45,'Troca de sensor lambda (oxigênio)','Substituição da sonda lambda e apagamento dos códigos de falha.',400.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(46,'Troca de sensor de temperatura','Substituição do sensor de temperatura do motor e teste de leitura no scanner.',250.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(47,'Troca de sensor de rotação (CKP)','Substituição do sensor de rotação do virabrequim e verificação do sinal.',350.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(48,'Troca de corpo de borboleta','Substituição do corpo de borboleta com aprendizado eletrônico da marcha lenta.',500.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(49,'Troca de módulo de ignição (bobina)','Substituição da bobina de ignição e teste de faísca nos cilindros.',350.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(50,'Troca de ECU / módulo de injeção','Substituição e codificação do módulo de injeção eletrônica.',800.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(51,'Troca de catalisador','Substituição do catalisador e verificação da emissão de gases.',700.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(52,'Troca de corrente de comando','Substituição da corrente de comando com sincronismo do motor.',800.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(53,'Troca de tensor de corrente de comando','Substituição do tensor da corrente de comando e verificação da pressão de óleo.',350.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(54,'Troca de guia de corrente de comando','Substituição das guias da corrente de comando.',280.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(55,'Reparo de ruído em corrente de comando','Diagnóstico e correção de ruído de estiramento na corrente de comando.',200.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(56,'Troca de bicos injetores multiponto','Substituição dos bicos injetores em motores de injeção multiponto.',600.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(57,'Troca de regulador de pressão de combustível','Substituição do regulador de pressão e aferição da pressão da linha.',350.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(58,'Diagnóstico de sistema de injeção multiponto','Diagnóstico eletrônico completo do sistema de injeção multiponto com scanner.',200.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(59,'Troca de injetores de injeção direta','Substituição dos injetores de injeção direta com codificação no módulo.',1000.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(60,'Troca de bomba de alta pressão (injeção direta)','Substituição da bomba de alta pressão do sistema de injeção direta.',1200.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(61,'Troca de rail de combustível (injeção direta)','Substituição do rail (flauta) de combustível e teste de estanqueidade.',700.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(62,'Diagnóstico de sistema de injeção direta','Diagnóstico do sistema de injeção direta com leitura de pressão de rail.',200.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(63,'Limpeza de injetores diesel (common rail)','Limpeza e teste dos injetores diesel common rail em bancada.',700.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(64,'Troca de injetores diesel','Substituição dos injetores diesel com codificação IMA no módulo.',1500.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(65,'Troca de bomba injetora diesel','Substituição da bomba injetora diesel e calibração do ponto de injeção.',1800.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(66,'Troca de bomba de alta pressão diesel','Substituição da bomba de alta pressão do sistema diesel.',1400.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(67,'Reparo de sistema common rail','Diagnóstico e reparo de perda de pressão no sistema common rail.',900.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(68,'Troca de filtro de partículas (DPF)','Substituição do filtro de partículas diesel e reprogramação do módulo.',1000.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(69,'Troca de carburador','Substituição do carburador com regulagem de marcha lenta e mistura.',700.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(70,'Regulagem de mistura (carburador)','Ajuste da mistura ar-combustível do carburador com analisador de gases.',120.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(71,'Troca de kit de reparo de carburador','Desmontagem do carburador e substituição do kit de reparo (juntas e agulhas).',300.00,'REVISAO','CORRETIVA','MOTOR',NULL,NULL),(72,'Troca de homocinética / semi-eixo','Substituição da junta homocinética ou do semi-eixo completo com coifa e graxa.',600.00,'REVISAO','CORRETIVA','TRANSMISSAO',NULL,NULL),(73,'Reparo de câmbio manual','Desmontagem e reparo interno do câmbio manual com troca de rolamentos e sincronizadores.',1500.00,'REVISAO','CORRETIVA','TRANSMISSAO',NULL,NULL),(74,'Reparo de câmbio automático','Desmontagem e reparo do câmbio automático com troca de fluido e filtro.',2500.00,'REVISAO','CORRETIVA','TRANSMISSAO',NULL,NULL),(75,'Troca de disco e platô de embreagem','Substituição do kit de embreagem com remoção da caixa de câmbio.',800.00,'REVISAO','CORRETIVA','TRANSMISSAO',NULL,NULL),(76,'Troca de rolamento de câmbio','Substituição do rolamento da caixa de câmbio e verificação de ruído.',400.00,'REVISAO','CORRETIVA','TRANSMISSAO',NULL,NULL),(77,'Troca de bomba de direção hidráulica','Substituição da bomba de direção hidráulica e sangria do sistema.',700.00,'REVISAO','CORRETIVA','DIRECAO',NULL,NULL),(78,'Troca de cremalheira de direção','Substituição da cremalheira de direção seguida de alinhamento.',900.00,'REVISAO','CORRETIVA','DIRECAO',NULL,NULL),(79,'Troca de coluna de direção','Substituição da coluna de direção com verificação do travamento e do curso.',600.00,'REVISAO','CORRETIVA','DIRECAO',NULL,NULL),(80,'Troca de pivô / terminal de direção','Substituição dos pivôs e terminais de direção seguida de alinhamento.',300.00,'REVISAO','CORRETIVA','DIRECAO',NULL,NULL),(81,'Troca de caixa de direção','Substituição da caixa de direção completa seguida de alinhamento.',1200.00,'REVISAO','CORRETIVA','DIRECAO',NULL,NULL),(82,'Troca de cubo e rolamento de roda','Substituição do cubo e do rolamento de roda com torque especificado.',500.00,'REVISAO','CORRETIVA','SUSPENSAO',NULL,NULL),(83,'Troca de mola de suspensão','Substituição das molas helicoidais da suspensão em par.',400.00,'REVISAO','CORRETIVA','SUSPENSAO',NULL,NULL),(84,'Troca de amortecedor','Substituição dos amortecedores em par com batentes e coifas.',500.00,'REVISAO','CORRETIVA','SUSPENSAO',NULL,NULL),(85,'Troca de barra estabilizadora','Substituição da barra estabilizadora e das bieletas.',400.00,'REVISAO','CORRETIVA','SUSPENSAO',NULL,NULL),(86,'Troca de buchas de suspensão','Substituição das buchas da bandeja e demais buchas de suspensão.',350.00,'REVISAO','CORRETIVA','SUSPENSAO',NULL,NULL),(87,'Troca de braço de suspensão / bandeja','Substituição do braço de suspensão (bandeja) seguida de alinhamento.',600.00,'REVISAO','CORRETIVA','SUSPENSAO',NULL,NULL),(88,'Troca de disco de freio','Substituição dos discos de freio em par com verificação da espessura mínima.',500.00,'REVISAO','CORRETIVA','FREIOS',NULL,NULL),(89,'Troca de tambor de freio','Substituição dos tambores de freio e regulagem das lonas.',350.00,'REVISAO','CORRETIVA','FREIOS',NULL,NULL),(90,'Troca de cilindro de roda','Substituição do cilindro de roda e sangria do circuito traseiro.',250.00,'REVISAO','CORRETIVA','FREIOS',NULL,NULL),(91,'Troca de cilindro mestre de freio','Substituição do cilindro mestre de freio e sangria completa do sistema.',400.00,'REVISAO','CORRETIVA','FREIOS',NULL,NULL),(92,'Reparo de freio de estacionamento','Regulagem ou substituição do cabo do freio de estacionamento.',200.00,'REVISAO','CORRETIVA','FREIOS',NULL,NULL),(93,'Troca de radiador','Substituição do radiador com reposição do fluido e sangria do sistema.',800.00,'REVISAO','CORRETIVA','ARREFECIMENTO',NULL,NULL),(94,'Troca de bomba d\'água','Substituição da bomba d\'água com troca da junta e do fluido de arrefecimento.',500.00,'REVISAO','CORRETIVA','ARREFECIMENTO',NULL,NULL),(95,'Reparo de vazamento de arrefecimento','Localização e correção de vazamento no sistema de arrefecimento por teste de pressão.',300.00,'REVISAO','CORRETIVA','ARREFECIMENTO',NULL,NULL),(96,'Troca de válvula termostática','Substituição da válvula termostática e teste de temperatura de trabalho.',250.00,'REVISAO','CORRETIVA','ARREFECIMENTO',NULL,NULL),(97,'Troca de eletroventilador','Substituição do eletroventilador e verificação do relé de acionamento.',400.00,'REVISAO','CORRETIVA','ARREFECIMENTO',NULL,NULL),(98,'Troca de mangueiras de arrefecimento','Substituição das mangueiras de arrefecimento e abraçadeiras.',200.00,'REVISAO','CORRETIVA','ARREFECIMENTO',NULL,NULL),(99,'Troca de alternador','Substituição do alternador e teste da tensão de carga.',800.00,'REVISAO','CORRETIVA','ELETRICA',NULL,NULL),(100,'Troca de motor de arranque','Substituição do motor de arranque e verificação do circuito de partida.',600.00,'REVISAO','CORRETIVA','ELETRICA',NULL,NULL),(101,'Reparo de compressor de ar condicionado','Reparo do compressor do ar-condicionado com recarga de gás e óleo.',700.00,'REVISAO','CORRETIVA','ELETRICA',NULL,NULL),(102,'Troca de compressor de ar condicionado','Substituição do compressor do ar-condicionado com vácuo e recarga do sistema.',1200.00,'REVISAO','CORRETIVA','ELETRICA',NULL,NULL),(103,'Reparo de curto-circuito elétrico','Localização e reparo de curto-circuito no chicote elétrico.',300.00,'REVISAO','CORRETIVA','ELETRICA',NULL,NULL),(104,'Troca de sensor ABS / ESP','Substituição do sensor de rotação de roda do sistema ABS/ESP.',500.00,'REVISAO','CORRETIVA','ELETRICA',NULL,NULL),(105,'Reparo de vidro elétrico','Reparo ou substituição do motor e da máquina do vidro elétrico.',250.00,'REVISAO','CORRETIVA','ELETRICA',NULL,NULL),(106,'Reparo de travas elétricas','Reparo ou substituição dos atuadores de trava elétrica das portas.',200.00,'REVISAO','CORRETIVA','ELETRICA',NULL,NULL),(107,'Diagnóstico e reparo de falhas eletrônicas','Diagnóstico eletrônico com scanner e reparo das falhas registradas.',350.00,'REVISAO','CORRETIVA','ELETRICA',NULL,NULL),(108,'Troca de painel de instrumentos','Substituição do painel de instrumentos com codificação da quilometragem.',900.00,'REVISAO','CORRETIVA','ELETRICA',NULL,NULL),(109,'Reparo de escapamento / silencioso','Reparo ou substituição do silencioso e das conexões do escapamento.',400.00,'REVISAO','CORRETIVA','OUTROS',NULL,NULL),(110,'Polimento e cristalização','Polimento da pintura com cristalização para proteção e brilho.',300.00,'REVISAO','CORRETIVA','OUTROS',NULL,NULL),(111,'Higienização de estofados e cabine','Higienização a vapor dos estofados, carpetes e cabine.',250.00,'REVISAO','CORRETIVA','OUTROS',NULL,NULL),(112,'Reparo de para-choque','Reparo ou substituição do para-choque com pintura de acabamento.',350.00,'REVISAO','CORRETIVA','OUTROS',NULL,NULL),(113,'Reparo de lataria','Reparo de amassados e riscos na lataria com massa, lixamento e pintura.',500.00,'REVISAO','CORRETIVA','OUTROS',NULL,NULL),(114,'Reparo de vidros (trinca / quebrado)','Reparo ou substituição de vidros trincados ou quebrados.',300.00,'REVISAO','CORRETIVA','OUTROS',NULL,NULL);
/*!40000 ALTER TABLE `catalogo_servico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `catalogo_servico_peca`
--

DROP TABLE IF EXISTS `catalogo_servico_peca`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `catalogo_servico_peca` (
  `id_catalogo_servico_peca` int NOT NULL AUTO_INCREMENT,
  `id_catalogo_servico` int NOT NULL,
  `id_catalogo_peca` int NOT NULL,
  PRIMARY KEY (`id_catalogo_servico_peca`),
  KEY `fk_catpeca_catalogo` (`id_catalogo_servico`),
  KEY `fk_catpeca_peca` (`id_catalogo_peca`),
  CONSTRAINT `fk_catpeca_catalogo` FOREIGN KEY (`id_catalogo_servico`) REFERENCES `catalogo_servico` (`id_catalogo_servico`),
  CONSTRAINT `fk_catpeca_peca` FOREIGN KEY (`id_catalogo_peca`) REFERENCES `catalogo_peca` (`id_catalogo_peca`)
) ENGINE=InnoDB AUTO_INCREMENT=124 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `catalogo_servico_peca`
--

LOCK TABLES `catalogo_servico_peca` WRITE;
/*!40000 ALTER TABLE `catalogo_servico_peca` DISABLE KEYS */;
INSERT INTO `catalogo_servico_peca` VALUES (1,1,1),(2,1,2),(3,2,4),(4,3,5),(5,4,7),(6,5,10),(7,5,11),(8,5,12),(9,6,14),(10,7,99),(11,8,20),(12,9,42),(13,12,18),(14,13,11),(15,14,12),(16,15,13),(17,16,31),(18,18,6),(19,20,41),(20,21,44),(21,22,45),(22,23,48),(23,25,53),(24,31,75),(25,32,71),(26,33,79),(27,36,81),(28,39,92),(29,40,90),(30,41,19),(31,41,20),(32,42,43),(33,42,20),(34,42,21),(35,43,22),(36,44,23),(37,45,24),(38,46,25),(39,47,26),(40,48,27),(41,49,9),(42,49,8),(43,50,28),(44,51,29),(45,52,15),(46,53,16),(47,54,17),(48,56,30),(49,57,32),(50,59,33),(51,60,34),(52,61,35),(53,63,31),(54,64,36),(55,65,37),(56,66,38),(57,68,39),(58,69,40),(59,71,41),(60,72,47),(61,72,48),(62,72,49),(63,73,51),(64,73,50),(65,74,52),(66,74,45),(67,75,46),(68,76,50),(69,77,55),(70,77,53),(71,78,56),(72,79,58),(73,80,54),(74,80,64),(75,81,57),(76,82,68),(77,82,69),(78,83,61),(79,84,59),(80,84,60),(81,84,62),(82,85,66),(83,85,65),(84,86,63),(85,87,67),(86,88,73),(87,89,74),(88,89,72),(89,90,76),(90,91,77),(91,91,75),(92,92,78),(93,93,82),(94,93,79),(95,94,80),(96,94,79),(97,95,83),(98,95,84),(99,95,79),(100,96,81),(101,97,85),(102,98,83),(103,98,84),(104,99,88),(105,99,14),(106,100,89),(107,101,92),(108,102,91),(109,102,92),(110,103,96),(111,103,97),(112,104,93),(113,105,94),(114,106,95),(115,108,98),(116,109,101),(117,109,102),(118,110,107),(119,111,108),(120,112,103),(121,113,104),(122,114,106),(123,114,105);
/*!40000 ALTER TABLE `catalogo_servico_peca` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `id_cliente` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  PRIMARY KEY (`id_cliente`),
  KEY `fk_cliente_usuario` (`id_usuario`),
  CONSTRAINT `fk_cliente_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente`
--

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` VALUES (1,2),(2,10),(3,11),(4,12),(5,13),(6,14);
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `despesa`
--

DROP TABLE IF EXISTS `despesa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `despesa` (
  `id_despesa` int NOT NULL AUTO_INCREMENT,
  `descricao` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `categoria` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'OUTROS',
  `valor` decimal(10,2) NOT NULL,
  `data_despesa` date NOT NULL,
  `forma_pagamento` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'OUTROS',
  `observacao` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `id_oficina` int NOT NULL,
  PRIMARY KEY (`id_despesa`),
  KEY `fk_despesa_oficina` (`id_oficina`),
  CONSTRAINT `fk_despesa_oficina` FOREIGN KEY (`id_oficina`) REFERENCES `oficina` (`id_oficina`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `despesa`
--

LOCK TABLES `despesa` WRITE;
/*!40000 ALTER TABLE `despesa` DISABLE KEYS */;
/*!40000 ALTER TABLE `despesa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalhes_veiculo`
--

DROP TABLE IF EXISTS `detalhes_veiculo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalhes_veiculo` (
  `id_detalhes` int NOT NULL AUTO_INCREMENT,
  `id_veiculo` int NOT NULL,
  `motor` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cambio` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `direcao` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sistema_freios` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cor` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `vin` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_detalhes`),
  KEY `fk_detalhes_veiculo` (`id_veiculo`),
  CONSTRAINT `fk_detalhes_veiculo` FOREIGN KEY (`id_veiculo`) REFERENCES `veiculo` (`id_veiculo`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalhes_veiculo`
--

LOCK TABLES `detalhes_veiculo` WRITE;
/*!40000 ALTER TABLE `detalhes_veiculo` DISABLE KEYS */;
INSERT INTO `detalhes_veiculo` VALUES (1,1,'1.4 8V Flex','Manual 5 marchas','Hidráulica','Disco / Tambor','Prata','9BHZC13406P100411'),(2,2,'1.0 8V Flex','Manual 5 marchas','Elétrica','Disco / Tambor','Branco','9BWAA45U0EP512337'),(3,3,'1.0 Turbo Flex','Automática 6 marchas','Elétrica','Disco ventilado / Tambor','Preto','9BGKS48T0KG227118'),(4,4,'1.0 8V Fire Flex','Manual 5 marchas','Mecânica','Disco / Tambor','Vermelho','9BD17106G62310945'),(5,5,'2.0 16V Flex','Automática CVT','Elétrica','Disco ventilado / Disco','Cinza','9BRBLWHE1J0330872'),(6,6,'1.0 12V Flex','Manual 5 marchas','Elétrica','Disco / Tambor','Azul','9BFZH55L1H8441209'),(7,7,'2.0 16V Flexone','Automática CVT','Elétrica','Disco ventilado / Disco','Branco','93HFB2650FZ118874'),(8,8,'1.0 8V Fire Evo','Manual 5 marchas','Mecânica','Disco / Tambor','Prata','9BD195132C2660513');
/*!40000 ALTER TABLE `detalhes_veiculo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `funcionario`
--

DROP TABLE IF EXISTS `funcionario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `funcionario` (
  `id_funcionario` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cargo` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `endereco` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `id_usuario` int NOT NULL,
  PRIMARY KEY (`id_funcionario`),
  KEY `fk_funcionario_usuario` (`id_usuario`),
  CONSTRAINT `fk_funcionario_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `funcionario`
--

LOCK TABLES `funcionario` WRITE;
/*!40000 ALTER TABLE `funcionario` DISABLE KEYS */;
INSERT INTO `funcionario` VALUES (1,'Arthur Cecconi','Gerente','Rua Itapetinga, 800 - Santa Luzia',1),(2,'Adriano Imperador','Gerente','Rua dos Bobos, 67 - Centro',3),(3,'Roberto Carlos','Gerente','Rua dos Bobos, 10 - Centro',4),(4,'Marcos Vinícius Prado','Mecânico Chefe','Rua das Oliveiras, 120 - Santa Luzia',5),(5,'Fábio Nakamura','Mecânico','Av. Brasil, 455 - Centro',6),(6,'Diego Barbosa','Mecânico','Rua Piratininga, 88 - Vila Nova',7),(7,'Leandro Souza','Eletricista Automotivo','Rua Dom Pedro I, 341 - Jardim Aurora',8),(8,'Paulo Henrique Dias','Auxiliar Mecânico','Rua das Acácias, 27 - Santa Luzia',9);
/*!40000 ALTER TABLE `funcionario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_servico`
--

DROP TABLE IF EXISTS `item_servico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_servico` (
  `id_item_servico` int NOT NULL AUTO_INCREMENT,
  `etapa` int NOT NULL DEFAULT '0',
  `codigo` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `descricao` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'PENDENTE',
  `tempo_gasto` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `data_realizacao` date DEFAULT NULL,
  `id_peca` int DEFAULT NULL,
  `id_servico` int NOT NULL,
  `id_funcionario` int DEFAULT NULL,
  PRIMARY KEY (`id_item_servico`),
  KEY `fk_itemservico_peca` (`id_peca`),
  KEY `fk_itemservico_servico` (`id_servico`),
  KEY `fk_itemservico_funcionario` (`id_funcionario`),
  CONSTRAINT `fk_itemservico_funcionario` FOREIGN KEY (`id_funcionario`) REFERENCES `funcionario` (`id_funcionario`),
  CONSTRAINT `fk_itemservico_peca` FOREIGN KEY (`id_peca`) REFERENCES `peca` (`id_peca`),
  CONSTRAINT `fk_itemservico_servico` FOREIGN KEY (`id_servico`) REFERENCES `servico` (`id_servico`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_servico`
--

LOCK TABLES `item_servico` WRITE;
/*!40000 ALTER TABLE `item_servico` DISABLE KEYS */;
INSERT INTO `item_servico` VALUES (1,1,'0001','Drenagem do óleo e troca do filtro de óleo','CONCLUIDA','1h00','2026-03-06',1,1,4),(2,2,'0002','Substituição do filtro de ar do motor','CONCLUIDA','0h20','2026-03-06',3,1,8),(3,3,'0003','Sangria e troca do fluido de freio','CONCLUIDA','0h50','2026-03-06',4,1,4),(4,4,'0004','Inspeção geral e teste de rodagem','CONCLUIDA','0h30','2026-03-06',NULL,1,4),(5,1,'0005','Desmontagem das rodas e diagnóstico dos freios','CONCLUIDA','0h40','2026-03-20',NULL,2,5),(6,2,'0006','Substituição das pastilhas dianteiras','CONCLUIDA','1h10','2026-03-20',5,2,5),(7,3,'0007','Substituição dos discos de freio ventilados','CONCLUIDA','1h30','2026-03-20',6,2,5),(8,4,'0008','Troca do fluido de freio e sangria do sistema','CONCLUIDA','0h50','2026-03-20',7,2,8),(9,1,'0009','Leitura de códigos de falha com scanner','CONCLUIDA','0h40','2026-04-09',NULL,3,4),(10,2,'0010','Substituição do jogo de velas de ignição','CONCLUIDA','1h20','2026-04-09',8,3,4),(11,3,'0011','Limpeza dos bicos injetores em ultrassom','CONCLUIDA','2h00','2026-04-09',9,3,4),(12,4,'0012','Substituição do filtro de ar do motor','CONCLUIDA','0h20','2026-04-09',10,3,8),(13,1,'0013','Teste de carga do alternador e da bateria','CONCLUIDA','0h50','2026-05-07',NULL,4,7),(14,2,'0014','Substituição do alternador','CONCLUIDA','2h10','2026-05-07',14,4,7),(15,3,'0015','Substituição da correia do alternador','CONCLUIDA','0h40','2026-05-07',16,4,7),(16,4,'0016','Substituição da bateria 60Ah','CONCLUIDA','0h20','2026-05-07',15,4,8),(17,1,'0017','Teste de pressão do sistema de arrefecimento','CONCLUIDA','0h50','2026-05-21',NULL,5,4),(18,2,'0018','Substituição da bomba d\'água','CONCLUIDA','2h30','2026-05-21',17,5,4),(19,3,'0019','Substituição da válvula termostática','CONCLUIDA','0h50','2026-05-21',18,5,4),(20,4,'0020','Substituição da mangueira superior do radiador','CONCLUIDA','0h30','2026-05-21',20,5,8),(21,5,'0021','Troca do fluido e sangria do sistema','CONCLUIDA','0h40','2026-05-21',19,5,8),(22,1,'0022','Troca do óleo do motor e do filtro de óleo','CONCLUIDA','1h00','2026-06-11',21,6,5),(23,2,'0023','Substituição do filtro de combustível','CONCLUIDA','0h40','2026-06-11',23,6,5),(24,3,'0024','Alinhamento da geometria de direção','CONCLUIDA','0h50','2026-06-11',NULL,6,8),(25,4,'0025','Balanceamento das quatro rodas','CONCLUIDA','0h40','2026-06-11',NULL,6,8),(26,5,'0026','Rodízio dos pneus','CONCLUIDA','0h30','2026-06-11',NULL,6,8),(27,6,'0027','Troca da lâmpada do farol baixo direito','CONCLUIDA','0h20','2026-06-11',24,6,8),(28,1,'0028','Substituição da junta homocinética do semi-eixo','CONCLUIDA','2h40','2026-06-15',25,7,5),(29,2,'0029','Substituição da coifa de homocinética','CONCLUIDA','1h00','2026-06-15',26,7,5),(30,3,'0030','Substituição do terminal de direção','CONCLUIDA','1h10','2026-06-15',27,7,5),(31,4,'0031','Alinhamento após a troca do terminal','CONCLUIDA','0h50','2026-06-15',NULL,7,8),(32,1,'0032','Remoção da caixa de câmbio','CANCELADA','',NULL,NULL,8,6),(33,2,'0033','Substituição do kit de embreagem','CANCELADA','',NULL,28,8,6),(34,1,'0034','Remoção das capas e travamento do motor no PMS','CONCLUIDA','1h20','2026-07-23',NULL,9,4),(35,2,'0035','Substituição da correia dentada','CONCLUIDA','2h30','2026-07-23',31,9,4),(36,3,'0036','Substituição do tensor da correia dentada','CONCLUIDA','0h50','2026-07-23',32,9,4),(37,4,'0037','Substituição do rolamento tensionador','CONCLUIDA','0h40','2026-07-23',33,9,4),(38,5,'0038','Troca do óleo do motor e do filtro de óleo','CONCLUIDA','1h00','2026-07-23',34,9,8),(39,1,'0039','Diagnóstico e remoção do escapamento furado','CONCLUIDA','1h00','2026-08-26',NULL,10,6),(40,2,'0040','Substituição do coxim do escapamento','CONCLUIDA','0h30','2026-08-26',37,10,6),(41,3,'0041','Instalação do silencioso traseiro novo','EM_ANDAMENTO','',NULL,36,10,6),(42,1,'0042','Troca do óleo do motor e do filtro de óleo','PENDENTE','',NULL,38,11,4),(43,2,'0043','Substituição do filtro de ar do motor','PENDENTE','',NULL,40,11,4),(44,3,'0044','Substituição do filtro de combustível','PENDENTE','',NULL,41,11,4),(45,4,'0045','Troca do óleo da caixa de câmbio manual','PENDENTE','',NULL,42,11,4),(46,5,'0046','Rodízio dos pneus','PENDENTE','',NULL,NULL,11,8),(47,6,'0047','Alinhamento da geometria de direção','PENDENTE','',NULL,NULL,11,8);
/*!40000 ALTER TABLE `item_servico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `midias_ordemservico`
--

DROP TABLE IF EXISTS `midias_ordemservico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `midias_ordemservico` (
  `id_midia` int NOT NULL AUTO_INCREMENT,
  `id_servico` int NOT NULL,
  `tipo` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'FOTO',
  `nome_arquivo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `caminho` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descricao` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `data_upload` date NOT NULL,
  PRIMARY KEY (`id_midia`),
  KEY `fk_midia_servico` (`id_servico`),
  CONSTRAINT `fk_midia_servico` FOREIGN KEY (`id_servico`) REFERENCES `servico` (`id_servico`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `midias_ordemservico`
--

LOCK TABLES `midias_ordemservico` WRITE;
/*!40000 ALTER TABLE `midias_ordemservico` DISABLE KEYS */;
/*!40000 ALTER TABLE `midias_ordemservico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `modelo`
--

DROP TABLE IF EXISTS `modelo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `modelo` (
  `id_modelo` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ano` int NOT NULL,
  `tipo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Carro',
  `id_montadora` int NOT NULL,
  PRIMARY KEY (`id_modelo`),
  KEY `fk_modelo_montadora` (`id_montadora`),
  CONSTRAINT `fk_modelo_montadora` FOREIGN KEY (`id_montadora`) REFERENCES `montadora` (`id_montadora`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `modelo`
--

LOCK TABLES `modelo` WRITE;
/*!40000 ALTER TABLE `modelo` DISABLE KEYS */;
INSERT INTO `modelo` VALUES (1,'Fit',2006,'Carro',1),(2,'Mustang',1978,'Carro',2),(3,'Palio',2006,'Carro',3),(4,'Versalles',1993,'Carro',2),(5,'Gol',2014,'Carro',4),(6,'Onix',2019,'Carro',5),(7,'Corolla',2018,'Carro',6),(8,'Civic',2015,'Carro',1),(9,'Ka',2017,'Carro',2),(10,'Uno',2012,'Carro',3),(11,'Uno Mille',2013,'Carro',3);
/*!40000 ALTER TABLE `modelo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `montadora`
--

DROP TABLE IF EXISTS `montadora`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `montadora` (
  `id_montadora` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `pais_origem` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_montadora`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `montadora`
--

LOCK TABLES `montadora` WRITE;
/*!40000 ALTER TABLE `montadora` DISABLE KEYS */;
INSERT INTO `montadora` VALUES (1,'Honda','Japão'),(2,'Ford','EUA'),(3,'Fiat','Itália'),(4,'Volkswagen','Alemanha'),(5,'Chevrolet','EUA'),(6,'Toyota','Japão');
/*!40000 ALTER TABLE `montadora` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movimentacao_estoque`
--

DROP TABLE IF EXISTS `movimentacao_estoque`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimentacao_estoque` (
  `id_movimentacao` int NOT NULL AUTO_INCREMENT,
  `id_peca` int NOT NULL,
  `tipo` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `quantidade` int NOT NULL,
  `data_movimentacao` date NOT NULL,
  `origem` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'MANUAL',
  `id_servico` int DEFAULT NULL,
  `observacao` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `valor` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id_movimentacao`),
  KEY `fk_mov_peca` (`id_peca`),
  KEY `fk_mov_servico` (`id_servico`),
  CONSTRAINT `fk_mov_peca` FOREIGN KEY (`id_peca`) REFERENCES `peca` (`id_peca`),
  CONSTRAINT `fk_mov_servico` FOREIGN KEY (`id_servico`) REFERENCES `servico` (`id_servico`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimentacao_estoque`
--

LOCK TABLES `movimentacao_estoque` WRITE;
/*!40000 ALTER TABLE `movimentacao_estoque` DISABLE KEYS */;
/*!40000 ALTER TABLE `movimentacao_estoque` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oficina`
--

DROP TABLE IF EXISTS `oficina`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oficina` (
  `id_oficina` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `endereco` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `telefone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cnpj` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_oficina`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oficina`
--

LOCK TABLES `oficina` WRITE;
/*!40000 ALTER TABLE `oficina` DISABLE KEYS */;
INSERT INTO `oficina` VALUES (1,'Garagem Cecconi','Rua Itapetinga, 794 - Santa Luzia','11 1111-1111','00.000.000/0000-00'),(2,'Oficina 01','Rua dos Bobos, 67','11 1111-1112','00.000.000/0000-01'),(3,'Oficina 02','Rua dos Bobos, 10','11 1111-1113','00.000.000/0000-02'),(4,'Oficina 03','Rua dos Bobos, 677','11 1111-1114','00.000.000/0000-03');
/*!40000 ALTER TABLE `oficina` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orcamento`
--

DROP TABLE IF EXISTS `orcamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orcamento` (
  `id_orcamento` int NOT NULL AUTO_INCREMENT,
  `valor` decimal(10,2) NOT NULL,
  `codigo` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `tipo` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ENTRADA',
  `responsavel` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `reclamacao` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `data_criacao` date DEFAULT NULL,
  `status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDENTE',
  `id_peca` int DEFAULT NULL,
  `id_veiculo` int NOT NULL,
  `id_cliente` int NOT NULL,
  `id_funcionario` int DEFAULT NULL,
  `id_servico_revisao` int DEFAULT NULL,
  PRIMARY KEY (`id_orcamento`),
  KEY `fk_orcamento_peca` (`id_peca`),
  KEY `fk_orcamento_veiculo` (`id_veiculo`),
  KEY `fk_orcamento_cliente` (`id_cliente`),
  KEY `fk_orcamento_funcionario` (`id_funcionario`),
  CONSTRAINT `fk_orcamento_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`),
  CONSTRAINT `fk_orcamento_funcionario` FOREIGN KEY (`id_funcionario`) REFERENCES `funcionario` (`id_funcionario`),
  CONSTRAINT `fk_orcamento_peca` FOREIGN KEY (`id_peca`) REFERENCES `peca` (`id_peca`),
  CONSTRAINT `fk_orcamento_veiculo` FOREIGN KEY (`id_veiculo`) REFERENCES `veiculo` (`id_veiculo`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orcamento`
--

LOCK TABLES `orcamento` WRITE;
/*!40000 ALTER TABLE `orcamento` DISABLE KEYS */;
INSERT INTO `orcamento` VALUES (1,705.00,'0001','ENTRADA','Marcos Vinícius Prado','Revisão preventiva dos 60.000 km','2026-03-05','APROVADO',2,1,1,4,NULL),(2,1590.00,'0002','ENTRADA','Fábio Nakamura','Ruído metálico ao frear e pedal de freio baixo','2026-03-19','APROVADO',6,3,2,5,NULL),(3,1000.00,'0003','ENTRADA','Marcos Vinícius Prado','Motor falhando em baixa rotação e consumo elevado','2026-04-08','APROVADO',8,5,3,4,NULL),(4,1910.00,'0004','ENTRADA','Diego Barbosa','Barulho na suspensão dianteira ao passar em lombadas','2026-04-22','RECUSADO',11,2,1,6,NULL),(5,2400.00,'0005','ENTRADA','Leandro Souza','Veículo não liga pela manhã e luz da bateria acesa no painel','2026-05-06','APROVADO',14,7,4,7,NULL),(6,1410.00,'0006','ENTRADA','Marcos Vinícius Prado','Superaquecimento e perda de líquido de arrefecimento','2026-05-20','APROVADO',17,8,5,4,NULL),(7,860.00,'0007','ENTRADA','Fábio Nakamura','Revisão geral antes de viagem longa','2026-06-10','APROVADO',22,4,2,5,NULL),(8,1435.00,'0008','REVISAO','Fábio Nakamura','Itens reprovados na revisão: coifa rasgada e folga no terminal','2026-06-12','APROVADO',25,4,2,5,6),(9,1520.00,'0009','ENTRADA','Diego Barbosa','Embreagem patinando em subidas e cheiro de queimado','2026-06-24','APROVADO',28,6,3,6,NULL),(10,2970.00,'0010','ENTRADA','Leandro Souza','Ar-condicionado não gela','2026-07-08','RECUSADO',29,1,1,7,NULL),(11,1775.00,'0011','ENTRADA','Marcos Vinícius Prado','Troca preventiva da correia dentada aos 120.000 km','2026-07-22','APROVADO',31,7,4,4,NULL),(12,785.00,'0012','ENTRADA','Diego Barbosa','Escapamento furado com ruído excessivo','2026-08-24','APROVADO',36,8,5,6,NULL),(13,1220.00,'0013','ENTRADA','Marcos Vinícius Prado','Revisão preventiva dos 90.000 km','2026-08-28','APROVADO',39,2,1,4,NULL),(14,890.00,'0014','ENTRADA','Leandro Souza','Vidro elétrico do motorista não sobe','2026-09-01','PENDENTE',43,7,4,7,NULL),(15,1220.00,'0015','ENTRADA','Fábio Nakamura','Ruído no rolamento da roda dianteira direita','2026-09-02','PENDENTE',44,5,3,5,NULL),(16,1100.00,'0009.0016','ENTRADA','Adriano Imperador','','2026-10-18','PENDENTE',NULL,9,6,2,NULL);
/*!40000 ALTER TABLE `orcamento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orcamento_peca`
--

DROP TABLE IF EXISTS `orcamento_peca`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orcamento_peca` (
  `id_orcamento_peca` int NOT NULL AUTO_INCREMENT,
  `id_orcamento` int NOT NULL,
  `id_peca` int NOT NULL,
  PRIMARY KEY (`id_orcamento_peca`),
  KEY `fk_orcpeca_orcamento` (`id_orcamento`),
  KEY `fk_orcpeca_peca` (`id_peca`),
  CONSTRAINT `fk_orcpeca_orcamento` FOREIGN KEY (`id_orcamento`) REFERENCES `orcamento` (`id_orcamento`),
  CONSTRAINT `fk_orcpeca_peca` FOREIGN KEY (`id_peca`) REFERENCES `peca` (`id_peca`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orcamento_peca`
--

LOCK TABLES `orcamento_peca` WRITE;
/*!40000 ALTER TABLE `orcamento_peca` DISABLE KEYS */;
INSERT INTO `orcamento_peca` VALUES (1,1,1),(2,1,2),(3,1,3),(4,1,4),(5,2,5),(6,2,6),(7,2,7),(8,3,8),(9,3,9),(10,3,10),(11,4,11),(12,4,12),(13,4,13),(14,5,14),(15,5,15),(16,5,16),(17,6,17),(18,6,18),(19,6,19),(20,6,20),(21,7,21),(22,7,22),(23,7,23),(24,7,24),(25,8,25),(26,8,26),(27,8,27),(28,9,28),(29,10,29),(30,10,30),(31,11,31),(32,11,32),(33,11,33),(34,11,34),(35,11,35),(36,12,36),(37,12,37),(38,13,38),(39,13,39),(40,13,40),(41,13,41),(42,13,42),(43,14,43),(44,15,44),(45,15,45),(46,16,46),(47,16,47),(48,16,48),(49,16,49);
/*!40000 ALTER TABLE `orcamento_peca` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orcamento_servico`
--

DROP TABLE IF EXISTS `orcamento_servico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orcamento_servico` (
  `id_orcamento_servico` int NOT NULL AUTO_INCREMENT,
  `id_orcamento` int NOT NULL,
  `id_catalogo_servico` int NOT NULL,
  `valor_cobrado` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_orcamento_servico`),
  KEY `fk_orcservico_orcamento` (`id_orcamento`),
  KEY `fk_orcservico_catalogo` (`id_catalogo_servico`),
  CONSTRAINT `fk_orcservico_catalogo` FOREIGN KEY (`id_catalogo_servico`) REFERENCES `catalogo_servico` (`id_catalogo_servico`),
  CONSTRAINT `fk_orcservico_orcamento` FOREIGN KEY (`id_orcamento`) REFERENCES `orcamento` (`id_orcamento`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orcamento_servico`
--

LOCK TABLES `orcamento_servico` WRITE;
/*!40000 ALTER TABLE `orcamento_servico` DISABLE KEYS */;
INSERT INTO `orcamento_servico` VALUES (1,1,1,120.00),(2,1,2,80.00),(3,1,31,130.00),(4,2,32,300.00),(5,2,88,500.00),(6,2,31,130.00),(7,3,4,200.00),(8,3,9,300.00),(9,3,2,80.00),(10,4,84,500.00),(11,4,86,350.00),(12,4,27,120.00),(13,5,99,800.00),(14,5,37,60.00),(15,6,94,500.00),(16,6,96,250.00),(17,6,33,150.00),(18,7,1,120.00),(19,7,3,100.00),(20,7,27,120.00),(21,7,28,100.00),(22,7,26,80.00),(23,7,40,50.00),(24,8,72,600.00),(25,8,80,300.00),(26,9,75,800.00),(27,10,102,1200.00),(28,10,39,200.00),(29,11,5,600.00),(30,11,1,120.00),(31,12,109,400.00),(32,13,1,120.00),(33,13,2,80.00),(34,13,3,100.00),(35,13,21,200.00),(36,13,26,80.00),(37,13,27,120.00),(38,14,105,250.00),(39,14,107,350.00),(40,15,82,500.00),(41,15,27,120.00),(42,16,84,500.00),(43,16,87,600.00);
/*!40000 ALTER TABLE `orcamento_servico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `peca`
--

DROP TABLE IF EXISTS `peca`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `peca` (
  `id_peca` int NOT NULL AUTO_INCREMENT,
  `id_catalogo_peca` int NOT NULL,
  `nome_tecnico` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `fabricante` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `valor` decimal(10,2) NOT NULL DEFAULT '0.00',
  `quantidade_estoque` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_peca`),
  KEY `fk_peca_catalogo` (`id_catalogo_peca`),
  CONSTRAINT `fk_peca_catalogo` FOREIGN KEY (`id_catalogo_peca`) REFERENCES `catalogo_peca` (`id_catalogo_peca`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `peca`
--

LOCK TABLES `peca` WRITE;
/*!40000 ALTER TABLE `peca` DISABLE KEYS */;
INSERT INTO `peca` VALUES (1,1,'OC-90915-YZZE1','Fram',45.00,5),(2,2,'5W30 SN 4L','Mobil',180.00,5),(3,4,'ARL-3021','Tecfil',90.00,5),(4,75,'DOT4 500ml','Bosch',60.00,5),(5,71,'BB-1234','Bosch',220.00,5),(6,73,'BD-7745 (par)','Fremax',380.00,5),(7,75,'DOT4 500ml','Bosch',60.00,5),(8,7,'IFR6T11 (jogo 4un)','NGK',240.00,5),(9,42,'Injec Clean 300ml','Bardahl',70.00,5),(10,4,'ARL-4110','Tecfil',110.00,5),(11,59,'GP-32456 (par)','Cofap',640.00,5),(12,62,'KB-1102','Cofap',180.00,5),(13,63,'BS-4409','Nakata',120.00,5),(14,88,'ALT-14V-90A','Bosch',890.00,5),(15,87,'M60GD 60Ah','Moura',520.00,5),(16,14,'6PK1120','Gates',130.00,5),(17,80,'BA-2210','Urba',260.00,5),(18,81,'VT-88C','Wahler',95.00,5),(19,79,'Orgânico rosa 1L','Paraflu',70.00,5),(20,83,'MS-3390','Cofap',85.00,5),(21,1,'OC-264','Fram',40.00,5),(22,3,'15W40 SL 4L','Ipiranga',150.00,5),(23,5,'GI-06/7','Tecfil',65.00,5),(24,90,'H4 12V 60/55W','Osram',35.00,5),(25,47,'JH-5521','Nakata',310.00,5),(26,48,'CF-1180','Nakata',85.00,5),(27,54,'TD-9902','Viemar',140.00,5),(28,46,'KE-6620','Luk',720.00,5),(29,91,'CP-8802','Denso',1450.00,5),(30,92,'R134a 750g','Dupont',120.00,5),(31,10,'CT-1088','Gates',480.00,5),(32,11,'TN-5540','Gates',190.00,5),(33,12,'RT-2214','Gates',160.00,5),(34,1,'OC-90915-YZZE1','Fram',45.00,5),(35,2,'5W30 SN 4L','Mobil',180.00,5),(36,101,'SL-4471','Tuper',340.00,5),(37,102,'CX-1120','Borflex',45.00,5),(38,1,'OC-90915-YZZE1','Fram',45.00,5),(39,2,'5W30 SN 4L','Mobil',180.00,5),(40,4,'ARL-3021','Tecfil',90.00,5),(41,5,'GI-06/7','Tecfil',65.00,5),(42,44,'75W80 GL4 2L','Motul',140.00,5),(43,94,'MV-7781','Bosch',290.00,5),(44,68,'CB-4410','SKF',320.00,5),(45,69,'RL-3308','SKF',280.00,5),(46,59,'','',0.00,0),(47,60,'','',0.00,0),(48,62,'','',0.00,0),(49,67,'','',0.00,0);
/*!40000 ALTER TABLE `peca` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servico`
--

DROP TABLE IF EXISTS `servico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servico` (
  `id_servico` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `data_servico` date NOT NULL,
  `km_servico` int NOT NULL,
  `titulo` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_servico` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'OUTROS',
  `tipo_manutencao` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CORRETIVA',
  `status` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'ABERTA',
  `id_veiculo` int NOT NULL,
  `id_oficina` int NOT NULL,
  `id_orcamento` int DEFAULT NULL,
  PRIMARY KEY (`id_servico`),
  KEY `fk_servico_veiculo` (`id_veiculo`),
  KEY `fk_servico_oficina` (`id_oficina`),
  KEY `fk_servico_orcamento` (`id_orcamento`),
  CONSTRAINT `fk_servico_oficina` FOREIGN KEY (`id_oficina`) REFERENCES `oficina` (`id_oficina`),
  CONSTRAINT `fk_servico_orcamento` FOREIGN KEY (`id_orcamento`) REFERENCES `orcamento` (`id_orcamento`),
  CONSTRAINT `fk_servico_veiculo` FOREIGN KEY (`id_veiculo`) REFERENCES `veiculo` (`id_veiculo`),
  CONSTRAINT `chk_tipo_manutencao` CHECK ((`tipo_manutencao` in (_utf8mb4'PREVENTIVA',_utf8mb4'CORRETIVA'))),
  CONSTRAINT `chk_tipo_servico` CHECK ((`tipo_servico` in (_utf8mb4'REVISAO',_utf8mb4'MOTOR',_utf8mb4'TRANSMISSAO',_utf8mb4'DIRECAO',_utf8mb4'SUSPENSAO',_utf8mb4'FREIOS',_utf8mb4'ARREFECIMENTO',_utf8mb4'ELETRICA',_utf8mb4'OUTROS')))
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servico`
--

LOCK TABLES `servico` WRITE;
/*!40000 ALTER TABLE `servico` DISABLE KEYS */;
INSERT INTO `servico` VALUES (1,'0001','2026-03-06',60800,'Revisão preventiva de 60.000 km','REVISAO','PREVENTIVA','CONCLUIDA',1,1,1),(2,'0002','2026-03-20',48200,'Reparo do sistema de freios dianteiro','FREIOS','CORRETIVA','CONCLUIDA',3,1,2),(3,'0003','2026-04-09',92400,'Reparo de falha de ignição do motor','MOTOR','CORRETIVA','CONCLUIDA',5,1,3),(4,'0004','2026-05-07',118500,'Reparo do sistema de carga elétrica','ELETRICA','CORRETIVA','CONCLUIDA',7,1,5),(5,'0005','2026-05-21',143700,'Reparo do sistema de arrefecimento','ARREFECIMENTO','CORRETIVA','CONCLUIDA',8,1,6),(6,'0006','2026-06-11',187300,'Revisão geral pré-viagem','REVISAO','PREVENTIVA','CONCLUIDA',4,1,7),(7,'0007','2026-06-15',187450,'Reparo de homocinética e terminal','TRANSMISSAO','CORRETIVA','CONCLUIDA',4,1,8),(8,'0008','2026-06-25',76900,'Troca do kit de embreagem','TRANSMISSAO','CORRETIVA','CANCELADA',6,1,9),(9,'0009','2026-07-23',121000,'Troca da correia dentada','MOTOR','PREVENTIVA','CONCLUIDA',7,1,11),(10,'0010','2026-08-26',145200,'Reparo do escapamento','OUTROS','CORRETIVA','EM_ANDAMENTO',8,1,12),(11,'0011','2026-09-01',89600,'Revisão preventiva de 90.000 km','REVISAO','PREVENTIVA','ABERTA',2,1,13);
/*!40000 ALTER TABLE `servico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipo_servico`
--

DROP TABLE IF EXISTS `tipo_servico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_servico` (
  `id_tipo_servico` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_tipo_servico`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipo_servico`
--

LOCK TABLES `tipo_servico` WRITE;
/*!40000 ALTER TABLE `tipo_servico` DISABLE KEYS */;
INSERT INTO `tipo_servico` VALUES (1,'REVISAO'),(2,'MOTOR'),(3,'TRANSMISSAO'),(4,'DIRECAO'),(5,'SUSPENSAO'),(6,'FREIOS'),(7,'ARREFECIMENTO'),(8,'ELETRICA'),(9,'OUTROS');
/*!40000 ALTER TABLE `tipo_servico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cpf` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `senha` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `telefone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_oficina` int NOT NULL,
  PRIMARY KEY (`cpf`),
  UNIQUE KEY `uq_usuario_id` (`id_usuario`),
  KEY `fk_usuario_oficina` (`id_oficina`),
  CONSTRAINT `fk_usuario_oficina` FOREIGN KEY (`id_oficina`) REFERENCES `oficina` (`id_oficina`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'Arthur Cecconi','00000000000','oficina@geargest.com','123456','(11) 90000-0000',1),(14,'Edu','123.123.123-12','edu@gmail.com','1234','(11) 98888-8888',2),(2,'João Silva','12345678900','joaosilva@gmail.com','silva123','(11) 98765-4321',1),(3,'Adriano Imperador','12345678901','adrianoimperador@gmail.com','apa123','(11) 98765-4322',2),(4,'Roberto Carlos','12345678902','robertocarlos@gmail.com','ime123','(11) 98765-4323',3),(5,'Marcos Vinícius Prado','31122233344','marcos.prado@geargest.com','marcos123','(11) 97111-0001',1),(6,'Fábio Nakamura','31122233355','fabio.nakamura@geargest.com','fabio123','(11) 97111-0002',1),(7,'Diego Barbosa','31122233366','diego.barbosa@geargest.com','diego123','(11) 97111-0003',1),(8,'Leandro Souza','31122233377','leandro.souza@geargest.com','leandro123','(11) 97111-0004',1),(9,'Paulo Henrique Dias','31122233388','paulo.dias@geargest.com','paulo123','(11) 97111-0005',1),(10,'Maria Fernanda Lopes','45566677788','mariaflopes@gmail.com','maria123','(11) 98120-1122',1),(11,'Carlos Eduardo Ramos','45566677799','carlosramos@gmail.com','carlos123','(11) 98120-1133',1),(12,'Beatriz Almeida Nunes','45566677800','beatriznunes@gmail.com','beatriz123','(11) 98120-1144',1),(13,'Ricardo Tanaka','45566677811','ricardotanaka@gmail.com','ricardo123','(11) 98120-1155',1);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `veiculo`
--

DROP TABLE IF EXISTS `veiculo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `veiculo` (
  `id_veiculo` int NOT NULL AUTO_INCREMENT,
  `placa` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `codigo` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `id_cliente` int NOT NULL,
  `id_modelo` int NOT NULL,
  PRIMARY KEY (`placa`),
  UNIQUE KEY `uq_veiculo_id` (`id_veiculo`),
  KEY `fk_veiculo_cliente` (`id_cliente`),
  KEY `fk_veiculo_modelo` (`id_modelo`),
  CONSTRAINT `fk_veiculo_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`),
  CONSTRAINT `fk_veiculo_modelo` FOREIGN KEY (`id_modelo`) REFERENCES `modelo` (`id_modelo`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `veiculo`
--

LOCK TABLES `veiculo` WRITE;
/*!40000 ALTER TABLE `veiculo` DISABLE KEYS */;
INSERT INTO `veiculo` VALUES (8,'BCD8H90','0008',5,10),(9,'DASDAS4','003.011.0009',6,11),(1,'GGG1A23','0001',1,1),(2,'JKL2B34','0002',1,5),(3,'MNO3C45','0003',2,6),(4,'PQR4D56','0004',2,3),(5,'STU5E67','0005',3,7),(6,'VWX6F78','0006',3,9),(7,'YZA7G89','0007',4,8);
/*!40000 ALTER TABLE `veiculo` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-20 22:02:04
