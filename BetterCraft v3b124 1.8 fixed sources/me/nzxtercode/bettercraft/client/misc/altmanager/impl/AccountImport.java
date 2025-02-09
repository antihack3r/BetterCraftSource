/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.altmanager.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.Account;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.AccountManager;

public class AccountImport
extends JPanel
implements ActionListener {
    private static final long serialVersionUID = 1L;
    public JButton openButton;
    private JFileChooser fc = new JFileChooser();

    public AccountImport() {
        this.fc.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        this.openButton = new JButton("Open a File...");
        this.openButton.addActionListener(this);
        this.add(this.openButton);
    }

    @Override
    public void actionPerformed(ActionEvent e2) {
        int returnVal;
        if (e2.getSource() == this.openButton && (returnVal = this.fc.showOpenDialog(this)) == 0) {
            try {
                Throwable throwable = null;
                Object var4_6 = null;
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(this.fc.getSelectedFile()));){
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] arguments = line.split(":");
                        int i2 = 0;
                        while (i2 < 2) {
                            arguments[i2].replace(" ", "");
                            ++i2;
                        }
                        AccountManager.getInstance().getAccounts().add(new Account(arguments[0], arguments[1], ""));
                    }
                    AccountManager.getInstance().save();
                }
                catch (Throwable throwable2) {
                    if (throwable == null) {
                        throwable = throwable2;
                    } else if (throwable != throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
            }
            catch (Exception ex2) {
                JOptionPane.showMessageDialog(null, "An error happened.", "ERROR", 1);
            }
        }
    }
}

