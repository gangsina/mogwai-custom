package de.erdesignerng.visual.common;

import com.bentengwu.view.TableMemoView;
import de.erdesignerng.model.ModelItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 这个类用来注册到 alt+1的快捷键中,用来显示表的具体说明.
 * 区别与以前的说明,这个说明是富文本的.
 * 可以做深入业务的说明.
 * @Author <a href="bentengwu@163.com">thender.xu</a>
 * @Date 2017/7/23 17:40.
 */
public class TableRichTextViewAction extends UICommand {

    protected final static Logger logger = LoggerFactory.getLogger(TableRichTextViewAction.class);
    @Override
    public void execute() {
        ERDesignerComponent component = ERDesignerComponent.getDefault();
        ModelItem modelItem = component.getSelectedObject();

        logger.info("{}",modelItem);

        File currentFile = component.currentEditingFile;
        TableMemoView memoView = new TableMemoView(modelItem.toString(), currentFile.getParent());
        memoView.show();
    }
}
