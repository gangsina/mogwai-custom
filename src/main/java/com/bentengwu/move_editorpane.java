package com.bentengwu;

/**
 * 这个接口用来实现移动编辑界面的画布.
 * Created by thender on 2017/3/19.
 */
public interface move_editorpane extends base_intf {

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
     */
    public void moveOrNot(Object...obj);

    /**
     *
     * @param obj
     * @return 水平轴位置
     */
    public int findScrollX(Object obj);

    /**
     * @param obj
     * @return 垂直轴位置
     */
    public int findScrollY(Object obj);

    /**
     * @param x 当ctrl处于按下状态时, 按下鼠标左键的X位置(相对于画布)
     * @param y 当ctrl处于按下状态时, 按下鼠标左键的Y位置(相对于画布)
     * @return 1 SUCESS -1 FAILED
     */
    public int setMpXY(int x, int y);

    /**
     * @param x 当ctrl处于按下状态时, 松下鼠标左键的X位置(相对于画布)
     * @param y 当ctrl处于按下状态时, 松下鼠标左键的Y位置(相对于画布)
     * @return 1 SUCESS -1 FAILED
     */

    public int setMXY(int x, int y);


}
