# Sistemas Operacionais
Professor Fernando Luís Dotti
Desenvolvimento da maquina virtual

## Projeto de desenvolvimento da VM

### 1. Definição da Máquina Virtual (MV)
Nossa máquina virtual (MV) tem CPU e Memória.

- [x] 1.1 CPU
O processador possui os seguintes registradores:
• um contador de instruções (PC)
• Um registrados de instruções (IR)
• oito registradores, 0 a 7
O conjunto de instruções é apresentado na tabela a seguir, adaptado de [1].
A tabela atual está revisada para não ter mais operações a nível de bit. As colunas em vermelho substituem a codificação em
bits para designar os registradores e parâmetros utilizados, e compõem os “campos" de uma posicaoDeMemoria vide 1.2.

- [x] 1.2 Memória
Considere a memória como um array contíguo de posições de memória. Cada posição de memória são 4 bytes. A memória
tem 1024 posições.
 tamMemoria = 1024
 array mem[tamMemoria] of posicaoDeMemoria
Cada posiçãoDeMemória codifica [ OPCODE, 1 REG de 1..8, 1 REG de 1..8, PARAMETRO K OU A conforme operação ]. Em um sistema real
estes dados são codificados em bits de uma palavra. No nosso trabalho, podemos adotar que uma posicaoDeMemoria é um
registro (objeto) com estes atributos. Note que no caso da posição de memória não ter uma instrução, temos que adotar
então uma codificação própria. Neste caso podemos ter um OPCODE especial para significar uma posição de dados, e, no
campo de K ou A, temos um valor inteiro. Um valor inteiro é suficiente pois a nossa arquitetura manipulará inteiros apenas.

- [x] 1.3 Funcionamento da CPU: o ciclo de instruções
A CPU executa o ciclo de instruções. Dado um valor no PC, ela faz:
 Loop
 busca a posição de memória apontada por PC, carrega no RI
 Decodifica o RI
 se RI inválido - sinaliza interrupção, acaba esta instrução
 executa operação
 Se erro: sinaliza interrupcao, acaba esta instrução
 conforme operação efetua a mesma e atualiza PC
 se existe interrupção
 desvia para rotina de tratamento
 fimLoop
Um vetor de interrupções associa o código da interrupção com a rotina a ser executada.

- [ ] 1.4 Programas
Neste momento não temos um Sistema Operacional. Para fazer a Máquina Virtual funcionar, você deve carregar um
programa no início da memória, atribuir um valor ao PC (o início do código do seu programa), e liberar a CPU para executar.
A CPU vai executar até parar, encontrar um erro, ou então vai entrar em loop conforme o programa estiver correto ou não.
Você deve criar formas de acompanhar esta evolução.
Nossos programas podem ser escritos em TXT e lidos para a memória, ou então eles podem ser codificados em Java como a
criação de um vetor de posicaoDeMemoria inicializado em cada posição do vetor como uma linha do programa.
A seguir o programa P1, que escreve em posições sabidas de memória os 10 números da sequência de Fibonacci. Ou seja,
ao final do programa a memória tem estes 10 números em posições convencionadas no programa. Avalie se P1 está
correto.
Como parte do exercício, construa P2, um programa que le um valor de uma determinada posição (carregada no inicio),
 se o número for menor que zero coloca -1 no início da posição de memória para saída;
 se for maior que zero este é o número de valores
 da sequencia de fibonacci a serem escritos em sequencia a partir de uma posição de
 memoria;







## Pre-requisitos
1. Instalar o Gradle 6.6.1 (adicionar nas variaveis de ambiente do sistema);
2. Java 1.8.0 (adicionar o JAVA_HOME nas variaveis de ambiente do sistema);

## Adicionando uma nova variavel de ambiente do sistema
1. Na pesquisa do sistema, procure por "Variaveis de ambiente do sistema" (Edit the system environment variables)
2. Clique em "Variaveis de ambiente..." (Environment variables...)
3. Na aba inferior da nova janela, em variaveis do sistema, clique no botão "nova variavel"
4. Digite o nome da variavel [GRADLE_HOME ou JAVA_HOME]
5. O valor da variável deverá ser a pasta raiz do package, por exemplo, C:\Program Files\opt\gradle. Clique em Ok
6. Procure pela variável "Path" e adicione um novo parametro entre '%' com o nome da variável recem criada e adicione \bin no final. Por exemplo, %GRADLE_HOME%\bin
*Para verificar se funcionou, não utilize terminais abertos antes desse processo. Feche-o e abra um novo terminal.*

## Verificando o ambiente
1. Abra o terminal (cmd)
2. Verificar a versão do Java Compiler 'javac -version'
3. Verificar a versão do Gradle 'gradle --version'
4. Verificar se a variavel de ambiente do sistema está configurada 'echo %JAVA_HOME%'

## Como executar o código groovy
1. Abrir o terminal na pasta raiz do projeto
2. Executar o comando ´gradle run --args"[Kgs de chocolate]"´

## Como executar os testes
1. Abrir o terminal na pasta raiz do projeto
2. Executar o comando ´gradle test´

Observações:
* A primeira execução vai ser mais demorada, dado que o gradle vai baixar as dependencias como o Groovy-all e o JUnit5-Jupiter. As demais execuções serão mais rápidas;
* Não tenho certeza quanto a necessidade de baixar e instalar o package da linguagem Groovy. Eu tenho instalado, mas acredito que apenas faça a função de me permitir compilar e executar o programa pelo cmd (fora do gradle);
* Eu utilizo a versão 6.6.1 do Gradle (<= 4.5.1 não funcionam), não tenho certeza até qual versão anterior as dependencias funcionam. Recomendo utilizar a 6.6.1 (última disponível).

*Comandos gradle*
* gradle build - cria o artefato .jar/.war (executando os testes)
* gradle clean - Exclui todos os arquivos de compilação e artefatos do projeto
* gradle run - Executa o programa
* gradle test - Executa os testes unitários do projeto

**Todos os comandos gradle compilam o projeto automagicamente**


### Links úteis
* https://groovyconsole.appspot.com
