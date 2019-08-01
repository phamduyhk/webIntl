import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Assert;
import java.net.*;
import javax.net.ssl.*;

public class PersonRelAnalyzer {
	/**
     * CaboChaの係り受け解析を試す例
     */
	
	
	public static String getPhrases(Chunk headChunk, String joshi) {
		String caseStr = "";
		List<Chunk> dependents = headChunk.getDependents();
		for (Chunk dependent: dependents) {
        	Morpheme headMorph = dependent.getHeadMorpheme();
        	if(headMorph.getPos().equals("助詞") &&
        			headMorph.getSurface().equals(joshi))
        	{
                for (int i = 0; i < dependent.size(); i++) {
                    caseStr += dependent.get(i).getSurface();
                }
//        		System.out.println("助詞の「の」がついた語句:"+caseStr);
        	}
        	else {
        		getPhrases(dependent, joshi);
        	}
        }
		return caseStr;
	}
	
	// 小課題0705-4
	/**
     * 与えられた文の集合から，表層格を抽出してみる
     * @param sentences 文の集合
     */
	public static void printCaseStr(List<Sentence> sentences) {
		 for (Sentence sentence : sentences) { // 文を1つずつ処理するループ
		     // 主辞（最後の文節）を取得
		     Chunk headChunk = sentence.getHeadChunk();
		     // 主辞に係る文節のリストを返す
		     List<Chunk> dependents = headChunk.getDependents();
		     for (Chunk dependent : dependents) {
		       // 文節dependentの主辞（最後の形態素）を取得
		       Morpheme headMorph = dependent.getHeadMorpheme();
		       // 助詞かどうか判定
		       if (headMorph.getPos().equals("助詞")) {
		         // 文節dependent内の助詞以外の形態素をつなげる
		         String caseStr = "";
		         for (int i = 0; i < dependent.size()-1; i++) {
		           caseStr += dependent.get(i).getSurface();
			 }
			 System.out.println(headMorph.getSurface()+"格: "+caseStr);
		       }
		     }
		 }
	}
	/**
     * 与えられたURLからHTML等のコンテンツを取得し，返す．
     * @param url 取得するコンテンツのURL
     * @param enc コンテンツの文字コード（UTF-8やEUC-JP, Shift_JISなど）
     * @return コンテンツ
     */
    public static String getWebContent(String url, String enc) {
	StringBuffer sb = new StringBuffer();
	try {
	    BufferedReader in = null;
	    if (url.startsWith("https")) {
		HttpsURLConnection conn = (HttpsURLConnection)new URL(url).openConnection();
		in = new BufferedReader(new InputStreamReader(conn.getInputStream(), enc));
	    } else {
		URLConnection conn = new URL(url).openConnection();
		in = new BufferedReader(new InputStreamReader(conn.getInputStream(), enc));
	    }
	    for (String line = in.readLine(); line != null; line = in.readLine()) {
		sb.append(line);
		sb.append("\n");
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return sb.toString();
    }
	/**
     * 文の中の人名を抽出する
     * @param sentences 文章
     * @return 人名のArrayList
     */
    public static List<String> getPersonName(List<Sentence> sentences) {
    	List<String> personNameFreq = new ArrayList<String>();
    	Set<String> personNameList = new HashSet<String>();
    	for (final Sentence sentence : sentences) { 
	        final Chunk headChunk = sentence.getHeadChunk(); 
	        final List<Chunk> dependents = headChunk.getDependents();
	        for (final Chunk dependent : dependents) {
	        	for(int i = 0; i<dependent.size();i++) {
	        		Morpheme item = dependent.get(i);
	        		// e.g: <形態素 表層="僕ら" 品詞="名詞,代名詞,一般,*" 活用形="*" 活用型="*" 原形="僕ら" 読み="ボクラ" 発音="ボクラ" />
	        		if(item.posStr.contains("名詞,固有名詞,人名")) {
	        			personNameList.add(item.getSurface());
	        		}
////	        		 特別なパターン
//	         	   if(i>0) {
//	     		   Morpheme preItem = dependent.get(i-1);
//	     		   if(item.posStr.contains("名詞,一般,*,*")&&
//	     				   preItem.posStr.equals("名詞,一般,*,*")) {
//	         		   System.out.println(preItem);
//	         		  System.out.println(preItem.getBaseform());
//	         	   }
//	     	   }
	        	}
	        }
    	}
    	for(final Sentence sentence:sentences) {
    		final Chunk headChunk = sentence.getHeadChunk(); 
	        final List<Chunk> dependents = headChunk.getDependents();
	        for (final Chunk dependent : dependents) {
	        	for(Morpheme mor : dependent) {
	        		System.out.println(mor
	        				);
	        		if(personNameList.contains(mor.getSurface())){
	        			personNameFreq.add(mor.getSurface());
	        		}
	        	}
	        }
    	}
    	System.out.println(personNameFreq);
    	return personNameFreq;
    }
    
	/**
     * 文の中の人名を抽出する
     * @param sentences 文章
     * @param countPersonFreq 上位n人の名前を標準出力に表示する
     * @return 人名のArrayList
     */
    
    //【課題１】
    public static void countPersonFreq(List<Sentence> sentences,int countPersonFreq) {
    	List<String> personNames = getPersonName(sentences);
    	Map<String, Integer> map = new HashMap<>();
    	for(String name:personNames) {
    		if (!map.containsKey(name)) {
    			map.put(name, 1);
    		}
    		else {
    			map.put(name,map.get(name)+1);
    		}
    	}
         Map<String, Integer> sortedMap = 
        	     map.entrySet().stream()
        	    .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
        	    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
        	                              (e1, e2) -> e1, LinkedHashMap::new));;
        int count = 0;
    	for (Entry<String, Integer> entry : sortedMap.entrySet()) {
    		count++;
    		if(count>countPersonFreq) {
    			break;
    		}
    		System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
    
    //【課題２】
    public static void getPredArgStructure(Sentence sentence) {
    	boolean printEndLine = false;
    	PredArgs args = sentence.getPredArgStruct();
    	System.out.println(args.getSurface());
//    	for(Chunk element:sentence) {
//    		int dependency = element.getDependency();
//    		if(dependency<0) {
//    			Morpheme headMorpheme = element.getHeadMorpheme();
//    			System.out.print(element.get(0).getBaseform());
////        		System.out.println(element);
////    			System.out.println(element.getDependents());
//    			List<Chunk> dependents = element.getDependents();
//    			for(Chunk dependent:dependents) {
//    				if(dependent.getHeadMorpheme().getPos().contains("助詞")&&
//    						dependent.getHeadMorpheme().getSurface().equals("が")) {
//    					   String caseStr = "";
//    				         for (int i = 0; i < dependent.size()-1; i++) {
//    				           caseStr += dependent.get(i).getSurface();
//    					 }
//    					System.out.print("(が格="+caseStr+")");
//    					printEndLine = true;
//    				}
//    				if(dependent.getHeadMorpheme().getPos().contains("助詞")&&
//    						dependent.getHeadMorpheme().getSurface().equals("に")) {
//    					 String caseStr = "";
//				         for (int i = 0; i < dependent.size()-1; i++) {
//				           caseStr += dependent.get(i).getSurface();
//					 }
//    					System.out.print("(に格="+caseStr+")");
//    					printEndLine = true;
//    				}
//    			}
//    		}
//    	}
    	if(printEndLine)
    		System.out.println();
    }
    
    //【小課題0705-2】
    public static String getTextFromUrl(String url) {
    	String html = getWebContent(url, "Shift_JIS"); 
        Pattern mainTextPat = Pattern.compile("<div class=\"main_text\">(.+?)</div>", Pattern.DOTALL | Pattern.MULTILINE); 
        String text = "";
        Matcher mainTextMat = mainTextPat.matcher(html); 
        if (mainTextMat.find()) {
            String mainText = mainTextMat.group(1);  // 本文を取り出す
            // 【小課題0705-1】 HTMLタグ全般にマッチする正規表現
            String strReg = "<[^>]*>";
            mainText = mainText.replaceAll(strReg, "");
            text = mainText.replace("\n", "").replace("\r", "");
        }
        return text;
    }
    
    public static void main(final String[] args) {
    	String url = "https://www.aozora.gr.jp/cards/000009/files/50717_36994.html"; // 「まだらのひも」のURL
    	String url2 = "https://www.aozora.gr.jp/cards/000022/files/4874_14008.html"; // [昔の女 三島霜川]
        String text = getTextFromUrl(url);
        String test_text = "ボブが彼に殴られた";

//        System.out.println("解析対象のテキスト: "+text);

        //【小課題0705-3】
        // 対象テキストを文に分割した上でCaboChaに渡し，解析結果を受け取る．
        final DepParser parser = new CaboCha();
//        final List<Sentence> sentences = parser.parseText(text);
        final List<Sentence> sentences = parser.parseText(text);
//        System.out.println("係り受け解析結果:\n" + sentences);
        
          
        for (final Sentence sentence : sentences) { 
            // 主辞（最後の文節）を取得
            final Chunk headChunk = sentence.getHeadChunk(); 
//            System.out.println(headChunk);
            // 主辞に係る文節のリストを返す
            final List<Chunk> dependents = headChunk.getDependents();
            for (final Chunk dependent : dependents) {
                // 文節dependentの主辞（最後の形態素）を取得
                final Morpheme headMorph = dependent.getHeadMorpheme();
                
                // 【小課題0705-4】 表層格を取得
//                if(headMorph.getPos().equals("助詞")) { 
//            		String caseStr = ""; 
//                    for (int i = 0; i < dependent.size()-1; i++) {
//                        caseStr += dependent.get(i).getSurface();
//                    }
//                    System.out.println(headMorph.getSurface()+ "格:" +caseStr);
//                }
                
                // 助詞の「は」かどうか判定するif文
//                if (headMorph.getPos().equals("助詞") && 
//                        headMorph.getSurface().equals("は")) 
//                {
//                    // 文節dependent内の助詞以外の形態素をつなげる
//                    String caseStr = "";
//                    for (int i = 0; i < dependent.size()-1; i++) {
//                        caseStr += dependent.get(i).getSurface();
//                    }
//                    System.out.println("主辞に係るハ格: "+caseStr);
//                }
                
//                // ヲ格
//                if (headMorph.getPos().equals("助詞") && 
//                        headMorph.getSurface().equals("を")) 
//                {
//                    // 文節dependent内の助詞以外の形態素をつなげる
//                    String caseStr = "";
//                    for (int i = 0; i < dependent.size()-1; i++) {
//                        caseStr += dependent.get(i).getSurface();
//                    }
//                    System.out.println("主辞に係るヲ格: "+caseStr);
//                }                      
            }
            
//            getPhrases(headChunk,"の");
        }
        
       //小課題0705-4
//        printCaseStr(sentences);
        
      //【小課題0705-5】人名のリストを抽出
        List<String> personNames = new ArrayList<String>();
    	personNames = getPersonName(sentences);
//    	System.out.println(personNames);
        
      	//【課題１】上位人名
      	countPersonFreq(sentences,10);
      	
      	//【課題２】
//      	for (Sentence sentence:sentences) {
//      		getPredArgStructure(sentence);
//      	}
    }
}