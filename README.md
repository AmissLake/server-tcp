# server-tcp

Criar pasta em /var/log/server no windows criar em C:/var/log/server 

###### Listening
Escutando na porta 9000

### A estrutura de mensagens do protocolo é composta pelos seguintes campos:

  INIT - Indica o início da mensagem  - 1 byte
  
  BYTES - Indica quantos bytes a mensagem vai ter (A quantidade de bytes inclui todos os campos descritos aqui) - 1 byte	
  
  FRAME - Indica qual é o tipo da mensagem - 1 byte
  ###### Frame	Descrição
  * 0xA0 - ACK
  * 0xA1 Mensagem de texto
  * 0xA2 Informações do usuário
  * 0xA3 Solicitar data e hora

  DATA - É o corpo da mensagem, quando necessário será descrito cada campo individualmente na sua mensagem. -	n bytes
  ###### Mensagem de texto
  * Data : Ex Hello World - n bytes
  ###### Informações de um usuário Estrutura
  *	IDADE: 1 byte
  *	PESO: 1 byte
  *	ALTURA: 1 byte
  *	TAMANHO_NOME: 1 byte
  *	NOME: n bytes
  
  ###### Solicitar data e hora 
  * Data : Ex America/Sao_Paulo - n bytes
  ###### Resposta data e hora
  * DIA : 1 byte	
  * MÊS	: 1 byte
  * ANO	: 1 byte 
  * HORA : 1 byte
  * MINUTO	: 1 byte
  * SEGUNDO : 1 byte
  
  CRC - Indica o cálculo do CRC para validar se a mensagem foi recebida corretamente. - 1 byte
  
  END - Indica o final da mensagem - 1 byte
  
