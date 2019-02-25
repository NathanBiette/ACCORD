package tv.danmaku.ijk.media.example.smartcams;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Iann2 on 14/04/2017.
 */

public class PointView extends View {

    private int x;
    private int y;
    private Paint paint;

    public PointView(Context context, int x, int y) {
        super(context);

        this.x = x;
        this.y = y;

        paint = new Paint();
        paint.setStrokeWidth(15);
        paint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPoint(x, y, paint);
    }

}
