package plp.enquanto;

import java.util.ArrayList;
import java.util.List;

import plp.enquanto.Linguagem.*;
import plp.enquanto.parser.EnquantoBaseListener;
import plp.enquanto.parser.EnquantoParser.*;

import static java.lang.Integer.parseInt;

public class Regras extends EnquantoBaseListener {
	private final Leia leia;
	private final Skip skip;
	private final Propriedades valores;

	private Programa programa;

	public Regras() {
		leia = new Leia();
		skip = new Skip();
		valores = new Propriedades();
	}

	public Programa getPrograma() {
		return programa;
	}

	@Override
	public void exitBool(BoolContext ctx) {
		valores.insira(ctx, new Booleano("verdadeiro".equals(ctx.getText())));
	}

	@Override
	public void exitLeia(LeiaContext ctx) {
		valores.insira(ctx, leia);
	}

	@Override
	public void exitSe(SeContext ctx) {
		final List<Bool> condicoes = new ArrayList<>();
		final List<Comando> comandos = new ArrayList<>(); 

		for(BooleanoContext c: ctx.booleano()){
			condicoes.add(valores.pegue(c));
		}

		for(ComandoContext c: ctx.comando()){
			comandos.add(valores.pegue(c));
		}

		valores.insira(ctx, new Se(condicoes, comandos));
	}

	@Override
	public void exitQuando(QuandoContext ctx){
		final List<Expressao> expressoes = new ArrayList<>();
		final List<Comando> comandos = new ArrayList<>();

		for(ExpressaoContext c: ctx.expressao()){
			expressoes.add(valores.pegue(c));
		}

		for(ComandoContext c: ctx.comando()){
			comandos.add(valores.pegue(c));
		}

		valores.insira(ctx, new Quando(expressoes, comandos));

	}

	@Override
	public void exitRepita(RepitaContext ctx){
		final Expressao exp = valores.pegue(ctx.expressao());
		final Comando comando = valores.pegue(ctx.comando());
		valores.insira(ctx, new Repita(exp, comando));
	}

	@Override
	public void exitPara(ParaContext ctx){
		final String id = ctx.ID().getText();
		final Expressao exp1 = valores.pegue(ctx.expressao(0));
		final Expressao exp2 = valores.pegue(ctx.expressao(1));
		final Comando comando = valores.pegue(ctx.comando());
		valores.insira(ctx, new Para(id, exp1, exp2, comando));
	}

	@Override
	public void exitInteiro(InteiroContext ctx) {
		valores.insira(ctx, new Inteiro(parseInt(ctx.getText())));
	}

	@Override
	public void exitSkip(SkipContext ctx) {
		valores.insira(ctx, skip);
	}

	@Override
	public void exitEscreva(EscrevaContext ctx) {
		final Expressao exp = valores.pegue(ctx.expressao());
		valores.insira(ctx, new Escreva(exp));
	}

	@Override
	public void exitPrograma(ProgramaContext ctx) {
		final List<Comando> cmds = valores.pegue(ctx.seqComando());
		programa = new Programa(cmds);
		valores.insira(ctx, programa);
	}

	@Override
	public void exitId(IdContext ctx) {
		final String id = ctx.ID().getText();
		valores.insira(ctx, new Id(id));
	}

	@Override
	public void exitSeqComando(SeqComandoContext ctx) {
		final List<Comando> comandos = new ArrayList<>();
		for (ComandoContext c : ctx.comando()) {
			comandos.add(valores.pegue(c));
		}
		valores.insira(ctx, comandos);
	}

	@Override
	public void exitAtribuicao(AtribuicaoContext ctx) {
		final List<String> ids = new ArrayList<>();
		final List<Expressao> exps = new ArrayList<>();
		int i = 0;

		for (ExpressaoContext c : ctx.expressao()) {
			exps.add(valores.pegue(c));
			ids.add(ctx.ID(i).getText());
			i++;
		}

		valores.insira(ctx, new Atribuicao(ids, exps));
	}

	@Override
	public void exitBloco(BlocoContext ctx) {
		final List<Comando> cmds = valores.pegue(ctx.seqComando());
		valores.insira(ctx, new Bloco(cmds));
	}

	@Override
	public void exitOpBin(OpBinContext ctx) {
		final Expressao esq = valores.pegue(ctx.expressao(0));
		final Expressao dir = valores.pegue(ctx.expressao(1));
		final String op = ctx.getChild(1).getText();
		final Expressao exp = switch (op) {
			case "^" -> new ExpExpon(esq, dir);
			case "*" -> new ExpMult(esq, dir);
			case "/" -> new ExpDiv(esq, dir);
			case "-" -> new ExpSub(esq, dir);
			default  -> new ExpSoma(esq, dir);
		};
		valores.insira(ctx, exp);
	}

	@Override
	public void exitEnquanto(EnquantoContext ctx) {
		final Bool condicao = valores.pegue(ctx.booleano());
		final Comando comando = valores.pegue(ctx.comando());
		valores.insira(ctx, new Enquanto(condicao, comando));
	}


	@Override
	public void exitELogico(ELogicoContext ctx) {
		final Bool esq = valores.pegue(ctx.booleano(0));
		final Bool dir = valores.pegue(ctx.booleano(1));
		valores.insira(ctx, new ELogico(esq, dir));
	}

	@Override
	public void exitOuLogico(OuLogicoContext ctx){
		final Bool esq = valores.pegue(ctx.booleano(0));
		final Bool dir = valores.pegue(ctx.booleano(1));
		valores.insira(ctx, new OuLogico(esq, dir));
	}

	@Override
	public void exitXorLogico(XorLogicoContext ctx){
		final Bool esq = valores.pegue(ctx.booleano(0));
		final Bool dir = valores.pegue(ctx.booleano(1));
		valores.insira(ctx, new XorLogico(esq, dir));
	}

	@Override
	public void exitBoolPar(BoolParContext ctx) {
		final Bool booleano = valores.pegue(ctx.booleano());
		valores.insira(ctx, booleano);
	}

	@Override
	public void exitNaoLogico(NaoLogicoContext ctx) {
		final Bool b = valores.pegue(ctx.booleano());
		valores.insira(ctx, new NaoLogico(b));
	}

	@Override
	public void exitExpPar(ExpParContext ctx) {
		final Expressao exp = valores.pegue(ctx.expressao());
		valores.insira(ctx, exp);
	}

	@Override
	public void exitExiba(ExibaContext ctx) {
		final String t = ctx.TEXTO().getText();
		final String texto = t.substring(1, t.length() - 1);
		valores.insira(ctx, new Exiba(texto));
	}

	@Override
	public void exitOpRel(OpRelContext ctx) {
		final Expressao esq = valores.pegue(ctx.expressao(0));
		final Expressao dir = valores.pegue(ctx.expressao(1));
		final String op = ctx.getChild(1).getText();
		final Bool exp = switch (op) {
			case "="  -> new ExpIgual(esq, dir);
			case "<=" -> new ExpMenorIgual(esq, dir);
			case ">=" -> new ExpMaiorIgual(esq, dir);
			case "<"  -> new ExpMenor(esq, dir);
			case ">"  -> new ExpMaior(esq, dir);
			case "!=" -> new ExpDiff(esq, dir);
			default   -> new ExpIgual(esq, esq);
		};
		valores.insira(ctx, exp);
	}
}
