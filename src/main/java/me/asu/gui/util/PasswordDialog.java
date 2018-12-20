package me.asu.gui.util;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

/**
 * @author suk
 * @since 2018/8/20
 */
public class PasswordDialog {
    static {
        try {
            GUITools.initLookAndFeel();
        } catch (Exception e) {
            // ignore
        }
    }
    public static char[] show(String title) {
        JPasswordField pw = new JPasswordField();
        JOptionPane.showMessageDialog(null, pw, title,
                JOptionPane.PLAIN_MESSAGE);
        return pw.getPassword();
    }
}
