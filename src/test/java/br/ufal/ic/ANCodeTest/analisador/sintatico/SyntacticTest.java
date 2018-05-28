package br.ufal.ic.ANCodeTest.analisador.sintatico;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import br.ufal.ic.ANCodeTest.analisador.lexico.Lexic;

class SyntacticTest {

	@Test
	void declarationTest() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, UnsupportedEncodingException {
		InputStream is = new ByteArrayInputStream("int: a = 3, b = 1+1, c;".getBytes());
		Constructor<Lexic> c = Lexic.class.getDeclaredConstructor(new Class[]{BufferedReader.class});
		c.setAccessible(true);
		Lexic lexic = c.newInstance(new BufferedReader(new InputStreamReader(is)));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		assertTrue(lexic.hasNextToken());
		Syntactic syntactic = new Syntactic(lexic, lexic.nextToken(), new PrintStream(output, false));
		syntactic.S();
		String data = new String(output.toByteArray());
		assertFalse(data.contains("Error"));
	}
	
	@Test
	void BOFTest() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, UnsupportedEncodingException {
		InputStream is = new ByteArrayInputStream("if(true){}".getBytes());
		Constructor<Lexic> c = Lexic.class.getDeclaredConstructor(new Class[]{BufferedReader.class});
		c.setAccessible(true);
		Lexic lexic = c.newInstance(new BufferedReader(new InputStreamReader(is)));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		assertTrue(lexic.hasNextToken());
		Syntactic syntactic = new Syntactic(lexic, lexic.nextToken(), new PrintStream(output, false));
		syntactic.S();
		String data = new String(output.toByteArray());
		
		is = new ByteArrayInputStream("for(a = 0 ! a > 1 ! a++){}".getBytes());
		lexic = c.newInstance(new BufferedReader(new InputStreamReader(is)));
		
		output = new ByteArrayOutputStream();
		assertTrue(lexic.hasNextToken());
		syntactic = new Syntactic(lexic, lexic.nextToken(), new PrintStream(output, false));
		syntactic.S();
		String data2 = new String(output.toByteArray());
		
		assertAll(
				() -> assertTrue(data.contains("Error")),
				() -> assertTrue(data2.contains("Error"))
		);
		
	}
	

}
