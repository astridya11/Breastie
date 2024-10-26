package fyp.yuki.vitrox2;

import android.graphics.Bitmap;
import com.squareup.picasso.Transformation;

public class CircleTransformation implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        // Create a Canvas with the specified Bitmap
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        // Create a Paint object to define the style for the circular shape
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setAntiAlias(true);
        paint.setShader(new android.graphics.BitmapShader(squaredBitmap, android.graphics.Shader.TileMode.CLAMP, android.graphics.Shader.TileMode.CLAMP));

        // Draw a circle on the Canvas with the specified Paint
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        squaredBitmap.recycle();

        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}

