package br.ufal.ic.ANCodeTest.analisador.lexico;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;

import br.ufal.ic.ANCodeTest.analisador.lexico.Lexic;
import br.ufal.ic.ANCodeTest.token.Token;
import br.ufal.ic.ANCodeTest.token.TokenCategory;

class LexicTest {

	@Test
	void trailingSpacesTest01() {
		InputStream is = new ByteArrayInputStream("  thisIsAnId  ".getBytes());
		Lexic lexic = new Lexic(new BufferedReader(new InputStreamReader(is)));
		assertTrue(lexic.hasNextToken());
		assertEquals(new Token("thisIsAnId", 0, 2, TokenCategory.id), lexic.nextToken());	
	}
	
	@Test
	void trailingSpacesTest02() {
		InputStream is = new ByteArrayInputStream("  \" thisIsAString \"  ".getBytes());
		Lexic lexic = new Lexic(new BufferedReader(new InputStreamReader(is)));
		assertTrue(lexic.hasNextToken());
		assertEquals(new Token("\" thisIsAString \"", 0, 2, TokenCategory.stringCons), lexic.nextToken());	
	}
	
	@Test
	void trailingLinesTest01() {
		InputStream is = new ByteArrayInputStream("testId\n".getBytes());
		Lexic lexic = new Lexic(new BufferedReader(new InputStreamReader(is)));
		assertTrue(lexic.hasNextToken());
		assertEquals(new Token("testId", 0, 0, TokenCategory.id), lexic.nextToken());
		assertFalse(lexic.hasNextToken());
	}
	
	@Test
	void trailingLinesTest02() {
		InputStream is = new ByteArrayInputStream("testId\n ".getBytes());
		Lexic lexic = new Lexic(new BufferedReader(new InputStreamReader(is)));
		assertTrue(lexic.hasNextToken());
		assertEquals(new Token("testId", 0, 0, TokenCategory.id), lexic.nextToken());
		assertFalse(lexic.hasNextToken());
	}
	
	@Test
	void trailingLinesTest03() {
		InputStream is = new ByteArrayInputStream("testId\n\"\"".getBytes());
		Lexic lexic = new Lexic(new BufferedReader(new InputStreamReader(is)));
		assertTrue(lexic.hasNextToken());
		assertEquals(new Token("testId", 0, 0, TokenCategory.id), lexic.nextToken());
		assertTrue(lexic.hasNextToken());
		assertEquals(new Token("\"\"", 1, 0, TokenCategory.stringCons), lexic.nextToken());
	}

}
