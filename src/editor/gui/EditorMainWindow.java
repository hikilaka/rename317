package editor.gui;

import editor.EditorMain;
import editor.gui.dockables.EditorToolbar;
import editor.gui.dockables.FloorTypeSelection;
import editor.gui.dockables.SettingsBrushEditor;
import editor.gui.dockables.ToolSelectionBar;
import editor.gui.dockables.flooreditor.FloorEditorWindow;
import editor.renderer.GameViewPanel;
import editor.renderer.MapViewPanel;
import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 6/24/11
 * Time: 5:27 PM
 * Computer: Peterbjornx-PC.rootdomain.asn.local (192.168.178.27)
 */
public class EditorMainWindow {
    private JFrame rootFrame;
    private ToolWindowManager toolWindowManager;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem openCacheMenuItem;
    private JMenuItem newMapMenuItem;
    private JMenuItem openMapMenuItem;
    private JMenuItem saveMapMenuItem;
    private JMenuItem exitMenuItem;
    private JMenuItem preferencesMenuItem;
    private JMenu editMenu;
    private JMenuItem copyTileMenuItem;
    private JMenuItem pasteTileMenuItem;

    private ToolSelectionBar toolSelectionBar;
    private FloorTypeSelection floorTypeSelectionWindow;
    private GameViewPanel gameViewPanel;
    private FloorEditorWindow floorEditorWindow;
    private SettingsBrushEditor settingsBrushEditorWindow;
    private MapViewPanel mapViewPanel;
    private EditorToolbar editorToolbar;
    private EditorMain editor;


    public EditorMainWindow(){
        initGUI();
    }

    private void initGUI(){
        rootFrame = new JFrame("RuneScape Map Editor");
        rootFrame.setSize(1000,786);
        initMenus();
        rootFrame.getContentPane().setLayout(new TableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));
        initToolWindows();
        ContentManager contentManager = toolWindowManager.getContentManager();
        gameViewPanel = new GameViewPanel();
        Content content = contentManager.addContent("lol",
                                                    "3D Viewport",
                                                    null,      // An icon
                                                    gameViewPanel);
        loadWorkspace();
    }

    private void initToolWindows() {
        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager();
        toolWindowManager = myDoggyToolWindowManager;
        toolSelectionBar = new ToolSelectionBar();
        toolWindowManager.registerToolWindow("Tools", null, null, toolSelectionBar.getMainPane(), ToolWindowAnchor.TOP);
        editorToolbar = new EditorToolbar();
        toolWindowManager.registerToolWindow("Toolbar", null, null, editorToolbar.getMainPane(), ToolWindowAnchor.TOP);
        editorToolbar.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (editorToolbar.getHeightlevel() != editor.getHeightLevel())
                    editor.setHeightLevel(editorToolbar.getHeightlevel());
                editor.setShowAllHLs(editorToolbar.getShowAllHLs());
                editor.setTargetHeight(editorToolbar.getTargetHeight());
                if (e != null)
                    editor.setRenderSettings(editorToolbar.getSettings());
            }
        });
        floorTypeSelectionWindow = new FloorTypeSelection();
        floorTypeSelectionWindow.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if ((!floorEditorWindow.isDirty()) || JOptionPane.showConfirmDialog(rootFrame
                        ,"Are you sure you want to lose all changes to the edited floor?","RuneScape Map Editor",
                        JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION )
                    if (floorTypeSelectionWindow.getSelectedFloorId() != -1)
                        floorEditorWindow.setCurrentFloor(floorTypeSelectionWindow.getSelectedFloor());
            }
        });
        toolWindowManager.registerToolWindow("Floors","Floor type selection",null,floorTypeSelectionWindow, ToolWindowAnchor.LEFT);
        floorEditorWindow = new FloorEditorWindow();
        floorEditorWindow.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                floorTypeSelectionWindow.refresh();
            }
        });
        toolWindowManager.registerToolWindow("Floor Editor", "Floor editor", null, floorEditorWindow.getMainPane(), ToolWindowAnchor.RIGHT);
        mapViewPanel = new MapViewPanel();
        toolWindowManager.registerToolWindow("Minimap", null, null, mapViewPanel, ToolWindowAnchor.RIGHT);
        settingsBrushEditorWindow = new SettingsBrushEditor();
        toolWindowManager.registerToolWindow("Settings Editor","Settings brush editor",null,settingsBrushEditorWindow.getMainPane(), ToolWindowAnchor.RIGHT);
        for (ToolWindow window : toolWindowManager.getToolWindows())
            window.setAvailable(true);
        rootFrame.getContentPane().add(myDoggyToolWindowManager, "1,1,");
    }

    private void initMenus() {
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        newMapMenuItem = new JMenuItem("New map...");
        openMapMenuItem = new JMenuItem("Open map...");
        saveMapMenuItem = new JMenuItem("Save map");
        //----
        openCacheMenuItem = new JMenuItem("Open cache...");
        //----
        preferencesMenuItem = new JMenuItem("Preferences...");
        exitMenuItem = new JMenuItem("Exit");

        fileMenu.add(newMapMenuItem);
        fileMenu.add(openMapMenuItem);
        fileMenu.add(saveMapMenuItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(openCacheMenuItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(preferencesMenuItem);
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        editMenu = new JMenu("Edit");
        copyTileMenuItem = new JMenuItem("Copy tile");
        pasteTileMenuItem = new JMenuItem("Paste tile");

        editMenu.add(copyTileMenuItem);
        editMenu.add(pasteTileMenuItem);

        menuBar.add(editMenu);
        rootFrame.setJMenuBar(menuBar);
        openMapMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //For now use a JOptionPane
                try {
                    String[] s = JOptionPane.showInputDialog(rootFrame,"Coordinates?").split(",");
                    editor.loadMap(Integer.parseInt(s[0]),Integer.parseInt(s[1]));
                } catch (Exception e2){
                    JOptionPane.showMessageDialog(rootFrame,"Invalid coordinates, use format (X,Y)","RuneScape Map Editor",JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        saveMapMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //For now use a JOptionPane
                editor.saveMap();
            }
        });
    }

    public void show() {
        rootFrame.setVisible(true);
    }

    public GameViewPanel getGameViewPanel() {
        return gameViewPanel;
    }

    public MapViewPanel getMapViewPanel() {
        return mapViewPanel;
    }

    public void editorStarted(EditorMain editor) {
        this.editor = editor;
        floorTypeSelectionWindow.loadFloors();
        rootFrame.addWindowListener(editor);
    }

    public ToolSelectionBar getToolSelectionBar() {
        return toolSelectionBar;
    }

    public FloorTypeSelection getFloorTypeSelectionWindow() {
        return floorTypeSelectionWindow;
    }

    public SettingsBrushEditor getSettingsBrushEditorWindow() {
        return settingsBrushEditorWindow;
    }

    public FloorEditorWindow getFloorEditorWindow() {
        return floorEditorWindow;
    }

    public void storeWorkspace() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("./workspace.xml");
            toolWindowManager.getPersistenceDelegate().save(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e){
            JOptionPane.showMessageDialog(rootFrame,"Error storing workspace","Runescape Map Editor",JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadWorkspace() {
        try {
            FileInputStream fileInputStream = new FileInputStream("./workspace.xml");
            toolWindowManager.getPersistenceDelegate().apply(fileInputStream);
            fileInputStream.close();
        } catch (Exception e){
            JOptionPane.showMessageDialog(rootFrame,"Error loading workspace","Runescape Map Editor",JOptionPane.ERROR_MESSAGE);
        }
    }

    public EditorToolbar getEditorToolbar() {
        return editorToolbar;
    }
}
