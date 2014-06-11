package felipelageduarte.br.View;

import felipelageduarte.br.Core.ClientConfiguration;
import felipelageduarte.br.Core.ServerConfiguration;
import felipelageduarte.br.Network.ClientServer;
import felipelageduarte.br.Network.MessagePojo;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JOptionPane;

public class ClientServerView extends javax.swing.JFrame implements Observer {

    protected int Port = 4000;
    protected boolean conectado;
    private ClientServer clientServer;

    public ClientServerView(ClientServer clientServer) {
        initComponents();
        serverIPTextField.setText(localhost());
        this.clientServer = clientServer;
        clientServer.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        MessagePojo message = (MessagePojo) arg;
        switch (message.getReason()) {
            case ClientDown:
                if (clientServer.isServerRunning()) {
                    updateClientListInterface(clientServer.getClientList());
                } else if (clientServer.isClientRunning()){
                    clientStopped();
                }
            case ConnectionAccept:
                if (clientServer.isServerRunning()) {
                    updateClientListInterface(clientServer.getClientList());
                } 
                break;
            default:
                break;
        }
    }

    public void enableClientIntreface(boolean enable) {
        this.clientConnectButton.setEnabled(enable);
        this.clientDisconnectButton.setEnabled(enable);
        this.clientIPTextField.setEnabled(enable);
        this.clientPortSpinner.setEnabled(enable);
        this.clientNickNameTextField.setEnabled(enable);
        this.clientPasswordField.setEnabled(enable);
    }

    public void enableServerIntreface(boolean enable) {
        this.serverConnectButton.setEnabled(enable);
        this.serverDisconnectButton.setEnabled(enable);
        this.serverIPTextField.setEditable(enable);
        this.serverPortSpinner.setEnabled(enable);
        this.serverPasswordField.setEditable(enable);
        this.serverPasswordCheckBox.setEnabled(enable);
    }

    public void clientStoppedAndAlert(String titleBar, String msg) {
        JOptionPane.showMessageDialog(null, msg, titleBar, JOptionPane.WARNING_MESSAGE);
        clientStopped();
    }

    public void clientStopped() {
        enableClientIntreface(true);
        enableServerIntreface(true);

        this.clientDisconnectButton.setEnabled(false);
        this.serverDisconnectButton.setEnabled(false);
        this.conectado = false;
        this.clientStatusLabel.setText("Disconectado");
        this.clientStatusLabel.setForeground(new java.awt.Color(255, 0, 0));
    }

    public void clientConnected() {
        enableClientIntreface(false);
        enableServerIntreface(false);

        this.conectado = true;
        this.clientDisconnectButton.setEnabled(true);
        this.clientStatusLabel.setForeground(new java.awt.Color(255, 255, 0));
        this.clientStatusLabel.setText("Conectando...");
    }

    public void clientConnecting() {
        enableClientIntreface(false);
        enableServerIntreface(false);

        this.clientDisconnectButton.setEnabled(true);
        this.clientStatusLabel.setForeground(new java.awt.Color(255, 255, 0));
        this.clientStatusLabel.setText("Conectando...");
    }

    public void serverStoppedAndAlert(String titleBar, String msg) {
        JOptionPane.showMessageDialog(null, msg, titleBar, JOptionPane.WARNING_MESSAGE);
        serverStopped();
    }

    public void serverStopped() {
        enableClientIntreface(true);
        enableServerIntreface(true);

        this.clientDisconnectButton.setEnabled(false);
        this.serverDisconnectButton.setEnabled(false);
        this.conectado = false;
        this.serverStatusLabel.setText("Fora do Ar");
        this.serverStatusLabel.setForeground(new java.awt.Color(255, 0, 0));
    }

    public void serverRunning() {
        enableClientIntreface(false);
        enableServerIntreface(false);

        this.conectado = true;
        this.serverDisconnectButton.setEnabled(true);
        this.serverStatusLabel.setForeground(new java.awt.Color(0, 255, 0));
        this.serverStatusLabel.setText("No Ar");
    }

    public void serverConnecting() {
        enableClientIntreface(false);
        enableServerIntreface(false);

        this.serverDisconnectButton.setEnabled(true);
        this.serverStatusLabel.setForeground(new java.awt.Color(255, 255, 0));
        this.serverStatusLabel.setText("Iniciando...");
    }

    private String localhost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return new String("0.0.0.0");
        }
    }

    public void clientConected() {
        this.clientStatusLabel.setForeground(new java.awt.Color(0, 255, 0));
        this.clientStatusLabel.setText("Conectado");
    }

    public void updateClientListInterface(ArrayList<String> list) {
        String clientList = "";
        for (String client : list) {
            clientList += client + "\n";
        }
        serverClientListTextArea.setText(clientList);
    }

    private void Fechar() {
        /**/
         this.setVisible(false);
        /*/
        clientServer.shutdown();
        while (!clientServer.isStopped()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }
        System.exit(0);
        /**/
    }

    private void serverConnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serverConnectButtonActionPerformed
        if (!conectado) {
            serverConnecting();
            ServerConfiguration config = new ServerConfiguration((Integer) this.serverPortSpinner.getValue());
            config.setEnableClientEdition(serverClientEditCheckBox.isSelected());
            if (serverPasswordCheckBox.isSelected()) {
                config.setPassword(serverPasswordField.getText());
            }
            config.setConfirmConnection(serverConfirmConectionCheckBox.isSelected());
            if (clientServer.newServer(config)) {
                serverRunning();
            } else {
                serverStopped();
            }
        }
    }//GEN-LAST:event_serverConnectButtonActionPerformed

    private void serverDisconnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serverDisconnectButtonActionPerformed
        if (conectado) {
            clientServer.shutdownServer();
            serverStopped();
        }
    }//GEN-LAST:event_serverDisconnectButtonActionPerformed

    private void serverCloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serverCloseButtonActionPerformed
        Fechar();
    }//GEN-LAST:event_serverCloseButtonActionPerformed

    private void clientConnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientConnectButtonActionPerformed
        if (!conectado) {
            clientConnecting();
            ClientConfiguration config = new ClientConfiguration(clientPasswordField.getText(),
                    clientNickNameTextField.getText().trim(),
                    clientIPTextField.getText(),
                    (Integer) clientPortSpinner.getValue());
            if (clientServer.newClient(config)) {
                clientConected();
            } else {
                clientStopped();
            }
        }
    }//GEN-LAST:event_clientConnectButtonActionPerformed

    private void clientDisconnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientDisconnectButtonActionPerformed
        if (clientServer.shutdownClient()) {
            clientStopped();
        }
    }//GEN-LAST:event_clientDisconnectButtonActionPerformed

    private void clientCloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientCloseButtonActionPerformed
        Fechar();
    }//GEN-LAST:event_clientCloseButtonActionPerformed

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        applicationTabbedPane = new javax.swing.JTabbedPane();
        serverPanel = new javax.swing.JPanel();
        serverPortLabel = new javax.swing.JLabel();
        serverIPLabel = new javax.swing.JLabel();
        serverIPTextField = new javax.swing.JTextField();
        serverPortSpinner = new javax.swing.JSpinner();
        serverStatusLabel = new javax.swing.JLabel();
        serverClientListScrollPane = new javax.swing.JScrollPane();
        serverClientListTextArea = new javax.swing.JTextArea();
        serverConnectButton = new javax.swing.JButton();
        serverDisconnectButton = new javax.swing.JButton();
        serverCloseButton = new javax.swing.JButton();
        serverPasswordLabel = new javax.swing.JLabel();
        serverPasswordField = new javax.swing.JPasswordField();
        serverPasswordCheckBox = new javax.swing.JCheckBox();
        serverClientEditCheckBox = new javax.swing.JCheckBox();
        serverConfirmConectionCheckBox = new javax.swing.JCheckBox();
        clientPanel = new javax.swing.JPanel();
        clientIPLabel = new javax.swing.JLabel();
        clientIPTextField = new javax.swing.JTextField();
        clientPortLabel = new javax.swing.JLabel();
        clientPortSpinner = new javax.swing.JSpinner();
        clientStatusLabel = new javax.swing.JLabel();
        clientConnectButton = new javax.swing.JButton();
        clientDisconnectButton = new javax.swing.JButton();
        clientCloseButton = new javax.swing.JButton();
        clientPasswordLabel = new javax.swing.JLabel();
        clientPasswordField = new javax.swing.JPasswordField();
        clientNickNameLabel = new javax.swing.JLabel();
        clientNickNameTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Client Server Application");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        applicationTabbedPane.setName("applicationTabbedPane"); // NOI18N

        serverPanel.setName("serverPanel"); // NOI18N

        serverPortLabel.setText("Porta :");
        serverPortLabel.setName("serverPortLabel"); // NOI18N

        serverIPLabel.setText("IP : ");
        serverIPLabel.setName("serverIPLabel"); // NOI18N

        serverIPTextField.setBackground(new java.awt.Color(204, 204, 204));
        serverIPTextField.setEditable(false);
        serverIPTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        serverIPTextField.setText("127.0.0.1");
        serverIPTextField.setName("serverIPTextField"); // NOI18N

        serverPortSpinner.setModel(new javax.swing.SpinnerNumberModel(8080, 1050, 9999, 1));
        serverPortSpinner.setName("serverPortSpinner"); // NOI18N

        serverStatusLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        serverStatusLabel.setForeground(new java.awt.Color(255, 0, 0));
        serverStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        serverStatusLabel.setText("Fora do Ar");
        serverStatusLabel.setName("serverStatusLabel"); // NOI18N

        serverClientListScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Lista Clientes Conectados"));
        serverClientListScrollPane.setName("serverClientListScrollPane"); // NOI18N

        serverClientListTextArea.setBackground(new java.awt.Color(204, 204, 204));
        serverClientListTextArea.setColumns(20);
        serverClientListTextArea.setEditable(false);
        serverClientListTextArea.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        serverClientListTextArea.setRows(5);
        serverClientListTextArea.setName("serverClientListTextArea"); // NOI18N
        serverClientListScrollPane.setViewportView(serverClientListTextArea);

        serverConnectButton.setText("Conectar");
        serverConnectButton.setMaximumSize(new java.awt.Dimension(93, 23));
        serverConnectButton.setMinimumSize(new java.awt.Dimension(93, 23));
        serverConnectButton.setName("serverConnectButton"); // NOI18N
        serverConnectButton.setPreferredSize(new java.awt.Dimension(93, 23));
        serverConnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serverConnectButtonActionPerformed(evt);
            }
        });

        serverDisconnectButton.setText("Desconectar");
        serverDisconnectButton.setName("serverDisconnectButton"); // NOI18N
        serverDisconnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serverDisconnectButtonActionPerformed(evt);
            }
        });

        serverCloseButton.setText("Fechar");
        serverCloseButton.setMaximumSize(new java.awt.Dimension(93, 23));
        serverCloseButton.setMinimumSize(new java.awt.Dimension(93, 23));
        serverCloseButton.setName("serverCloseButton"); // NOI18N
        serverCloseButton.setPreferredSize(new java.awt.Dimension(93, 23));
        serverCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serverCloseButtonActionPerformed(evt);
            }
        });

        serverPasswordLabel.setText("Password:");
        serverPasswordLabel.setName("serverPasswordLabel"); // NOI18N

        serverPasswordField.setName("serverPasswordField"); // NOI18N
        serverPasswordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                serverPasswordFieldKeyReleased(evt);
            }
        });

        serverPasswordCheckBox.setText("Habilitar");
        serverPasswordCheckBox.setName("serverPasswordCheckBox"); // NOI18N

        serverClientEditCheckBox.setText("Habilitar Edição");
        serverClientEditCheckBox.setName("serverClientEditCheckBox"); // NOI18N

        serverConfirmConectionCheckBox.setText("Confirmar a Conexão");
        serverConfirmConectionCheckBox.setName("serverConfirmConectionCheckBox"); // NOI18N

        javax.swing.GroupLayout serverPanelLayout = new javax.swing.GroupLayout(serverPanel);
        serverPanel.setLayout(serverPanelLayout);
        serverPanelLayout.setHorizontalGroup(
            serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(serverClientListScrollPane)
                    .addGroup(serverPanelLayout.createSequentialGroup()
                        .addComponent(serverIPLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(serverIPTextField)
                        .addGap(18, 18, 18)
                        .addComponent(serverPortLabel)
                        .addGap(8, 8, 8)
                        .addComponent(serverPortSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(serverPanelLayout.createSequentialGroup()
                        .addComponent(serverPasswordLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(serverPasswordField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(serverPasswordCheckBox))
                    .addGroup(serverPanelLayout.createSequentialGroup()
                        .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(serverStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(serverPanelLayout.createSequentialGroup()
                                .addComponent(serverConnectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(serverDisconnectButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(serverCloseButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(serverPanelLayout.createSequentialGroup()
                                .addComponent(serverClientEditCheckBox)
                                .addGap(18, 18, 18)
                                .addComponent(serverConfirmConectionCheckBox)))
                        .addGap(0, 3, Short.MAX_VALUE)))
                .addContainerGap())
        );
        serverPanelLayout.setVerticalGroup(
            serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(serverStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(serverIPLabel)
                    .addComponent(serverIPTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(serverPortLabel)
                    .addComponent(serverPortSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(serverPasswordLabel)
                    .addComponent(serverPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(serverPasswordCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(serverClientEditCheckBox)
                    .addComponent(serverConfirmConectionCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(serverClientListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(serverCloseButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(serverDisconnectButton)
                    .addComponent(serverConnectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        applicationTabbedPane.addTab("Server", serverPanel);

        clientPanel.setName("clientPanel"); // NOI18N

        clientIPLabel.setText("IP : ");
        clientIPLabel.setName("clientIPLabel"); // NOI18N

        clientIPTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        clientIPTextField.setText("127.0.0.1");
        clientIPTextField.setName("clientIPTextField"); // NOI18N

        clientPortLabel.setText("Porta :");
        clientPortLabel.setName("clientPortLabel"); // NOI18N

        clientPortSpinner.setModel(new javax.swing.SpinnerNumberModel(8080, 1025, 9999, 1));
        clientPortSpinner.setName("clientPortSpinner"); // NOI18N

        clientStatusLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        clientStatusLabel.setForeground(new java.awt.Color(255, 0, 0));
        clientStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        clientStatusLabel.setText("Desconectado");
        clientStatusLabel.setName("clientStatusLabel"); // NOI18N

        clientConnectButton.setText("Conectar");
        clientConnectButton.setMaximumSize(new java.awt.Dimension(93, 23));
        clientConnectButton.setMinimumSize(new java.awt.Dimension(93, 23));
        clientConnectButton.setName("clientConnectButton"); // NOI18N
        clientConnectButton.setPreferredSize(new java.awt.Dimension(93, 23));
        clientConnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientConnectButtonActionPerformed(evt);
            }
        });

        clientDisconnectButton.setText("Desconectar");
        clientDisconnectButton.setName("clientDisconnectButton"); // NOI18N
        clientDisconnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientDisconnectButtonActionPerformed(evt);
            }
        });

        clientCloseButton.setText("Fechar");
        clientCloseButton.setMaximumSize(new java.awt.Dimension(93, 23));
        clientCloseButton.setMinimumSize(new java.awt.Dimension(93, 23));
        clientCloseButton.setName("clientCloseButton"); // NOI18N
        clientCloseButton.setPreferredSize(new java.awt.Dimension(93, 23));
        clientCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientCloseButtonActionPerformed(evt);
            }
        });

        clientPasswordLabel.setText("Password:");
        clientPasswordLabel.setName("clientPasswordLabel"); // NOI18N

        clientPasswordField.setName("clientPasswordField"); // NOI18N

        clientNickNameLabel.setText("Apelido:");
        clientNickNameLabel.setName("clientNickNameLabel"); // NOI18N

        clientNickNameTextField.setName("clientNickNameTextField"); // NOI18N

        javax.swing.GroupLayout clientPanelLayout = new javax.swing.GroupLayout(clientPanel);
        clientPanel.setLayout(clientPanelLayout);
        clientPanelLayout.setHorizontalGroup(
            clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, clientPanelLayout.createSequentialGroup()
                        .addComponent(clientIPLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clientIPTextField)
                        .addGap(18, 18, 18)
                        .addComponent(clientPortLabel)
                        .addGap(8, 8, 8)
                        .addComponent(clientPortSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(clientPanelLayout.createSequentialGroup()
                        .addComponent(clientPasswordLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clientPasswordField))
                    .addGroup(clientPanelLayout.createSequentialGroup()
                        .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(clientStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(clientPanelLayout.createSequentialGroup()
                                .addComponent(clientConnectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clientDisconnectButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clientCloseButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 3, Short.MAX_VALUE))
                    .addGroup(clientPanelLayout.createSequentialGroup()
                        .addComponent(clientNickNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clientNickNameTextField)))
                .addContainerGap())
        );
        clientPanelLayout.setVerticalGroup(
            clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(clientStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clientIPLabel)
                    .addComponent(clientIPTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clientPortLabel)
                    .addComponent(clientPortSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clientNickNameLabel)
                    .addComponent(clientNickNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clientPasswordLabel)
                    .addComponent(clientPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 184, Short.MAX_VALUE)
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clientCloseButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clientDisconnectButton)
                    .addComponent(clientConnectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        applicationTabbedPane.addTab("Client", clientPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(applicationTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(applicationTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void serverPasswordFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_serverPasswordFieldKeyReleased
        if (serverPasswordField.getText().isEmpty()) {
            serverPasswordCheckBox.setSelected(false);
        } else {
            serverPasswordCheckBox.setSelected(true);
        }
    }//GEN-LAST:event_serverPasswordFieldKeyReleased

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Fechar();
    }//GEN-LAST:event_formWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane applicationTabbedPane;
    private javax.swing.JButton clientCloseButton;
    private javax.swing.JButton clientConnectButton;
    private javax.swing.JButton clientDisconnectButton;
    private javax.swing.JLabel clientIPLabel;
    private javax.swing.JTextField clientIPTextField;
    private javax.swing.JLabel clientNickNameLabel;
    private javax.swing.JTextField clientNickNameTextField;
    private javax.swing.JPanel clientPanel;
    private javax.swing.JPasswordField clientPasswordField;
    private javax.swing.JLabel clientPasswordLabel;
    private javax.swing.JLabel clientPortLabel;
    private javax.swing.JSpinner clientPortSpinner;
    private javax.swing.JLabel clientStatusLabel;
    private javax.swing.JCheckBox serverClientEditCheckBox;
    private javax.swing.JScrollPane serverClientListScrollPane;
    private javax.swing.JTextArea serverClientListTextArea;
    private javax.swing.JButton serverCloseButton;
    private javax.swing.JCheckBox serverConfirmConectionCheckBox;
    private javax.swing.JButton serverConnectButton;
    private javax.swing.JButton serverDisconnectButton;
    private javax.swing.JLabel serverIPLabel;
    private javax.swing.JTextField serverIPTextField;
    private javax.swing.JPanel serverPanel;
    private javax.swing.JCheckBox serverPasswordCheckBox;
    private javax.swing.JPasswordField serverPasswordField;
    private javax.swing.JLabel serverPasswordLabel;
    private javax.swing.JLabel serverPortLabel;
    private javax.swing.JSpinner serverPortSpinner;
    private javax.swing.JLabel serverStatusLabel;
    // End of variables declaration//GEN-END:variables
}
