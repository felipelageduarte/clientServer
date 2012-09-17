package Interface;

import Client.Client;
import Server.Server;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JTextArea;

public class Interface extends javax.swing.JFrame {

    private static Interface SINGLETON = null;
    protected int Port = 4000;
    protected Server server = null;
    protected Client client = null;
    //protected Client2 client = null;
    protected boolean Conectado;

    private Interface() {
        initComponents();
        serverDisconnectButton.setEnabled(false);
        serverConnectButton.setEnabled(true);
        clientDisconnectButton.setEnabled(false);
        clientConnectButton.setEnabled(true);
        Conectado = false;
        serverIPTextField.setText(localhost());
        //this.client = new Client();
        this.server = new Server();
    }

    public static Interface getInstance() {
        if (SINGLETON == null) {
            SINGLETON = new Interface();
        }
        return SINGLETON;
    }

    public JTextArea getServerClientListTextArea() {
        return serverClientListTextArea;
    }

    public void setServerClientListTextArea(JTextArea serverClientListTextArea) {
        this.serverClientListTextArea = serverClientListTextArea;
    }

    private String localhost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return new String("0.0.0.0");
        }
    }

    private void enableClientIntreface(boolean enable) {
        this.clientCancelButton.setEnabled(enable);
        this.clientConnectButton.setEnabled(enable);
        this.clientDisconnectButton.setEnabled(enable);
        this.clientIPTextField.setEnabled(enable);
        this.clientPortSpinner.setEnabled(enable);
    }

    private void enableServerIntreface(boolean enable) {
        this.serverCancelButton.setEnabled(enable);
        this.serverConnectButton.setEnabled(enable);
        this.serverDisconnectButton.setEnabled(enable);
        this.serverIPTextField.setEnabled(enable);
        this.serverPortSpinner.setEnabled(enable);
    }

    public void serverStoped() {
        if (Conectado) {
            serverDisconnectButton.setEnabled(false);
            serverConnectButton.setEnabled(true);
            Conectado = false;
            serverStatusLabel.setText("Fora do Ar");
            serverStatusLabel.setForeground(new java.awt.Color(255, 0, 0));
            this.enableClientIntreface(true);
        }
    }

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
        serverCancelButton = new javax.swing.JButton();
        serverPasswordLabel = new javax.swing.JLabel();
        serverPasswordField = new javax.swing.JPasswordField();
        serverPasswordCheckBox = new javax.swing.JCheckBox();
        serverClientEditCheckBox = new javax.swing.JCheckBox();
        clientPanel = new javax.swing.JPanel();
        clientIPLabel = new javax.swing.JLabel();
        clientIPTextField = new javax.swing.JTextField();
        clientPortLabel = new javax.swing.JLabel();
        clientPortSpinner = new javax.swing.JSpinner();
        clientStatusLabel = new javax.swing.JLabel();
        clientConnectButton = new javax.swing.JButton();
        clientDisconnectButton = new javax.swing.JButton();
        clientCancelButton = new javax.swing.JButton();
        clientPasswordLabel = new javax.swing.JLabel();
        clientPasswordField = new javax.swing.JPasswordField();
        jMenuBar = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Client Server Application");
        setResizable(false);

        applicationTabbedPane.setName("applicationTabbedPane");

        serverPanel.setName("serverPanel");

        serverPortLabel.setText("Porta :");
        serverPortLabel.setName("serverPortLabel"); // NOI18N

        serverIPLabel.setText("IP : ");
        serverIPLabel.setName("serverIPLabel"); // NOI18N

        serverIPTextField.setBackground(new java.awt.Color(204, 204, 204));
        serverIPTextField.setEditable(false);
        serverIPTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        serverIPTextField.setText("127.0.0.1");
        serverIPTextField.setName("serverIPTextField"); // NOI18N
        serverIPTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serverIPTextFieldActionPerformed(evt);
            }
        });

        serverPortSpinner.setModel(new javax.swing.SpinnerNumberModel(8080, 1050, 9999, 1));
        serverPortSpinner.setName("serverPortSpinner"); // NOI18N

        serverStatusLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        serverStatusLabel.setForeground(new java.awt.Color(255, 0, 0));
        serverStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        serverStatusLabel.setText("Fora do Ar");
        serverStatusLabel.setName("serverStatusLabel"); // NOI18N

        serverClientListScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Lista Clientes Conectados"));
        serverClientListScrollPane.setName("serverClientListScrollPane");

        serverClientListTextArea.setBackground(new java.awt.Color(204, 204, 204));
        serverClientListTextArea.setColumns(20);
        serverClientListTextArea.setEditable(false);
        serverClientListTextArea.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        serverClientListTextArea.setRows(5);
        serverClientListTextArea.setName("serverClientListTextArea");
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

        serverCancelButton.setText("Cancelar");
        serverCancelButton.setMaximumSize(new java.awt.Dimension(93, 23));
        serverCancelButton.setMinimumSize(new java.awt.Dimension(93, 23));
        serverCancelButton.setName("serverCancelButton"); // NOI18N
        serverCancelButton.setPreferredSize(new java.awt.Dimension(93, 23));
        serverCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serverCancelButtonActionPerformed(evt);
            }
        });

        serverPasswordLabel.setText("Password:");
        serverPasswordLabel.setName("serverPasswordLabel");

        serverPasswordField.setName("serverPasswordField");

        serverPasswordCheckBox.setText("Habilitar");
        serverPasswordCheckBox.setName("serverPasswordCheckBox");

        serverClientEditCheckBox.setText("Habilitar Edição do Cliente");
        serverClientEditCheckBox.setName("serverClientEditCheckBox");

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
                                .addComponent(serverCancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(serverClientEditCheckBox))
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
                .addComponent(serverClientEditCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(serverClientListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(serverCancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(serverDisconnectButton)
                    .addComponent(serverConnectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        applicationTabbedPane.addTab("Server", serverPanel);

        clientPanel.setName("clientPanel");

        clientIPLabel.setText("IP : ");
        clientIPLabel.setName("clientIPLabel");

        clientIPTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        clientIPTextField.setText("127.0.0.1");
        clientIPTextField.setName("clientIPTextField");
        clientIPTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientIPTextFieldActionPerformed(evt);
            }
        });

        clientPortLabel.setText("Porta :");
        clientPortLabel.setName("clientPortLabel");

        clientPortSpinner.setModel(new javax.swing.SpinnerNumberModel(8080, 1025, 9999, 1));
        clientPortSpinner.setName("clientPortSpinner");

        clientStatusLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        clientStatusLabel.setForeground(new java.awt.Color(255, 0, 0));
        clientStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        clientStatusLabel.setText("Desconectado");
        clientStatusLabel.setName("clientStatusLabel");

        clientConnectButton.setText("Conectar");
        clientConnectButton.setMaximumSize(new java.awt.Dimension(93, 23));
        clientConnectButton.setMinimumSize(new java.awt.Dimension(93, 23));
        clientConnectButton.setName("clientConnectButton");
        clientConnectButton.setPreferredSize(new java.awt.Dimension(93, 23));
        clientConnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientConnectButtonActionPerformed(evt);
            }
        });

        clientDisconnectButton.setText("Desconectar");
        clientDisconnectButton.setName("clientDisconnectButton");
        clientDisconnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientDisconnectButtonActionPerformed(evt);
            }
        });

        clientCancelButton.setText("Cancelar");
        clientCancelButton.setMaximumSize(new java.awt.Dimension(93, 23));
        clientCancelButton.setMinimumSize(new java.awt.Dimension(93, 23));
        clientCancelButton.setName("clientCancelButton");
        clientCancelButton.setPreferredSize(new java.awt.Dimension(93, 23));
        clientCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientCancelButtonActionPerformed(evt);
            }
        });

        clientPasswordLabel.setText("Password:");
        clientPasswordLabel.setName("clientPasswordLabel");

        clientPasswordField.setName("clientPasswordField");

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
                        .addComponent(clientIPTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(clientPortLabel)
                        .addGap(8, 8, 8)
                        .addComponent(clientPortSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(clientPanelLayout.createSequentialGroup()
                        .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(clientStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(clientPanelLayout.createSequentialGroup()
                                .addComponent(clientConnectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clientDisconnectButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clientCancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(clientPanelLayout.createSequentialGroup()
                        .addComponent(clientPasswordLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clientPasswordField, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)))
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
                    .addComponent(clientPasswordLabel)
                    .addComponent(clientPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 219, Short.MAX_VALUE)
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clientCancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clientDisconnectButton)
                    .addComponent(clientConnectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        applicationTabbedPane.addTab("Client", clientPanel);

        jMenuBar.setName("jMenuBar");

        jMenu1.setText("File");
        jMenu1.setName("jMenu1");
        jMenuBar.add(jMenu1);

        jMenu2.setText("Edit");
        jMenu2.setName("jMenu2");
        jMenuBar.add(jMenu2);

        setJMenuBar(jMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(applicationTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(applicationTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void serverIPTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serverIPTextFieldActionPerformed
    }//GEN-LAST:event_serverIPTextFieldActionPerformed

    private void serverDisconnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serverDisconnectButtonActionPerformed
        if (Conectado) {
            serverDisconnectButton.setEnabled(false);
            serverConnectButton.setEnabled(true);
            Conectado = false;
            server.stop();
            serverStatusLabel.setText("Fora do Ar");
            serverStatusLabel.setForeground(new java.awt.Color(255, 0, 0));
            this.enableClientIntreface(true);
        }
    }//GEN-LAST:event_serverDisconnectButtonActionPerformed

    private void serverCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serverCancelButtonActionPerformed
    }//GEN-LAST:event_serverCancelButtonActionPerformed

    private void serverConnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serverConnectButtonActionPerformed
        if (!Conectado) {
            Conectado = true;
            this.enableClientIntreface(false);
            serverDisconnectButton.setEnabled(true);
            serverConnectButton.setEnabled(false);
            serverStatusLabel.setForeground(new java.awt.Color(0, 255, 0));
            serverStatusLabel.setText("No Ar");
            if (!server.start((Integer) serverPortSpinner.getValue())) {
                serverDisconnectButton.setEnabled(false);
                serverConnectButton.setEnabled(true);
                Conectado = false;
                serverStatusLabel.setText("Fora do Ar");
                serverStatusLabel.setForeground(new java.awt.Color(255, 0, 0));
                this.enableClientIntreface(true);
            }
        }
    }//GEN-LAST:event_serverConnectButtonActionPerformed

    private void clientIPTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientIPTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clientIPTextFieldActionPerformed

    private void clientConnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientConnectButtonActionPerformed
        if (!Conectado) {
            Conectado = true;
            clientDisconnectButton.setEnabled(true);
            clientConnectButton.setEnabled(false);
            clientStatusLabel.setForeground(new java.awt.Color(0, 255, 0));
            clientStatusLabel.setText("Connected");
            client.start(clientIPTextField.getText(), (Integer) clientPortSpinner.getValue());
            this.enableServerIntreface(false);
        }
    }//GEN-LAST:event_clientConnectButtonActionPerformed

    private void clientDisconnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientDisconnectButtonActionPerformed
        this.enableServerIntreface(true);
    }//GEN-LAST:event_clientDisconnectButtonActionPerformed

    private void clientCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientCancelButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clientCancelButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane applicationTabbedPane;
    private javax.swing.JButton clientCancelButton;
    private javax.swing.JButton clientConnectButton;
    private javax.swing.JButton clientDisconnectButton;
    private javax.swing.JLabel clientIPLabel;
    private javax.swing.JTextField clientIPTextField;
    private javax.swing.JPanel clientPanel;
    private javax.swing.JPasswordField clientPasswordField;
    private javax.swing.JLabel clientPasswordLabel;
    private javax.swing.JLabel clientPortLabel;
    private javax.swing.JSpinner clientPortSpinner;
    private javax.swing.JLabel clientStatusLabel;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JButton serverCancelButton;
    private javax.swing.JCheckBox serverClientEditCheckBox;
    private javax.swing.JScrollPane serverClientListScrollPane;
    private javax.swing.JTextArea serverClientListTextArea;
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

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Interface.getInstance().setVisible(true);
            }
        });
    }
}
