package org.ltimindtree;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.hyperledger.fabric.client.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class App extends JPanel implements ActionListener {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static JFrame frame;
    private static CloseableIterator<ChaincodeEvent> handleContractEvents;
    private User user_d;
    private Gateway gateway_d;
    private Contract contract_d;
    private Network network_d;
    private Path p_key_d = Paths.get("C:\\Users\\agarg\\organizations\\peerOrganizations\\org1.example.com\\users\\User1@org1.example.com\\msp\\keystore");
    private Path pub_key_d = Paths.get("C:\\Users\\agarg\\organizations\\peerOrganizations\\org1.example.com\\users\\User1@org1.example.com\\msp\\signcerts\\cert.pem");
    private Path tls_cert_d = Paths.get("C:\\Users\\agarg\\organizations\\peerOrganizations\\org1.example.com\\peers\\peer0.org1.example.com\\tls\\ca.crt");

    private Border greenBorder = BorderFactory.createLineBorder(Color.GREEN);
    private Border redBorder = BorderFactory.createLineBorder(Color.RED);

    private FileNameExtensionFilter certificateFilter = new FileNameExtensionFilter("Certificates", "crt", "cer");
    private FileNameExtensionFilter publicKeyFilter = new FileNameExtensionFilter("Public Key", "pem");

    private JLabel jcomp1;
    private JLabel jcomp2;
    private JLabel jcomp3;
    private JLabel jcomp4;
    private JButton jcomp5;
    private JButton jcomp6;
    private JButton jcomp7;
    private JTextField jcomp8;//MSPID
    private JLabel jcomp9;//
    private JLabel jcomp10;//PeerEndpoint
    private JLabel jcomp11;
    private JLabel jcomp12;
    private JLabel jcomp13;
    private JButton jcomp14;
    private JButton jcomp15;
    private JButton jcomp16;
    private JButton jcomp17;
    private JCheckBox jcomp18;//Block Listener
    private JLabel jcomp19;
    private JTextField jcomp20;//patttern
    private JCheckBox jcomp21;//ContractEvent
    private JTextField jcomp22;//peer
    private JTextField jcomp23;//auth
    private JTextField jcomp24;//channel
    private JButton jcomp245;
    private JTextField jcomp25;//chain
    private JButton jcomp26;
    private JLabel jcomp27;
    private JLabel jcomp28;
    private JTextField jcomp29;//func
    private JTextField jcomp30;//0
    private JTextField jcomp31;//1
    private JTextField jcomp32;//2
    private JTextField jcomp33;//3
    private JTextField jcomp34;//4

    public App() {
        //construct components
        jcomp1 = new JLabel ("Wallet");
        jcomp2 = new JLabel ("Network Connection");
        jcomp3 = new JLabel ("Smart Contract");
        jcomp4 = new JLabel ("Listeners");
        jcomp5 = new JButton ("Prepare Wallet");
        jcomp5.addActionListener(this);
        jcomp6 = new JButton ("Pick Signing Key");
        jcomp6.addActionListener(this);
        jcomp7 = new JButton ("Pick Certificate");
        jcomp7.addActionListener(this);
        jcomp8 = new JTextField (5);
        jcomp8.setToolTipText("Org1MSP");
        jcomp9 = new JLabel ("MSP ID");
        jcomp10 = new JLabel ("Peer Endpoint");
        jcomp11 = new JLabel ("Override Auth");
        jcomp12 = new JLabel ("Channel Name");
        jcomp13 = new JLabel ("Chaincode Name");
        jcomp14 = new JButton ("Connect Network");
        jcomp14.addActionListener(this);
        jcomp15 = new JButton ("Contract Ref.");
        jcomp15.addActionListener(this);
        jcomp16 = new JButton ("Query");
        jcomp16.addActionListener(this);
        jcomp17 = new JButton ("Update");
        jcomp17.addActionListener(this);
        jcomp18 = new JCheckBox ("Block Listener");
        jcomp18.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                boolean listening = (e.getStateChange()==1) ? true: false;
                if (listening) {

                } else {

                }
            }
        });
        jcomp19 = new JLabel ("Event Name Pattern");
        jcomp20 = new JTextField (5);
        jcomp21 = new JCheckBox ("Contract Event Listener");
        jcomp21.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                boolean listening = e.getStateChange()==1? true: false;

                if (listening) {
                    App.handleContractEvents = startChaincodeEventListening();
                } else {
                    App.handleContractEvents.close();
                }
            }
        });


        jcomp22 = new JTextField (5);
        jcomp22.setToolTipText("172.24.148.93:7051");
        jcomp23 = new JTextField (5);
        jcomp23.setToolTipText("peer0.org1.example.com");
        jcomp24 = new JTextField (5);
        jcomp24.setToolTipText("mychannel");
        jcomp245 = new JButton ("TLS Cert");
        jcomp245.addActionListener(this);
        jcomp25 = new JTextField (5);
        jcomp25.setToolTipText("basic");
        jcomp26 = new JButton ("Async");
        jcomp26.addActionListener(this);
        jcomp27 = new JLabel ("Function");
        jcomp28 = new JLabel ("Arguments");
        jcomp29 = new JTextField (5);//func
        jcomp29.setToolTipText("InitLedger, GetAllAssets, CreateAsset, TransferAsset, ReadAsset, UpdateAsset");
        jcomp30 = new JTextField (5);
        jcomp30.setToolTipText("Asset ID");
        jcomp31 = new JTextField (5);
        jcomp31.setToolTipText("Asset Color");
        jcomp32 = new JTextField (5);
        jcomp32.setToolTipText("Asset Size");
        jcomp33 = new JTextField (5);
        jcomp33.setToolTipText("Asset Owner");
        jcomp34 = new JTextField (5);
        jcomp34.setToolTipText("Asset Value");
        //adjust size and set layout
        setPreferredSize (new Dimension (944, 574));
        setLayout (null);

        //add components
        add (jcomp1);
        add (jcomp2);
        add (jcomp3);
        add (jcomp4);
        add (jcomp5);
        add (jcomp6);
        add (jcomp7);
        add (jcomp8);
        add (jcomp9);
        add (jcomp10);
        add (jcomp11);
        add (jcomp12);
        add (jcomp13);
        add (jcomp14);
        add (jcomp15);
        add (jcomp16);
        add (jcomp17);
        add (jcomp18);
        add (jcomp19);
        add (jcomp20);
        add (jcomp21);
        add (jcomp22);
        add (jcomp23);
        add (jcomp24);
        add (jcomp245);
        add (jcomp25);
        add (jcomp26);
        add (jcomp27);
        add (jcomp28);
        add (jcomp29);
        add (jcomp30);
        add (jcomp31);
        add (jcomp32);
        add (jcomp33);
        add (jcomp34);

        //set component bounds (only needed by Absolute Positioning)
        jcomp1.setBounds (10, 30, 100, 25);
        jcomp2.setBounds (10, 130, 127, 25);
        jcomp3.setBounds (10, 230, 100, 25);
        jcomp4.setBounds (10, 420, 100, 25);
        jcomp5.setBounds (775, 62, 150, 50);
        jcomp6.setBounds (205, 80, 134, 25);
        jcomp7.setBounds (344, 80, 121, 25);
        jcomp8.setBounds (100, 80, 100, 25);
        jcomp9.setBounds (50, 80, 100, 25);
        jcomp10.setBounds (50, 180, 100, 25);
        jcomp11.setBounds (355, 180, 100, 25);
        jcomp12.setBounds (50, 280, 100, 25);
        jcomp13.setBounds (325, 280, 100, 25);
        jcomp14.setBounds (775, 162, 150, 50);
        jcomp15.setBounds (775, 262, 150, 50);
        jcomp16.setBounds (775, 335, 72, 50);
        jcomp17.setBounds (853, 335, 73, 50);
        jcomp18.setBounds (50, 470, 109, 25);
        jcomp19.setBounds (295, 470, 115, 25);
        jcomp20.setBounds (414, 470, 90, 25);
        jcomp21.setBounds (510, 470, 166, 25);
        jcomp22.setBounds (135, 180, 100, 25);
        jcomp23.setBounds (437, 180, 100, 25);
        jcomp24.setBounds (137, 280, 100, 25);
        jcomp245.setBounds (605, 180, 100, 25);
        jcomp25.setBounds (425, 280, 100, 25);
        jcomp26.setBounds (775, 390, 150, 50);
        jcomp27.setBounds (50, 350, 50, 25);
        jcomp28.setBounds (220, 350, 69, 25);
        jcomp29.setBounds (105, 350, 100, 25);
        jcomp30.setBounds (286, 350, 75, 25);
        jcomp31.setBounds (364, 350, 75, 25);
        jcomp32.setBounds (443, 350, 75, 25);
        jcomp33.setBounds (522, 350, 75, 25);
        jcomp34.setBounds (600, 350, 75, 25);
    }


    public static void main (String[] args) {
        frame = new JFrame ();
        frame.setTitle("Sign in Required ...");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add (new App());
        frame.setResizable(false);
        frame.pack();
        frame.setVisible (true);
    }

    private CloseableIterator<ChaincodeEvent> startChaincodeEventListening() {
        System.out.println("\n*** Start chaincode event listening");

        CloseableIterator<ChaincodeEvent> eventIter = this.network_d.getChaincodeEvents(this.contract_d.getChaincodeName());

        CompletableFuture.runAsync(() -> {
            eventIter.forEachRemaining(event -> {
                String payload = prettyJson(event.getPayload());
                System.out.println("\n<-- Chaincode event received: " + event.getEventName() + " - " + payload);
                showDialog(false, "Chaincode event received:" + event.getEventName(), payload);
            });
        });

        return eventIter;
    }




    private void prepareWallet() {
        try {
            System.out.println(this.jcomp8.getText() + this.p_key_d + this.pub_key_d);
            this.user_d = new User(this.jcomp8.getText(), this.pub_key_d, this.p_key_d);
            frame.setTitle(this.user_d.getSubjectDN());
            int color = this.jcomp8.getText().hashCode() % 5;

            URL url = App.class.getClassLoader().getResource("img/" + Integer.toString(color) + ".jpeg");
            frame.setIconImage(ImageIO.read(url));
            this.jcomp5.setBorder(greenBorder);
        } catch (InvalidKeyException e) {
            showDialog(true, "InvalidKeyException", e.getMessage());
            this.jcomp5.setBorder(redBorder);
        } catch (CertificateException e) {
            showDialog(true, "CertificateException", e.getMessage());
            this.jcomp5.setBorder(redBorder);
        } catch (Exception e) {
            showDialog(true, "Exception", e.getMessage());
            this.jcomp5.setBorder(redBorder);
        }
    }

    private void connectNetwork() {
        try {
            System.out.println(this.jcomp22.getText()+this.jcomp23.getText()+this.tls_cert_d);
            this.gateway_d = new GatewayBuilder(this.jcomp22.getText(), this.user_d, this.tls_cert_d, this.jcomp23.getText()).getGateway();
            this.jcomp14.setBorder(greenBorder);
        } catch (CertificateException e) {
            showDialog(true, "CertificateException", e.getMessage());
            this.jcomp14.setBorder(redBorder);
        } catch (Exception e) {
            showDialog(true, "Exception", e.getMessage());
            this.jcomp14.setBorder(redBorder);
        }
    }

    private void contractRef() {
        network_d = this.gateway_d.getNetwork(this.jcomp24.getText());
        this.contract_d = network_d.getContract(this.jcomp25.getText());
        showDialog(false, "Chaincode name", this.contract_d.getChaincodeName());
        System.out.println(this.contract_d.getChaincodeName());
        this.jcomp15.setBorder(greenBorder);
    }

    private void query() {
        String[] args = getArgs();
        byte[] evaluateResult = null;
        try {
            if (args == null) {
                evaluateResult = this.contract_d.evaluateTransaction(this.jcomp29.getText());
            } else {
                evaluateResult = this.contract_d.evaluateTransaction(this.jcomp29.getText(), args);
            }
            this.jcomp16.setBorder(greenBorder);
        } catch (GatewayException e) {
            this.jcomp16.setBorder(redBorder);
            showDialog(true, "GatewayException", e.getMessage());
        }


        if (evaluateResult != null) {
            System.out.println(new String(evaluateResult));
            showDialog(false, "Response Payload", new String(evaluateResult));
        }
    }

    private void update() {
        String[] args = getArgs();
        try {
            byte[] resp;
            if (args == null) {
                System.out.println("Null  " + this.jcomp29.getText());
                resp = this.contract_d.submitTransaction(this.jcomp29.getText());
            } else {
                System.out.println("Not Null");
                resp = this.contract_d.submitTransaction(this.jcomp29.getText(), args);
            }
            if (resp != null) {
                System.out.println(new String(resp));
                showDialog(false, "Response Payload", new String(resp));
            }
            this.jcomp17.setBorder(greenBorder);
        } catch (EndorseException e) {
            this.jcomp17.setBorder(redBorder);
            showDialog(true, "EndorseException", e.getMessage());
            System.out.println(e.getMessage());
        } catch (SubmitException e) {
            this.jcomp17.setBorder(redBorder);
            showDialog(true, "SubmitException", e.getMessage());
            System.out.println(e.getMessage());
        } catch (CommitStatusException e) {
            this.jcomp17.setBorder(redBorder);
            showDialog(true, "CommitStatusException", e.getMessage());
            System.out.println(e.getMessage());
        } catch (CommitException e) {
            this.jcomp17.setBorder(redBorder);
            showDialog(true, "CommitException", e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    private void async() {
        String[] args = getArgs();
        Commit commit;
        try {
            if (args == null) {
                commit = this.contract_d.newProposal(this.jcomp29.getText())
                        .build()
                        .endorse()
                        .submitAsync();
            } else {
                commit = this.contract_d.newProposal(this.jcomp29.getText())
                        .addArguments(args)
                        .build()
                        .endorse()
                        .submitAsync();
            }
            if (commit != null) {
                System.out.println(commit.getTransactionId());
                showDialog(false, "Transaction ID", commit.getTransactionId());
            }
            this.jcomp26.setBorder(greenBorder);
        } catch (SubmitException e) {
            this.jcomp26.setBorder(redBorder);
            showDialog(true, "SubmitException", e.getMessage());
        } catch (EndorseException e) {
            this.jcomp26.setBorder(redBorder);
            showDialog(true, "EndorseException", e.getMessage());
        }
    }

    private String[] getArgs() {
        String[] params = {this.jcomp30.getText(), this.jcomp31.getText(), this.jcomp32.getText(), this.jcomp33.getText(), this.jcomp34.getText()};
        System.out.println(Arrays.asList(params));
        ArrayList<String> nz_params = new ArrayList<String>();
        for (String param: params) {
            if (!param.equals("")) {
                nz_params.add(param);
            } else {
                break;
            }
        }
        String[] vals = nz_params.toArray(new String[nz_params.size()]);
        System.out.println(Arrays.asList(vals));
        String[] outs = vals.length == 0 ? null : vals;
        return outs;
    }

    private void pickPrivateKey() {
        this.p_key_d = pickFile(null);
    }

    private void pickCertificate() {
        this.tls_cert_d = pickFile(certificateFilter);
    }

    private void pickPublicKey() {
        this.pub_key_d = pickFile(publicKeyFilter);
    }

    private Path pickFile(FileNameExtensionFilter filter) {
        JFileChooser choose = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        if (filter != null) {
            choose.setFileFilter(filter);
        }
        // Open the file
        int res = choose.showOpenDialog(null);
        // Save the file
        // int res = choose.showSaveDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = choose.getSelectedFile();
            System.out.println(file.getAbsolutePath());
            return file.toPath();
        }
        return null;
    }

    private String prettyJson(final byte[] json) {
        return prettyJson(new String(json, StandardCharsets.UTF_8));
    }

    private String prettyJson(final String json) {
        JsonElement parsedJson = JsonParser.parseString(json);
        return gson.toJson(parsedJson);
    }

    private void showDialog(boolean error, String title, String body) {
        JDialog d = new JDialog(frame);

        // create a label
        JTextArea  l = new JTextArea(body);

        // create a button
        JButton b = new JButton("Dismiss");

        // add Action Listener
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                d.dispose();
            }
        });

        // create a panel
        JPanel p = new JPanel();
        l.setColumns(100);
        l.setRows(5);
        p.add(l);
        p.add(b);

        // add panel to dialog
        d.add(p);
        String heading = error ? "Error: " + title: "Success: " + title;
        d.setTitle(heading);

        d.setSize(300, 300);
        d.setLocationRelativeTo(frame);
        d.setLocation(100, 100);
        d.setResizable(false);

        // set visibility of dialog
        d.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getSource());
        if (e.getSource() == this.jcomp5) {
            prepareWallet();
        } else if (e.getSource() == this.jcomp6) {
            pickPrivateKey();
        } else if (e.getSource() == this.jcomp7) {
            pickPublicKey();
        } else if (e.getSource() == this.jcomp14) {
            connectNetwork();
        } else if (e.getSource() == this.jcomp15) {
            contractRef();
        } else if (e.getSource() == this.jcomp16) {
            query();
        } else if (e.getSource() == this.jcomp17) {
            update();
        } else if (e.getSource() == this.jcomp245) {
            pickCertificate();
        } else if (e.getSource() == this.jcomp26) {
            async();
        } else {
        }
    }
}
