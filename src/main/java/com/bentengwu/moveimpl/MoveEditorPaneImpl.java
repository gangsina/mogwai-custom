package com.bentengwu.moveimpl;

import com.bentengwu.move_editorpane;
import org.jgraph.JGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @Author <a href="bentengwu@163.com">thender.xu</a>
 * @Date 2017/3/20 17:19.
 */
public class MoveEditorPaneImpl implements move_editorpane {

    protected final static Logger logger = LoggerFactory.getLogger(MoveEditorPaneImpl.class);

    static int mpx = 0; //当ctrl处于按下状态时, 按下鼠标左键的X位置(相对于坐标系)
    static int mpy = 0; //当ctrl处于按下状态时, 按下鼠标左键的Y位置(相对于坐标系)
    static int mx = 0;  //当ctrl处于按下状态时, 松下鼠标左键的X位置(相对于坐标系)
    static int my = 0;  //当ctrl处于按下状态时, 松下鼠标左键的Y位置(相对于坐标系)

    static int scrollX =0; //当ctrl处于按下状态时, 按下鼠标左键; 水平滚动抽的value值
    static int scrollY =0; //当ctrl处于按下状态时, 按下鼠标左键; 垂直滚动抽的VALUE值.

    public MoveEditorPaneImpl( int scrollX_, int scrollY_,Point mousePoint) {
        scrollX = scrollX_;
        scrollY = scrollY_;
        mpx = mousePoint.x;
        mpy = mousePoint.y;
    }

    /**
     * 调用这个接口用来实现移动画布,或者停止移动画布
     * 具体根据ctrl 和 鼠标 是否处于按下状态来决定.
     * <p>
     * 当按下鼠标左键时,发现ctrl处于按下状态, 调用本接口.
     * 记录鼠标相对于画布的(mpx,mpy) 坐标.
     * <p>
     * 当松开鼠标左键时,发现ctrl处于按下状态,调用本接口.
     * 记录(mx,my)
     * 计算并移动一次画布.
     *
     * @param obj  java.awt.Point
     */
    @Override
    public void moveOrNot(Object...obj) {
        if (obj[0] instanceof Point) {
            Point point = (Point) obj[0];
            setMXY(point.x, point.y);
        }else{
            throw  new RuntimeException("Not right Class");
        }

        JGraph jGraph = null;
        if (obj[1] instanceof JGraph) {
             jGraph = (JGraph) obj[1];
        }else{
            throw  new RuntimeException("Not right Class");
        }

        if (jGraph.getParent() instanceof JViewport) {
            JScrollPane scrollPane = (JScrollPane) jGraph.getParent().getParent();
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
            horizontalScrollBar.setValue(scrollX + mpx - mx);
            verticalScrollBar.setValue(scrollY + mpy - my);
        }else{
            logger.info("Not support move yet! ");
        }
    }

    /**
     * @param obj
     * @return 画布正方形在坐标系中的X坐标. 当按住ctrl后,鼠标左键按下时.
     */
    @Override
    public int findScrollX(Object obj) {
        return this.scrollX;
    }

    /**
     * @param obj
     * @return 画布正方形在坐标系中的Y坐标. 当按住ctrl后,鼠标左键按下时.
     */
    @Override
    public int findScrollY(Object obj) {
        return this.scrollY;
    }

    /**
     * @param x 当ctrl处于按下状态时, 按下鼠标左键的X位置(相对于坐标系)
     * @param y 当ctrl处于按下状态时, 按下鼠标左键的Y位置(相对于坐标系)
     * @return 1 SUCESS -1 FAILED
     */
    @Override
    public int setMpXY(int x, int y) {
        mpx = x;
        mpy = y;
        return 1;
    }

    /**
     * @param x 当ctrl处于按下状态时, 松下鼠标左键的X位置(相对于坐标系)
     * @param y 当ctrl处于按下状态时, 松下鼠标左键的Y位置(相对于坐标系)
     * @return 1 SUCESS -1 FAILED
     */
    @Override
    public int setMXY(int x, int y) {
        mx = x;
        my = y;
        return 1;
    }


}
