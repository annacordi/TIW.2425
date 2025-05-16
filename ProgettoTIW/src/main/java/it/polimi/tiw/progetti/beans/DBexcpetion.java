package it.polimi.tiw.progetti.beans;

import java.sql.SQLException;

/**
 * Un'eccezione relativa a un errore nelle interazioni col DBMS è "wrappata" su un eccezione "DbErrorException"
 * al fine di restituire un messaggio più conforme alle esigenze dell'applicazione. 
 */
public class DBexcpetion extends Exception
	{
		private static final long serialVersionUID = 1L;
		
		private String message;
		
		public DBexcpetion(String affected_value, SQLException father_exception)
			{
				String template = affected_value + " ";
				
				switch (father_exception.getErrorCode())
					{
						case 2627,1062:
							this.message = template + " e' gia' presente";
							break;
						case 0:
							this.message = template + " non esiste";
							break;
						case 1452:
							this.message = template + " non e' associabile";
							break;
						default:
							this.message = "si e' verificato un errore imprevisto nel database";
					}
			}
		public DBexcpetion()
			{
				this.message = "si e' verificato un errore imprevisto nel database";
			}
		public DBexcpetion (String message)
			{
				this.message = message;
			}
		public String getMessage ()
			{
				return this.message;
			}
		public String toString()
			{
				return (this.message);
			}
	}