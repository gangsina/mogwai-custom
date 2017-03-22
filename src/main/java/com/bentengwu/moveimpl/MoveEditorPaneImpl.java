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

    static final  int scrollStepLen = 60;

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
        } else {
            throw new RuntimeException("Not right Class");
        }

        JGraph jGraph = null;
        if (obj[1] instanceof JGraph) {
            jGraph = (JGraph) obj[1];
        } else {
            throw new RuntimeException("Not right Class");
        }

        if (jGraph.getParent() instanceof JViewport) {
            JScrollPane scrollPane = (JScrollPane) jGraph.getParent().getParent();
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();

//            logger.info("x: {} -->{}, y: {} -->{}",scrollX,scrollX + mpx - mx,scrollY,scrollY + mpy - my);
//            logger.info("({},{})", mx, my);

            scrollX = scrollX + mpx - mx < 0 ? 0 : scrollX + mpx - mx;
            scrollY = scrollY + mpy - my < 0 ? 0 : scrollY + mpy - my;

//            moveByStep(jGraph, horizontalScrollBar, verticalScrollBar, scrollX, scrollY);
            horizontalScrollBar.setValue(scrollX);
            verticalScrollBar.setValue(scrollY);

            mpx = mx;
            mpy = my;

        } else {
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
     * 设置当前横竖滚动轴的位置.
     *
     * @param x
     * @param y
     * @return 1 成功  -1 失败.
     */
    @Override
    public int setScrollXY(int x, int y) {
         scrollX = x;
        scrollY = y;
        return 1;
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

    /**
     * 滚动效果不理想,尝试将滚动按照步长来滚动.
     * @param horizontal 水平轴
     * @param vertical 垂直抽
     * @param horizontalEnd 水平轴滚动的终点
     * @param verticalEnd  The vertical scroll end position.
     */
    public void moveByStep(JGraph jGraph, JScrollBar horizontal, JScrollBar vertical,  int horizontalEnd,
                            int verticalEnd) {
        int oldH = horizontal.getValue();
        int oldV = vertical.getValue();
        logger.info("20170322: move by step : ({},{}),({},{})", oldH, oldV, horizontalEnd, verticalEnd);

        if ( Math.abs(oldH - horizontalEnd) < scrollStepLen
                && Math.abs(oldV - verticalEnd) < scrollStepLen) {
            horizontal.setValue(horizontalEnd);
            vertical.setValue(verticalEnd);
            return;
        }else {
            //move one step;
            int newH = oldH<horizontalEnd?oldH+scrollStepLen : oldH - scrollStepLen;
            newH = newH<0?0:newH;

            int newV = oldV<verticalEnd?oldV + scrollStepLen : oldV-scrollStepLen;
            newV = newV<0?0:newV;

            horizontal.setValue(newH);
            vertical.setValue(newV);

            jGraph.repaint();
            moveByStep(jGraph,horizontal,vertical,horizontalEnd,verticalEnd);
        }

    }


}
