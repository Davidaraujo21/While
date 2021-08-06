package plp.enquanto;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

interface Linguagem {
	Map<String, Integer> ambiente = new HashMap<>();
	Scanner scanner = new Scanner(System.in);

	interface Bool {
		boolean getValor();
	}

	interface Comando {
		void execute();
	}

	interface Expressao {
		int getValor();
	}

	/*
	  Comandos
	 */
	class Programa {
		private final List<Comando> comandos;
		public Programa(List<Comando> comandos) {
			this.comandos = comandos;
		}
		public void execute() {
			try {
				comandos.forEach(Comando::execute);
			}
			catch(NullPointerException e) {}	
		}
	}

	class Se implements Comando {
		
		private List<Bool> condicoes = new ArrayList<>();
		private List<Comando> comandos = new ArrayList<>();

		public Se(List<Bool> condicoes, List<Comando> comandos) {
			this.condicoes = condicoes;
			this.comandos = comandos;
		}
		
		@Override
		public void execute() {

			if (condicoes.get(0).getValor()){
				comandos.get(0).execute();
			}else if(condicoes.size() > 1){
				for(int i = 1; i < condicoes.size(); i++){
					if(condicoes.get(i).getValor()){
						comandos.get(i).execute();
						return;
					}
				}
				//Se a verificação dos senaose não passar executar o comando naose do final do array comandos
				comandos.get(comandos.size() - 1).execute();
			}else{
				comandos.get(1).execute();
			}
		}
	}

	class Repita implements Comando{
		private final Expressao exp;
		private final Comando comando;

		public Repita(Expressao exp, Comando comando){
			this.exp = exp;
			this.comando = comando;
		}

		@Override
		public void execute(){
			for(int i = 1; i < exp.getValor(); i++){
				comando.execute();
			}
		}

	}

	class Para implements Comando{
		private final String id;
		private final Expressao exp1;
		private final Expressao exp2;
		private final Comando comando;

		public Para(String id, Expressao exp1, Expressao exp2, Comando comando){
			this.id = id;
			this.exp1 = exp1;
			this.exp2 = exp2;
			this.comando = comando;
		}

		@Override
		public void execute(){
			for(int i = exp1.getValor(); i < exp2.getValor(); i++){
				ambiente.put(id, i);
				comando.execute();
			}
		}
	}

	Skip skip = new Skip();
	class Skip implements Comando {
		@Override
		public void execute() {}
	}

	class Escreva implements Comando {
		private final Expressao exp;

		public Escreva(Expressao exp) {
			this.exp = exp;
		}

		@Override
		public void execute() {
			System.out.println(exp.getValor());
		}
	}

	class Enquanto implements Comando {
		private final Bool condicao;
		private final Comando comando;

		public Enquanto(Bool condicao, Comando comando) {
			this.condicao = condicao;
			this.comando = comando;
		}

		@Override
		public void execute() {
			while (condicao.getValor()) {
				comando.execute();
			}
		}
	}

	class Quando implements Comando{
		
		private List<Expressao> expressoes = new ArrayList<>();
		private List<Comando> comandos = new ArrayList<>();

		public Quando(List<Expressao> expressoes, List<Comando> comandos) {
			this.expressoes = expressoes;
			this.comandos = comandos;
		}

		@Override
		public void execute(){
			for(int i = 0; i < comandos.size(); i++){
				if(expressoes.get(0).getValor() == expressoes.get(i + 1).getValor()){
					comandos.get(i).execute();
					return;
				}
			}
			
			if(expressoes.get(expressoes.size() - 1).getValor() == 0){
				comandos.get(comandos.size() - 1).execute();
			}
		}

	}

	class Exiba implements Comando {
		private final String texto;

		public Exiba(String texto) {
			this.texto = texto;
		}

		@Override
		public void execute() {
			System.out.println(texto);
		}
	}

	class Bloco implements Comando {
		private final List<Comando> comandos;

		public Bloco(List<Comando> comandos) {
			this.comandos = comandos;
		}

		@Override
		public void execute() {
			comandos.forEach(Comando::execute);
		}
	}

	class Atribuicao implements Comando {
		private List<String> ids = new ArrayList<>();
		private List<Expressao> exps = new ArrayList<>();

		Atribuicao(List<String> ids, List<Expressao> exps) {
			this.ids = ids;
			this.exps = exps;
		}

		@Override
		public void execute() {
			for(int i = 0; i < ids.size(); i++){
				ambiente.put(ids.get(i), exps.get(i).getValor());
			}
		}
	}

	/*
	   Expressoes
	 */

	abstract class OpBin<T>  {
		protected final T esq;
		protected final T dir;

		OpBin(T esq, T dir) {
			this.esq = esq;
			this.dir = dir;
		}
	}

	abstract class OpUnaria<T>  {
		protected final T operando;

		OpUnaria(T operando) {
			this.operando = operando;
		}
	}

	class Inteiro implements Expressao {
		private final int valor;

		Inteiro(int valor) {
			this.valor = valor;
		}

		@Override
		public int getValor() {
			return valor;
		}
	}

	class Id implements Expressao {
		private final String id;

		Id(String id) {
			this.id = id;
		}

		@Override
		public int getValor() {
			return ambiente.getOrDefault(id, 0);
		}
	}

	Leia leia = new Leia();
	class Leia implements Expressao {
		@Override
		public int getValor() {
			return scanner.nextInt();
		}
	}

	class ExpSoma extends OpBin<Expressao> implements Expressao {
		ExpSoma(Expressao esq, Expressao dir) {
			super(esq, dir);
		}

		@Override
		public int getValor() {
			return esq.getValor() + dir.getValor();
		}
	}

	class ExpSub extends OpBin<Expressao> implements Expressao {
		ExpSub(Expressao esq, Expressao dir) {
			super(esq, dir);
		}

		@Override
		public int getValor() {
			return esq.getValor() - dir.getValor();
		}
	}

	class ExpMult extends OpBin<Expressao> implements Expressao{
		ExpMult(Expressao esq, Expressao dir) {
			super(esq, dir);
		}

		@Override
		public int getValor() {
			return esq.getValor() * dir.getValor();
		}
	}

	class ExpDiv extends OpBin<Expressao> implements Expressao{
		ExpDiv(Expressao esq, Expressao dir){
			super(esq, dir);
		}

		@Override
		public int getValor() {
			return esq.getValor() / dir.getValor();
		}
	}

	class ExpExpon extends OpBin<Expressao> implements Expressao{
		ExpExpon(Expressao esq, Expressao dir){
			super(esq, dir);
		}

		@Override
		public int getValor(){
			return (int)Math.pow(esq.getValor(), dir.getValor());
		}
	}

	class Booleano implements Bool {
		private final boolean valor;

		Booleano(boolean valor) {
			this.valor = valor;
		}

		@Override
		public boolean getValor() {
			return valor;
		}
	}

	class ExpIgual extends OpBin<Expressao> implements Bool {
		ExpIgual(Expressao esq, Expressao dir) {
			super(esq, dir);
		}

		@Override
		public boolean getValor() {
			return esq.getValor() == dir.getValor();
		}
	}

	class ExpMenorIgual extends OpBin<Expressao> implements Bool{
		ExpMenorIgual(Expressao esq, Expressao dir) {
			super(esq, dir);
		}

		@Override
		public boolean getValor() {
			return esq.getValor() <= dir.getValor();
		}
	}

	class ExpMaiorIgual extends OpBin<Expressao> implements Bool{
		ExpMaiorIgual(Expressao esq, Expressao dir){
			super(esq, dir);
		}

		@Override
		public boolean getValor(){
			return esq.getValor() >= dir.getValor();
		}
	}

	class ExpMenor extends OpBin<Expressao> implements Bool{
		ExpMenor(Expressao esq, Expressao dir){
			super(esq, dir);
		}

		@Override
		public boolean getValor(){
			return esq.getValor() < dir.getValor();
		}
	}

	class ExpMaior extends OpBin<Expressao> implements Bool{
		ExpMaior(Expressao esq, Expressao dir){
			super(esq, dir);
		}

		@Override
		public boolean getValor(){
			return esq.getValor() > dir.getValor();
		}
	}

	class ExpDiff extends OpBin<Expressao> implements Bool{
		ExpDiff(Expressao esq, Expressao dir){
			super(esq, dir);
		}

		@Override
		public boolean getValor(){
			return esq.getValor() != dir.getValor();
		}
	}

	class NaoLogico extends OpUnaria<Bool> implements Bool{
		NaoLogico(Bool operando) {
			super(operando);
		}

		@Override
		public boolean getValor() {
			return !operando.getValor();
		}
	}

	class ELogico extends OpBin<Bool> implements Bool{
		ELogico(Bool esq, Bool dir) {
			super(esq, dir);
		}

		@Override
		public boolean getValor() {
			return esq.getValor() && dir.getValor();
		}
	}

	class OuLogico extends OpBin<Bool> implements Bool{
		OuLogico(Bool esq, Bool dir){
			super(esq, dir);
		}

		@Override
		public boolean getValor(){
			return esq.getValor() || dir.getValor();
		}
	}

	class XorLogico extends OpBin<Bool> implements Bool{
		XorLogico(Bool esq, Bool dir){
			super(esq, dir);
		}

		@Override
		public boolean getValor(){
			return (!esq.getValor() && dir.getValor()) || (esq.getValor() && !dir.getValor());
		}
	}

}
