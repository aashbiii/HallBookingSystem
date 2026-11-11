package com.mca.hallbooking.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import com.mca.hallbooking.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageUtil {

    /**
     * Loads a circular or regular avatar safely from file path, content URI, or resource URI into an ImageView.
     */
    public static void loadAvatar(Context context, String uriStr, ImageView imageView, int fallbackResId) {
        if (imageView == null) return;
        int fallback = fallbackResId != 0 ? fallbackResId : R.drawable.ic_person;

        if (uriStr == null || uriStr.trim().isEmpty()) {
            imageView.setImageResource(fallback);
            return;
        }

        try {
            if (uriStr.startsWith("file://") || uriStr.startsWith("/")) {
                String path = uriStr.startsWith("file://") ? uriStr.substring(7) : uriStr;
                File f = new File(path);
                if (f.exists()) {
                    Bitmap bmp = decodeSampledBitmap(f, 300, 300);
                    if (bmp != null) {
                        imageView.setImageBitmap(bmp);
                        return;
                    }
                }
            } else if (uriStr.startsWith("content://")) {
                Uri uri = Uri.parse(uriStr);
                Bitmap bmp = decodeSampledBitmapFromUri(context, uri, 300, 300);
                if (bmp != null) {
                    imageView.setImageBitmap(bmp);
                    return;
                }
            } else {
                Uri uri = Uri.parse(uriStr);
                imageView.setImageURI(uri);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView.setImageResource(fallback);
    }

    /**
     * Copies and persists an image picked from photo picker/gallery into internal app storage
     * so it never expires and is always available offline.
     */
    public static String saveProfileImage(Context context, long userId, Uri sourceUri) {
        try {
            File dir = new File(context.getFilesDir(), "profile_avatars");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File destFile = new File(dir, "avatar_" + userId + "_" + System.currentTimeMillis() + ".jpg");

            Bitmap bitmap = decodeSampledBitmapFromUri(context, sourceUri, 600, 600);
            if (bitmap != null) {
                try (OutputStream out = new FileOutputStream(destFile)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                }
                return Uri.fromFile(destFile).toString();
            } else {
                // Fallback direct stream copy
                try (InputStream in = context.getContentResolver().openInputStream(sourceUri);
                     OutputStream out = new FileOutputStream(destFile)) {
                    if (in == null) return null;
                    byte[] buffer = new byte[8192];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                }
                return Uri.fromFile(destFile).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Bitmap decodeSampledBitmap(File file, int reqWidth, int reqHeight) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Bitmap decodeSampledBitmapFromUri(Context context, Uri uri, int reqWidth, int reqHeight) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try (InputStream is = context.getContentResolver().openInputStream(uri)) {
                if (is == null) return null;
                BitmapFactory.decodeStream(is, null, options);
            }
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            try (InputStream is = context.getContentResolver().openInputStream(uri)) {
                if (is == null) return null;
                return BitmapFactory.decodeStream(is, null, options);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
