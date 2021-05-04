package brosbros;

import javax.imageio.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Write a description of class GetImageBytes here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class GetImageBytes{

    public static void main(String[] args) throws Exception{
        if (args.length < 2){
            System.out.println("Usage: <file in> <file out>");
            System.exit(0);
        }
        BufferedImage img = ImageIO.read(new File(args[0]));
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(args[1]));
        HashMap<Integer,Integer> hm = new HashMap<>();
        for (int y = 0; y < img.getHeight(); y++){
            for (int x = 0; x < img.getWidth(); x++){
                int p = img.getRGB(x, y);
                Integer old = hm.get(p);
                if (old == null) old = 0;
                hm.put(p,old+1);
//                System.out.print(p+" ");
                if (p == -16777216) out.write(1); //black
                else if (p == -256 || p == -3584) out.write(2); //Yellow (ladder)
                else if (p == -65536 || p == -1237980) out.write(3); //Red (lava)
                else out.write(0);
                //out.write(
            }
        }
        out.flush();
        out.close();
        System.out.println();
        Vector<Integer> v = new Vector<>();
        for (Integer i : hm.keySet()) v.addElement(i);
        Collections.sort(v,new Comparator<Integer>(){
            public int compare(Integer i1, Integer i2){
                Integer c1 = hm.get(i1);
                Integer c2 = hm.get(i2);
                return c1.compareTo(c2);
            }
        });
        for (Integer i : v) System.out.println(i+"\t"+hm.get(i));
        System.out.println("Finished");
        System.out.println(img.getWidth()+" "+img.getHeight());
        System.exit(0);
    }
}
