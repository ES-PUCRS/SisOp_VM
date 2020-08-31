# Verificação e Validação de Software
Professor Bernardo Copstein  
Introdução ao Teste Unitário
## Exercícios sobre uso básico de JUnit para entregar

1. Uma  indústria  de  chocolate  tem  de  despachar  para  seus  clientes  pacotes  com  diferentes pesos.  As  barras  são  produzidas  com  dois  pesos:  1Kg  e  5Kg.  Precisa-se  saber  quantas barras  de  cada  peso  deve-se  enviar  para  cada  cliente  sabendo  que  a  prioridade  é  enviar barras  de  5Kg.  A  classe  “Encomenda”  que  segue  possui  um  método  chamado “qtdadeBarras”.  Este  retorna  um  arranjo  com  duas  posições:  a  primeira  indica  a quantidade  de  barras  de  5Kg  e  a  segunda  a  quantidade  de  barras  de  1Kg  necessárias para  completar  a  encomenda.  O  “esqueleto  da  classe’  pode  ser  visto  abaixo. Implemente  um  driver  de  teste  capaz  de  testar  a  classe  adequadamente.  Em  seguida implemente a classe e verifique a qualidade de sua implementação

~~~Java
class Encomenda{
	int[] qtdadeBarras(int peso){
			...
	}
}
~~~

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
