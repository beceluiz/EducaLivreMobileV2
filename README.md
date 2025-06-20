# 📲 EducaLivre - Versão Mobile (Android)

Essa é a versão **mobile (Android)** do **EducaLivre**, uma plataforma de estudos gratuita focada no ENEM, desenvolvida como projeto de TCC. Essa versão permite que os alunos acessem conteúdos educacionais, realizem simulados, visualizem dicas e gerenciem sua conta diretamente pelo smartphone.

---

## 📦 Tecnologias e bibliotecas utilizadas

- **Java (Android)**
- **Retrofit `2.9.0`** – comunicação com API interna  
- **Gson Converter `2.9.0`** – conversão JSON  
- **Glide `4.16.0`** – carregamento de imagens  
- **jtds `1.3.1`** – driver JDBC para SQL Server  
- **jbcrypt `0.4`** – criptografia de senhas  
- **API pública [ENEM.dev](https://enem.dev/)** – questões do simulado ENEM

---

## ✨ Funcionalidades

- Login e cadastro de usuários
- Simulados em tempo real
- Acesso a conteúdos organizados por área
- Visualização de dicas para vestibulares
- Atualização e exclusão da conta pelo próprio app
- Comunicação direta com o banco de dados via JDBC

---

## ▶️ Como rodar o projeto

### ✅ Pré-requisitos

- Android Studio instalado (última versão recomendada)
- Emulador ou dispositivo físico com modo desenvolvedor ativado
- SQL Server instalado e configurado corretamente
- Banco de dados `educaLivre` criado e populado

---

## ⚙️ Etapas de configuração do ambiente

### 🛠️ **Fase 1 – Configurar o SQL Server para aceitar conexões**

1. Abrir o **SQL Server Configuration Manager**
2. Ir em **Configuração de Rede do SQL Server > Protocolos para SQLEXPRESS**
3. Habilitar todos os protocolos
4. Abrir o protocolo **TCP/IP**
5. Ir até a aba **Endereço de IP**
6. Rolar até o final em **IPAll**
7. Preencher o campo **Porta TCP** com: `1433`
8. Voltar em **Serviços do SQL Server** e **reiniciar todos os serviços que estiverem em execução**

---

### 🧱 **Fase 2 – Criar e popular o banco de dados**

Execute o script `educalivre.sql` (presente na pasta `/bd` do projeto web ou desktop) no SQL Server para criar todas as tabelas e registros iniciais.

---

### 🌐 **Fase 3 – Descobrir o IP da sua máquina**

Abra o terminal (cmd) e digite:

```bash
ipconfig
```
Anote o IPv4 da sua rede local.

### ✏️ Fase 4 – Atualizar o IP no código da aplicação
Abra a classe Acessa.java (ou equivalente, onde está a conexão com o banco) e atualize a string de conexão com o IP da sua máquina:

```Java
String url = "jdbc:jtds:sqlserver://192.168.X.X;databaseName=educaLivre";
```
>⚠️ Substitua 192.168.X.X pelo IP encontrado na Fase 3.

### ▶️ Rodar o app
Clique em Run no Android Studio

Escolha um emulador ou dispositivo físico

O app será instalado e aberto automaticamente

## 📱 Acesse outras versões

- [Versão WEB](https://github.com/beceluiz/EducaLivre-WEB)
- [Versão Desktop (Admin)](https://github.com/beceluiz/EducaLivre-Desktop)

📌 Observações
O app conecta diretamente ao banco SQL Server via JDBC, portanto o servidor precisa estar acessível via IP.

Algumas funcionalidades requerem permissão de acesso à internet no AndroidManifest.xml.

Todas as informações sensíveis foram removidas desta versão pública.

Desenvolvido por Luiz Fernando dos Santos Luz
Projeto de TCC em Desenvolvimento de Sistemas
