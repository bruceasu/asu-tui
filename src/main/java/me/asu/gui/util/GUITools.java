/*
 * Copyright © 2016 suk
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * “Software”), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package me.asu.gui.util;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

public class GUITools {
    private static volatile boolean initLaF = false;

    public static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public static void center(Window win) {
        int windowWidth = win.getWidth(); // 获得窗口宽
        int windowHeight = win.getHeight(); // 获得窗口高
        Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
        Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
        int screenWidth = screenSize.width; // 获取屏幕的宽
        int screenHeight = screenSize.height; // 获取屏幕的高
        win.setLocation(screenWidth / 2 - windowWidth / 2,
                screenHeight / 2 - windowHeight / 2);// 设置窗口居中显示
    }

    /**
     * attach Ctrl+a, Ctrl+x, Ctrl+v, Ctrl+z, Ctrl+r
     *
     * @param editors JTextComponent[]
     */
    public static void attachKeyListener(JTextComponent... editors) {
        UndoManager um = new UndoManager();//撤销管理类
        attachKeyListener(um, editors);
    }

    public static void attachKeyListener(final UndoManager um, JTextComponent... editors) {
        for (JTextComponent editor : editors) {
            editor.addKeyListener(GUITools.getKeyAdapter(editor, um));
            editor.getDocument().addUndoableEditListener(new UndoableEditListener() {
                @Override
                public void undoableEditHappened(UndoableEditEvent e) {
                    //编辑撤销的监听
                    um.addEdit(e.getEdit());
                }
            });
        }
    }

    /**
     * attach Ctrl+a, Ctrl+x, Ctrl+v, Ctrl+z, Ctrl+r
     *
     * @param component JTextComponent
     * @param um        UndoManager
     * @return KeyAdapter
     */
    public static KeyAdapter getKeyAdapter(final JTextComponent component, final UndoManager um) {

        return new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             *
             * @param e
             */
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_A/*全选*/
                        || e.getKeyCode() == KeyEvent.VK_C/* 复制 */
                        || e.getKeyCode() == KeyEvent.VK_X/*剪切*/ || e.getKeyCode() == KeyEvent.VK_V
                        || e.getKeyCode() == KeyEvent.VK_Z || e.getKeyCode() == KeyEvent.VK_R)) {
                    //                      System.out.println(e.getKeyCode());
                } else {
                    return;
                }
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A:
                        component.setSelectionStart(0);
                        component.setSelectionEnd(component.getText().length());
                        break;
                    case KeyEvent.VK_C:
                        String selectedText = component.getSelectedText();

                        if (isBlank(selectedText)) {
                            selectedText = component.getText();
                        }
                        //设置字符串
                        //构建String数据类型
                        StringSelection selection = new StringSelection(selectedText);
                        //添加文本到系统剪切板
                        clipboard.setContents(selection, null);
                        break;
                    case KeyEvent.VK_V:
                        Transferable content = clipboard.getContents(null);//从系统剪切板中获取数据
                        if (content.isDataFlavorSupported(DataFlavor.stringFlavor)) {//判断是否为文本类型
                            String text = null;//从数据中获取文本值
                            try {
                                text = (String) content.getTransferData(DataFlavor.stringFlavor);
                            } catch (UnsupportedFlavorException e1) {
                                e1.printStackTrace();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            if (text == null) {
                                return;
                            }
                            if (component instanceof JTextArea) {
                                ((JTextArea) component)
                                        .replaceRange(text, component.getSelectionStart(),
                                                component.getSelectionEnd());
                            } else if (component instanceof JTextComponent) {
                                ((JTextComponent) component).replaceSelection(text);
                            }
                            component.paintImmediately(component.getBounds());
                        }
                        break;
                    case KeyEvent.VK_X:
                        String selectedText2 = component.getSelectedText();
                        if (isBlank(selectedText2)) {
                            selectedText2 = component.getText();
                            component.setText("");
                        } else {
                            if (component instanceof JTextArea) {
                                ((JTextArea) component)
                                        .replaceRange("", component.getSelectionStart(),
                                                component.getSelectionEnd());
                            } else if (component instanceof JTextComponent) {
                                ((JTextComponent) component).replaceSelection("");
                            }
                        }
                        //设置字符串
                        //构建String数据类型
                        StringSelection selection2 = new StringSelection(selectedText2);
                        //添加文本到系统剪切板
                        clipboard.setContents(selection2, null);
                        break;
                    case KeyEvent.VK_R:
                        um.redo();
                        break;
                    case KeyEvent.VK_Z:
                        um.undo();
                    default:
                        break;
                }
            }
        };
    }

    private static boolean isBlank(String selectedText) {
        return selectedText == null || selectedText.trim().isEmpty();
    }

    public static void attachKeyListener(UndoManager um, java.util.List<JTextComponent> editors) {
        attachKeyListener(um, editors.toArray(new JTextComponent[0]));
    }

    public static void attachKeyListener(java.util.List<JTextComponent> editors) {
        UndoManager um = new UndoManager();//撤销管理类
        attachKeyListener(um, editors.toArray(new JTextComponent[0]));
    }

    public static void initLookAndFeel() {
        if (initLaF) {
            return;
        }
        initLaF = true;
        initFont();
        // Install WebLaF as appllication L&F
        // WebLookAndFeel.install();
        // But you can also use a few "old" ways to do the same stuff:
        // UIManager.setLookAndFeel(WebLookAndFeel.class.getCannoicalName());
        // UIManager.setLookAndFeel(new WebLookAndFeel());
        // UIManager.setLookAndFeel("com.alee.laf.WebLookAndFeel");

        String[] lafArray = new String[]{
                // WebLaF
                "com.alee.laf.WebLookAndFeel",

                // 苹果的，有版权，不允许在非苹果机上运行
                "ch.randelshofer.quaqua.leopard.Quaqua16LeopardLookAndFeel",
                "ch.randelshofer.quaqua.snowleopard.Quaqua16SnowLeopardLookAndFeel",
                "ch.randelshofer.quaqua.lion.Quaqua16LionLookAndFeel",
                "ch.randelshofer.quaqua.mountainlion.Quaqua16MountainLionLookAndFeel",
                "ch.randelshofer.quaqua.jaguar.Quaqua15JaguarLookAndFeel",
                "ch.randelshofer.quaqua.jaguar.Quaqua15JaguarLookAndFeel",
                "ch.randelshofer.quaqua.panther.Quaqua15PantherLookAndFeel",
                "ch.randelshofer.quaqua.panther.Quaqua15PantherLookAndFeel",
                "ch.randelshofer.quaqua.tiger.Quaqua15TigerLookAndFeel",
                "ch.randelshofer.quaqua.tiger.Quaqua15TigerLookAndFeel",
                "ch.randelshofer.quaqua.leopard.Quaqua15LeopardLookAndFeel",
                "ch.randelshofer.quaqua.leopard.Quaqua15LeopardLookAndFeel",
                "ch.randelshofer.quaqua.tiger.Quaqua15TigerCrossPlatformLookAndFeel",
                "ch.randelshofer.quaqua.leopard.Quaqua15LeopardCrossPlatformLookAndFeel",
                "ch.randelshofer.quaqua.leopard.Quaqua15LeopardCrossPlatformLookAndFeel",

                // jvm 自带的
                "com.sun.java.swing.plaf.windows.WindowsLookAndFeel",
                "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel",
                "javax.swing.plaf.nimbus.NimbusLookAndFeel",
                "com.sun.java.swing.plaf.gtk.GTKLookAndFeel",
                UIManager.getSystemLookAndFeelClassName(),
                UIManager.getCrossPlatformLookAndFeelClassName(),
                "com.sun.java.swing.plaf.motif.MotifLookAndFeel"

        };

        for (String lookAndFeel : lafArray) {
            try {
                UIManager.setLookAndFeel(lookAndFeel);

                // If L&F = "Metal", set the theme
                if (lookAndFeel.equals("Metal")) {
                    MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                    UIManager.setLookAndFeel(new MetalLookAndFeel());
                }
                //System.out.println("using " + lookAndFeel);
                break;
            } catch (ClassNotFoundException e) {
                //                System.err.println("Couldn't find class for specified look and feel:"
                //                        + lookAndFeel);
                //                System.err.println("Did you include the L&F library in the class path?");
                //                System.err.println("Using the default look and feel.");
            } catch (UnsupportedLookAndFeelException e) {
                //                System.err.println("Can't use the specified look and feel ("
                //                        + lookAndFeel
                //                        + ") on this platform.");
                //                System.err.println("Using the default look and feel.");
            } catch (Throwable e) {
                //                System.err.println("Couldn't get specified look and feel ("
                //                        + lookAndFeel
                //                        + "), for some reason.");
                //                e.printStackTrace();
            }
        }
    }

    public static void initFont() {
        FontUIResource fontUIResource = new FontUIResource(new Font("宋体", Font.PLAIN, 12));
        for (Enumeration keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontUIResource);
            }
        }
    }

}
