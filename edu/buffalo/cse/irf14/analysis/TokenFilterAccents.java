package edu.buffalo.cse.irf14.analysis;

import java.util.Hashtable;




/**
 * Factory class for instantiating a given TokenFilter
 * @author nikhillo
 *
 */
public  class TokenFilterAccents extends TokenFilter implements Analyzer
{
	public TokenFilterAccents(TokenStream tokenStream)
	{
		super(tokenStream);
	}
public TokenFilter accentsProcessing(TokenStream ts)
{
	TokenFilter tfs=null;
	String str=null;
	if(ts.hasNext())
	{
		str=ts.next().getTermText();
		char[] accentbuffer= null;
		String replce=null;
		
		Hashtable<String, String> ht=new Hashtable<String, String>();
		
								ht.put("\u00C0","A");
								ht.put("\u00C1","A");
								ht.put("\u00C2","A");
								ht.put("\u00C3","A");
								ht.put("\u00C4","A");
								ht.put("\u00C5","A");
								ht.put("\u00C6","AE");
								ht.put("\u00C7","C");
								ht.put("\u00C8","E");
								ht.put("\u00C9","E");
								ht.put("\u00CA","E");
								ht.put("\u00CB","E");
								ht.put("\u00CC","I");
								ht.put("\u00CD","I");
								ht.put("\u00CE","I");
								ht.put("\u00CF","I");
								ht.put("\u0132","IJ");
								ht.put("\u00D0","D");
								ht.put("\u00D1","N");
								ht.put("\u00D2","O");
								ht.put("\u00D3","O");
								ht.put("\u00D4","O");
								ht.put("\u00D5","O");
								ht.put("\u00D6","O");
								ht.put("\u00D8","O");
								ht.put("\u0152","OE");
								ht.put("\u00DE","TH");
								ht.put("\u00D9","U");
								ht.put("\u00DA","U");
								ht.put("\u00DB","U");
								ht.put("\u00DC","U");
								ht.put("\u00DD","Y");
								ht.put("\u0178","Y");
								ht.put("\u00E0","a");
								ht.put("\u00E1","a");
								ht.put("\u00E2","a");
								ht.put("\u00E3","a");
								ht.put("\u00E4","a");
								ht.put("\u00E5","a");
								ht.put("\u00E6","ae");
								ht.put("\u00E7","c");
								ht.put("\u00E8","e");
								ht.put("\u00E9","e");
								ht.put("\u00EA","e");
								ht.put("\u00EB","e");
								ht.put("\u00EC","i");
								ht.put("\u00ED","i");
								ht.put("\u00EE","i");
								ht.put("\u00EF","i");
								ht.put("\u0133","ij");
								ht.put("\u00F0","d");
								ht.put("\u00F1","n");
								ht.put("\u00F2","o");
								ht.put("\u00F3","o");
								ht.put("\u00F4","o");
								ht.put("\u00F5","o");
								ht.put("\u00F6","o");
								ht.put("\u00F8","o");
								ht.put("\u0153","oe");
								ht.put("\u00DF","ss");
								ht.put("\u00FE","th");
								ht.put("\u00F9","u");
								ht.put("\u00FA","u");
								ht.put("\u00FB","u");
								ht.put("\u00FC","u");
								ht.put("\u00FD","y");
								ht.put("\u00FF","y");
								ht.put("\uFB00","ff");
								ht.put("\uFB01","fi");
								ht.put("\uFB02","fl");
								ht.put("\uFB03","ffi");
								ht.put("\uFB04","ffl");
								ht.put("\uFB05","ft");
								ht.put("\uFB06","st");
			accentbuffer =str.toCharArray();
			int i=0;
		for(char c:accentbuffer)
		{
			String sr=String.valueOf(c);
			if(ht.containsKey(sr))
			{
				repString stir=ht.get(sr);
					 for(int k=0;k<stir.length();k++)
					 {
			c=stir.charAt(k);
			accentbuffer[i]=c;
			i++;
			}
			else
			i++;
				
		}
		
		str=str.trim();
	}
	
	tfs =new TokenFilterAccents(ts);
	return tfs;
}

}
