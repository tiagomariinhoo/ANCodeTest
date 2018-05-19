package br.ufal.ic.ANCodeTest.token;

public class Token {
	
	private TokenCategory category;
	private int tokenLine;
	private int tokenColumn;
	private String value;
	
	public Token(String value, int tokenLine, int tokenColumn, TokenCategory category) {
		this.setCategory(category);
		this.setTokenLine(tokenLine);
		this.setTokenColumn(tokenColumn);
		if(category == TokenCategory.stringCons || category == TokenCategory.charCons) this.setValue(value.substring(1, value.length()-1));
		else this.setValue(value);
	}

	public TokenCategory getCategory() {
		return category;
	}

	public void setCategory(TokenCategory category) {
		this.category = category;
	}

	public int getTokenLine() {
		return tokenLine;
	}

	public void setTokenLine(int tokenLine) {
		this.tokenLine = tokenLine;
	}

	public int getTokenColumn() {
		return tokenColumn;
	}

	public void setTokenColumn(int tokenColumn) {
		this.tokenColumn = tokenColumn;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Token)) {
			return false;
		}
		final Token other = (Token) obj;
		if((this.value.equals(other.value)) && 
		   (this.category == other.category) && 
		   (this.tokenLine == other.tokenLine) && 
		   (this.tokenColumn == other.tokenColumn)) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		String fmt = "[%03d, %03d] (%04d, %10s) {%s}";
		return String.format(fmt, tokenLine+1, tokenColumn+1, category.ordinal(), category.toString(), value);
	}	

}
