package plp.enquanto;

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
			comandos.forEach(Comando::execute);
		}
	}

	class Se implements Comando {
		// private final Bool condicao;
		// private final Comando entao;
		// private final Bool condicao2;
		// private final Comando senaose;
		// private final Comando senao;

		private final List<Comando> comandos;
		private final List<Bool> condicoes;

		// public Se() {
		// 	// this.condicao = condicao;
		// 	// this.entao = entao;
		// 	// this.condicao2 = condicao2;
		// 	// this.senaose = senaose;
		// 	// this.senao = senao;
		// }
		
		public Se(List<Bool> condicoes, List<Comando> comandos) {
			this.condicoes = condicoes;
			this.comandos = comandos;
		}


		@Override
		public void execute() {

			if(){
				
			}
			// if (condicao.getValor()){
			// 	entao.execute();
			// }else if(this.condicao2 != null && condicao2.getValor()){
			// 	senaose.execute();
			// }else{
			// 	if(this.condicao2 != null){
			// 		senao.execute();
			// 	}else{
			// 		senaose.execute();
			// 	}
			// }
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
		private final String id;
		private final Expressao exp;

		Atribuicao(String id, Expressao exp) {
			this.id = id;
			this.exp = exp;
		}

		@Override
		public void execute() {
			ambiente.put(id, exp.getValor());
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
