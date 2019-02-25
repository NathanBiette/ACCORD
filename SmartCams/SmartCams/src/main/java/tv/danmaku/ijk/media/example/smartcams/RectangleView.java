package tv.danmaku.ijk.media.example.smartcams;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by Iann2 on 13/04/2017.
 */

public class RectangleView extends View {

    private Rect rectangle;
    private Paint paint;
    private int left;
    private int top;
    private int width;
    private int height;

    public RectangleView(Context context, int left, int top, int width, int height) {
        super(context);

        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        rectangle = new Rect(left, top, left + width, top + height);

        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(rectangle, paint);
    }

    public void validateColor() {
        paint.setColor(Color.WHITE);
    }

    public void setRectangle(int left, int top) {
        this.left = left;
        this.top = top;
        rectangle.set(left, top, left + width, top + height);
    }

    public void setRectangleSize(int right, int bot) {
        rectangle.set(left, top, right, bot);
    }

    public void sortRectangle() {
        rectangle.sort();
    }
}
