package com.bentengwu.view;

import com.bentengwu.component.dialog.JDialogUtils;
import com.bentengwu.config.MogwaiProperties;
import com.bentengwu.mogwaidemosupport.EditorView;
import com.bentengwu.mogwaidemosupport.XDocBuilderExt;
import com.hg.doc.a2;
import com.hg.doc.aa;
import com.hg.xdoc.XDoc;
import com.hg.xdoc.XDocBuilder;
import de.erdesignerng.DialogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

/**
 * 这个界面用来操作表的富文本说明.
 * @Author <a href="bentengwu@163.com">thender.xu</a>
 * @Date 2017/7/21 17:02.
 */
public class TableMemoView {
    protected final static Logger logger = LoggerFactory.getLogger(TableMemoView.class);
    private TableMemoView() {
    }

    /**
     * @param tableName 表名
     * @param storeDirectory 存放的路径.  ${storeDirectory}+File.sep
     *                       + ${rootDirctoryName}
     *                       + File.sep + ${tableName}
     *                       + "." +${storeFileSuffix}
     */
    public TableMemoView(String tableName, String storeDirectory) {
        this.tableName = tableName;
        this.storeDirectory = storeDirectory;
        readOrInitXdoc(getTableName(),getStoreDirectory());
    }

    /**
     * 将读取${storeDirectory}+File.sep
     *                       + ${rootDirctoryName}
     *                       + File.sep + ${tableName}
     *                       + "." +${storeFileSuffix} 文件.如果不存在文件,则创建.
     * @param tableName
     * @param storeDirectory
     */
    private void readOrInitXdoc(String tableName, String storeDirectory) {
        String workDirectory = storeDirectory + File.separator
                + getRootDirctoryName() ;

        String filePath = workDirectory + File.separator
                + tableName + "." + getStoreFileSuffix();
        try {
            EditorView.getEditorView().getXdoc().closeAll();
            EditorView.getEditorView().open(filePath,true);
        } catch (Exception ex) {
            JDialogUtils.showMsg(ex.getMessage());
        }
    }

    /**
     * 表名称. 加载数据时. 读取document下的 ${tableName}.xdoc文件.
     */
    private String tableName;
    private String storeDirectory;//存放的路径地址.
    private String rootDirctoryName="document";//${storeDirectory}下的跟目录名. 默认是document.
    private String storeFileSuffix = "xdoc";//文件类型. 默认使用xdoc. 当配置文件中没有配置的话.
    private EditorView jc; //用于内容xdoc的

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getStoreDirectory() {
        return storeDirectory;
    }

    public void setStoreDirectory(String storeDirectory) {
        this.storeDirectory = storeDirectory;
    }

    public String getRootDirctoryName() {
        return rootDirctoryName;
    }

    public void setRootDirctoryName(String rootDirctoryName) {
        this.rootDirctoryName = rootDirctoryName;
    }

    public String getStoreFileSuffix() {
        try {
            return MogwaiProperties.get("TABLE_MEMO_FILE_SUFFIX");
        } catch (Exception ex) {
            return storeFileSuffix;
        }
    }

    public void setStoreFileSuffix(String storeFileSuffix) {
        this.storeFileSuffix = storeFileSuffix;
    }


    public void show() {
        EditorView.getEditorView().setVisible(true);
        logger.info("isFocusable={},isFocused={}",EditorView.getEditorView().isFocusable(),EditorView.getEditorView().isFocused());
        if(!EditorView.getEditorView().isFocused()){
            EditorView.getEditorView().requestFocus();
        }
    }

    public void hide(){
        EditorView.getEditorView().setVisible(false);
    }

    public static void main(String[] args) {
        TableMemoView view = new TableMemoView("tbl_test", "e:\\tmp\\1");
        view.show();

        EditorView ev =  EditorView.getEditorView();
        XDocBuilderExt xd = (XDocBuilderExt)ev.getXdoc();

        aa a = xd.getAA();

        xd.open("e:\\tmp\\1\\document\\tbl_test.xdoc");
        xd.open("e:\\tmp\\1\\document\\tbl_test.xdoc");
//        xd.closeAll();
        for (int i = 0; i < xd.getXDocCount(); i++) {
            XDoc xdd = xd.getXDoc(i);
            System.out.println(xdd);
        }

    }
}
