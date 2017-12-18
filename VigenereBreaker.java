import java.util.*;
import edu.duke.*;
import java.io.*;
public class VigenereBreaker {
    public String sliceString(String message, int whichSlice, int totalSlices) {
        StringBuilder sb = new StringBuilder();
        for (int i = whichSlice; i < message.length(); i += totalSlices){
            sb.append(message.charAt(i));
        }
        return sb.toString();
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        CaesarCracker ck = new CaesarCracker(mostCommon);
        for(int i=0; i<klength; i++){
            String s = sliceString(encrypted, i, klength);
            int thisKey = ck.getKey(s);
            key[i] = thisKey;
            //System.out.println(thisKey);
        }
        return key;
    }
    
    public HashSet<String> readDictionary(FileResource fr){
        HashSet<String> set = new HashSet<String>();
        for(String line: fr.lines()){
            line = line.toLowerCase();
            set.add(line);
        }
        return set;
    }
    
    public int countWords(String message, HashSet<String> dictionary){
        int count = 0;
        for(String word: message.split("\\W+")){
            word = word.toLowerCase();
            if(dictionary.contains(word)){
                count++;
            }
        }
        return count;
    }
    
    public String breakForLanguage(String encrypted, HashSet<String> dictionary){
        char mostCommon = mostCommonCharIn(dictionary);
        String best = "";
        int larg = 0;
        int keyLength = 0;
        for(int i=1; i<=100; i++){
            int[] keys = tryKeyLength(encrypted, i, mostCommon);
            VigenereCipher vc = new VigenereCipher(keys);
            String decrypted  =vc.decrypt(encrypted); 
            int realWordCount = countWords(decrypted, dictionary);
            if(realWordCount > larg){
                larg = realWordCount;
                best = decrypted;
                keyLength = i;
            }
        }
        System.out.println("Count of Valid Words: " + larg);
        System.out.println("Key Length: " + keyLength);        
        return best;
    }
    
    public char mostCommonCharIn(HashSet<String> dictionary){
        HashMap<Character, Integer> hm = new HashMap<Character, Integer>();
        dictionary.stream().forEach((word) -> {
            word = word.toLowerCase();
            for(int i=0; i<word.length(); i++){
                char ch = word.charAt(i);
                if(hm.containsKey(ch)){
                    hm.put(ch, hm.get(ch)+1);
                }
                else {
                    hm.put(ch, 1);
                }
            }
        });
        int larg = 0;
        char common = 'e';
        for(char ch: hm.keySet()){
            if(hm.get(ch) > larg){
                larg = hm.get(ch);
                common = ch;
            }
        }
        return common;
    }
    
    public void breakVigenereKnownLengthEnglishOnly () {
        FileResource fr = new FileResource();
        String message = fr.asString();
        int[] key = tryKeyLength(message, 4, 'e');
        VigenereCipher vc = new VigenereCipher(key);
        String decrypted  =vc.decrypt(message);
        System.out.println(decrypted);
    }
    
    public void breakVigenereEnglishOnly () {
        FileResource fr = new FileResource();
        String message = fr.asString();
        System.out.println("File Read");
        
        FileResource dictFile = new FileResource();
        HashSet<String> dict = readDictionary(dictFile);
        System.out.println("Dictionary Read and compiled");
        
        String decrypted = breakForLanguage(message, dict);
        
        System.out.println("Decrypted Done and the Message is: ");
        System.out.println(decrypted);
    }
    
    public String breakForAllLangs(String encrypted, HashMap<String, HashSet<String>> languages){
        int larg = 0;
        String best = "";
        String languageIdentified = "";
        for(String language: languages.keySet()){
            HashSet<String> dict = languages.get(language);
            String decrypted = breakForLanguage(encrypted, dict);
            int realWordCount = countWords(decrypted, dict);
            if(realWordCount > larg){
                larg = realWordCount;
                best = decrypted;
                languageIdentified = language;
            }
        }   
        System.out.println("Language Identified :" + languageIdentified);
        System.out.println("Following is the decryted message: ");
        return best;
    }
    
    public void breakVigenere () {
        FileResource fr = new FileResource();
        String message = fr.asString();
        System.out.println("File Read");
        
        HashMap<String, HashSet<String>> dicts = new HashMap<String, HashSet<String>>();
        DirectoryResource dr = new DirectoryResource();
        for(File f: dr.selectedFiles()){
            FileResource dictFile = new FileResource(f);
            HashSet<String> dict = readDictionary(dictFile);
            dicts.put(f.getName(), dict);
            System.out.println("Dictionary Read and compiled");
        }
        String decrypted = breakForAllLangs(message, dicts);
        System.out.println("Decrypted Done and the Message is: ");
        System.out.println(decrypted);
    }
}
