Cleyson Braga de Oliveira
Gustavo Hernandez Duarte
Ismael Freitas de Vargas
Rafael dos Santos Cardoso

	Sistemas Operacionais - Máquina Virtual


## Como utilizar a VM
1. Adicione os programas assembly na pasta `./src/main/resources/HardDisk/`. *Os arquivos não devem ter extenção;
2. Abra o terminal na raiz do projeto e execute o comando `gradle run`;
3. Acesse os links `localhost:8080/shell` e `localhost:8080/console` no seu navegador;
4. Na aba do terminal shell, utiliza o comando `load --direct ` e o nome do programa assembly;
5. Ainda na aba terminal shell, execute o comando `run`;
6. Caso o programa tenha acesso `IO`, a sua solicitação ficará aguardando resposta na página console.


	Programas disponíveis na pasta `./src/main/resources/HardDisk/`
		Assembly_Fibonacci
			Programa para calcular o Fibonacci provido pelo professor na
			primeira entrega com intuito de testar a VM. Este programa já
			engloba a solicitação de memória para o gerente de memória e
			também utiliza a nova função TRAP que escreve no console a última
			resposta encontrada do Fibonacci.

		InvalidAddress
			É o mesmo programa Fibonacci anteriormente descrito, porem o mesmo
			faz acesso indevido da memória, tentando acertar uma posição que
			não faz parte do programa.

		Assembly_read
			É um pequeno programa que faz um request de leitura para o console
			e se finaliza logo em seguida

		Assembly_write
			É o mesmo programa acima alterando a instrução trap para escrever
			no console. 


    Eventuais limitações:
    	Não conseguimos descrever possíveis problemas, todas as situações
    	que a equipe conseguiu imaginar foi devidamente tratada.


	Validade do trabalho para a aprendizagem
    	Esse trabalho não teve apenas uma função avaliativa, muitos conceitos
    	e funcionalidades foram apenas compreendidos após a visualização do seu
    	funcionamento prático.


	Sugestão de melhoria
    	Mover a memória virtual para junto do conteúdo de gerente de memória	






χαριτωμενoπ ́oνυ και μαγικ ́oςκαπν ́oς