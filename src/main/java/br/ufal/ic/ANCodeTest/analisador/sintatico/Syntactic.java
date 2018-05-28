package br.ufal.ic.ANCodeTest.analisador.sintatico;

import java.io.PrintStream;

import br.ufal.ic.ANCodeTest.analisador.lexico.Lexic;
import br.ufal.ic.ANCodeTest.token.Token;
import br.ufal.ic.ANCodeTest.token.TokenCategory;

public class Syntactic {
	private Lexic lexic;
	private Token token;
	private PrintStream ps;
	private int scopeCounter;
	
	public Syntactic(Lexic lexic, Token token) {
		this(lexic, token, System.out);
	}
	
	Syntactic(Lexic lexic, Token token, PrintStream ps){
		this.lexic = lexic;
		this.token = token;
		this.ps = ps;
	}
	
	public void S() {
		if(token.getCategory().equals(TokenCategory.intType)
			|| token.getCategory().equals(TokenCategory.floatType)
			|| token.getCategory().equals(TokenCategory.charType)
			|| token.getCategory().equals(TokenCategory.boolType)
			|| token.getCategory().equals(TokenCategory.stringType)
			|| token.getCategory().equals(TokenCategory.arrayType)){
			printProduction("S", "Decl S");
			Decl();
			S();
		} else if(token.getCategory().equals(TokenCategory.funDef)) {
			printProduction("S", "'funDef' FunName '(' Param ')' FunType Body S");
			setNextToken();
			
			FunName();
			if(token.getCategory().equals(TokenCategory.paramBegin)) {
				setNextToken();
				
				Param();
				if(token.getCategory().equals(TokenCategory.paramEnd)){
					setNextToken();

					FunType();
					Body();
					S();
				} else unexpectedToken(")");
			} else unexpectedToken("(");
		} else if(!lexic.hasNextToken()) {
			printProduction("S", "epsilon");
		} else unexpectedToken("function or variable declaration");
	}
	
	private void FunName() {
		if(token.getCategory().equals(TokenCategory.main)) {
			printProduction("FunName", "'main'");
			setNextToken();
			
		} else if(token.getCategory().equals(TokenCategory.id)) {
			printProduction("FunName", "'id'");
			setNextToken();
			
		} else unexpectedToken("id or main");
	}
	
	private void Param() {
		if(token.getCategory().equals(TokenCategory.intType)
				|| token.getCategory().equals(TokenCategory.floatType)
				|| token.getCategory().equals(TokenCategory.charType)
				|| token.getCategory().equals(TokenCategory.boolType)
				|| token.getCategory().equals(TokenCategory.stringType)
				|| token.getCategory().equals(TokenCategory.arrayType)) {
			printProduction("Param", "Type 'id' ArrayOpt Paramr");
			Type();
			if(token.getCategory().equals(TokenCategory.id)) {
				setNextToken();
				
				ArrayOpt();
				Paramr();
				
			}else unexpectedToken("id");
		}else printProduction("Param", "epsilon");
	}
	
	private void Paramr() {
		if(token.getCategory().equals(TokenCategory.commaSep)) {
			printProduction("Paramr", "',' Type 'id' ArrayOpt Paramr");
			setNextToken();
			
			Type();
			if(token.getCategory().equals(TokenCategory.id)) {
				setNextToken();
				ArrayOpt();
				Paramr();
			} else unexpectedToken("id");
		}else printProduction("Paramr", "epsilon");
	}
	
	private void LEc() {
		printProduction("LEc", "Ec LEcr");
		Ec();
		LEcr();
	}
	
	private void LEcr() {
		if(token.getCategory().equals(TokenCategory.commaSep)) {
			printProduction("LEcr", "',' Ec LEcr");
			setNextToken();
			
			Ec();
			LEcr();
		}else printProduction("LEcr","epsilon");
	}
	
	private void FunType() {
		if(token.getCategory().equals(TokenCategory.funVoid)) {
			printProduction("FunType", "'funVoid'");
			setNextToken();
			
		} else {
			printProduction("FunType", "Type");
			Type();
		}
	}
	private void Body() {
		if(token.getCategory().equals(TokenCategory.escBegin)) {
			printProduction("Body", "'{' BodyPart '}'");
			setNextToken();
			scopeCounter++;
			BodyPart();
			if(token.getCategory().equals(TokenCategory.escEnd)) {
				scopeCounter--;
				setTokenAndCheckScope();
			} else unexpectedToken("}");
		} else unexpectedToken("{");
	}
	
	private void BodyPart() {
		if(token.getCategory().equals(TokenCategory.intType)
				|| token.getCategory().equals(TokenCategory.floatType)
				|| token.getCategory().equals(TokenCategory.charType)
				|| token.getCategory().equals(TokenCategory.boolType)
				|| token.getCategory().equals(TokenCategory.stringType)
				|| token.getCategory().equals(TokenCategory.arrayType)) {
			printProduction("BodyPart", "Decl BodyPart");
			Decl();
			BodyPart();
		} else if(token.getCategory().equals(TokenCategory.id)) {
			printProduction("BodyPart", "Atr ';' BodyPart");
			Atr();
			if(token.getCategory().equals(TokenCategory.lineEnd)) {
				setNextToken();
				
			}else unexpectedToken(";");
			BodyPart();
		} else if(token.getCategory().equals(TokenCategory.estWhile)
				|| token.getCategory().equals(TokenCategory.estDo)
				|| token.getCategory().equals(TokenCategory.estLoop)
				|| token.getCategory().equals(TokenCategory.estIf)
				|| token.getCategory().equals(TokenCategory.instRead)
				|| token.getCategory().equals(TokenCategory.instPrint)){
			printProduction("BodyPart", "Command BodyPart");
			Command();
			BodyPart();
		} else if(token.getCategory().equals(TokenCategory.funReturn)) {
			printProduction("BodyPart", "Return ';'");
			Return();
			if(token.getCategory().equals(TokenCategory.lineEnd)) {
				setNextToken();
				
			}else unexpectedToken(";");
		} else printProduction("BodyPart", "epsilon");
	}
	
	private void Return() {
		if(token.getCategory().equals(TokenCategory.funReturn)) {
			printProduction("Return", "'funReturn' Ec");
			setNextToken();
			Ec();
		} else unexpectedToken("return");
	}
	
	private void Command() {
		if(token.getCategory().equals(TokenCategory.estWhile)) {
			printProduction("Command", "'estWhile' '(' Eb ')' Body");
			setNextToken();
			
			if(token.getCategory().equals(TokenCategory.paramBegin)) {
				setNextToken();
				
				Eb();
				if(token.getCategory().equals(TokenCategory.paramEnd)) {
					setNextToken();
					
					Body();
				}else unexpectedToken(")");
			}else unexpectedToken("(");
		} else if(token.getCategory().equals(TokenCategory.estDo)) {
			printProduction("Command", "'estDo' Body 'estWhile' '(' Eb ')' ';'");
			setNextToken();
			
			Body();
			if(token.getCategory().equals(TokenCategory.estWhile)) {
				setNextToken();
				
				if(token.getCategory().equals(TokenCategory.paramBegin)) {
					setNextToken();
					
					Eb();
					if(token.getCategory().equals(TokenCategory.paramEnd)) {
						setNextToken();
						
						if(token.getCategory().equals(TokenCategory.lineEnd)) {
							setNextToken();
							
						}else unexpectedToken(";");
					}else unexpectedToken(")");
				}unexpectedToken("(");
			}unexpectedToken("while");
		} else if(token.getCategory().equals(TokenCategory.estLoop)) {
			printProduction("Command", "'estLoop' '(' Atr 'loopSep' Eb 'loopSep' Atr ')' Body");
			setNextToken();
			
			if(token.getCategory().equals(TokenCategory.paramBegin)) {
				setNextToken();
				
				Atr();
				if(token.getCategory().equals(TokenCategory.loopSep)) {
					setNextToken();
					
					Eb();
					if(token.getCategory().equals(TokenCategory.loopSep)) {
						setNextToken();
						
						Atr();
						if(token.getCategory().equals(TokenCategory.paramEnd)) {
							setNextToken();
							
							Body();
						}else unexpectedToken(")");
					}else unexpectedToken("!");
				}else unexpectedToken("!");
			}else unexpectedToken("(");
		} else if(token.getCategory().equals(TokenCategory.estIf)) {
			printProduction("Command", "'estIf' '(' Eb ')' Body IFr");
			setNextToken();
			
			if(token.getCategory().equals(TokenCategory.paramBegin)) {
				setNextToken();
				
				Eb();
				if(token.getCategory().equals(TokenCategory.paramEnd)) {
					setNextToken();
					Body();
					IFr();
				} else unexpectedToken(")");
			} else unexpectedToken("(");
		} else if(token.getCategory().equals(TokenCategory.instPrint)) {
			printProduction("Command", "'instPrint' '(' Ec ')' ';'");
			setNextToken();
			
			if(token.getCategory().equals(TokenCategory.paramBegin)) {
				setNextToken();
				
				Ec();
				if(token.getCategory().equals(TokenCategory.paramEnd)) {
					setNextToken();
					
					if(token.getCategory().equals(TokenCategory.lineEnd)) {
						setNextToken();
						
					}else unexpectedToken(";");
				} else unexpectedToken(")");
			}else unexpectedToken("(");
		} else if(token.getCategory().equals(TokenCategory.instRead)) {
			printProduction("Command", "'instRead' '(' IdL ')' ';'");
			setNextToken();
			
			if(token.getCategory().equals(TokenCategory.paramBegin)) {
				setNextToken();
				
				IdL();
				if(token.getCategory().equals(TokenCategory.paramEnd)) {
					setNextToken();
					
					if(token.getCategory().equals(TokenCategory.lineEnd)) {
						setNextToken();
						
					}else unexpectedToken(";");
				} else unexpectedToken(")");
			}else unexpectedToken("(");
		}else unexpectedToken("command");
	}
	
	private void IdL() {
		if(token.getCategory().equals(TokenCategory.id)) {
			printProduction("IdL", "'id' ArrayAccess IdLr");
			setNextToken();
			ArrayAccess();
			IdLr();
		} else unexpectedToken("id");
	}
	
	private void IdLr() {
		if(token.getCategory().equals(TokenCategory.commaSep)) {
			printProduction("IdLr", "',' 'id' ArrayAccess IdLr");
			setNextToken();
			
			if(token.getCategory().equals(TokenCategory.id)) {
				setNextToken();
				ArrayAccess();
				IdLr();
			} else unexpectedToken("id");
		} else printProduction("IdLr", "epsilon");
	}
	
	private void IFr() {
		if(token.getCategory().equals(TokenCategory.estElsif)) {
			printProduction("IFr", "'estElsif' '(' Eb ')' Body IFr");
			setNextToken();
			
			if(token.getCategory().equals(TokenCategory.paramBegin)) {
				setNextToken();
				
				Eb();
				if(token.getCategory().equals(TokenCategory.paramEnd)) {
					setNextToken();
					
					Body();
					IFr();
				} else unexpectedToken(")");
			} else unexpectedToken("(");
		} else if(token.getCategory().equals(TokenCategory.estElse)) {
			printProduction("IFr", "'estElse' Body");
			setNextToken();
			
			Body();
		} else printProduction("IFr", "epsilon");
	}
	
	private void Atr() {
		if(token.getCategory().equals(TokenCategory.id)) {
			printProduction("Atr", "'id' AtrR");
			setNextToken();
			
			AtrR();
		}else unexpectedToken("id");
	}
	
	private void AtrR() {
		if(token.getCategory().equals(TokenCategory.paramBegin)) {
			printProduction("AtrR", "FunCall");
			FunCall();
		} else {
			printProduction("AtrR", "ArrayAccess 'opEq' Ec");
			ArrayAccess();
			if(token.getCategory().equals(TokenCategory.opEq)) {
				setNextToken();
				
				Ec();
			}else unexpectedToken("=");	
		}
	}
	
	private void Decl() {
		printProduction("Decl", "Type LI");
		Type();
		LI();
	}
	
	private void Type() {
		if(token.getCategory().equals(TokenCategory.intType)
		   || token.getCategory().equals(TokenCategory.floatType)
		   || token.getCategory().equals(TokenCategory.charType)
		   || token.getCategory().equals(TokenCategory.boolType)
		   || token.getCategory().equals(TokenCategory.stringType)
		   || token.getCategory().equals(TokenCategory.arrayType)) {
			printProduction("Type", "'" + token.getCategory() + "'");
			setNextToken();
			
		} else unexpectedToken("type");
	}
	
	private void LI() {
		if(token.getCategory().equals(TokenCategory.id)) {
			printProduction("LI", "'id' ArrayOpt Inst LIr");
			setNextToken();

			ArrayOpt();
			Inst();
			LIr();
		} else unexpectedToken("id");
	}
	
	private void ArrayOpt() {
		if(token.getCategory().equals(TokenCategory.paramBegin)) {
			printProduction("ArrayOpt", "'(' Type ',' 'intCons' ')'");
			setNextToken();
			Type();
			if(token.getCategory().equals(TokenCategory.commaSep)) {
				setNextToken();
				if(token.getCategory().equals(TokenCategory.intCons)) {
					setNextToken();
					if(token.getCategory().equals(TokenCategory.paramEnd)) {
						setNextToken();
					}else unexpectedToken(")");
				}else unexpectedToken("integer constant");
			}else unexpectedToken(",");
		} else printProduction("ArrayOpt", "epsilon");
	}
	
	private void Inst() {
		if(token.getCategory().equals(TokenCategory.opEq)) {
			printProduction("Inst", "'opEq' Inr");
			setNextToken();
			
			Inr();
		} else printProduction("Inst", "epsilon");
	}
	
	private void Inr() {
		if(token.getCategory().equals(TokenCategory.arrayBegin)) {
			printProduction("Inr", "ArrayCons");
			ArrayCons();
		} else {
			printProduction("Inr", "Ec");
			Ec();
		}
	}
	
	private void ArrayCons() {
		if(token.getCategory().equals(TokenCategory.arrayBegin)) {
			printProduction("ArrayCons", "'[' LEc ']'");
			setNextToken();
			
			LEc();
			if(token.getCategory().equals(TokenCategory.arrayEnd)) {
				setNextToken();
				
			}else unexpectedToken("]");
		}else unexpectedToken("[");
	}
	
	private void Ec() {
		printProduction("Ec", "Fc Ecr");
		Fc();
		Ecr();
	}
	
	private void Fc() {
		if(token.getCategory().equals(TokenCategory.stringCons)) {
			printProduction("Fc", "'stringCons'");
			setNextToken();
			
		} else if(token.getCategory().equals(TokenCategory.charCons)) {
			printProduction("Fc", "'charCons'");
			setNextToken();
			
		} else {
			printProduction("Fc", "Eb");
			Eb();
		}
	}
	
	private void Eb() {
		printProduction("Eb", "Tb Ebr");
		Tb();
		Ebr();
	}
	
	private void Tb() {
		printProduction("Tb", "Fb Tbr");
		Fb();
		Tbr();
	}
	
	private void Fb() {
		if(token.getCategory().equals(TokenCategory.opLogNot)) {
			printProduction("Fb", "'opLogNot' Fb");
			setNextToken();
			
			Fb();
		} else if(token.getCategory().equals(TokenCategory.boolCons)) {
			printProduction("Fb", "'boolCons'");
			setNextToken();
			
		} else {
			printProduction("Fb", "Ra Fbr");
			Ra();
			Fbr();
		}
	}
	
	private void Ra() {
		printProduction("Ra", "Ea Rar");
		Ea();
		Rar();
	}
	
	private void Ea() {
		printProduction("Ea", "Ta Ear");
		Ta();
		Ear();
	}
	
	private void Ta() {
		printProduction("Ta", "Pa Tar");
		Pa();
		Tar();
	}
	
	private void Pa() {
		printProduction("Pa", "Fa Par");
		Fa();
		Par();
	}
	
	private void Par() {
		if(token.getCategory().equals(TokenCategory.opExp)) {
			printProduction("Par", "'opExp' Pa");
			setNextToken();
			
			Pa();
		}else printProduction("Par", "epsilon");
	}

	private void Fa() {
		if(token.getCategory().equals(TokenCategory.paramBegin)) {
			printProduction("Fa", "'(' Eb ')'");
			setNextToken();
			
			Eb();
			if(token.getCategory().equals(TokenCategory.paramEnd)) {
				setNextToken();
				
			}else unexpectedToken(")");
		} else if(token.getCategory().equals(TokenCategory.opUnNeg)) {
			printProduction("Fa", "'opUnNeg' Fa");
			setNextToken();
			
			Fa();
		} else if(token.getCategory().equals(TokenCategory.id)) {
			printProduction("Fa", "Id");
			Id();
		} else if(token.getCategory().equals(TokenCategory.intCons)) {
			printProduction("Fa", "'intCons'");
			setNextToken();
			
		} else if(token.getCategory().equals(TokenCategory.floatCons)) {
			printProduction("Fa", "'floatCons'");
			setNextToken();
			
		} else unexpectedToken("constant, id or expression");
	}
	
	private void Id() {
		if(token.getCategory().equals(TokenCategory.id)) {
			printProduction("Id", "'id' Idr");
			setNextToken();
			
			Idr();
		}else unexpectedToken("id");
	}
	
	private void Idr() {
		if(token.getCategory().equals(TokenCategory.paramBegin)) {
			printProduction("Idr", "FunCall");
			FunCall();
		} else {
			printProduction("Idr", "ArrayAccess");
			ArrayAccess();
		}
	}
	
	private void FunCall() {
		if(token.getCategory().equals(TokenCategory.paramBegin)) {
			printProduction("FunCall", "'(' LEc ')'");
			setNextToken();
			
			LEc();
			if(token.getCategory().equals(TokenCategory.paramEnd)) {
				setNextToken();
				
			} else unexpectedToken(")");
		}else unexpectedToken("(");
	}
	
	private void ArrayAccess() {
		if(token.getCategory().equals(TokenCategory.arrayBegin)) {
			printProduction("ArrayAccess", "'[' Ea ']'");
			setNextToken();
			
			Ea();
			if(token.getCategory().equals(TokenCategory.arrayEnd)) {
				setNextToken();
				
			} else unexpectedToken("]");
		}else printProduction("ArrayAccess", "epsilon");
	}
	
	private void Tar() {
		if(token.getCategory().equals(TokenCategory.opMult)) {
			printProduction("Tar", "'opMult' Pa Tar" );
			setNextToken();
			
			Pa();
			Tar();
		}else printProduction("Tar", "epsilon");
	}
	
	private void Ear() {
		if(token.getCategory().equals(TokenCategory.opAd)) {
			printProduction("Ear", "'opAd' Ta Ear");
			setNextToken();
			
			Ta();
			Ear();
		}else printProduction("Ear", "epsilon");
	}
	
	private void Rar() {
		if(token.getCategory().equals(TokenCategory.opRelEq)) {
			printProduction("Rar", "'opRelEq' Ea Rar");
			setNextToken();
			
			Ea();
			Rar();
		}else printProduction("Rar", "epsilon");
	}
	
	private void Fbr() {
		if(token.getCategory().equals(TokenCategory.opRelLtGt)) {
			printProduction("Fbr", "'opRelLtGt' Ra Fbr");
			setNextToken();
			
			Ra();
			Fbr();
		}else printProduction("Fbr", "epsilon");
	}
	
	private void Tbr() {
		if(token.getCategory().equals(TokenCategory.opLogAnd)) {
			printProduction("Tbr", "'opLogAnd' Fb Tbr");
			setNextToken();
			
			Fb();
			Tbr();
		}else printProduction("Tbr", "epsilon");
	}
	
	private void Ebr() {
		if(token.getCategory().equals(TokenCategory.opLogOr)) {
			printProduction("Ebr", "'opLogOr' Tb Ebr");
			setNextToken();
			
			Tb();
			Ebr();
		}else printProduction("Ebr", "epsilon");
	}
	
	private void Ecr() {
		if(token.getCategory().equals(TokenCategory.opConc)) {
			printProduction("Ecr", "'opConc' ConcOpt Fc Ecr");
			setNextToken();
			
			ConcOpt();
			Fc();
			Ecr();
		}else printProduction("Ecr", "epsilon");
	}
	
	private void ConcOpt() {
		if(token.getCategory().equals(TokenCategory.arrayBegin)) {
			printProduction("ConcOpt", "'[' 'floatCons' ']'");
			setNextToken();
			
			if(token.getCategory().equals(TokenCategory.floatCons)) {
				setNextToken();
				
				if(token.getCategory().equals(TokenCategory.arrayEnd)) {
					setNextToken();
					
				}else unexpectedToken("]");
			}else unexpectedToken("float constant");
		}else printProduction("ConcOpt", "epsilon");
	}
	
	private void LIr() {
		if(token.getCategory().equals(TokenCategory.commaSep)) {
			printProduction("LIr", "',' 'id' ArrayOpt Inst LIr");
			setNextToken();
			
			if(token.getCategory().equals(TokenCategory.id)) {
				setNextToken();

				ArrayOpt();
				Inst();
				LIr();
			} else unexpectedToken("id");
		} else if(token.getCategory().equals(TokenCategory.lineEnd)) {
			printProduction("LIr", "';'");
			setTokenAndCheckScope();
		}
	}
	
	private void setNextToken() {
		if(lexic.hasNextToken()) token = lexic.nextToken();
		else sendError("Unexpected end of file");
	}
	
	private void setTokenAndCheckScope() {
		if(scopeCounter > 0) {
			setNextToken();
		} else {
			if(lexic.hasNextToken()) token = lexic.nextToken();
		}
	}
	
	private void unexpectedToken(String expected) {
		if(lexic.getPreviousToken() != null)
			sendError("Expected " +expected+ " after "+lexic.getPreviousToken()+ " but got "+token);
		else
			sendError("Expected " +expected+ " at beginning of file but got "+token);
	}
	
	private void printProduction(String left, String right) {
		String format = "%10s%s = %s";

		ps.println(String.format(format, "", left, right));
	}
	
	private void sendError(String message) {
		ps.println("Error: "+ message);
	}
	
}