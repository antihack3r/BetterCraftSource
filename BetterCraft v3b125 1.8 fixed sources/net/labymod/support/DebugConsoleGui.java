/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.support;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import net.labymod.addon.AddonLoader;
import net.labymod.api.LabyModAddon;
import net.labymod.core.asm.LabyModCoreMod;
import net.labymod.main.LabyMod;
import net.labymod.main.Source;
import net.labymod.support.util.Debug;
import net.labymod.support.util.Hastebin;
import net.labymod.user.User;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.labymod.utils.Consumer;
import net.labymod.utils.ModUtils;
import net.minecraft.client.Minecraft;

public class DebugConsoleGui
extends JFrame {
    private final JTextPane textPane = new JTextPane();
    private long idle = 0L;

    public DebugConsoleGui(final Consumer<Boolean> consumer) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        this.setSize(900, 520);
        this.setLocationRelativeTo(null);
        this.setTitle("LabyMod Debug Console");
        try {
            this.setIconImage(Toolkit.getDefaultToolkit().getImage(LabyMod.class.getResource("/assets/minecraft/labymod/textures/labymod_logo.png")));
        }
        catch (Exception error) {
            error.printStackTrace();
        }
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e2) {
                consumer.accept(true);
                e2.getWindow().dispose();
            }
        });
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), 1));
        JPanel controlPanel = new JPanel();
        this.getContentPane().add(controlPanel);
        controlPanel.setLayout(new FlowLayout(2, 5, 5));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        JButton uploadButton = new JButton("Upload");
        uploadButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e2) {
                String log = DebugConsoleGui.this.createLog();
                Hastebin.upload(log);
            }
        });
        controlPanel.add(uploadButton);
        JPanel guiPanel = new JPanel();
        controlPanel.add(guiPanel);
        guiPanel.setLayout(new BorderLayout(0, 0));
        JButton restartDebugButton = new JButton("Restart in debug");
        restartDebugButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e2) {
                try {
                    Debug.DEBUG_FILE.createNewFile();
                }
                catch (IOException e22) {
                    e22.printStackTrace();
                }
                Minecraft.getMinecraft().shutdown();
            }
        });
        guiPanel.add(restartDebugButton);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));
        this.textPane.setFont(new Font("Courier New", 0, 15));
        this.textPane.setEditable(false);
        this.textPane.addCaretListener(new CaretListener(){

            @Override
            public void caretUpdate(CaretEvent evt) {
                if (evt.getDot() == evt.getMark()) {
                    return;
                }
                JTextPane txtPane = (JTextPane)evt.getSource();
                DefaultHighlighter highlighter = (DefaultHighlighter)txtPane.getHighlighter();
                highlighter.removeAllHighlights();
                DefaultHighlighter.DefaultHighlightPainter hPainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(0xFFAA00));
                String selText = txtPane.getSelectedText();
                if (selText.isEmpty() || selText.equals("\n")) {
                    return;
                }
                String contText = "";
                DefaultStyledDocument document = (DefaultStyledDocument)txtPane.getDocument();
                try {
                    contText = document.getText(0, document.getLength());
                    if (contText.isEmpty() || contText.equals("\n")) {
                        return;
                    }
                }
                catch (BadLocationException ex2) {
                    ex2.printStackTrace();
                }
                int index = 0;
                while ((index = contText.indexOf(selText, index)) > -1) {
                    try {
                        highlighter.addHighlight(index, selText.length() + index, hPainter);
                        index += selText.length();
                    }
                    catch (BadLocationException ex2) {
                        ex2.printStackTrace();
                    }
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(this.textPane);
        panel.add(scrollPane);
        this.getContentPane().add(panel);
        this.setVisible(true);
        new Thread(new Runnable(){

            @Override
            public void run() {
                Debug.log(Debug.EnumDebugMode.GENERAL, "Debug console started");
                try {
                    BufferedReader br2 = new BufferedReader(new FileReader(new File("logs/latest.log")));
                    while (DebugConsoleGui.this.isVisible()) {
                        DebugConsoleGui.this.readLogFile(br2);
                        if (DebugConsoleGui.this.idle >= System.currentTimeMillis()) continue;
                        try {
                            Thread.sleep(2000L);
                        }
                        catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
                Debug.log(Debug.EnumDebugMode.GENERAL, "Debug console closed");
            }
        }).start();
    }

    private String createLog() {
        String log = this.textPane.getText();
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long percent = usedMemory * 100L / maxMemory;
        String addons = "";
        for (LabyModAddon addon : AddonLoader.getAddons()) {
            if (addon == null || addon.about == null) continue;
            addons = String.valueOf(addons) + addon.about.name + ", ";
        }
        User user = LabyMod.getInstance().getUserManager().getUser(LabyMod.getInstance().getPlayerUUID());
        log = String.valueOf(log) + "\n---------------------------------------";
        log = String.valueOf(log) + "\nTime: " + new Date().toString();
        log = String.valueOf(log) + "\nUsername: " + LabyMod.getInstance().getPlayerName();
        log = String.valueOf(log) + "\nUUID: " + user.getUuid().toString();
        log = String.valueOf(log) + "\nGroup: " + (user.getGroup() == null ? "NONE" : user.getGroup().getName());
        log = String.valueOf(log) + "\nWhitelist: " + LabyMod.getInstance().getUserManager().isWhitelisted(user.getUuid());
        log = String.valueOf(log) + "\nCosmetics: " + user.getCosmetics().size();
        for (CosmeticData cos : user.getCosmetics().values()) {
            log = String.valueOf(log) + "\n- " + cos.getClass().getSimpleName();
        }
        log = String.valueOf(log) + "\nMemory: " + (int)percent + "% (" + ModUtils.humanReadableByteCount(maxMemory, true, true) + ")";
        log = String.valueOf(log) + "\nAddons: [" + AddonLoader.getAddons().size() + "] " + addons;
        log = String.valueOf(log) + "\nForge: " + LabyModCoreMod.isForge();
        log = String.valueOf(log) + "\nVersion: " + Source.getUserAgent();
        log = String.valueOf(log) + "\n---------------------------------------";
        return log;
    }

    private void readLogFile(BufferedReader br2) {
        try {
            String line;
            boolean changed = false;
            while ((line = br2.readLine()) != null) {
                if (line.contains("Session ID is token")) continue;
                this.addString(String.valueOf(line) + "\n");
                changed = true;
            }
            if (changed) {
                this.textPane.repaint();
                this.idle = System.currentTimeMillis() + 5000L;
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void addString(String string) {
        try {
            Document doc = this.textPane.getDocument();
            StringReader sr2 = new StringReader(string);
            EditorKit ek2 = this.textPane.getEditorKit();
            ek2.read(sr2, doc, this.textPane.getDocument().getLength());
            this.textPane.setCaretPosition(doc.getLength());
        }
        catch (Exception error) {
            error.printStackTrace();
        }
    }
}

