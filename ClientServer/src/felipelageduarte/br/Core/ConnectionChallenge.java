
package felipelageduarte.br.Core;

import java.util.Random;

/**
 * Esta Classe tem como objetivo realizar um desafio de conexão em que é
 * gerado um numero randomico e calculado uma função cúbica dado este 
 * número, caso o cliente que esteja tentando a conexão responda corretamente
 * o desafio, a conexão então é estabelecida, caso contrario o servidor 
 * derruba a conexao
 * 
 * @author Felipe Duarte
 * @email felipelageduarte at gmail dot com
 */

public class ConnectionChallenge {
    /**
     * funcao que dado um numero desafio calcula a resposta
     * @param number para calculo da funcao resposta
     * @return resposta do desafio calculado
     */
    public static double calc(double number){
        return (Math.pow(number, 3) + 3*number - Math.pow(number, 2) - 4*Math.sqrt(number));
    }
    
    /**
     * gera um numero desafio aleatorio
     * @return numero de precisão dupla aleatorio
     */
    public static double getNumber(){
        return new Random().nextDouble();
    }
}
