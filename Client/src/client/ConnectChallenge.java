
package client;

import java.util.Random;

public class ConnectChallenge {
    public static double calc(double number){
        return (Math.pow(number, 3) + 3*number - Math.pow(number, 2) - 4*Math.sqrt(number));
    }
    
    public static double getNumber(){
        return new Random().nextDouble();
    }
}
