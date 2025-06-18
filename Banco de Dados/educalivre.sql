-- Criando o banco de dados
CREATE DATABASE educaLivre;
GO

-- Usando o banco de dados criado
USE educaLivre;
GO

SET DATEFORMAT DMY;
GO

-- A partir daqui, todos os comandos serão executados no banco educaLivre
CREATE TABLE Usuario (
    idUsuario INT IDENTITY(1,1) PRIMARY KEY,
    nome VARCHAR(50),
    sobrenome VARCHAR(50),
    email VARCHAR(100),
    celular VARCHAR(20),
    senha VARCHAR(255),
    genero CHAR(1),
    dataCriacao DATE,
    isAdmin BIT NOT NULL DEFAULT 0,
    CONSTRAINT UNIQ_EMAIL UNIQUE (email)
);

CREATE TABLE simulado (
    id_simulado INT IDENTITY(1,1) PRIMARY KEY,
    ano_prova_simulado INT NOT NULL,
    idUsuario INT NOT NULL,
    total_questions INT NOT NULL,
    total_correct_questions INT NOT NULL,
    percentual_acerto DECIMAL(5,2) NOT NULL,
    data_simulado DATETIME2 DEFAULT GETDATE(),
    status VARCHAR(20) DEFAULT 'CONCLUIDO',
    FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario)
);


CREATE TABLE simulado_resposta (
    id_resposta INT IDENTITY(1,1) PRIMARY KEY,
    id_simulado INT NOT NULL,
    numero_questao INT NOT NULL,
    resposta_usuario VARCHAR(1) NOT NULL,
    resposta_correta VARCHAR(1) NOT NULL,
    acertou BIT NOT NULL,
    disciplina VARCHAR(50),
    FOREIGN KEY (id_simulado) REFERENCES simulado(id_simulado)
);

CREATE INDEX idx_simulado_usuario ON simulado(idUsuario);
CREATE INDEX idx_simulado_data ON simulado(data_simulado);
CREATE INDEX idx_simulado_ano ON simulado(ano_prova_simulado);

CREATE TABLE password_resets (
    id INT IDENTITY(1,1) PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    token CHAR(32)NOT NULL, 
    expires_at DATETIME2 NOT NULL,
    created_at DATETIME2 NOT NULL,
    CONSTRAINT UQ_password_resets_token UNIQUE(token),
    INDEX IX_password_resets_email (email)
);

CREATE TABLE Feedback (
    idFeedback INT IDENTITY(1,1) PRIMARY KEY,
    idUsuario INT,
    textoFeedback VARCHAR(MAX),
	  dataFeedback DATE,
	  notaFeedback INT NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario)
	ON DELETE CASCADE
)


CREATE TABLE Dica (
    idDica INT IDENTITY(1,1) PRIMARY KEY,
    idUsuario INT,
    textoDica VARCHAR(MAX) NULL,
    tituloDica VARCHAR(100) NULL,
    dataDica DATE,
    FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario)
	ON DELETE CASCADE
);

CREATE TABLE ProvasAnteriores (
    idProva INT IDENTITY(1,1) PRIMARY KEY,
    anoProva INT NOT NULL,
    diaProva INT NOT NULL,
    nomeArquivoProva VARCHAR(255) NOT NULL,
    nomeArquivoGabarito VARCHAR(255),
);

CREATE TABLE AreaEstudo (
    idAreaEstudo INT IDENTITY(1,1) PRIMARY KEY,
    nomeAreaEstudo VARCHAR(100) NOT NULL
);

CREATE TABLE Materia (
    idMateria INT IDENTITY(1,1) PRIMARY KEY,
    idAreaEstudo INT NOT NULL,
    nomeMateria VARCHAR(255) NOT NULL,
    FOREIGN KEY (idAreaEstudo) REFERENCES AreaEstudo(idAreaEstudo)
);

CREATE TABLE Topico (
    idTopico INT IDENTITY(1,1) PRIMARY KEY,
    idMateria INT NOT NULL,
    nomeTopico VARCHAR(255) NOT NULL,
    linkTopico VARCHAR(MAX),
    FOREIGN KEY (idMateria) REFERENCES Materia(idMateria)
);

-- Procedure
GO
CREATE PROCEDURE sp_LimparTokensExpirados
AS
BEGIN
    DELETE FROM password_resets 
    WHERE expires_at < GETDATE()
    
    SELECT @@ROWCOUNT AS tokensRemovidos
END
GO


-- Inserindo Dados

-- Inserindo Dados na tabela AreaEstudo
INSERT INTO AreaEstudo(nomeAreaEstudo) VALUES ('Ciências Humanas e suas Tecnologias'),('Linguagens, Códigos e suas Tecnologias'),('Ciências da Natureza e suas Tecnologias'),('Matemática e suas Tecnologias'),('Redação');

INSERT INTO Materia(idAreaEstudo,nomeMateria) VALUES (1,'História'),(1,'Geografia'),(1,'Filosofia'),(1,'Sociologia');
INSERT INTO Materia(idAreaEstudo,nomeMateria) VALUES (2,'Lingua Portuguesa e Literatura'),(2,'Inglês'),(2,'Espanhol'),(2,'Artes'),(2,'Educação Fisica');
INSERT INTO Materia(idAreaEstudo,nomeMateria) VALUES (3,'Química'),(3,'Física'),(3,'Biologia');
INSERT INTO Materia(idAreaEstudo,nomeMateria) VALUES (4,'Matemática');
INSERT INTO Materia(idAreaEstudo,nomeMateria) VALUES (5,'Temas Anteriores');
INSERT INTO Materia(idAreaEstudo,nomeMateria) VALUES (5,'Curso de redação para Enem');
GO

-- Inserindo uma dica default no banco, as demais serão adicionadas dinamicamente no site pelos usuarios com permissão de adm.
INSERT INTO Dica (dataDica, tituloDica,textoDica) VALUES ('2024-12-03','É melhor fazer a redação primeiro no Enem?','Não há resposta exata, pois isso depende do perfil do candidato. A primeira dica é levar em consideração o tempo que será necessário dedicar à redação e à resolução das questões. Caso você tenha controle do seu tempo e seja ágil o suficiente para não “boiar” nas questões mais difíceis, pode ser uma boa terminar a prova pela redação. Outro ponto é que se você possui facilidade com a redação, é interessante resolver as questões da prova que, com certeza, oferecerão algum nível de dificuldade. E escrever o texto no fim. Mas há o outro lado da moeda. Começar pela redação pode ser interessante, pois se está com a mente fresca no início da prova e é muito mais fácil raciocinar para atender todas as propostas exigidas pelo texto dissertativo. Outro ponto é que caso você tenha problemas com gerenciamento de tempo, começar pela parte escrita ajuda a tirar o peso de voltar para a redação depois da prova. Também diminui a ansiedade e o medo de não dar conta do texto após resolver as questões. Caso comece pela redação, o ideal é terminar tudo (rascunho e versão final) para só aí ir às perguntas de múltipla escolha. Assim, o raciocínio não é interrompido — no que se trata de redação, uma vez que a linha de pensamento é perdida, é difícil retomá-la depois. Ou fazer o rascunho, deixar a mente “descansar” respondendo outras perguntas e voltar para passar a redação a limpo, antes do final da prova. Mas, claro, a decisão final é sua. Por isso, a autoanálise e os treinos contínuos por meio de simulados são fundamentais para descobrir o que é melhor para você.')

-- Inserindo um feedback default no site, as demais serão adicionadas pelos usuarios comuns.
INSERT INTO Feedback(textoFeedback,dataFeedback,notaFeedback) VALUES ('A plataforma está de parabéns, estava perdido sobre o enem e a plataforma vem me ajudado muito!','2024-12-03',5)

-- Inserindo Dados na tabela topico

-- Topcios Historia
INSERT INTO Topico (idMateria, nomeTopico, linkTopico) VALUES
(1, 'Idade Contemporânea', 'https://www.youtube.com/watch?v=KlUWB_k0Kf4&list=PLPNLvl90MqKQFcZIk1H7_yv0pwcc9ynx0'),
(1, '2ª Guerra Mundial e suas consequências', 'https://www.youtube.com/watch?v=4hEDjSdszSU'),
(1, 'Brasil Colônia', 'https://www.youtube.com/watch?v=sP2kl0irjBQ&list=PLPNLvl90MqKStxdRIwxQeu3LPzFPsMfap'),
(1, 'Primeiro Reinado', 'https://www.youtube.com/watch?v=TzwFrYCd-6M'),
(1, 'Segundo Reinado', 'https://www.youtube.com/watch?v=7zs6-atp_ik'),
(1, 'Governos pós-regime militar - Redemocratização', 'https://www.youtube.com/watch?v=4ceFMDndn_0'),
(1, 'Era Vargas', 'https://www.youtube.com/watch?v=jQU6Ojetq8M'),
(1, 'História Política', 'https://www.youtube.com/watch?v=cuHddXfinDE'),
(1, 'República Velha', 'https://www.youtube.com/watch?v=8PAMZzDvN1A'),
(1, 'Patrimônio Histórico-Cultural e Memória', 'https://www.youtube.com/watch?v=RJX-wTMONWM');

-- Topicos Geografia
INSERT INTO Topico (idMateria, nomeTopico, linkTopico) VALUES
(2, 'Geografia Agrária', 'https://www.youtube.com/watch?v=RzeYTW1Y3Kk&list=PLH0kQ6bXLZnU-vg8iDRymngVRqIFylQ3l'),
(2, 'Questões Ambientais', 'https://www.youtube.com/watch?v=kyPD3yze4DQ'),
(2, 'Geografia Física', 'https://www.youtube.com/watch?v=tkn-YU7rleQ'),
(2, 'Geografia Urbana', 'https://www.youtube.com/watch?v=jPZyKuMrAj4'),
(2, 'Climatologia', 'https://www.youtube.com/watch?v=Ekxzj16mA8A&list=PL2Znz6U0vJHUfcU2xt9G1ClRZl0DCB8Yw'),
(2, 'Urbanização', 'https://www.youtube.com/watch?v=gwXKwVwfQmU'),
(2, 'Globalização', 'https://www.youtube.com/watch?v=WG_1PC9Ht9M'),
(2, 'Cartografia', 'https://www.youtube.com/watch?v=5bObBFWg0t0&list=PL2Znz6U0vJHVRmWoGfM4_EwBr4XtBpIn-'),
(2, 'Indústria', 'https://www.youtube.com/watch?v=kayp5ikmdRc');
-- Filosofia
INSERT INTO Topico (idMateria, nomeTopico, linkTopico) VALUES
(3, 'Aristóteles e escola helenística', 'https://www.youtube.com/watch?v=9-bvJy6CvOY'),
(3, 'Ética e Justiça', 'https://www.youtube.com/watch?v=92OBGFJg0lM'),
(3, 'Racionalismo moderno', 'https://www.youtube.com/watch?v=IUSCQr9OtqM'),
(3, 'Filosofia Antiga', 'https://www.youtube.com/watch?v=Zy7EwpV3qqI'),
(3, 'Escola sofística, Sócrates e Platão', 'https://www.youtube.com/watch?v=HJ0G0FNDrb4'),
(3, 'Filosofia Contemporânea', 'https://www.youtube.com/watch?v=6COggNzoqME&list=PLRuYby0fu5YLxYUwQG3RP3BT8QzWsC8SP'),
(3, 'Natureza do Conhecimento', 'https://www.youtube.com/watch?v=5SMC3YJSqu0'),
(3, 'Filosofia Moderna', 'https://www.youtube.com/watch?v=IOPir9GymRA&list=PLH0kQ6bXLZnXGUfZir7i7Z-R4nSHVk-3m'),
(3, 'Escola de Frankfurt', 'https://www.youtube.com/watch?v=2cRmI_bYEyc');

-- Sociologia
INSERT INTO Topico (idMateria, nomeTopico, linkTopico) VALUES
(4, 'Sociologia contemporânea', 'https://www.youtube.com/watch?v=hh19mqzQJaU'),
(4, 'Mundo do Trabalho', 'https://www.youtube.com/watch?v=HUO3MquFdjk'),
(4, 'Cultura e Indústria Cultural', 'https://www.youtube.com/watch?v=QCKuVoJlHZU'),
(4, 'Ideologia', 'https://www.youtube.com/watch?v=85V6shQPPL8'),
(4, 'Meios de Comunicação, Tecnologia e Cultura de Massa', 'https://www.youtube.com/watch?v=0M82Owal1Us'),
(4, 'Cidadania', 'https://www.youtube.com/watch?v=yztZtoo7yRg'),
(4, 'Capitalismo', 'https://www.youtube.com/watch?v=hjyfDZMZHUo'),
(4, 'Economia e sociedade', 'https://www.youtube.com/watch?v=LQPffshbEvE');

-- Lingua Portuguesa e Literatura
INSERT INTO Topico (idMateria, nomeTopico, linkTopico) VALUES
(5, 'Interpretação de textos', 'https://www.youtube.com/watch?v=XIXUpBBBQLE&list=PLEjz9losBoODKff5MqWwMQtibltWjJ31N'),
(5, 'Tendências contemporâneas', 'https://www.youtube.com/watch?v=zo4ABcSAO_0'),
(5, 'Estrutura e formação das palavras', 'https://www.youtube.com/watch?v=FcymE4kwvgQ'),
(5, 'Tipos de texto', 'https://www.youtube.com/watch?v=51Vj6uzsdaA'),
(5, 'Elementos da narrativa: análise da pessoa, do espaço e do tempo', 'https://www.youtube.com/watch?v=ViYtv5dOLUY'),
(5, 'Funções da linguagem', 'https://www.youtube.com/watch?v=d6kS7zj8p2Q'),
(5, 'Pontuação', 'https://www.youtube.com/watch?v=ODkVN0kRciE'),
(5, 'Narratividade', 'https://www.youtube.com/watch?v=hxjT67hHy4A'),
(5, 'Literatura', 'https://www.youtube.com/watch?v=xXyfKXW-Y0M'),
(5, 'Classe de palavras', 'https://www.youtube.com/watch?v=iepzxa5QBt0'),
(5, 'Verbo', 'https://www.youtube.com/watch?v=TUX8TNThh0c');

-- Inglês
INSERT INTO Topico (idMateria, nomeTopico, linkTopico) VALUES
(6, 'Interpretação de Textos', 'https://www.youtube.com/watch?v=jBkRAG8L12o'),
(6, 'Domínio Lexical', 'https://www.youtube.com/watch?v=kFAZMHepv9I'),
(6, 'Identificação de Função do Texto', 'https://www.youtube.com/watch?v=WOR5hbFIoSI');

-- Espanhol
INSERT INTO Topico (idMateria, nomeTopico, linkTopico) VALUES
(7, 'Interpretação de Textos', 'https://www.youtube.com/watch?v=4hSLNq18IdY'),
(7, 'Domínio Lexical', 'https://www.youtube.com/watch?v=ajllxqY24zU'),
(7, 'Identificação de Função do Texto', 'https://www.youtube.com/watch?v=TeG208iwuuc');

-- Artes
INSERT INTO Topico (idMateria, nomeTopico, linkTopico) VALUES
(8, 'Arte Contemporânea', 'https://www.youtube.com/watch?v=vQoJ7eG992Y'),
(8, 'Arte nos séculos XV e XVI', 'https://www.youtube.com/watch?v=fxqxH5A3Ok8&t=42s'),
(8, 'Elementos básicos das Artes Plásticas', 'https://www.youtube.com/watch?v=8S_8vYCqrNE'),
(8, 'Elementos básicos de Música', 'https://www.youtube.com/watch?v=PVybw8JWUsM'),
(8, 'Música no século XX', 'https://www.youtube.com/watch?v=vgVjt1uUSao');

-- Educação Fisica
INSERT INTO Topico (idMateria, nomeTopico, linkTopico) VALUES
(9, 'Esporte e espetáculo', 'https://www.youtube.com/watch?v=C0XtsQ89gHA'),
(9, 'Influência da mídia no corpo', 'https://www.youtube.com/watch?v=F8X3_4cHKWgz');

-- Quimica
INSERT INTO Topico (idMateria, nomeTopico, linkTopico) VALUES
(10, 'Físico-Química', 'https://www.youtube.com/watch?v=btRNcrRYBas'),
(10, 'Química Geral', 'https://www.youtube.com/watch?v=S5O-_kHn3W0'),
(10, 'Química Orgânica', 'https://www.youtube.com/watch?v=SGTXk-2uSys'),
(10, 'Ligações químicas, polaridade e forças', 'https://www.youtube.com/watch?v=6CK3WbBLt_k'),
(10, 'Reações orgânicas', 'https://www.youtube.com/watch?v=OwAVDeLnCig'),
(10, 'Compostos orgânicos', 'https://www.youtube.com/watch?v=AmTCrsqD0P4'),
(10, 'Eletroquímica', 'https://www.youtube.com/watch?v=0Yl42XmgOkk'),
(10, 'Soluções', 'https://www.youtube.com/watch?v=m3mUEb6ULf8'),
(10, 'Energia', 'https://www.youtube.com/watch?v=HVKA6xLhBsY');

-- Física
INSERT INTO Topico (idMateria, nomeTopico, linkTopico) VALUES
(11, 'Mecânica', 'https://www.youtube.com/watch?v=E3q4yTpPy3M'),
(11, 'Eletricidade e Energia', 'https://www.youtube.com/watch?v=HVKA6xLhBsY'),
(11, 'Ondulatória', 'https://www.youtube.com/watch?v=0siWJcMRM-A'),
(11, 'Termologia', 'https://www.youtube.com/watch?v=nLn5BxL4Cn4&list=PLbBLq1So0quXo0r4Kt-d--1WrdtXiN0gx'),
(11, 'Acústica', 'https://www.youtube.com/watch?v=Nlxs8r4xvpk'),
(11, 'Energia, Trabalho e Potência', 'https://www.youtube.com/watch?v=Ml_NyaV6oNk&list=PL2QMqoE75Xroa0UHXCQs1hG6Vij63j4NJ'),
(11, 'Resistores', 'https://www.youtube.com/watch?v=1GjTKvyzIdA'),
(11, 'Calorimetria', 'https://www.youtube.com/watch?v=axZtbpC-wFk'),
(11, 'Impulso, Quantidade de Movimento e Análise Dimensional', 'https://www.youtube.com/watch?v=SReHoDOMYW0'),
(11, 'Introdução à Óptica Geométrica', 'https://www.youtube.com/watch?v=UMn7hAfpU6o');


-- Biologia
INSERT INTO Topico (idMateria, nomeTopico, linkTopico) VALUES
(12, 'Humanidade e Ambiente', 'https://www.youtube.com/watch?v=utxzvj61WmI&list=PL6jnBBqoDNyIQnCByEQq6kPDW-7JaUmoc'),
(12, 'Citologia', 'https://www.youtube.com/watch?v=N33jWXzV8RU&list=PLSCwcUriz1RJznmlFfSVlHvPCp8Cg2r9X'),
(12, 'Histologia e Fisiologia', 'https://www.youtube.com/watch?v=emucgjvtdw0&list=PLj4yVuRqCKGFCWTdCgeImy9ZA23KNC4eJ'),
(12, 'Sistema Imunológico', 'https://www.youtube.com/watch?v=pJnPe3M013w'),
(12, 'Ecossistemas', 'https://www.youtube.com/watch?v=8W_U7WOS0_0'),
(12, 'Fundamentos da Ecologia', 'https://www.youtube.com/watch?v=Z5cll6n3hHw'),
(12, 'Biotecnologia', 'https://www.youtube.com/watch?v=RDmb9OXtS4w'),
(12, 'DNA e RNA', 'https://www.youtube.com/watch?v=LFqaBPFXAh0'),
(12, 'Genética', 'https://www.youtube.com/watch?v=FZK4MJbafTU');

-- Matemática
INSERT INTO Topico (idMateria, nomeTopico, linkTopico) VALUES
(13, 'Geometria', 'https://www.youtube.com/watch?v=M_gUL7yxl_A&list=PLQVUQftDIJQEEsEuc9ieGsNmUdi9uI5Xr'),
(13, 'Equação do Primeiro Grau', 'https://www.youtube.com/watch?v=NhwrayP3DjY'),
(13, 'Equação do Segundo Grau', 'https://www.youtube.com/watch?v=Sa7BjeyitV0'),
(13, 'Escalas, Razão e Proporção', 'https://www.youtube.com/watch?v=QiEnHOk0jys'),
(13, 'Grandezas proporcionais e médias algébricas', 'https://www.youtube.com/watch?v=s0acSwFvsm4'),
(13, 'Aritmética', 'https://www.youtube.com/watch?v=ByihuRMs-uc'),
(13, 'Porcentagem e Matemática Financeira', 'https://www.youtube.com/watch?v=UByudQUOZrw'),
(13, 'Gráficos e Tabelas', 'https://www.youtube.com/watch?v=XzZGAwfKs_k'),
(13, 'Funções', 'https://www.youtube.com/watch?v=hgKTmAO-k7E'),
(13, 'Noções Básicas de Estatística', 'https://www.youtube.com/watch?v=KEbbZULcnOQ'),
(13, 'Probabilidade', 'https://www.youtube.com/watch?v=yCAhR3T_r5g'),
(13, 'Área de Figuras Planas e Área dos Polígonos', 'https://www.youtube.com/watch?v=th5k6bzSDTA');

--Temas Anteriores
INSERT INTO Topico (idMateria, nomeTopico) VALUES 
(14, '2024 - Desafios para a valorização da herança africana no Brasil'),
(14, '2023 - Desafios para o enfrentamento da invisibilidade do trabalho de cuidado realizado pela mulher no Brasil'),
(14, '2022 - Desafios para a valorização de comunidades e povos tradicionais no Brasil'),
(14, '2021 - Invisibilidade e registro civil: garantia de acesso à cidadania no Brasil'),
(14, '2020 - O estigma associado às doenças mentais na sociedade brasileira'),
(14, '2020 - O desafio de reduzir as desigualdades entre as regiões do Brasil'),
(14, '2019 - Democratização do acesso ao cinema no Brasil'),
(14, '2018 - Manipulação do comportamento do usuário pelo controle de dados na Internet'),
(14, '2017 - Desafios para a formação educacional de surdos no Brasil'),
(14, '2016 - Caminhos para combater a intolerância religiosa no Brasil'),
(14, '2015 - A persistência da violência contra a mulher na sociedade brasileira'),
(14, '2014 - Publicidade infantil em questão no Brasil'),
(14, '2013 - Efeitos da implantação da Lei Seca no Brasil'),
(14, '2012 - Movimento imigratório para o Brasil no século 21'),
(14, '2011 - Viver em rede no século XXI: os limites entre o público e o privado'),
(14, '2010 - O trabalho na construção da dignidade humana'),
(14, '2009 - O indivíduo frente à ética nacional'),
(14, '2008 - Como preservar a floresta Amazônica'),
(14, '2007 - O desafio de se conviver com a diferença'),
(14, '2006 - O poder de transformação da leitura'),
(14, '2005 - O trabalho infantil na realidade brasileira'),
(14, '2004 - Como garantir a liberdade de informação e evitar abusos nos meios de comunicação'),
(14, '2003 - A violência na sociedade brasileira: como mudar as regras desse jogo?'),
(14, '2002 - O direito de votar: como fazer dessa conquista um meio para promover as transformações sociais de que o Brasil necessita?'),
(14, '2001 - Desenvolvimento e preservação ambiental: como conciliar interesses em conflito?'),
(14, '2000 - Direitos da criança e do adolescente: como enfrentar esse desafio nacional?'),
(14, '1999 - Cidadania e participação social'),
(14, '1998 - Viver e aprender');
-- Inserindo os tópicos para Curso de Redação (idMateria = 15)
INSERT INTO Topico (idMateria,nomeTopico,linkTopico) VALUES
(15,'CURSO REDAÇÃO ENEM: COMEÇANDO DO ZERO','https://www.youtube.com/watch?v=7bP1auwDPJM&list=PLQVUQftDIJQFv71TmR-j-wfMOYsB-f6tk');
GO

-- Inserindo provas anteriores do enem

-- 1998
INSERT INTO provasAnteriores (anoProva, diaProva, nomeArquivoProva) VALUES (1998, 1, '1998_dia1_prova.pdf');

-- 1999
INSERT INTO provasAnteriores (anoProva, diaProva, nomeArquivoProva) VALUES (1999, 1, '1999_dia1_prova.pdf');

-- 2000
INSERT INTO provasAnteriores (anoProva, diaProva, nomeArquivoProva) VALUES (2000, 1, '2000_dia1_prova.pdf');

-- 2001
INSERT INTO provasAnteriores (anoProva, diaProva, nomeArquivoProva) VALUES (2001, 1, '2001_dia1_prova.pdf');

-- 2002
INSERT INTO provasAnteriores (anoProva, diaProva, nomeArquivoProva) VALUES (2002, 1, '2002_dia1_prova.pdf');

-- 2003
INSERT INTO provasAnteriores (anoProva, diaProva, nomeArquivoProva) VALUES (2003, 1, '2003_dia1_prova.pdf');

-- 2004
INSERT INTO provasAnteriores (anoProva, diaProva, nomeArquivoProva) VALUES (2004, 1, '2004_dia1_prova.pdf');

-- 2005
INSERT INTO provasAnteriores (anoProva, diaProva, nomeArquivoProva) VALUES (2005, 1, '2005_dia1_prova.pdf');

-- 2006
INSERT INTO provasAnteriores (anoProva, diaProva, nomeArquivoProva) VALUES (2006, 1, '2006_dia1_prova.pdf');

-- 2007
INSERT INTO provasAnteriores (anoProva, diaProva, nomeArquivoProva) VALUES (2007, 1, '2007_dia1_prova.pdf');

-- 2008
INSERT INTO provasAnteriores (anoProva, diaProva, nomeArquivoProva) VALUES (2008, 1, '2008_dia1_prova.pdf');

-- 2009 - dia 1
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2009, 1, '2009_dia1_prova.pdf', '2009_dia1_gabarito.pdf');

-- 2009 - dia 2
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2009, 2, '2009_dia2_prova.pdf', '2009_dia2_gabarito.pdf');

-- 2010 - dia 1
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2010, 1, '2010_dia1_prova.pdf', '2010_dia1_gabarito.pdf');

-- 2010 - dia 2
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2010, 2, '2010_dia2_prova.pdf', '2010_dia2_prova.pdf');

-- 2011 - dia 1
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2011, 1, '2011_dia1_prova.pdf', '2011_dia1_gabarito.pdf');

-- 2011 - dia 2
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2011, 2, '2011_dia2_prova.pdf', '2011_dia2_gabarito.pdf');

-- 2012 - dia 1
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2012, 1, '2012_dia1_prova.pdf', '2012_dia1_gabarito.pdf');

-- 2012 - dia 2
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2012, 2, '2012_dia2_prova.pdf', '2012_dia2_gabarito.pdf');

-- 2013 - dia 1
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2013, 1, '2013_dia1_prova.pdf', '2013_dia1_gabarito.pdf');

-- 2013 - dia 2
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2013, 2, '2013_dia2_prova.pdf', '2013_dia2_gabarito.pdf');

-- 2014 - dia 1
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2014, 1, '2014_dia1_prova.pdf', '2014_dia1_gabarito.pdf');

-- 2014 - dia 2
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2014, 2, '2014_dia2_prova.pdf', '2000_dia2_gabarito.pdf');

-- 2015 - dia 1
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2015, 1, '2015_dia1_prova.pdf', '2015_dia1_gabarito.pdf');

-- 2015 - dia 2
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2015, 2, '2015_dia2_prova.pdf', '2015_dia2_gabarito.pdf');

-- 2016 - dia 1
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2016, 1, '2016_dia1_prova.pdf', '2016_dia1_gabarito.pdf');

-- 2016 - dia 2
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2016, 2, '2016_dia2_prova.pdf', '2016_dia2_gabarito.pdf');

-- 2017 - dia 1
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2017, 1, '2017_dia1_prova.pdf', '2017_dia1_gabarito.pdf');

-- 2017 - dia 2
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2017, 2, '2017_dia2_prova.pdf', '2017_dia2_gabarito.pdf');

-- 2018 - dia 1
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2018, 1, '2018_dia1_prova.pdf', '2018_dia1_gabarito.pdf');

-- 2018 - dia 2
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2018, 2, '2018_dia2_prova.pdf', '2018_dia2_gabarito.pdf');

-- 2019 - dia 1
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2019, 1, '2019_dia1_prova.pdf', '2019_dia1_gabarito.pdf');

-- 2019 - dia 2
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2019, 2, '2019_dia2_prova.pdf', '2019_dia2_gabarito.pdf');

-- 2020 - dia 1
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2020, 1, '2020_dia1_prova.pdf', '2020_dia1_gabarito.pdf');

-- 2020 - dia 2
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2020, 2, '2020_dia2_prova.pdf', '2020_dia2_gabarito.pdf');

-- 2021 - dia 1
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2021, 1, '2021_dia1_prova.pdf', '2021_dia1_gabarito.pdf');

-- 2021 - dia 2
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2021, 2, '2021_dia2_prova.pdf', '2021_dia2_gabarito.pdf');

-- 2022 - dia 1
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2022, 1, '2022_dia1_prova.pdf', '2022_dia1_gabarito.pdf');

-- 2022 - dia 2
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2022, 2, '2022_dia2_prova.pdf', '2022_dia2_gabarito.pdf');

-- 2023 - dia 1
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2023, 1, '2023_dia1_prova.pdf', '2023_dia1_gabarito.pdf');

-- 2023 - dia 2
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2023, 2, '2023_dia2_prova.pdf', '2023_dia2_gabarito.pdf');

-- 2024 - dia 1
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2024, 1, '2024_dia1_prova.pdf', '2024_dia1_gabarito.pdf');

-- 2024 - dia 2
INSERT INTO provasAnteriores(anoProva, diaProva, nomeArquivoProva, nomeArquivoGabarito) 
VALUES (2024, 2, '2024_dia2_prova.pdf', '2024_dia2_gabarito.pdf');
