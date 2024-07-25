package org.addon.friza;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.AbstractPanel;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.utils.FontUtils;
import org.zaproxy.zap.view.ZapMenuItem;


public class ExtensionFriza extends ExtensionAdaptor implements ActionListener {

    // The name is public so that other extensions can access it
    public static final String NAME = "ExtensionFriza";

    // The i18n prefix, by default the package name - defined in one place to make it easier
    // to copy and change this example
    protected static final String PREFIX = "friza";

   
    private static final String RESOURCES = "resources";

    private ZapMenuItem menuExample;

    private RightClickMsgMenu popupMsgMenuExample;
    private AbstractPanel statusPanel;

    private FrizaAPI api;

    private JTextField pythonText;
    private JPanel pythonPathPanel;
    private JLabel pythonPort;
    private JTextPane serverStatus;
    private JTextPane appStatus;
    private JTextPane applicationStatus;
    private JButton selectFile;

    private PrintWriter stderr;
    private PrintWriter stdout;

    DefaultStyledDocument documentServerStatus;
    DefaultStyledDocument documentAppStatus;

    private static final Logger LOGGER = LogManager.getLogger(ExtensionFriza.class);

    public ExtensionFriza() {
        super(NAME);
        setI18nPrefix(PREFIX);
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);

        this.api = new FrizaAPI();
        extensionHook.addApiImplementor(this.api);

       
        if (hasView()) {
            extensionHook.getHookMenu().addToolsMenuItem(getMenuExample());
            extensionHook.getHookMenu().addPopupMenuItem(getPopupMsgMenuExample());
            extensionHook.getHookView().addStatusPanel(getStatusPanel());
        }
    }

    @Override
    public boolean canUnload() {
      
        return true;
    }

    @Override
    public void unload() {
        super.unload();
    }

    private AbstractPanel getStatusPanel() {
        if (statusPanel == null) {
            statusPanel = new AbstractPanel(); 
            statusPanel.setLayout(new CardLayout());
            statusPanel.setName(Constant.messages.getString(PREFIX + ".panel.title"));
            // statusPanel.setIcon(new ImageIcon(getClass().getResource(RESOURCES + "/cake.png")));
            JTextPane pane = new JTextPane();
            pane.setEditable(false);
            
            pane.setFont(FontUtils.getFont("Dialog", Font.PLAIN));
            pane.setContentType("text/html");
            pane.setText(Constant.messages.getString(PREFIX + ".panel.msg"));

            JTabbedPane tabbedPane = new JTabbedPane();
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

            JPanel jsEditorPanel = new JPanel();
            JPanel binaryPanel = new JPanel();

            JPanel configPanel = new JPanel();
            configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));

            JPanel rightSplitPane = new JPanel();
            rightSplitPane.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // ***** server status , just using SERVER status Label
            StyleContext styleContext = new StyleContext(); // RED color
            Style redStyle = styleContext.addStyle("red", null);
            StyleConstants.setForeground(redStyle, Color.RED);

            // Green style
            Style greenStyle = styleContext.addStyle("green", null);
            StyleConstants.setForeground(greenStyle, Color.GREEN);

            JPanel serverStatusPanel = new JPanel();
            serverStatusPanel.setLayout(new BoxLayout(serverStatusPanel, BoxLayout.X_AXIS));
            serverStatusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel labelServerStatus = new JLabel("Server status: ");
            documentServerStatus = new DefaultStyledDocument();
            serverStatus = new JTextPane(documentServerStatus);

            try {
                documentServerStatus.insertString(0, "NOT RUNNING", redStyle);
            } catch (BadLocationException e) {
                stderr.println(e.toString());
            }
            serverStatus.setMaximumSize(serverStatus.getPreferredSize());
            serverStatusPanel.add(labelServerStatus);
            serverStatusPanel.add(serverStatus);

            // ****** Application status
            JPanel appStatusPanel = new JPanel();
            appStatusPanel.setLayout(new BoxLayout(appStatusPanel, BoxLayout.X_AXIS));
            appStatusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel labelAppStatus = new JLabel("Application status: ");
            documentAppStatus = new DefaultStyledDocument();
            appStatus = new JTextPane(documentAppStatus);
            try {
                documentAppStatus.insertString(0, "NOT SPAWNED", redStyle);
            } catch (BadLocationException e) {
                stderr.println(e.toString());
            }
            appStatus.setMaximumSize(appStatus.getPreferredSize());
            appStatusPanel.add(labelAppStatus);
            appStatusPanel.add(appStatus);

            // ********Pyro port
            JPanel pyroPortPanel = new JPanel();
            pyroPortPanel.setLayout(new BoxLayout(pyroPortPanel, BoxLayout.X_AXIS));
            pyroPortPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel pyroPort = new JLabel("Pyro port:");
            JTextField pyroText = new JTextField(100);
            pyroText.setMaximumSize(pyroPort.getPreferredSize());
            pyroPortPanel.add(pyroPort);
            pyroPortPanel.add(pyroText);

            // *******python path
            pythonPathPanel = new JPanel();
            pythonPathPanel.setLayout(new BoxLayout(pythonPathPanel, BoxLayout.X_AXIS));
            pythonPathPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            pythonPort = new JLabel("Python Binary Path:");
            pythonText = new JTextField(100); // class member
            selectFile = new JButton("Select file");
            selectFile.setActionCommand("select");
            selectFile.addActionListener(this);
            pythonText.setMaximumSize(pyroPort.getPreferredSize());
            pythonPathPanel.add(pythonPort);
            pythonPathPanel.add(pythonText);
            pythonPathPanel.add(selectFile);

            // ***** frida JS API
            JPanel fridaJSPanel = new JPanel();
            fridaJSPanel.setLayout(new BoxLayout(fridaJSPanel, BoxLayout.X_AXIS));
            fridaJSPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel labelFridaJS = new JLabel("Frida JS files");
            JTextField fridaJSText = new JTextField(100);
            JButton selectJS = new JButton("Select files");
            JButton createJS = new JButton("Create JS files");
            fridaJSText.setMaximumSize(labelFridaJS.getPreferredSize());
            fridaJSPanel.add(labelFridaJS);
            fridaJSPanel.add(fridaJSText);
            fridaJSPanel.add(selectJS);
            fridaJSPanel.add(createJS);

            // **** Application ID
            JPanel appIDPanel = new JPanel();
            appIDPanel.setLayout(new BoxLayout(appIDPanel, BoxLayout.X_AXIS));
            appIDPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel labelAppID = new JLabel("Application ID/PID");
            JTextField appField = new JTextField(100);
            appField.setMaximumSize(labelAppID.getPreferredSize());
            appIDPanel.add(labelAppID);
            appIDPanel.add(appField);

            // *****Adding all mini panels
            configPanel.add(serverStatusPanel);
            configPanel.add(appStatusPanel);
            configPanel.add(pythonPathPanel);
            configPanel.add(pyroPortPanel);
            configPanel.add(fridaJSPanel);
            configPanel.add(appIDPanel);

            // RIGHT PANE
            JButton startServer = new JButton("Start server");
            JButton killServer = new JButton("Kill server");
            JButton spawnApplication = new JButton("Spawn application");
            JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
            separator.setBorder(BorderFactory.createMatteBorder(3, 0, 3, 0, Color.ORANGE));
            rightSplitPane.add(startServer, gbc);
            rightSplitPane.add(killServer, gbc);
            rightSplitPane.add(spawnApplication, gbc);
            rightSplitPane.add(separator, gbc);

            tabbedPane.setBounds(50, 50, 200, 200);
            tabbedPane.add("Configuration", configPanel);
            tabbedPane.add("JS Editor", jsEditorPanel);
            tabbedPane.add("Graphical Analysis", binaryPanel);

            splitPane.setLeftComponent(tabbedPane);
            splitPane.setRightComponent(rightSplitPane);
            splitPane.setResizeWeight(.9d);
            statusPanel.add(splitPane);

            statusPanel.add(pane);
        }
        return statusPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("select")) {

            JFrame parentFrame = new JFrame();
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Python binary path");
            int result = fileChooser.showOpenDialog(parentFrame);

            if (result == JFileChooser.APPROVE_OPTION) {
                final File selectedFile = fileChooser.getSelectedFile();
                SwingUtilities.invokeLater(
                        new Runnable() {
                            @Override
                            public void run() {
                                pythonText.setText(selectedFile.getAbsolutePath());
                            }
                        });
            }
        }
    }

    private ZapMenuItem getMenuExample() {
        if (menuExample == null) {
            menuExample = new ZapMenuItem(PREFIX + ".topmenu.tools.title");

            menuExample.addActionListener(
                    e -> {
                       
                        View.getSingleton()
                                .showMessageDialog(
                                        Constant.messages.getString(PREFIX + ".topmenu.tools.msg"));
                        
                    });
        }
        return menuExample;
    }

    private void displayFile(String file) {
        if (!View.isInitialised()) {
            // Running in daemon mode, shouldnt have been called
            return;
        }
        try {
            File f = new File(Constant.getZapHome(), file);
            if (!f.exists()) {
               
                View.getSingleton()
                        .showWarningDialog(
                                Constant.messages.getString(
                                        ExtensionFriza.PREFIX + ".error.nofile",
                                        f.getAbsolutePath()));
                return;
            }
            // Quick way to read a small text file
            String contents = new String(Files.readAllBytes(f.toPath()));
            // Write to the output panel
            View.getSingleton().getOutputPanel().append(contents);
            // Give focus to the Output tab
            View.getSingleton().getOutputPanel().setTabFocus();
        } catch (Exception e) {
            // Something unexpected went wrong, write the error to the log
            LOGGER.error(e.getMessage(), e);
        }
    }

    private RightClickMsgMenu getPopupMsgMenuExample() {
        if (popupMsgMenuExample == null) {
            popupMsgMenuExample =
                    new RightClickMsgMenu(
                            this, Constant.messages.getString(PREFIX + ".popup.title"));
        }
        return popupMsgMenuExample;
    }

    @Override
    public String getDescription() {
        return Constant.messages.getString(PREFIX + ".desc");
    }
}
