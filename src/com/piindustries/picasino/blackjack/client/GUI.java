package com.piindustries.picasino.blackjack.client;
import com.piindustries.picasino.blackjack.domain.GameEventType;
import com.piindustries.picasino.api.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Mike
 * Date: 11/19/13
 * Time: 11:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class GUI extends JFrame implements GuiHandler {

    private Player player;      //The player in which this GUI belongs to
    private ClientNetworkHandler client; //The client
    private JLabel[] dealerHand;
    private JLabel[] p1Hand;
    private JLabel[] p2Hand;
    private JLabel[] p3Hand;
    private JLabel[] p4Hand;
    private JLabel[] p5Hand;
    private JLabel[] p6Hand;
    private JLabel[] p7Hand;
    private JLabel[] p8Hand;
    private JPanel cardPanel;   //holds all of the cards on the table
    private JLabel chipCount1;  //holds the chip count of each player
    private JLabel chipCount2;
    private JLabel chipCount3;
    private JLabel chipCount4;
    private JLabel chipCount5;
    private JLabel chipCount6;
    private JLabel chipCount7;
    private JLabel chipCount8;
    private JButton doubleDownButton;   //buttons
    private JLabel doubleDownImage;
    private JLabel doubleDownText;
    private JButton hitButton;
    private JLabel hitImage;
    private JLabel hitText;
    private JButton splitButton;
    private JLabel splitImage;
    private JLabel splitText;
    private JButton stayButton;
    private JLabel stayImage;
    private JLabel stayText;
    private JLabel info1;               //boxes holding the information(username and chip count)
    private JLabel info2;
    private JLabel info3;
    private JLabel info4;
    private JLabel info5;
    private JLabel info6;
    private JLabel info7;
    private JLabel info8;
    private JButton stool1;             //stools to sit down at
    private JButton stool2;
    private JButton stool3;
    private JButton stool4;
    private JButton stool5;
    private JButton stool6;
    private JButton stool7;
    private JButton stool8;
    private JLabel table;               //background
    private JLabel username1;           //client usernames to be displayed in info
    private JLabel username2;
    private JLabel username3;
    private JLabel username4;
    private JLabel username5;
    private JLabel username6;
    private JLabel username7;
    private JLabel username8;

    /**
     * Creates the background.
     */
    public GUI( Player player, ClientNetworkHandler client  ) {
        this.player = player;
        this.client = client;
        initComponents();
        setVisible(true);
    }

    /**
     * Initializes all of the objects from the GUI Builder.
     * Do not modify.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        dealerHand = new JLabel[5];
        p1Hand = new JLabel[5];
        p2Hand = new JLabel[5];
        p3Hand = new JLabel[5];
        p4Hand = new JLabel[5];
        p5Hand = new JLabel[5];
        p6Hand = new JLabel[5];
        p7Hand = new JLabel[5];
        p8Hand = new JLabel[5];
        cardPanel = new JPanel();
        JLabel dealer5 = new JLabel();
        JLabel dealer4 = new JLabel();
        JLabel dealer3 = new JLabel();
        JLabel dealer2 = new JLabel();
        JLabel dealer1 = new JLabel();
        JLabel p1_5 = new JLabel();
        JLabel p1_4 = new JLabel();
        JLabel p1_3 = new JLabel();
        JLabel p1_2 = new JLabel();
        JLabel p1_1 = new JLabel();
        JLabel p2_5 = new JLabel();
        JLabel p2_4 = new JLabel();
        JLabel p2_3 = new JLabel();
        JLabel p2_2 = new JLabel();
        JLabel p2_1 = new JLabel();
        JLabel p3_5 = new JLabel();
        JLabel p3_4 = new JLabel();
        JLabel p3_3 = new JLabel();
        JLabel p3_2 = new JLabel();
        JLabel p3_1 = new JLabel();
        JLabel p4_5 = new JLabel();
        JLabel p4_4 = new JLabel();
        JLabel p4_3 = new JLabel();
        JLabel p4_2 = new JLabel();
        JLabel p4_1 = new JLabel();
        JLabel p5_5 = new JLabel();
        JLabel p5_4 = new JLabel();
        JLabel p5_3 = new JLabel();
        JLabel p5_2 = new JLabel();
        JLabel p5_1 = new JLabel();
        JLabel p6_5 = new JLabel();
        JLabel p6_4 = new JLabel();
        JLabel p6_3 = new JLabel();
        JLabel p6_2 = new JLabel();
        JLabel p6_1 = new JLabel();
        JLabel p7_5 = new JLabel();
        JLabel p7_4 = new JLabel();
        JLabel p7_3 = new JLabel();
        JLabel p7_2 = new JLabel();
        JLabel p7_1 = new JLabel();
        JLabel p8_5 = new JLabel();
        JLabel p8_4 = new JLabel();
        JLabel p8_3 = new JLabel();
        JLabel p8_2 = new JLabel();
        JLabel p8_1 = new JLabel();
        stool1 = new JButton();
        stool2 = new JButton();
        stool3 = new JButton();
        stool4 = new JButton();
        stool5 = new JButton();
        stool6 = new JButton();
        stool7 = new JButton();
        stool8 = new JButton();
        username1 = new JLabel();
        chipCount1 = new JLabel();
        username2 = new JLabel();
        chipCount2 = new JLabel();
        username3 = new JLabel();
        chipCount3 = new JLabel();
        username4 = new JLabel();
        chipCount4 = new JLabel();
        username5 = new JLabel();
        chipCount5 = new JLabel();
        username6 = new JLabel();
        chipCount6 = new JLabel();
        username7 = new JLabel();
        chipCount7 = new JLabel();
        username8 = new JLabel();
        chipCount8 = new JLabel();
        info1 = new JLabel();
        info2 = new JLabel();
        info3 = new JLabel();
        info4 = new JLabel();
        info5 = new JLabel();
        info6 = new JLabel();
        info7 = new JLabel();
        info8 = new JLabel();
        hitButton = new JButton();
        stayButton = new JButton();
        doubleDownButton = new JButton();
        splitButton = new JButton();
        hitText = new JLabel();
        stayText = new JLabel();
        doubleDownText = new JLabel();
        splitText = new JLabel();
        hitImage = new JLabel();
        stayImage = new JLabel();
        doubleDownImage = new JLabel();
        splitImage = new JLabel();
        table = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(930, 720));
        setResizable(false);
        getContentPane().setLayout(null);

        cardPanel.setOpaque(false);
        cardPanel.setLayout(null);

        cardPanel.add(dealer5);
        dealer5.setBounds(330, 60, 45, 60);
        dealerHand[4] = dealer5;

        cardPanel.add(dealer4);
        dealer4.setBounds(320, 50, 45, 60);
        dealerHand[3] = dealer4;

        cardPanel.add(dealer3);
        dealer3.setBounds(310, 40, 45, 60);
        dealerHand[2] = dealer3;

        cardPanel.add(dealer2);
        dealer2.setBounds(300, 30, 45, 60);
        dealerHand[1] = dealer2;

        cardPanel.add(dealer1);
        dealer1.setBounds(290, 20, 45, 60);
        dealerHand[0] = dealer1;

        cardPanel.add(p1_5);
        p1_5.setBounds(440, 90, 45, 60);
        p1Hand[4] = p1_5;

        cardPanel.add(p1_4);
        p1_4.setBounds(450, 80, 45, 60);
        p1Hand[3] = p1_4;

        cardPanel.add(p1_3);
        p1_3.setBounds(460, 70, 45, 60);
        p1Hand[2] = p1_3;

        cardPanel.add(p1_2);
        p1_2.setBounds(470, 60, 45, 60);
        p1Hand[1] = p1_2;

        cardPanel.add(p1_1);
        p1_1.setBounds(480, 50, 45, 60);
        p1Hand[0] = p1_1;

        cardPanel.add(p2_5);
        p2_5.setBounds(490, 150, 45, 60);
        p2Hand[4] = p2_5;

        cardPanel.add(p2_4);
        p2_4.setBounds(510, 150, 45, 60);
        p2Hand[3] = p2_4;

        cardPanel.add(p2_3);
        p2_3.setBounds(530, 150, 45, 60);
        p2Hand[2] = p2_3;

        cardPanel.add(p2_2);
        p2_2.setBounds(550, 150, 45, 60);
        p2Hand[1] = p2_2;

        cardPanel.add(p2_1);
        p2_1.setBounds(570, 150, 45, 60);
        p2Hand[0] = p2_1;

        cardPanel.add(p3_5);
        p3_5.setBounds(450, 200, 45, 60);
        p3Hand[4] = p3_5;

        cardPanel.add(p3_4);
        p3_4.setBounds(460, 210, 45, 60);
        p3Hand[3] = p3_4;

        cardPanel.add(p3_3);
        p3_3.setBounds(470, 220, 45, 60);
        p3Hand[2] = p3_3;

        cardPanel.add(p3_2);
        p3_2.setBounds(480, 230, 45, 60);
        p3Hand[1] = p3_2;

        cardPanel.add(p3_1);
        p3_1.setBounds(490, 240, 45, 60);
        p3Hand[0] = p3_1;

        cardPanel.add(p4_5);
        p4_5.setBounds(350, 240, 45, 60);
        p4Hand[4] = p4_5;

        cardPanel.add(p4_4);
        p4_4.setBounds(360, 250, 45, 60);
        p4Hand[3] = p4_4;

        cardPanel.add(p4_3);
        p4_3.setBounds(370, 260, 45, 60);
        p4Hand[2] = p4_3;

        cardPanel.add(p4_2);
        p4_2.setBounds(380, 270, 45, 60);
        p4Hand[1] = p4_2;

        cardPanel.add(p4_1);
        p4_1.setBounds(390, 280, 45, 60);
        p4Hand[0] = p4_1;

        cardPanel.add(p5_5);
        p5_5.setBounds(240, 240, 45, 60);
        p5Hand[4] = p5_5;

        cardPanel.add(p5_4);
        p5_4.setBounds(230, 250, 45, 60);
        p5Hand[3] = p5_4;

        cardPanel.add(p5_3);
        p5_3.setBounds(220, 260, 45, 60);
        p5Hand[2] = p5_3;

        cardPanel.add(p5_2);
        p5_2.setBounds(210, 270, 45, 60);
        p5Hand[1] = p5_2;

        cardPanel.add(p5_1);
        p5_1.setBounds(200, 280, 45, 60);
        p5Hand[0] = p5_1;

        cardPanel.add(p6_5);
        p6_5.setBounds(140, 200, 45, 60);
        p6Hand[4] = p6_5;

        cardPanel.add(p6_4);
        p6_4.setBounds(130, 210, 45, 60);
        p6Hand[3] = p6_4;

        cardPanel.add(p6_3);
        p6_3.setBounds(120, 220, 45, 60);
        p6Hand[2] = p6_3;

        cardPanel.add(p6_2);
        p6_2.setBounds(110, 230, 45, 60);
        p6Hand[1] = p6_2;

        cardPanel.add(p6_1);
        p6_1.setBounds(100, 240, 45, 60);
        p6Hand[0] = p6_1;

        cardPanel.add(p7_5);
        p7_5.setBounds(100, 140, 45, 60);
        p7Hand[4] = p7_5;

        cardPanel.add(p7_4);
        p7_4.setBounds(80, 140, 45, 60);
        p7Hand[3] = p7_4;

        cardPanel.add(p7_3);
        p7_3.setBounds(60, 140, 45, 60);
        p7Hand[2] = p7_3;

        cardPanel.add(p7_2);
        p7_2.setBounds(40, 140, 45, 60);
        p7Hand[1] = p7_2;

        cardPanel.add(p7_1);
        p7_1.setBounds(20, 140, 45, 60);
        p7Hand[0] = p7_1;

        cardPanel.add(p8_5);
        p8_5.setBounds(170, 80, 45, 60);
        p8Hand[4] = p8_5;

        cardPanel.add(p8_4);
        p8_4.setBounds(160, 70, 45, 60);
        p8Hand[3] = p8_4;

        cardPanel.add(p8_3);
        p8_3.setBounds(150, 60, 45, 60);
        p8Hand[2] = p8_3;

        cardPanel.add(p8_2);
        p8_2.setBounds(140, 50, 45, 60);
        p8Hand[1] = p8_2;

        cardPanel.add(p8_1);
        p8_1.setBounds(130, 40, 45, 60);
        p8Hand[0] = p8_1;

        getContentPane().add(cardPanel);
        cardPanel.setBounds(140, 80, 640, 370);

        stool1.setBorderPainted(false);
        stool1.setContentAreaFilled(false);
        stool1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stool1ActionPerformed(evt);
            }
        });
        getContentPane().add(stool1);
        stool1.setBounds(670, 20, 130, 130);

        stool2.setBorderPainted(false);
        stool2.setContentAreaFilled(false);
        stool2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stool2ActionPerformed(evt);
            }
        });
        getContentPane().add(stool2);
        stool2.setBounds(790, 190, 120, 130);

        stool3.setBorderPainted(false);
        stool3.setContentAreaFilled(false);
        stool3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stool3ActionPerformed(evt);
            }
        });
        getContentPane().add(stool3);
        stool3.setBounds(690, 370, 120, 130);

        stool4.setBorderPainted(false);
        stool4.setContentAreaFilled(false);
        stool4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stool4ActionPerformed(evt);
            }
        });
        getContentPane().add(stool4);
        stool4.setBounds(500, 440, 130, 130);

        stool5.setBorderPainted(false);
        stool5.setContentAreaFilled(false);
        stool5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stool5ActionPerformed(evt);
            }
        });
        getContentPane().add(stool5);
        stool5.setBounds(280, 440, 130, 130);

        stool6.setBorderPainted(false);
        stool6.setContentAreaFilled(false);
        stool6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stool6ActionPerformed(evt);
            }
        });
        getContentPane().add(stool6);
        stool6.setBounds(110, 380, 120, 130);

        stool7.setBorderPainted(false);
        stool7.setContentAreaFilled(false);
        stool7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stool7ActionPerformed(evt);
            }
        });
        getContentPane().add(stool7);
        stool7.setBounds(10, 200, 120, 130);

        stool8.setBorderPainted(false);
        stool8.setContentAreaFilled(false);
        stool8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stool8ActionPerformed(evt);
            }
        });
        getContentPane().add(stool8);
        stool8.setBounds(120, 30, 120, 120);

        username1.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        username1.setForeground(new Color(255, 255, 255));
        username1.setHorizontalAlignment(SwingConstants.CENTER);
        username1.setText("Username1");
        getContentPane().add(username1);
        username1.setBounds(790, 130, 130, 20);

        chipCount1.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        chipCount1.setForeground(new Color(255, 255, 255));
        chipCount1.setHorizontalAlignment(SwingConstants.CENTER);
        chipCount1.setText("ChipCount1");
        getContentPane().add(chipCount1);
        chipCount1.setBounds(790, 150, 130, 30);

        username2.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        username2.setForeground(new Color(255, 255, 255));
        username2.setHorizontalAlignment(SwingConstants.CENTER);
        username2.setText("Username2");
        getContentPane().add(username2);
        username2.setBounds(790, 330, 130, 20);

        chipCount2.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        chipCount2.setForeground(new Color(255, 255, 255));
        chipCount2.setHorizontalAlignment(SwingConstants.CENTER);
        chipCount2.setText("ChipCount2");
        getContentPane().add(chipCount2);
        chipCount2.setBounds(790, 350, 130, 30);

        username3.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        username3.setForeground(new Color(255, 255, 255));
        username3.setHorizontalAlignment(SwingConstants.CENTER);
        username3.setText("Username3");
        getContentPane().add(username3);
        username3.setBounds(790, 500, 130, 20);

        chipCount3.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        chipCount3.setForeground(new Color(255, 255, 255));
        chipCount3.setHorizontalAlignment(SwingConstants.CENTER);
        chipCount3.setText("ChipCount3");
        getContentPane().add(chipCount3);
        chipCount3.setBounds(790, 520, 130, 30);

        username4.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        username4.setForeground(new Color(255, 255, 255));
        username4.setHorizontalAlignment(SwingConstants.CENTER);
        username4.setText("Username4");
        getContentPane().add(username4);
        username4.setBounds(630, 520, 130, 20);

        chipCount4.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        chipCount4.setForeground(new Color(255, 255, 255));
        chipCount4.setHorizontalAlignment(SwingConstants.CENTER);
        chipCount4.setText("ChipCount4");
        getContentPane().add(chipCount4);
        chipCount4.setBounds(630, 540, 130, 30);

        username5.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        username5.setForeground(new Color(255, 255, 255));
        username5.setHorizontalAlignment(SwingConstants.CENTER);
        username5.setText("Username5");
        getContentPane().add(username5);
        username5.setBounds(150, 520, 130, 20);

        chipCount5.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        chipCount5.setForeground(new Color(255, 255, 255));
        chipCount5.setHorizontalAlignment(SwingConstants.CENTER);
        chipCount5.setText("ChipCount5");
        getContentPane().add(chipCount5);
        chipCount5.setBounds(150, 540, 130, 30);

        username6.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        username6.setForeground(new Color(255, 255, 255));
        username6.setHorizontalAlignment(SwingConstants.CENTER);
        username6.setText("Username6");
        getContentPane().add(username6);
        username6.setBounds(0, 500, 130, 20);

        chipCount6.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        chipCount6.setForeground(new Color(255, 255, 255));
        chipCount6.setHorizontalAlignment(SwingConstants.CENTER);
        chipCount6.setText("ChipCount6");
        getContentPane().add(chipCount6);
        chipCount6.setBounds(0, 520, 130, 30);

        username7.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        username7.setForeground(new Color(255, 255, 255));
        username7.setHorizontalAlignment(SwingConstants.CENTER);
        username7.setText("Username7");
        getContentPane().add(username7);
        username7.setBounds(0, 340, 130, 20);

        chipCount7.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        chipCount7.setForeground(new Color(255, 255, 255));
        chipCount7.setHorizontalAlignment(SwingConstants.CENTER);
        chipCount7.setText("ChipCount7");
        getContentPane().add(chipCount7);
        chipCount7.setBounds(0, 360, 130, 30);

        username8.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        username8.setForeground(new Color(255, 255, 255));
        username8.setHorizontalAlignment(SwingConstants.CENTER);
        username8.setText("Username8");
        getContentPane().add(username8);
        username8.setBounds(0, 140, 130, 20);

        chipCount8.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        chipCount8.setForeground(new Color(255, 255, 255));
        chipCount8.setHorizontalAlignment(SwingConstants.CENTER);
        chipCount8.setText("ChipCount8");
        getContentPane().add(chipCount8);
        chipCount8.setBounds(0, 160, 130, 30);

        info1.setIcon(new ImageIcon(getClass().getResource("/PiCasinoPlayerInfo.png"))); // NOI18N
        getContentPane().add(info1);
        info1.setBounds(790, 120, 132, 60);

        info2.setIcon(new ImageIcon(getClass().getResource("/PiCasinoPlayerInfo.png"))); // NOI18N
        getContentPane().add(info2);
        info2.setBounds(790, 320, 132, 60);

        info3.setIcon(new ImageIcon(getClass().getResource("/PiCasinoPlayerInfo.png"))); // NOI18N
        getContentPane().add(info3);
        info3.setBounds(790, 490, 132, 60);

        info4.setIcon(new ImageIcon(getClass().getResource("/PiCasinoPlayerInfo.png"))); // NOI18N
        getContentPane().add(info4);
        info4.setBounds(630, 510, 132, 60);

        info5.setIcon(new ImageIcon(getClass().getResource("/PiCasinoPlayerInfo.png"))); // NOI18N
        getContentPane().add(info5);
        info5.setBounds(150, 510, 132, 60);

        info6.setIcon(new ImageIcon(getClass().getResource("/PiCasinoPlayerInfo.png"))); // NOI18N
        getContentPane().add(info6);
        info6.setBounds(0, 490, 132, 60);

        info7.setIcon(new ImageIcon(getClass().getResource("/PiCasinoPlayerInfo.png"))); // NOI18N
        getContentPane().add(info7);
        info7.setBounds(0, 330, 132, 60);

        info8.setIcon(new ImageIcon(getClass().getResource("/PiCasinoPlayerInfo.png"))); // NOI18N
        getContentPane().add(info8);
        info8.setBounds(0, 130, 132, 60);

        hitButton.setBorderPainted(false);
        hitButton.setContentAreaFilled(false);
        getContentPane().add(hitButton);
        hitButton.setBounds(530, 570, 150, 100);

        stayButton.setBorderPainted(false);
        stayButton.setContentAreaFilled(false);
        getContentPane().add(stayButton);
        stayButton.setBounds(360, 570, 150, 100);

        doubleDownButton.setBorderPainted(false);
        doubleDownButton.setContentAreaFilled(false);
        getContentPane().add(doubleDownButton);
        doubleDownButton.setBounds(190, 570, 150, 100);

        splitButton.setBorderPainted(false);
        splitButton.setContentAreaFilled(false);
        getContentPane().add(splitButton);
        splitButton.setBounds(20, 570, 150, 100);

        hitText.setFont(new Font("Tahoma", 1, 18)); // NOI18N
        hitText.setForeground(new Color(255, 255, 255));
        hitText.setHorizontalAlignment(SwingConstants.CENTER);
        hitText.setText("HIT");
        getContentPane().add(hitText);
        hitText.setBounds(530, 570, 150, 110);

        stayText.setFont(new Font("Tahoma", 1, 18)); // NOI18N
        stayText.setForeground(new Color(255, 255, 255));
        stayText.setHorizontalAlignment(SwingConstants.CENTER);
        stayText.setText("STAY");
        getContentPane().add(stayText);
        stayText.setBounds(360, 570, 150, 110);

        doubleDownText.setFont(new Font("Tahoma", 1, 18)); // NOI18N
        doubleDownText.setForeground(new Color(255, 255, 255));
        doubleDownText.setHorizontalAlignment(SwingConstants.CENTER);
        doubleDownText.setText("DOUBLE DOWN");
        getContentPane().add(doubleDownText);
        doubleDownText.setBounds(190, 570, 150, 110);

        splitText.setFont(new Font("Tahoma", 1, 18)); // NOI18N
        splitText.setForeground(new Color(255, 255, 255));
        splitText.setHorizontalAlignment(SwingConstants.CENTER);
        splitText.setText("SPLIT");
        getContentPane().add(splitText);
        splitText.setBounds(20, 570, 150, 110);

        hitImage.setForeground(new Color(255, 255, 255));
        hitImage.setIcon(new ImageIcon(getClass().getResource("/Buttons.png"))); // NOI18N
        getContentPane().add(hitImage);
        hitImage.setBounds(530, 570, 150, 120);

        stayImage.setIcon(new ImageIcon(getClass().getResource("/Buttons.png"))); // NOI18N
        getContentPane().add(stayImage);
        stayImage.setBounds(360, 570, 150, 120);

        doubleDownImage.setIcon(new ImageIcon(getClass().getResource("/Buttons.png"))); // NOI18N
        getContentPane().add(doubleDownImage);
        doubleDownImage.setBounds(190, 570, 150, 120);

        splitImage.setIcon(new ImageIcon(getClass().getResource("/Buttons.png"))); // NOI18N
        getContentPane().add(splitImage);
        splitImage.setBounds(20, 570, 150, 120);

        table.setIcon(new ImageIcon(getClass().getResource("/PiCasinoTable.png"))); // NOI18N
        getContentPane().add(table);
        table.setBounds(0, 0, 925, 700);

        setStoolsHidden();
        pack();

        //Make stools unclickable if the player is already sitting down
        //if( player.getIndex() != 99 ) {
        //    setStoolsHidden();
        //}


    }// </editor-fold>

    ///////////////////////////////Buttons for taking a seat//////////////////////////////////
    /**
     * For all stoolXActionPerformed : the button is hidden.  When clicked, a user
     * will sit down, the button will then be unusable until the user leaves the spot
     * TODO send that a player sat down through network handler
     */
    private void stool1ActionPerformed(java.awt.event.ActionEvent evt) {
        //player.setIndex( 1 );
        setStoolsHidden();
    }

    private void stool2ActionPerformed(java.awt.event.ActionEvent evt) {
        //player.setIndex( 2 );
        setStoolsHidden();
    }

    private void stool3ActionPerformed(java.awt.event.ActionEvent evt) {
        //player.setIndex( 3 );
        setStoolsHidden();
    }

    private void stool4ActionPerformed(java.awt.event.ActionEvent evt) {
        //player.setIndex( 4 );
        setStoolsHidden();
    }

    private void stool5ActionPerformed(java.awt.event.ActionEvent evt) {
        //player.setIndex( 5 );
        setStoolsHidden();
    }

    private void stool6ActionPerformed(java.awt.event.ActionEvent evt) {
        //player.setIndex( 6 );
        setStoolsHidden();
    }

    private void stool7ActionPerformed(java.awt.event.ActionEvent evt) {
        //player.setIndex( 7 );
        setStoolsHidden();
    }

    private void stool8ActionPerformed(java.awt.event.ActionEvent evt) {
        //player.setIndex( 8 );
        setStoolsHidden();
    }

    private void setStoolsHidden() {
        stool1.setEnabled( false );
        stool2.setEnabled( false );
        stool3.setEnabled( false );
        stool4.setEnabled( false );
        stool5.setEnabled( false );
        stool6.setEnabled( false );
        stool7.setEnabled( false );
        stool8.setEnabled( false );
    }

    //TODO send that the player hit
    private void hit(java.awt.event.ActionEvent evt) {

    }

    //TODO send that the player stood
    private void stand(java.awt.event.ActionEvent evt) {

    }

    //TODO send that the player doubleddown
    private void doubleDown(java.awt.event.ActionEvent evt) {

    }

    //TODO send that the player split
    private void split(java.awt.event.ActionEvent evt) {

    }

    public void updateGui( java.util.List<GameEventType> possibleActions, GuiData data ) { //TODO Add functionality for split and Double Down

        //if players turn, display buttons that he can use
        for( GameEventType g : possibleActions ) {
            switch(g) {
                case HIT:
                    hitButton.setVisible( true );
                    hitButton.setEnabled( true );
                    hitText.setVisible( true );
                    hitImage.setVisible( true );
                    break;
                case STAND:
                    stayButton.setVisible( true );
                    stayButton.setEnabled( true );
                    stayText.setVisible( true );
                    stayImage.setVisible( true );
                    break;
                case DOUBLE_DOWN:
                    doubleDownButton.setVisible( true );
                    doubleDownButton.setEnabled( true );
                    doubleDownText.setVisible( true );
                    doubleDownImage.setVisible( true );
                    break;
                case SPLIT:
                    splitButton.setVisible( true );
                    splitButton.setEnabled( true );
                    splitText.setVisible( true );
                    splitImage.setVisible( true );
                    break;
            }
        }

        for( int i = 0; i < 9; i++ ) {
            if( data.getPlayer(i) != null ) {
                updatePlayerInfo( data.getPlayer(i) );
            }
        }

        repaint();
    }

    /**
     * Called by gamestate when any player gets a new card.  This then updates the GUI to reflect the new card
     * being added by the given player
     *
     * @param toUpdate The player whose card was added
     */
    public void updatePlayerInfo( Player toUpdate ) {

            switch( toUpdate.getIndex() ) {
                case 1: updateP1Info( toUpdate ); break;
                case 2: updateP2Info(toUpdate); break;
                case 3: updateP3Info(toUpdate); break;
                case 4: updateP4Info(toUpdate); break;
                case 5: updateP5Info(toUpdate); break;
                case 6: updateP6Info(toUpdate); break;
                case 7: updateP7Info(toUpdate); break;
                case 8: updateP8Info(toUpdate); break;
                case 0: updateDealerInfo( toUpdate ); break;
            }
    }

    private void updateDealerInfo(Player toUpdate) {

        LinkedList<Integer> hand = toUpdate.getHands().getFirst();

        for( int j = 0; j < hand.size(); j++ ) {
            StringBuilder sb = new StringBuilder();
            sb.append("/");
            sb.append(Integer.toString(hand.get(j)));
            sb.append(".png");
            dealerHand[j].setIcon(new ImageIcon(getClass().getResource(sb.toString())));
            dealerHand[j].setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        }
    }

    private void updateP1Info( Player toUpdate ) {

        username1.setText( toUpdate.getUsername() );
        chipCount1.setText(Integer.toString(toUpdate.getValue()));

        LinkedList<Integer> hand;

        if( toUpdate.isHasSplit() )
            hand = toUpdate.getHands().getLast();
        else
            hand = toUpdate.getHands().getFirst();

        for( int j = 0; j < hand.size(); j++ ) {
            StringBuilder sb = new StringBuilder();
            sb.append("/");
            sb.append(Integer.toString(hand.get(j)));
            sb.append(".png");
            p1Hand[j].setIcon(new ImageIcon(getClass().getResource(sb.toString())));
            p1Hand[j].setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        }

    }

    private void updateP2Info( Player toUpdate ) {
        username2.setText( toUpdate.getUsername() );
        chipCount2.setText( Integer.toString(toUpdate.getValue()) );

        LinkedList<Integer> hand;

        if( toUpdate.isHasSplit() )
            hand = toUpdate.getHands().getLast();
        else
            hand = toUpdate.getHands().getFirst();

        for( int j = 0; j < hand.size(); j++ ) {
            StringBuilder sb = new StringBuilder();
            sb.append("/");
            sb.append(Integer.toString(hand.get(j)));
            sb.append(".png");
            p2Hand[j].setIcon(new ImageIcon(getClass().getResource(sb.toString())));
            p2Hand[j].setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        }

    }

    private void updateP3Info( Player toUpdate ) {
        username3.setText( toUpdate.getUsername() );
        chipCount3.setText( Integer.toString(toUpdate.getValue()) );

        LinkedList<Integer> hand;

        if( toUpdate.isHasSplit() )
            hand = toUpdate.getHands().getLast();
        else
            hand = toUpdate.getHands().getFirst();

        for( int j = 0; j < hand.size(); j++ ) {
            StringBuilder sb = new StringBuilder();
            sb.append("/");
            sb.append(Integer.toString(hand.get(j)));
            sb.append(".png");
            p3Hand[j].setIcon(new ImageIcon(getClass().getResource(sb.toString())));
            p3Hand[j].setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        }

    }

    private void updateP4Info( Player toUpdate ) {
        username4.setText( toUpdate.getUsername() );
        chipCount4.setText( Integer.toString(toUpdate.getValue()) );

        LinkedList<Integer> hand;

        if( toUpdate.isHasSplit() )
            hand = toUpdate.getHands().getLast();
        else
            hand = toUpdate.getHands().getFirst();

        for( int j = 0; j < hand.size(); j++ ) {
            StringBuilder sb = new StringBuilder();
            sb.append("/");
            sb.append(Integer.toString(hand.get(j)));
            sb.append(".png");
            p4Hand[j].setIcon(new ImageIcon(getClass().getResource(sb.toString())));
            p4Hand[j].setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        }

    }

    private void updateP5Info( Player toUpdate ) {
        username5.setText( toUpdate.getUsername() );
        chipCount5.setText( Integer.toString(toUpdate.getValue()) );

        LinkedList<Integer> hand;

        if( toUpdate.isHasSplit() )
            hand = toUpdate.getHands().getLast();
        else
            hand = toUpdate.getHands().getFirst();

        for( int j = 0; j < hand.size(); j++ ) {
            StringBuilder sb = new StringBuilder();
            sb.append("/");
            sb.append(Integer.toString(hand.get(j)));
            sb.append(".png");
            p5Hand[j].setIcon(new ImageIcon(getClass().getResource(sb.toString())));
            p5Hand[j].setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        }

    }

    private void updateP6Info( Player toUpdate ) {
        username6.setText( toUpdate.getUsername() );
        chipCount6.setText( Integer.toString(toUpdate.getValue()) );

        LinkedList<Integer> hand;

        if( toUpdate.isHasSplit() )
            hand = toUpdate.getHands().getLast();
        else
            hand = toUpdate.getHands().getFirst();

        for( int j = 0; j < hand.size(); j++ ) {
            StringBuilder sb = new StringBuilder();
            sb.append("/");
            sb.append(Integer.toString(hand.get(j)));
            sb.append(".png");
            p6Hand[j].setIcon(new ImageIcon(getClass().getResource(sb.toString())));
            p6Hand[j].setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        }
    }

    private void updateP7Info( Player toUpdate ) {
        username7.setText( toUpdate.getUsername() );
        chipCount7.setText( Integer.toString(toUpdate.getValue()) );

        LinkedList<Integer> hand;

        if( toUpdate.isHasSplit() )
            hand = toUpdate.getHands().getLast();
        else
            hand = toUpdate.getHands().getFirst();

        for( int j = 0; j < hand.size(); j++ ) {
            StringBuilder sb = new StringBuilder();
            sb.append("/");
            sb.append(Integer.toString(hand.get(j)));
            sb.append(".png");
            p7Hand[j].setIcon(new ImageIcon(getClass().getResource(sb.toString())));
            p7Hand[j].setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        }
    }

    private void updateP8Info( Player toUpdate ) {
        username8.setText( toUpdate.getUsername() );
        chipCount8.setText( Integer.toString(toUpdate.getValue()) );

        LinkedList<Integer> hand;

        if( toUpdate.isHasSplit() )
            hand = toUpdate.getHands().getLast();
        else
            hand = toUpdate.getHands().getFirst();

        for( int j = 0; j < hand.size(); j++ ) {
            StringBuilder sb = new StringBuilder();
            sb.append("/");
            sb.append(Integer.toString(hand.get(j)));
            sb.append(".png");
            p8Hand[j].setIcon(new ImageIcon(getClass().getResource(sb.toString())));
            p8Hand[j].setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        }
    }
}
