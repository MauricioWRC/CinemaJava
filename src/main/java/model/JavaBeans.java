package model;

public class JavaBeans {
	private String idcon;
	private String nome;
	private String genero;
	private int idade;
	private float duracao;
	private String lancamento;
	private String poster;
	
	
	public JavaBeans() {
		super();
		
	}
	
	public JavaBeans(String idcon, String nome, String genero, int idade, float duracao, String lancamento,
			String poster) {
		super();
		this.idcon = idcon;
		this.nome = nome;
		this.genero = genero;
		this.idade = idade;
		this.duracao = duracao;
		this.lancamento = lancamento;
		this.poster = poster;
	}

	public String getIdcon() {
		return idcon;
	}
	public void setIdcon(String idcon) {
		this.idcon = idcon;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getGenero() {
		return genero;
	}
	public void setGenero(String genero) {
		this.genero = genero;
	}
	public int getIdade() {
		return idade;
	}
	public void setIdade(int idade) {
		this.idade = idade;
	}
	public float getDuracao() {
		return duracao;
	}
	public void setDuracao(Float duracao) {
		this.duracao = duracao;
	}
	public String getLancamento() {
		return lancamento;
	}
	public void setLancamento(String lancamento) {
		this.lancamento = lancamento;
	}
	public String getPoster() {
		return poster;
	}
	public void setPoster(String poster) {
		this.poster = poster;
	}
	
}
