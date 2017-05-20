package com.newit.bsrpos_sql.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.newit.bsrpos_sql.Activity.ActBase;

import java.io.FileOutputStream;
import java.io.IOException;


public class AdpPrint extends PrintDocumentAdapter {
    private PrintedPdfDocument mDocument;
    private Context mContext;
    private View mView;
    private String filename;

    public AdpPrint(ActBase actInvoicePrint, View viewById, String filename) {
        mContext = actInvoicePrint;
        mView = viewById;
        this.filename = filename;
        mView = mView.getRootView();
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        mDocument = new PrintedPdfDocument(mContext, newAttributes);

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                .Builder(filename + ".pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1);

        PrintDocumentInfo info = builder.build();
        callback.onLayoutFinished(info, true);
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
// Start the page
        PdfDocument.Page page = mDocument.startPage(0);
        // Create a bitmap and put it a canvas for the view to draw to. Make it the size of the view
        mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mView.layout(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(mView.getMeasuredWidth(), mView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        mView.draw(canvas);
        // create a Rect with the view's dimensions.
        Rect src = new Rect(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());
        // get the page canvas and measure it.
        Canvas pageCanvas = page.getCanvas();
        float pageWidth = pageCanvas.getWidth();
        float pageHeight = pageCanvas.getHeight();
        // how can we fit the Rect src onto this page while maintaining aspect ratio?
        float scale = Math.min(pageWidth / src.width(), pageHeight / src.height());
        float left = (float) (pageWidth / 2 - src.width() * scale / 2.3);
        float top = (float) (pageHeight / 2 - src.height() * scale / 2.3);
        float right = (float) (pageWidth / 2 + src.width() * scale / 2.3);
        float bottom = (float) (pageHeight / 2 + src.height() * scale / 2.3);
        RectF dst = new RectF(left, top, right, bottom);

        pageCanvas.drawBitmap(bitmap, src, dst, null);
        mDocument.finishPage(page);

        try {
            mDocument.writeTo(new FileOutputStream(
                    destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            mDocument.close();
            mDocument = null;
        }
        callback.onWriteFinished(new PageRange[]{new PageRange(0, 0)});
    }

    public static void formatListView(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            //do nothing return null
            return;
        }
        //set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //setting listview item in adapter
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        myListView.setLayoutParams(params);
        // print height of adapter on log
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }
}
