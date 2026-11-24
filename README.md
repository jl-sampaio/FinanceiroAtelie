# Ateliê de Maquiagem — Controle Financeiro

Sistema desktop em Java (Swing + H2) para registrar entradas e saídas do ateliê.

## Funcionalidades
- Lançamento de **entradas** (serviços, cursos, vendas) e **saídas** (compras, aluguel, contas...)
- Categorias sugeridas, mas editáveis (digite a sua)
- Filtro por período e por tipo; botão "Este mês"
- Resumo com total de entradas, saídas e saldo do período
- Cada lançamento grava **usuário** e **data/hora do registro**
- Exportação para **CSV** (abre direto no Excel)
- Login com senha armazenada como hash (SHA-256 + salt). No primeiro acesso você cria o usuário administrador.

## Como executar
Requer **JDK 17 ou superior**.

- Windows: dê dois cliques em `setup_e_rodar.bat`
- Linux/macOS: `./setup_e_rodar.sh`

Na primeira vez o script baixa o `h2.jar` (~2,5 MB). Se falhar, baixe em
https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar e salve como `lib/h2.jar`.

O banco é criado em `financeiro_atelie.mv.db` na pasta do projeto. **Faça backup desse arquivo**
e não o publique no GitHub (já está no `.gitignore`).

## Estrutura
```
src/
  Main.java              ponto de entrada
  Database.java          conexão H2 e criação das tabelas
  Lancamento.java        modelo
  FinanceiroDAO.java     SQL de lançamentos
  UsuarioDAO.java        usuários e senhas (hash)
  NovoUsuarioDialog.java criação de usuário
  LoginFrame.java        tela de login
  LancamentoDialog.java  formulário de entrada/saída
  MainFrame.java         tela principal
  Util.java              formatação de moeda e leitura de valores/datas
```
