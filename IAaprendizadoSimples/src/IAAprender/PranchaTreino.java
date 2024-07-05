/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package IAAprender;

import OpIO.IO;
import TrabalhaBits.NumeroBits;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import meupneuronio.NeuronioAJC;

class IA implements Serializable {

    ArrayList<NeuronioAJC> nr = new ArrayList<>();

    public void zera() {
        nr = new ArrayList<>();
    }

    public NeuronioAJC getNR(int cont, int size) {

        if (cont >= nr.size()) {

            nr.add(new NeuronioAJC(size * 2));

        }

        return nr.get(cont);
    }
    int ini = 0;

    public void treinar(JProgressBar progresso, int size, JButton btreino) {

        int valueInc = 100 / nr.size();

        ini = 0;

        for (NeuronioAJC n : nr) {

            n.treinarRedePS(size);

            progresso.setValue((ini += valueInc));

        }
        btreino.setEnabled(true);

    }

    public String getResult(PranchaTreino.PaintLivre p, ArrayList<PranchaTreino.dadosTreino> dadostreinos) {

        ArrayList<NumeroBits> ar = new ArrayList<>();
        ar.add(nr.get(0).getValorTreinoNovo(p.geraImagemReconhecimento()));

        for (int cont = 0; cont < nr.size(); cont++) {

            System.out.println("Results " + nr.get(cont).saidaPS(ar, 0).toString());
            if (nr.get(cont).saidaPS(ar, 0).get(0) < 0) {

                return dadostreinos.get(cont).tituloRec;
            }
        }
        return null;
    }
}

class Salvar implements Serializable {

    ArrayList<CilquesSave> cliquesFila = new ArrayList<>();

}

class SalvarList implements Serializable {

    ArrayList<Salvar> saves = new ArrayList<>();

}

class CliquesPontos implements Serializable {

    String titulo = "";
    int x, y;

    public CliquesPontos(int x, int y) {
        this.x = x;
        this.y = y;

    }

    public CliquesPontos clone() {

        try {
            return (CliquesPontos) super.clone();
        } catch (CloneNotSupportedException ex) {
        }

        return null;
    }

}

class CilquesSave implements Serializable {

    ArrayList<CliquesPontos> cliques = new ArrayList<>();
    public String titulo;
}

public class PranchaTreino extends javax.swing.JFrame {

    boolean carregando = true;
    ArrayList<dadosTreino> dadostreinos = new ArrayList<>();
    ArrayList<dadosTreino> dadostreinosRec = new ArrayList<>();
    IA ia = new IA();
    static int SIM = -2;
    static int NAO = 2;

    public PranchaTreino() {
        super("IA - Aprendizado ");
        initComponents();
        setVisible(true);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    SalvarList sl = (SalvarList) IO.ler("save");

                    for (Salvar s : sl.saves) {

                        dadosTreino dados = new dadosTreino();
                        dados.tituloRec = s.cliquesFila.get(0).titulo;

                        dados.init(true);
                        dadostreinos.add(dados);

                        for (CilquesSave c : s.cliquesFila) {

                            PaintLivre p = new PaintLivre(c.titulo);
                            dados.imagens.add(p);
                            p.cls.cliques = c.cliques;

                            dados.jPanel2.add(p, 3);
                            if (pranchaTreino.getWidth() < dados.imagens.size() * 170) {
                                pranchaTreino.setPreferredSize(new Dimension(dados.imagens.size() * 170, pranchaTreino.getHeight()));
                            }

                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    SalvarList sl = (SalvarList) IO.ler("save2");

                    for (Salvar s : sl.saves) {

                        dadosTreino dados = new dadosTreino();

                        dados.tituloRec = s.cliquesFila.get(0).titulo;
                        dados.initValida(true);
                        dadostreinosRec.add(dados);

                        for (CilquesSave c : s.cliquesFila) {

                            PaintLivre p = new PaintLivre(c.titulo);
                            p.cls.cliques = c.cliques;
                            dados.addNovoItem(p, dados.imagens, dados.tituloRec);

                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    ia = (IA) IO.ler("ia");

                } catch (Exception e) {
                    e.printStackTrace();

                }

                try {
                    tamanho.setValue((int) IO.ler("tam"));

                } catch (Exception e) {
                    e.printStackTrace();

                }

                if (ia == null) {
                    ia = new IA();
                }

                txIa.setText("IA OK");

                refreshFrame();

                carregando = false;
                jloading.setVisible(false);

            }
        }).start();

    }

    class PaintLivre extends JPanel {

        int tLargura = 100;

        CilquesSave cls = new CilquesSave();

        int contP = 0;

        public String titulo;

        public PaintLivre(String titulo) {

            setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
            cls.titulo = titulo;
            this.titulo = titulo;
            setSize(tLargura, tLargura);
            setMaximumSize(new Dimension(tLargura, tLargura));
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                    if (SwingUtilities.isRightMouseButton(e)) {

                        cls.cliques.clear();
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });

            addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {

                    // System.out.println(""+cliques.toString());
                    cls.cliques.add(new CliquesPontos(e.getX(), e.getY()));

                }

                @Override
                public void mouseMoved(MouseEvent e) {
                }
            });

        }

        @Override
        public void paintComponent(Graphics go) {

            Graphics2D g = (Graphics2D) go;
            g.setColor(Color.white);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.black);
            for (int cont = 0; cont < cls.cliques.size(); cont++) {
                g.fillOval(cls.cliques.get(cont).x, cls.cliques.get(cont).y, 2, 2);
            }

            repaint();

        }

        public BufferedImage geraImagem(int index, int i) {

            BufferedImage bImg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D cg = bImg.createGraphics();

            printAll(cg);

            try {
                if (ImageIO.write(bImg, "png",
                        new File("IMG" + index + "/output_image" + i + ".png"))) {
                    System.out.println("-- saved");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return bImg;
        }

        public File geraImagemReconhecimento() {

            BufferedImage bImg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D cg = bImg.createGraphics();
            printAll(cg);

            try {
                if (ImageIO.write(bImg, "png",
                        new File("rec.png"))) {
                    System.out.println("-- saved");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return new File("rec.png");
        }

        public PaintLivre getClone() {

            PaintLivre p = new PaintLivre(titulo);

            ArrayList<CliquesPontos> cliquesC = new ArrayList<CliquesPontos>();

            for (int cont = 0; cont < cls.cliques.size(); cont++) {

                cliquesC.add(new CliquesPontos(cls.cliques.get(cont).x, cls.cliques.get(cont).y));
            }

            p.cls.cliques = cliquesC;

            return p;
        }

    }

    class dadosTreino {

        public javax.swing.JPanel jPanel2 = null;
        public javax.swing.JPanel jPaneltreino = null;
        ArrayList< PaintLivre> imagens = new ArrayList<>();

        String tituloRec;

        public void init(boolean sMsgg) {

            if (!sMsgg) {
                tituloRec = JOptionPane.showInputDialog("Digite o texto de referencia");
            }

            jPanel2 = new javax.swing.JPanel();

            javax.swing.JLabel titulo = new javax.swing.JLabel();
            javax.swing.JButton jButton1 = new javax.swing.JButton();
            javax.swing.JButton jButton2 = new javax.swing.JButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

            jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

            pranchaTreino.setLayout(new javax.swing.BoxLayout(pranchaTreino, javax.swing.BoxLayout.Y_AXIS));

            jPanel2.setMaximumSize(new java.awt.Dimension(200000, 200));
            jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

            titulo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
            titulo.setText(tituloRec);
            jPanel2.add(titulo);

            jButton1.setText("Adicionar Card");
            jPanel2.add(jButton1);

            jButton2.setText("Remover Card");
            jPanel2.add(jButton2);
            pranchaTreino.add(jPanel2, 0);
            Dimension di = pranchaTreino.getPreferredSize();
            di.height = dadostreinos.size() * 170;
            pranchaTreino.setPreferredSize(di);
            refreshFrame();

            jButton1.addActionListener((e) -> {

                PaintLivre p = new PaintLivre(tituloRec);
                imagens.add(p);

                jPanel2.add(p, 3);
                if (pranchaTreino.getWidth() < imagens.size() * 170) {
                    pranchaTreino.setPreferredSize(new Dimension(imagens.size() * 170, pranchaTreino.getHeight()));
                }
                //repaint();
                refreshFrame();

            });

            jButton2.addActionListener((e) -> {

                if (imagens.size() != 0) {
                    jPanel2.remove(imagens.get(imagens.size() - 1));
                    imagens.remove(imagens.size() - 1);
                } else {

                    pranchaTreino.remove(jPanel2);
                }
                jPanel2.repaint();
                refreshFrame();

            });

        }

        public void initValida(boolean sMsgg) {

            if (!sMsgg) {
                tituloRec = JOptionPane.showInputDialog("Digite o texto de referencia");
            }

            jPaneltreino = new javax.swing.JPanel();

            javax.swing.JLabel titulo = new javax.swing.JLabel();
            javax.swing.JButton jButton1 = new javax.swing.JButton();
            javax.swing.JButton jButton2 = new javax.swing.JButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

            jPaneltreino.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

            pranchaValida.setLayout(new javax.swing.BoxLayout(pranchaValida, javax.swing.BoxLayout.Y_AXIS));

            jPaneltreino.setMaximumSize(new java.awt.Dimension(200000, 200));
            jPaneltreino.setLayout(new javax.swing.BoxLayout(jPaneltreino, javax.swing.BoxLayout.LINE_AXIS));

            titulo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
            titulo.setText(tituloRec);
            jPaneltreino.add(titulo);

            jButton1.setText("Adicionar Card");
            jPaneltreino.add(jButton1);

            jButton2.setText("Remover Card");
            jPaneltreino.add(jButton2);
            pranchaValida.add(jPaneltreino, 0);
            Dimension di = pranchaValida.getPreferredSize();
            di.height = 200 + (dadostreinosRec.size() * 190);
            pranchaValida.setPreferredSize(di);
            refreshFrame();

            jButton1.addActionListener((e) -> {

                addNovoItem(null, imagens, tituloRec);
            });

            jButton2.addActionListener((e) -> {

                if (imagens.size() != 0) {
                    jPaneltreino.remove(3);
                    jPaneltreino.remove(imagens.get(imagens.size() - 1));
                    imagens.remove(imagens.get(imagens.size() - 1));
                } else {
                    pranchaValida.remove(jPaneltreino);
                }

                refreshFrame();

            });
        }

        public void addNovoItem(PaintLivre pExiste, ArrayList<PaintLivre> imagens, String tituloRec) {

            javax.swing.JButton enviarTreino = new javax.swing.JButton();

            enviarTreino.setText("UsarTreino");
            final PaintLivre p = pExiste == null ? new PaintLivre(tituloRec) : pExiste;
            imagens.add(p);

            jPaneltreino.add(p, 3);
            if (pranchaValida.getWidth() < 500 + (imagens.size() * 170)) {
                pranchaValida.setPreferredSize(new Dimension(500 + (imagens.size() * 170), pranchaValida.getHeight()));
            }

            jPaneltreino.add(enviarTreino, 3);

            enviarTreino.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String[] list = new String[dadostreinos.size()];

                    for (int cont = 0; cont < list.length; cont++) {

                        list[cont] = dadostreinos.get(cont).tituloRec;

                    }

                    String input = (String) JOptionPane.showInputDialog(
                            PranchaTreino.this,
                            "Selecione a base de treino",
                            "Onde quer enviar",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            list,
                            null);

                    if (input != null) {

                        for (int cont = 0; cont < dadostreinos.size(); cont++) {

                            if (dadostreinos.get(cont).tituloRec.contentEquals(input)) {

                                PaintLivre pNovo = p.getClone();
                                dadostreinos.get(cont).imagens.add(pNovo);

                                dadostreinos.get(cont).jPanel2.add(pNovo, 3);
                                if (pranchaTreino.getWidth() < dadostreinos.get(cont).imagens.size() * 170) {
                                    pranchaTreino.setPreferredSize(new Dimension(dadostreinos.get(cont).imagens.size() * 170, pranchaTreino.getHeight()));
                                }
                            }

                        }
                    }
                    //treinarRede();
                    refreshFrame();
                }
            });

            //repaint();
            refreshFrame();

        }
    }

    public void refreshFrame() {

        invalidate();
        validate();
        repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        pranchaTreino = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        pranchaValida = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        tamanho = new javax.swing.JSlider();
        tm = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        results = new javax.swing.JTextArea();
        btreino = new javax.swing.JButton();
        progresso = new javax.swing.JProgressBar();
        txIa = new javax.swing.JLabel();
        jloading = new CustomInfiniteProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pranchaTreino.setLayout(new javax.swing.BoxLayout(pranchaTreino, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(pranchaTreino);

        jButton1.setText("Adicionar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Adicionar Dados");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        pranchaValida.setLayout(new javax.swing.BoxLayout(pranchaValida, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane2.setViewportView(pranchaValida);

        jButton3.setText("Salvar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel7.setText("Tamanho da rede");

        tamanho.setMajorTickSpacing(1);
        tamanho.setMaximum(10);
        tamanho.setMinimum(1);
        tamanho.setValue(1);
        tamanho.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tamanhoStateChanged(evt);
            }
        });

        tm.setText("1");

        jButton4.setText("Reconhecer");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        results.setColumns(20);
        results.setRows(5);
        jScrollPane3.setViewportView(results);

        btreino.setText("Treinar");
        btreino.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btreinoActionPerformed(evt);
            }
        });

        txIa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txIa.setText("Aguarde CARREGANDO");

        javax.swing.GroupLayout jloadingLayout = new javax.swing.GroupLayout(jloading);
        jloading.setLayout(jloadingLayout);
        jloadingLayout.setHorizontalGroup(
            jloadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 366, Short.MAX_VALUE)
        );
        jloadingLayout.setVerticalGroup(
            jloadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 39, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane3)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton4)
                            .addComponent(jButton2)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(tamanho, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(tm, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btreino, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(progresso, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txIa, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(61, 61, 61)
                                .addComponent(jloading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton1)
                            .addComponent(jloading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txIa)
                                .addGap(10, 10, 10))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(tamanho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tm))
                    .addComponent(progresso, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btreino)
                .addGap(23, 23, 23)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        dadosTreino dados = new dadosTreino();
        dados.init(false);
        dadostreinos.add(dados);
        //refreshFrame();

        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        dadosTreino dados = new dadosTreino();

        dados.initValida(false);

        dadostreinosRec.add(dados);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        SalvarList sl = new SalvarList();

        for (dadosTreino dt : dadostreinos) {
            Salvar s = new Salvar();
            for (PaintLivre p : dt.imagens) {
                s.cliquesFila.add(p.cls);
            }
            sl.saves.add(s);
        }

        try {
            IO.inserir("save", sl);
        } catch (IOException ex) {
            Logger.getLogger(PranchaTreino.class.getName()).log(Level.SEVERE, null, ex);
        }

        SalvarList sl2 = new SalvarList();

        for (dadosTreino dt : dadostreinosRec) {
            Salvar s = new Salvar();
            for (PaintLivre p : dt.imagens) {
                s.cliquesFila.add(p.cls);
            }
            sl2.saves.add(s);
        }

        try {
            IO.inserir("save2", sl2);
        } catch (IOException ex) {
            Logger.getLogger(PranchaTreino.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            IO.inserir("ia", ia);
        } catch (IOException ex) {
            Logger.getLogger(PranchaTreino.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            IO.inserir("tam", tamanho.getValue());
        } catch (IOException ex) {
            Logger.getLogger(PranchaTreino.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButton3ActionPerformed

    private void tamanhoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tamanhoStateChanged

        tm.setText(tamanho.getValue() + "");

        if (!carregando) {
            ia.zera();
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_tamanhoStateChanged

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        results.setText("");

        for (dadosTreino dt : dadostreinosRec) {

            for (PaintLivre p : dt.imagens) {

                results.insert(ia.getResult(p, dadostreinos), 0);

            }
            results.insert("\n", 0);
        }

    }//GEN-LAST:event_jButton4ActionPerformed

    private void btreinoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btreinoActionPerformed

        treinarRede();
        // TODO add your handling code here:
    }//GEN-LAST:event_btreinoActionPerformed

    /**
     * @param args the command line arguments
     */
    public void treinarRede() {
        btreino.setEnabled(false);
        progresso.setValue(0);

        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int cont = 0; cont < dadostreinos.size(); cont++) {

                    try {

                        for (File file : new File("IMG" + cont + "/").listFiles()) {
                            if (!file.isDirectory()) {
                                file.delete();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {

                        new File("IMG" + cont + "/").mkdir();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < dadostreinos.get(cont).imagens.size(); i++) {

                        dadostreinos.get(cont).imagens.get(i).geraImagem(cont, i);
                    }

                }

                if (ia.nr.size() != dadostreinos.size()) {
                    ia.zera();
                }

                int size = -1;
                for (int cont = 0; cont < dadostreinos.size(); cont++) {

                    try {

                        File[] file = new File("IMG" + cont + "/").listFiles();

                        for (int f = 0; f < file.length; f++) {

                            if (size == -1) {
                                size = NeuronioAJC.lenghtImagem(file[f]);
                            }

                            ia.getNR(cont, size).setValorTreinoNovo(file[f], SIM);

                        }

                        for (int nn = 0; nn < dadostreinos.size(); nn++) {

                            if (nn != cont) {

                                File[] nfile = new File("IMG" + nn + "/").listFiles();

                                for (int f = 0; f < nfile.length; f++) {

                                    if (size == -1) {
                                        size = NeuronioAJC.lenghtImagem(nfile[f]);
                                    }

                                    ia.getNR(cont, size).setValorTreinoNovo(nfile[f], NAO);

                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                // System.out.println(""+nr.getValorTreinoNovo(sfile[0]).paraStringCast()
                //  .contentEquals(nr.getValorTreinoNovo(sfile[0]).paraStringCast()));
                ia.treinar(progresso, tamanho.getValue(), btreino);

            }
        }).start();

    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PranchaTreino.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PranchaTreino.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PranchaTreino.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PranchaTreino.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PranchaTreino();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btreino;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel jloading;
    private javax.swing.JPanel pranchaTreino;
    private javax.swing.JPanel pranchaValida;
    private javax.swing.JProgressBar progresso;
    private javax.swing.JTextArea results;
    private javax.swing.JSlider tamanho;
    private javax.swing.JLabel tm;
    private javax.swing.JLabel txIa;
    // End of variables declaration//GEN-END:variables
}
