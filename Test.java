import edu.duke.*;
/**
 * Write a description of Test here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Test {
    public void tryKeyLength() {
        FileResource fr = new FileResource();
        String message = fr.asString();
        VigenereBreaker vb = new VigenereBreaker();
        int[] key = vb.tryKeyLength(message, 4, 'e');
        for(int i=0; i<key.length; i++){
            System.out.print(key[i] + ", ");
        }
    }   
}
