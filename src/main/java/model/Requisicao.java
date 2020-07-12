package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Requisicao {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private Long dataRecebimento;
	private String mensagem;

	public Integer getId() {
		return id;
	}

	public Long getDataRecebimento() {
		return dataRecebimento;
	}

	public void setDataRecebimento(Long dataRecebimento) {
		this.dataRecebimento = dataRecebimento;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

}
