package ClientServer.igeom.usp.br.Core;

import ClientServer.igeom.usp.br.Protocol.CommunicationType;
import java.io.Serializable;

/**
 * Classe Pojo para comunicação entre cliente e servidor
 *
 * @author Felipe Duarte @email felipelageduarte at gmail dot com
 */
public class MessagePojo implements Serializable {

    Integer whoSending; // ponteiro para quem esta enviando a mensagem
    CommunicationType reason; // razão da mensagem
    Object obj; // objeto que sera transportado pela rede

    /**
     * Construtor
     *
     * @param reason Tipo de communicação que sera transportado
     * @param obj objeto que sera transportado
     */
    public MessagePojo(Integer whoSending, CommunicationType reason, Object obj) {
        this.whoSending = null;
        this.reason = reason;
        this.obj = obj;
    }

    /**
     *
     * @return objeto da mensagem
     */
    public Object getObj() {
        return obj;
    }

    /**
     *
     * @param obj objeto da mensagem
     */
    public void setObj(Object obj) {
        this.obj = obj;
    }

    /**
     *
     * @return razao da mensagem
     */
    public CommunicationType getReason() {
        return reason;
    }

    /**
     *
     * @param reason razao da mesagem
     */
    public void setReason(CommunicationType reason) {
        this.reason = reason;
    }

    /**
     * Metodo que informa quem criou a mensagem
     *
     * @return Ponteiro para o objeto que criou a mensagem
     */
    int getWhoSend() {
        return whoSending;
    }
}
