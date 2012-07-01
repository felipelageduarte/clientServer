/*-------------------------------------------------------------\
 |             SCC204 - Programação Orientada a Objeto          |
 |              Bacharelado em Ciencias de Computacao           |
 |                     2o Semestre - 2009                       |
 |                   ICMC - USP - Sao Carlos                    |
 |               Prof. Joao do E.S. Batista Neto                |
 |                    TRABALHO 02 - Paint                       |
 |                                                              |
 |  Felipe Simoes Lage Gomes Duarte - NoUSP 6426830             |
 |  Felipe Eduardo Gomes Sikansi    - NoUSP 6513441             |
 \-------------------------------------------------------------*/
package server;

import java.net.*;

public class JanelaServidor extends javax.swing.JFrame {

    protected int Port = 4000;
    protected Server Server = null;
    protected boolean Conectado;

    public JanelaServidor() {
        initComponents();
        Desconectar.setEnabled(false);
        Conectar.setEnabled(true);
        Conectado = false;
        IPText.setText(localhost());
    }

    private String localhost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return new String("0.0.0.0");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PainelServidor = new javax.swing.JPanel();
        PortLabel = new javax.swing.JLabel();
        IPLabel = new javax.swing.JLabel();
        IPText = new javax.swing.JTextField();
        BCancelar = new javax.swing.JButton();
        Desconectar = new javax.swing.JButton();
        LabelStatus = new javax.swing.JLabel();
        StatusLabel = new javax.swing.JLabel();
        SpinnerPorta = new javax.swing.JSpinner();
        LabelInfoPorta = new javax.swing.JLabel();
        StatusBar = new javax.swing.JLabel();
        ScrollConectados = new javax.swing.JScrollPane();
        ListaConectados = new javax.swing.JList();
        LabelListaConectados = new javax.swing.JLabel();
        Conectar = new javax.swing.JButton();

        setTitle("Configurar Servidor");

        PainelServidor.setName("PainelServidor"); // NOI18N

        PortLabel.setText("Porta :");
        PortLabel.setName("PortLabel"); // NOI18N

        IPLabel.setText("IP : ");
        IPLabel.setName("IPLabel"); // NOI18N

        IPText.setBackground(new java.awt.Color(204, 204, 204));
        IPText.setEditable(false);
        IPText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        IPText.setText("000.000.000.000");
        IPText.setName("IPText"); // NOI18N
        IPText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IPTextActionPerformed(evt);
            }
        });

        BCancelar.setText("Cancelar");
        BCancelar.setMaximumSize(new java.awt.Dimension(93, 23));
        BCancelar.setMinimumSize(new java.awt.Dimension(93, 23));
        BCancelar.setName("BCancelar"); // NOI18N
        BCancelar.setPreferredSize(new java.awt.Dimension(93, 23));
        BCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BCancelarActionPerformed(evt);
            }
        });

        Desconectar.setText("Desconectar");
        Desconectar.setName("Desconectar"); // NOI18N
        Desconectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DesconectarActionPerformed(evt);
            }
        });

        LabelStatus.setText("Status: ");
        LabelStatus.setName("LabelStatus"); // NOI18N

        StatusLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        StatusLabel.setForeground(new java.awt.Color(255, 0, 0));
        StatusLabel.setText("Fora do Ar");
        StatusLabel.setName("StatusLabel"); // NOI18N

        SpinnerPorta.setModel(new javax.swing.SpinnerNumberModel(4000, 4000, 5000, 1));
        SpinnerPorta.setName("SpinnerPorta"); // NOI18N

        LabelInfoPorta.setText("( Valor entre 4.000 e 5.000 )");
        LabelInfoPorta.setName("LabelInfoPorta"); // NOI18N

        StatusBar.setName("StatusBar"); // NOI18N

        ScrollConectados.setName("ScrollConectados"); // NOI18N

        ListaConectados.setName("ListaConectados"); // NOI18N
        ScrollConectados.setViewportView(ListaConectados);

        LabelListaConectados.setText("Lista Conectados:");
        LabelListaConectados.setName("LabelListaConectados"); // NOI18N

        Conectar.setText("Conectar");
        Conectar.setMaximumSize(new java.awt.Dimension(93, 23));
        Conectar.setMinimumSize(new java.awt.Dimension(93, 23));
        Conectar.setName("Conectar"); // NOI18N
        Conectar.setPreferredSize(new java.awt.Dimension(93, 23));
        Conectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConectarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PainelServidorLayout = new javax.swing.GroupLayout(PainelServidor);
        PainelServidor.setLayout(PainelServidorLayout);
        PainelServidorLayout.setHorizontalGroup(
            PainelServidorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PainelServidorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PainelServidorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PainelServidorLayout.createSequentialGroup()
                        .addComponent(PortLabel)
                        .addGap(8, 8, 8)
                        .addComponent(SpinnerPorta, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LabelInfoPorta))
                    .addGroup(PainelServidorLayout.createSequentialGroup()
                        .addGroup(PainelServidorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(ScrollConectados, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PainelServidorLayout.createSequentialGroup()
                                .addComponent(LabelListaConectados)
                                .addGap(159, 159, 159)
                                .addComponent(StatusBar))
                            .addGroup(PainelServidorLayout.createSequentialGroup()
                                .addComponent(Conectar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Desconectar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE))
                    .addGroup(PainelServidorLayout.createSequentialGroup()
                        .addGap(104, 104, 104)
                        .addGroup(PainelServidorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PainelServidorLayout.createSequentialGroup()
                                .addComponent(LabelStatus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(StatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                            .addGroup(PainelServidorLayout.createSequentialGroup()
                                .addComponent(IPLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(IPText, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        PainelServidorLayout.setVerticalGroup(
            PainelServidorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PainelServidorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PainelServidorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(StatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PainelServidorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(IPLabel)
                    .addComponent(IPText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PainelServidorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PortLabel)
                    .addComponent(SpinnerPorta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelInfoPorta))
                .addGap(31, 31, 31)
                .addGroup(PainelServidorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LabelListaConectados)
                    .addComponent(StatusBar, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ScrollConectados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PainelServidorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Desconectar)
                    .addComponent(Conectar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(38, Short.MAX_VALUE)
                .addComponent(PainelServidor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(PainelServidor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void IPTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IPTextActionPerformed
    }//GEN-LAST:event_IPTextActionPerformed

    private void DesconectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DesconectarActionPerformed
        if (Conectado) {
            Desconectar.setEnabled(false);
            Conectar.setEnabled(true);
            Conectado = false;
            Server.closesocket();
            StatusLabel.setText("Fora do Ar");
            StatusLabel.setForeground(new java.awt.Color(255, 0, 0));
        }
    }//GEN-LAST:event_DesconectarActionPerformed

    private void BCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BCancelarActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_BCancelarActionPerformed

    private void ConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConectarActionPerformed
        if (!Conectado) {
            Conectado = true;
            Desconectar.setEnabled(true);
            Conectar.setEnabled(false);
            StatusBar.setText("Esperando Conexão do Cliente");
            StatusBar.setForeground(new java.awt.Color(0, 255, 0));

            StatusLabel.setText("No Ar");
            StatusLabel.setForeground(new java.awt.Color(0, 255, 0));
            Server = new Servidor(Port);
            Server.start();
        }
    }//GEN-LAST:event_ConectarActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BCancelar;
    private javax.swing.JButton Conectar;
    private javax.swing.JButton Desconectar;
    private javax.swing.JLabel IPLabel;
    private javax.swing.JTextField IPText;
    private javax.swing.JLabel LabelInfoPorta;
    private javax.swing.JLabel LabelListaConectados;
    private javax.swing.JLabel LabelStatus;
    private javax.swing.JList ListaConectados;
    private javax.swing.JPanel PainelServidor;
    private javax.swing.JLabel PortLabel;
    private javax.swing.JScrollPane ScrollConectados;
    private javax.swing.JSpinner SpinnerPorta;
    private javax.swing.JLabel StatusBar;
    private javax.swing.JLabel StatusLabel;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
    }
}
