# Sistemas Operacionais
Professor Fernando Luís Dotti
Desenvolvimento da maquina virtual

## Projeto de desenvolvimento da VM

### 1. Definição da Máquina Virtual (MV)

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

- [X] 1.4 Programas
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

- [X] 3 Gerente de Processos (GP)
O Gerente de Processos é responsável pela:
• criação de processos;
• escalonamento, mudanças de estados;
• terminação de processos.
- [X] 3.1. Criação de Processos
Dado um programa a ser executado, solicitado pelo usuário através de um Shell, gera-se uma solicitação de criação de
processo ao gerente de processos, com o nome (identificador) do programa.
O gerente de processos é responsável pela criação do novo processo, envolvendo:
• Aloca um novo identificador ao processo;
• Aloca um Process Control Block (estrutura de dados) para representar o processo;
• Solicita frames de memória para conter todo processo em memória (assume-se que o tamanho é conhecido),
incluindo área de código e dados;
• Carrega as páginas do programa nos frames;
• Armazena a tabela de páginas no PCB;
(até aqui foi ja feito na entrega anterior, agora voce vai organizar isso como uma funcionalidade do GP, e
atenção aos passos seguintes, pois eles têm novidades)
• Salvamento do estado da CPU: Nesta versão do sistema, os processos poderão ser escalonados. Isto significa que
eles vão poder utilizar a CPU diversas vezes na sua vida. Então o SO deve salvar o contexto de execução de um
processo saindo da CPU, assim como restaurar o estado de um processo quando colocar ele na CPU (dispatch).
Assim, o Process Control Block deve ter atributos para permitir salvar o estado de execução do processo na CPU.
Isto inclui todos os registradores, PC, IR. No início, PC=0.
• Depois de criar todas estruturas acima, o processo é colocado na fila de prontos.
• Se não á processo executando, o Dispatcher é liberado para escolher um entre os processos da fila de prontos e
executar.
- [X] 3.2. Finalização de Processos
Se um processo deve ser finalizado (operação STOP, erro de execução, violação de endereçamento, etc), uma rotina de
finalização de processo, do GP, é invocada. Esta rotina é responsável por deslocar todas estruturas alocadas por este
processo. Desaloca frames, PCB, etc.
Como no nosso sistema um processo só termina quando está executando (não existe opção de um processo matar outro), ao
final desta rotina de tratamento, o Dispatcher é liberado para escolher um processo para executar na CPU.

- [X] 4. Escalonamento
O escalonamento é uma tarefa de gerência de processos. Entretanto separamos aqui para melhor explanação.
Consideramos o Processos responsável por criar e finalizar processos apenas. E um escalonador para fazer o “dispatching”.
O Dispatcher continuamente escolhe um entre os processos na fila de prontos (ready) para executar na CPU.
Ele seta na CPU o contexto de execução do processo e libera a CPU para execução do processo escolhido a partir do
estado onde estava (PC e demais registradores …). O Dispatcher aguarda nova liberação para execução.
- [X] 4.1. Dispatcher RR (Round-Robin)
Implementa a política Round-Robin. Depois de uma fatia de tempo, o processo utilizando a CPU retorna para a fila de
prontos e outro processo é escolhido pelo dispatcher para executar.
Para simular a fatia de tempo, configuraremos um determinado numero X de instruções na CPU que o processo eXecuta.
por exemplo X=5, como sendo o tempo de permanência de um processo na CPU. Ao completar X instruções, gera-se um
sinal de interrupção significando final da fatia de tempo, e a CPU desvia para uma rotina específica para isso. Num sistema
completo, esta interrupção é (seria) gerada por um relógio do sistema.
Rotina de tratamento de final de fatia de tempo.
salva o estado do processo que estava rodando
retorna este processo para o final da fila de prontos
libera o Dispatcher para escolher o próximo processo a executar.
- [X] 4.2. Processo Rodando
O sistema operacional deve ter uma variável “processoRodando" que aponta para o PCB do processo executando neste
momento.
- [X] 4.3. Fila de Processos Prontos
O sistema operacional tem uma fila de processos prontos que trabalha como uma FIFO.

- [X] 5. Entrada e Saída
Nosso sistema operacional só faz leituras do teclado e escritas na tela, de valores inteiros. Assim, cada operação só envolve
um dispositivo (e não precisamos representar múltiplos).
- [X] 5.1. Interface para chamadas de sistema
Nosso sistema operacional oferece duas chamadas para entrada e saída, read e write. Convencionamos que: o número da
chamada de sistema é armazenado em R1 e os parâmetros conforme especificação da rotina:
- [X] 5.1.1 Leitura
Read: R1 = 1, R2: endereço de escrita na memória do valor lido, ou seja, uma chamada seria
 LDI R1,1 // leitura
 LDI R2, 30 // escrever o valor lido na posição 30 da memória
 Trap
- [X] 5.1.2 Escrita
 Write: R1 = 2, R2: endereço do valor a ser escrito
 LDI R1, 2
 LDI R2, endereço do valor a ser escrito
 Trap
- [X] 5.1.3 Trap - NOVA INSTRUCAO DA CPU
Trap : interrupção de SW. A interrupção de Software, ou Trap, salva o contexto do processo em execução, inicia a execução
da rotina de tratamento da Trap.
 Se for rotina de leitura, enfileira um pedido para a Console para ler um valor.
 Se for rotina de escrita, enfileira um pedido para a Console para escrever um valor.
Em ambos os casos, coloca o processo no estado bloqueado e provoca o escalonamento de outro processo.
- [X] 5.2. Console
A console é um processo concorrente (thread) que pega as requisições da fila, executa, e gera uma interrupção para indicar
que a operação está pronta. O pedido de leitura lê um valor inteiro da console. O usuário fornece o valor. O pedido de
escrita imprime na tela o valor escrito. Para facilitar, em cada execução de entrada ou saída, o sistema vai imprimir qual
processo e que operação está sendo feita.
Uma vez que a operação seja realizada:
• no caso de leitura, escreve na posição de memória dada como parâmetro
* no caso de leitura ou escrita, interrompe a CPU dizendo de qual processo a operação acabou
- [X] 5.3. Rotina de tratamento de interrupção - I/O pronto
A rotina de tratamento desta interrupção deve transferir o processo do estado bloqueado para o estado pronto, e retomar a
execução de processos, conforme a política do SO. 

**DISCLAIMER**
Nesta VM, a nova instrução TRAP não é sucede as instruções LDI para os RIO 1 e 2.
a função trap por sí só recebe esses valores por parametro, ´TRAP k, [Rs]´, onde k será o RIO1, que indicava a função de leitura ou escrita e Rs a posição de memória.
Os registradores RIO existem com ideia de facilitar o fluxo de dados com o Process Control Block. 


## Pre-requisitos
1. Gradle (5.1.1 ou superior) (adicionado nas variaveis de ambiente do sistema);
2. Java (1.8.0 ou superior) (adicionar o JAVA_HOME nas variaveis de ambiente do sistema);
3. Browser

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
2. Executar o comando ´gradle run´

## Como executar os testes
1. Abrir o terminal na pasta raiz do projeto
2. Executar o comando ´gradle test´

Observações:
* A primeira execução vai tomar mais tempo, dado que o gradle deve baixar todas as dependencias, como o Groovy-all e JUnit5-Jupiter. As demais execuções serão mais rápidas;
* Os testes unitários não estão funcionando.;

*Comandos gradle*
* gradle build - cria o artefato .jar/.war (executando os testes)
* gradle clean - Exclui todos os arquivos de compilação e artefatos do projeto
* gradle run - Executa o programa
* gradle test - Executa os testes unitários do projeto

**Todos os comandos gradle compilam o projeto automagicamente**

## Como utilizar a VM
1. Adicione os programas assembly na pasta `./src/main/resources/HardDisk/`. *Os arquivos não devem ter extenção;
2. Abra o terminal na raiz do projeto e execute o comando `gradle run`;
3. Acesse os links `localhost:8080/shell` e `localhost:8080/console` no seu navegador;
4. Na aba do terminal shell, utiliza o comando `load --direct ` e o nome do programa assembly;
5. Ainda na aba terminal shell, execute o comando `run`;
6. Caso o programa tenha acesso `IO`, a sua solicitação ficará aguardando resposta na pagina console.


```diff
# Esta VM funciona com pôneis fofinhos e fumaça mágica.
```

### Links úteis
* https://groovyconsole.appspot.com
