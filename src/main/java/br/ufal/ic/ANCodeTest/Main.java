package br.ufal.ic.ANCodeTest;

import java.io.FileNotFoundException;

import br.ufal.ic.ANCodeTest.analisador.lexico.Lexic;
import br.ufal.ic.ANCodeTest.analisador.sintatico.Syntactic;
import br.ufal.ic.ANCodeTest.token.Token;

public class Main {
	
	public static void main(String[] args) {
		try {
			Lexic lexic = new Lexic(args[0]);			
			//while(lexic.hasNextToken()) System.out.println(lexic.nextToken());
			if(lexic.hasNextToken()) {
				Token token = lexic.nextToken();
				Syntactic synAna = new Syntactic(lexic, token);
				synAna.S();
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found");
			//e.printStackTrace();
		} catch (Exception e) {
			System.err.println("IO Exception while reading file");
			//e.printStackTrace();
		}

	}

}
