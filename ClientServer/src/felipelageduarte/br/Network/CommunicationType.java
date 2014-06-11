package felipelageduarte.br.Network;

/**
 * Enumeration para comunicação entre cliente e servidor
 * 
 * @author Felipe Duarte
 * @email felipelageduarte at gmail dot com
 */
public enum CommunicationType {
    Exit,
    ConnectionAccept,
    ConnectionNotAccept,
    ChallengeNumber,
    ChallengeAnswer,    
    PasswordRequired,    
    Password,    
    WrongPassword, 
    NickNameRequired,
    NickName,
    IncommingData,
    SendData,
    NewClient,
    ClientDown,
}
